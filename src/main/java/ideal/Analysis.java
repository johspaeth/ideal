package ideal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import boomerang.accessgraph.WrappedSootField;
import boomerang.cfg.AbstractUpdatableExtendedICFG;
import boomerang.cfg.IExtendedICFG;
import heros.incremental.CFGChangeSet;
import heros.incremental.UpdatableWrapper;
import ideal.debug.IDebugger;
import ideal.incremental.accessgraph.UpdatableAccessGraph;
import ideal.incremental.accessgraph.Utils;
import soot.MethodOrMethodContext;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.toolkits.callgraph.ReachableMethods;
import soot.util.queue.QueueReader;

public class Analysis<V> {

	public static boolean FLOWS_WITH_NON_EMPTY_PTS_SETS = false;
	public static boolean ENABLE_STATIC_FIELDS = true;
	public static boolean ALIASING_FOR_STATIC_FIELDS = false;
	public static boolean SEED_IN_APPLICATION_CLASS_METHOD = false;
	public static boolean PRINT_OPTIONS = false;

	private final IDebugger<V> debugger;
	private final IExtendedICFG<Unit, SootMethod> icfg;
	protected final IDEALAnalysisDefinition<V> analysisDefinition;
	private LinkedList<PerSeedAnalysisContext<V>> perSeedContexts;
	protected Set<IFactAtStatement> initialSeeds;
	private Map<UpdatableWrapper<Unit>, PerSeedAnalysisContext<V>> seedToContextMapping;
	private List<UpdatableWrapper<Unit>> initialSeedStmts;

	private boolean resultsUpdated = false;

	@SuppressWarnings("rawtypes")
	CFGChangeSet cfgChangeSet = new CFGChangeSet<>();

	public Analysis(IDEALAnalysisDefinition<V> analysisDefinition) {
		this.analysisDefinition = analysisDefinition;
		this.icfg = analysisDefinition.eIcfg();
		this.debugger = analysisDefinition.debugger();
		this.perSeedContexts = new LinkedList<PerSeedAnalysisContext<V>>();
		this.seedToContextMapping = new HashMap<>();
		this.initialSeedStmts = new ArrayList<>();
	}

	public void run() {
		printOptions();
		WrappedSootField.TRACK_STMT = false;
		initialSeeds = computeSeeds();

		if (initialSeeds.isEmpty())
			System.err.println("No seeds found!");
		else
			System.err.println("Analysing " + initialSeeds.size() + " seeds!");
		debugger.beforeAnalysis();
		for (IFactAtStatement seed : initialSeeds) {
			analysisForSeed(seed);
		}
		debugger.afterAnalysis();
	}

	public void analysisForSeed(IFactAtStatement seed){
		PerSeedAnalysisContext<V> currContext = new PerSeedAnalysisContext<>(analysisDefinition, seed);
		perSeedContexts.add(currContext);
		seedToContextMapping.put(seed.getStmt(), currContext);
		initialSeedStmts.add(seed.getStmt());
		currContext.run();
	}

	public void update(AbstractUpdatableExtendedICFG<Unit, SootMethod> newCfg) {

		List<Unit> newSeedStmts = new ArrayList<>();
		Set<IFactAtStatement> newSeedsInScene = new HashSet<>();
		List<Unit> retainedSeedStmts = new ArrayList<>();
		for(PerSeedAnalysisContext<V> contextSolver: perSeedContexts) {
			contextSolver.updateSolverResults(newCfg, cfgChangeSet);

			if(cfgChangeSet.isChangeSetComputed() && !resultsUpdated) {
				newSeedsInScene = computeSeeds();
				
				List<Unit> updatedSeedStmts = getSeedStmts(newSeedsInScene);
				retainedSeedStmts = new ArrayList<>(updatedSeedStmts);
				newSeedStmts = new ArrayList<>(updatedSeedStmts);
				
				retainedSeedStmts.retainAll(Utils.getUnits(initialSeedStmts));
				newSeedStmts.removeAll(retainedSeedStmts);
				
				removeOldContexts(seedToContextMapping, retainedSeedStmts);
				resultsUpdated = true;
			}

			//			contextSolver.destroy();
			System.err.println("updated " + retainedSeedStmts.size() + " seeds!");
		}

		for (IFactAtStatement seed : newSeedsInScene) {
			if(newSeedStmts.contains(seed.getStmt().getContents()))
				analysisForSeed(seed);
		}
		System.err.println("analysed " + newSeedStmts.size() + " new seeds!");
	}

	private void removeOldContexts(Map<UpdatableWrapper<Unit>, PerSeedAnalysisContext<V>> seedToContextMapping, List<Unit> oldSeeds) {
		Map<UpdatableWrapper<Unit>, PerSeedAnalysisContext<V>> updatedSeedToContextMapping = new HashMap<>();
		for (Entry<UpdatableWrapper<Unit>, PerSeedAnalysisContext<V>> seedContext : updatedSeedToContextMapping.entrySet()) {
			if(oldSeeds.contains(seedContext.getKey().getContents())) {
				updatedSeedToContextMapping.put(seedContext.getKey(), seedContext.getValue());
			}
		}
	}

	private List<Unit> getSeedStmts(Set<IFactAtStatement> seeds) {
		List<Unit> seedStmts = new ArrayList<>();
		for (IFactAtStatement seed : seeds) {
			seedStmts.add(seed.getStmt().getContents());
		}
		return seedStmts;
	}

	private void printOptions() {
		if(PRINT_OPTIONS)
			System.out.println(analysisDefinition);
	}

	public Set<IFactAtStatement> computeSeeds() {
		Set<IFactAtStatement> seeds = new HashSet<>();
		ReachableMethods rm = Scene.v().getReachableMethods();
		QueueReader<MethodOrMethodContext> listener = rm.listener();
		while (listener.hasNext()) {
			MethodOrMethodContext next = listener.next();
			seeds.addAll(computeSeeds(icfg.wrap(next.method())));
		}
		return seeds;
	}

	private Collection<IFactAtStatement> computeSeeds(UpdatableWrapper<SootMethod> method) {
		Set<IFactAtStatement> seeds = new HashSet<>();
		if (!method.getContents().hasActiveBody())
			return seeds;
		if (SEED_IN_APPLICATION_CLASS_METHOD && !method.getContents().getDeclaringClass().isApplicationClass())
			return seeds;
		for (Unit u : method.getContents().getActiveBody().getUnits()) {
			Collection<UpdatableWrapper<SootMethod>> calledMethods = (Collection<UpdatableWrapper<SootMethod>>) (icfg.isCallStmt(icfg.wrap(u)) ? icfg.getCalleesOfCallAt(icfg.wrap(u))
					: new HashSet<UpdatableWrapper<SootMethod>>());
			for (UpdatableAccessGraph fact : analysisDefinition.generate(method, icfg.wrap(u), calledMethods, icfg)) {
				seeds.add(new FactAtStatement(icfg.wrap(u),fact));
			}
		}
		return seeds;
	}

	public Map<String, Map<UpdatableAccessGraph, V>> getSummaryResults() {
		Map<String, Map<UpdatableAccessGraph, V>> results = new HashMap<>();
		QueueReader<MethodOrMethodContext> reachableMethods = Scene.v().getReachableMethods().listener();
		while(reachableMethods.hasNext()) {
			UpdatableWrapper<SootMethod> currMethod = icfg.wrap(reachableMethods.next().method());
			if(currMethod.getContents().toString().contains("open") || currMethod.getContents().toString().contains("close"))
				continue;
			Collection<UpdatableWrapper<Unit>> endPoints = icfg.getEndPointsOf(currMethod);
			for (UpdatableWrapper<Unit> endpoint : endPoints) {
				for(PerSeedAnalysisContext<V> context: seedToContextMapping.values()) {
					Map<UpdatableAccessGraph, V> resultAtEndPoint = context.getResultAt(endpoint);
					if(!resultAtEndPoint.isEmpty())
						results.put(context.getSeed().getStmt().getContents().toString() + " : " + currMethod.getContents().getSignature() + endpoint, resultAtEndPoint);
				}
			}
		}
		return results;
	}

	public long getEdgeCount() {
		long edgeCount = 0;
		for (Entry<UpdatableWrapper<Unit>, PerSeedAnalysisContext<V>> seed : seedToContextMapping.entrySet()) {
			edgeCount += seed.getValue().getEdgeCount();
		}
		return edgeCount;
	}

}
