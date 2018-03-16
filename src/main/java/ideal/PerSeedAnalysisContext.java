package ideal;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;

import boomerang.AliasFinder;
import boomerang.AliasResults;
import boomerang.Query;
import boomerang.accessgraph.AccessGraph;
import boomerang.cfg.AbstractUpdatableExtendedICFG;
import boomerang.cfg.IExtendedICFG;
import boomerang.context.IContextRequester;
import heros.EdgeFunction;
import heros.edgefunc.EdgeIdentity;
import heros.incremental.CFGChangeSet;
import heros.incremental.UpdatableWrapper;
import heros.solver.Pair;
import heros.solver.PathEdge;
import heros.utilities.DefaultValueMap;
import ideal.debug.IDebugger;
import ideal.edgefunction.AnalysisEdgeFunctions;
import ideal.incremental.accessgraph.UpdatableAccessGraph;
import ideal.incremental.accessgraph.Utils;
import ideal.pointsofaliasing.NullnessCheck;
import ideal.pointsofaliasing.PointOfAlias;
import ideal.pointsofaliasing.ReturnEvent;
import soot.MethodOrMethodContext;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.util.queue.QueueReader;

public class PerSeedAnalysisContext<V> {

	/**
	 * Global debugger object.
	 */
	private Set<PointOfAlias<V>> poas = new HashSet<>();
	private boolean idePhase;
	private Multimap<PointOfAlias<V>, UpdatableAccessGraph> callSiteToFlows = HashMultimap.create();
	private Multimap<UpdatableWrapper<Unit>, UpdatableAccessGraph> callSiteToStrongUpdates = HashMultimap.create();
	private Set<Pair<Pair<UpdatableWrapper<Unit>, UpdatableWrapper<Unit>>, UpdatableAccessGraph>> nullnessBranches = new HashSet<>();
	private AnalysisSolver<V> solver;
	private AnalysisSolver<V> phaseOneSolver;
	private AnalysisSolver<V> phaseTwoSolver;
	private Multimap<Unit, UpdatableAccessGraph> eventAtCallSite = HashMultimap.create();
	private AliasFinder boomerang;
	private IDEALAnalysisDefinition<V> analysisDefinition;
	private Stopwatch startTime;
	private IFactAtStatement seed;
	private Set<PointOfAlias<V>> seenPOA = new HashSet<>();
	private Map<PathEdge<UpdatableWrapper<Unit>, UpdatableAccessGraph>, EdgeFunction<V>> pathEdgeToEdgeFunc = new HashMap<>();

	public PerSeedAnalysisContext(IDEALAnalysisDefinition<V> analysisDefinition, IFactAtStatement seed) {
		this.seed = seed;
		this.analysisDefinition = analysisDefinition;
		this.scheduler = this.analysisDefinition.getScheduler();
		this.scheduler.setContext(this);
	}

	public void setSolver(AnalysisSolver<V> solver) {
		this.solver = solver;
	}

	public AnalysisEdgeFunctions<V> getEdgeFunctions() {
		return analysisDefinition.edgeFunctions();
	}

	public boolean addPOA(PointOfAlias<V> poa) {
		return poas.add(poa);
	}

	public Set<PointOfAlias<V>> getAndClearPOA() {
		HashSet<PointOfAlias<V>> res = new HashSet<>(poas);
		poas.clear();
		return res;
	}

	public boolean isInIDEPhase() {
		return idePhase;
	}

	public IFactAtStatement getSeed() {
		return seed;
	}

	public void enableIDEPhase() {
		idePhase = true;
	}

	public void disableIDEPhase() {
		idePhase = false;
	}

	/**
	 * Retrieves for a given call site POA the flow that occured.
	 * 
	 * @param cs
	 *            The call site POA object.
	 * @return
	 */
	public Collection<UpdatableAccessGraph> getFlowAtPointOfAlias(PointOfAlias<V> cs) {
		if (!isInIDEPhase())
			throw new RuntimeException("This can only be applied in the kill phase");
		return callSiteToFlows.get(cs);
	}

	/**
	 * At a field write statement all indirect flows are stored by calling that
	 * function.
	 * 
	 * @param instanceFieldWrite
	 * @param outFlows
	 */
	public void storeFlowAtPointOfAlias(PointOfAlias<V> instanceFieldWrite, Collection<UpdatableAccessGraph> outFlows) {
		callSiteToFlows.putAll(instanceFieldWrite, outFlows);
	}

	/**
	 * For a given callSite check is a strong update can be performed for the
	 * returnSideNode.
	 * 
	 * @param callSite
	 * @param returnSideNode
	 * @return
	 */
	public boolean isStrongUpdate(UpdatableWrapper<Unit> callSite, UpdatableAccessGraph returnSideNode) {
		boolean isStrongUpdate = analysisDefinition.enableStrongUpdates()
				&& callSiteToStrongUpdates.get(callSite).contains(returnSideNode);
		return isStrongUpdate;
	}

	public void storeStrongUpdateAtCallSite(UpdatableWrapper<Unit> callSite, Collection<UpdatableAccessGraph> mayAliasSet) {
		callSiteToStrongUpdates.putAll(callSite, mayAliasSet);
	}

	public boolean isNullnessBranch(UpdatableWrapper<Unit> curr, UpdatableWrapper<Unit> succ, UpdatableAccessGraph returnSideNode) {
		Pair<Pair<UpdatableWrapper<Unit>, UpdatableWrapper<Unit>>, UpdatableAccessGraph> key = new Pair<>(new Pair<UpdatableWrapper<Unit>, UpdatableWrapper<Unit>>(curr, succ), returnSideNode);
		return nullnessBranches.contains(key);
	}

	public void storeComputedNullnessFlow(NullnessCheck<V> nullnessCheck, AliasResults results) {
		for (UpdatableAccessGraph receivesUpdate : Utils.getUpdatableAccessGraph(results.mayAliasSet(), analysisDefinition.eIcfg())) {
			nullnessBranches.add(new Pair<Pair<UpdatableWrapper<Unit>, UpdatableWrapper<Unit>>, UpdatableAccessGraph>(
					new Pair<UpdatableWrapper<Unit>, UpdatableWrapper<Unit>>(nullnessCheck.getCurr(), nullnessCheck.getSucc()), receivesUpdate));
		}
	}

	public IExtendedICFG<Unit, SootMethod> icfg() {
		return analysisDefinition.eIcfg();
	}

	public IContextRequester getContextRequestorFor(final UpdatableAccessGraph d1, final UpdatableWrapper<Unit> stmt) {
		return solver.getContextRequestorFor(d1, stmt);
	}

	private DefaultValueMap<BoomerangQuery, AliasResults> queryToResult = new DefaultValueMap<BoomerangQuery, AliasResults>() {

		@Override
		protected AliasResults createItem(PerSeedAnalysisContext<V>.BoomerangQuery key) {
			try {
				IExtendedICFG<Unit, SootMethod> icfg = analysisDefinition.eIcfg();
				UpdatableAccessGraph updatableAccessGraph = Utils.getUpdatableAccessGraph(key.getAp(), icfg);
				boomerang.startQuery();
				AliasResults res = boomerang
						//						.findAliasAtStmt(key.getAp(), key.getStmt(), getContextRequestorFor(key.d1, analysisDefinition.eIcfg().wrap(key.getStmt())))
						.findAliasAtStmt(key.getAp(), key.getStmt(), getContextRequestorFor(updatableAccessGraph, analysisDefinition.eIcfg().wrap(key.getStmt())))
						.withoutNullAllocationSites();
				/*analysisDefinition.debugger().onAliasesComputed(
						key.getAp(), 
						key.getStmt(), 
						new UpdatableAccessGraph(
								key.d1.getBase(), 
								new UpdatableFieldGraph(
										Utils.wrappedToUpdatable(
												key.d1.getFieldGraph().getFields(),
												analysisDefinition.eIcfg()
												)
										),
										analysisDefinition.eIcfg().wrap(key.d1.getSourceStmt()),
										key.d1.hasNullAllocationSite()
									),
								res
								);*/
				if (res.queryTimedout()) {
					//							analysisDefinition.debugger().onAliasTimeout(key.getAp(), key.getStmt(), key.d1);
				}
				return res;
			} catch (Exception e) {
				e.printStackTrace();
				//				analysisDefinition.debugger().onAliasTimeout(key.getAp(), key.getStmt(), key.d1);
				checkTimeout();
				return new AliasResults();
			}
		}
	};
	public final IDEALScheduler<V> scheduler;

	private class BoomerangQuery extends Query {

		private AccessGraph d1;

		public BoomerangQuery(AccessGraph accessPath, Unit stmt, AccessGraph d1) {
			super(accessPath, stmt);
			this.d1 = d1;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + ((d1 == null) ? 0 : d1.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			BoomerangQuery other = (BoomerangQuery) obj;
			if (d1 == null) {
				if (other.d1 != null)
					return false;
			} else if (!d1.equals(other.d1))
				return false;
			return true;
		}

	}

	public void run() {
		debugger().startWithSeed(seed);
		startTime = Stopwatch.createStarted();
		phaseOneSolver = new AnalysisSolver<>(analysisDefinition, this);
		setSolver(phaseOneSolver);
		analysisDefinition.onStartWithSeed(seed,phaseOneSolver);
		try {
			//			System.out.println("================== STARTING PHASE 1 ==================");
			phase1(phaseOneSolver);
			//			solver.destroy();
			phaseTwoSolver = new AnalysisSolver<>(analysisDefinition, this);
			//			System.out.println("================== STARTING PHASE 2 ==================");
			phase2(phaseTwoSolver);
			setSolver(phaseTwoSolver);
		} catch (IDEALTimeoutException e) {
			System.out.println("Timeout of IDEAL, Budget:" + analysisDefinition.analysisBudgetInSeconds());
			debugger().onAnalysisTimeout(seed);
			reporter().onSeedTimeout(seed);
		}
		if(reporter() != null)
			reporter().onSeedFinished(seed, solver);
		//		destroy();
		//		solver.destroy();
	}

	private ResultReporter<V> reporter() {
		return analysisDefinition.resultReporter();
	}

	private void phase1(AnalysisSolver<V> solver) {
		debugger().startPhase1WithSeed(seed, solver);
		Set<PathEdge<UpdatableWrapper<Unit>, UpdatableAccessGraph>> worklist = new HashSet<>();
		if (icfg().isExitStmt(seed.getStmt())) {
			worklist.add(new PathEdge<UpdatableWrapper<Unit>, UpdatableAccessGraph>(InternalAnalysisProblem.ZERO, seed.getStmt(), seed.getFact()));
		} else {
			for (UpdatableWrapper<Unit> u : icfg().getSuccsOf(seed.getStmt())) {
				worklist.add(new PathEdge<UpdatableWrapper<Unit>, UpdatableAccessGraph>(InternalAnalysisProblem.ZERO, u, seed.getFact()));
			}
		}
		while (!worklist.isEmpty()) {
			//			debugger().startForwardPhase(worklist);
			for (PathEdge<UpdatableWrapper<Unit>, UpdatableAccessGraph> s : worklist) {
				EdgeFunction<V> func = pathEdgeToEdgeFunc.get(s);
				if (func == null)
					func = EdgeIdentity.v();
				solver.injectPhase1Seed(s.factAtSource(), s.getTarget(), s.factAtTarget(), func);
			}
			worklist.clear();
			Set<PointOfAlias<V>> pointsOfAlias = getAndClearPOA();
			debugger().startAliasPhase(pointsOfAlias);
			for (PointOfAlias<V> p : pointsOfAlias) {
				if (seenPOA.contains(p))
					continue;
				seenPOA.add(p);
				debugger().solvePOA(p);
				Collection<PathEdge<UpdatableWrapper<Unit>, UpdatableAccessGraph>> edges = p.getPathEdges(this);
				worklist.addAll(edges);
				if (p instanceof ReturnEvent) {
					ReturnEvent<V> returnEvent = (ReturnEvent<V>) p;
					for (PathEdge<UpdatableWrapper<Unit>, UpdatableAccessGraph> edge : edges) {
						pathEdgeToEdgeFunc.put(edge, returnEvent.getEdgeFunction());
					}
				}
			}
		}
		debugger().finishPhase1WithSeed(seed, solver);
	}

	private void phase2(AnalysisSolver<V> solver) {
		debugger().startPhase2WithSeed(seed, solver);
		enableIDEPhase();
		if (icfg().isExitStmt(seed.getStmt())) {
			solver.injectPhase1Seed(InternalAnalysisProblem.ZERO, seed.getStmt(), seed.getFact(), EdgeIdentity.<V>v());
		} else {
			for (UpdatableWrapper<Unit> u : icfg().getSuccsOf(seed.getStmt())) {
				solver.injectPhase1Seed(InternalAnalysisProblem.ZERO, u, seed.getFact(), EdgeIdentity.<V>v());
			}
		}
		solver.runExecutorAndAwaitCompletion();
		Map<UpdatableWrapper<Unit>, Set<UpdatableAccessGraph>> map = new HashMap<UpdatableWrapper<Unit>, Set<UpdatableAccessGraph>>();
		for (UpdatableWrapper<Unit> sp : icfg().getStartPointsOf(icfg().getMethodOf(seed.getStmt()))) {
			map.put(sp, Collections.singleton(InternalAnalysisProblem.ZERO));
		}
		solver.computeValues(map);
		analysisDefinition.onFinishWithSeed(seed,solver);
		//		debugger().finishPhase2WithSeed(seed, solver);
	}

	public AliasResults aliasesFor(UpdatableAccessGraph boomerangAccessGraph, UpdatableWrapper<Unit> curr, UpdatableAccessGraph d1) {
		if (!analysisDefinition.enableAliasing())
			return new AliasResults();
		//		checkTimeout();
		if (boomerang == null)
			boomerang = new AliasFinder(analysisDefinition.boomerangOptions());
		if (!boomerangAccessGraph.isStatic()
				&& Scene.v().getPointsToAnalysis().reachingObjects(boomerangAccessGraph.getBase().getContents()).isEmpty())
			return new AliasResults();

		//analysisDefinition.debugger().beforeAlias(boomerangAccessGraph.getAccessGraph(), curr.getContents(), d1.getAccessGraph());
		return queryToResult.getOrCreate(new BoomerangQuery(boomerangAccessGraph.getAccessGraph(), curr.getContents(), d1.getAccessGraph()));
	}

	public void destroy() {
		poas = null;
		callSiteToFlows.clear();
		callSiteToFlows = null;
		callSiteToStrongUpdates = null;
		nullnessBranches = null;
		eventAtCallSite.clear();
		analysisDefinition = null;

		//		destroy the solver later on if update needs to be called from Analysis.java on each analysisContext.
		phaseOneSolver.destroy();
		phaseTwoSolver.destroy();
	}

	public void checkTimeout() {
		if (startTime.elapsed(TimeUnit.SECONDS) > analysisDefinition.analysisBudgetInSeconds())
			throw new IDEALTimeoutException();
	}

	public IDebugger<V> debugger() {
		return analysisDefinition.debugger();
	}

	public boolean enableNullPointAlias() {
		return analysisDefinition.enableNullPointOfAlias();
	}

	@SuppressWarnings("unchecked")
	public void updateSolverResults(AbstractUpdatableExtendedICFG<Unit, SootMethod> newCfg, @SuppressWarnings("rawtypes") CFGChangeSet cfgChangeSet) {

		boomerang = null;
		seenPOA.clear();
		pathEdgeToEdgeFunc.clear();
		callSiteToFlows.clear();
		callSiteToStrongUpdates.clear();
		nullnessBranches.clear();
		pathEdgeToEdgeFunc.clear();
		queryToResult.clear();

		disableIDEPhase();
		phaseOneSolver.update(newCfg, cfgChangeSet, isInIDEPhase());

		Set<PointOfAlias<V>> pointsOfAlias = getAndClearPOA();
		//		debugger().startAliasPhase(pointsOfAlias);
		for (PointOfAlias<V> p : pointsOfAlias) {
			if (seenPOA.contains(p))
				continue;
			seenPOA.add(p);
			//			debugger().solvePOA(p);
			Collection<PathEdge<UpdatableWrapper<Unit>, UpdatableAccessGraph>> edges = p.getPathEdges(this);
			if (p instanceof ReturnEvent) {
				ReturnEvent<V> returnEvent = (ReturnEvent<V>) p;
				for (PathEdge<UpdatableWrapper<Unit>, UpdatableAccessGraph> edge : edges) {
					pathEdgeToEdgeFunc.put(edge, returnEvent.getEdgeFunction());
				}
			}
		}

		enableIDEPhase();
		phaseTwoSolver.update(newCfg, cfgChangeSet, isInIDEPhase());
	}

	public Table<UpdatableWrapper<Unit>, UpdatableAccessGraph, V> phaseOneResults() {
		return phaseOneSolver.allResults();
	}

	public Table<UpdatableWrapper<Unit>, UpdatableAccessGraph, V> phaseTwoResults() {
		return phaseTwoSolver.allResults();
	}

	/*public Map<String, Map<String, Map<UpdatableAccessGraph, V>>> getSummaryResults() {
		Map<String, Map<String, Map<UpdatableAccessGraph, V>>> results = new HashMap<>();

		QueueReader<MethodOrMethodContext> reachableMethods = Scene.v().getReachableMethods().listener();
		while(reachableMethods.hasNext()) {
			UpdatableWrapper<SootMethod> currMethod = icfg().wrap(reachableMethods.next().method());
			
			if(currMethod.getContents().getSignature().toString().contains("open") || currMethod.getContents().getSignature().toString().contains("close"))
				continue;
			
			Collection<UpdatableWrapper<Unit>> endPoints = icfg().getEndPointsOf(currMethod);
			Map<String, Map<UpdatableAccessGraph, V>> resultAtEndPoints = new HashMap<>();
			for (UpdatableWrapper<Unit> endPoint : endPoints) {
				resultAtEndPoints.put(endPoint.getContents().toString(), phaseTwoSolver.resultsAt(endPoint));
			}
			results.put(currMethod.getContents().getSignature().toString(), resultAtEndPoints);
		}

		return results;
	}*/

	public Map<UpdatableAccessGraph, V> getResultAt(UpdatableWrapper<Unit> stmt) {
		return phaseTwoSolver.resultsAt(stmt);
	}

	public long getEdgeCount() {
		return /*phaseOneSolver.getEdgeCount()*/phaseTwoSolver.getEdgeCount();
	}
}
