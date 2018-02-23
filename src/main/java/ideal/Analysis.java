package ideal;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import boomerang.accessgraph.WrappedSootField;
import boomerang.cfg.AbstractUpdatableExtendedICFG;
import boomerang.cfg.IExtendedICFG;
import heros.incremental.CFGChangeSet;
import heros.incremental.UpdatableWrapper;
import ideal.debug.IDebugger;
import ideal.incremental.accessgraph.UpdatableAccessGraph;
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

	public Analysis(IDEALAnalysisDefinition<V> analysisDefinition) {
		this.analysisDefinition = analysisDefinition;
		this.icfg = analysisDefinition.eIcfg();
		this.debugger = analysisDefinition.debugger();
		this.perSeedContexts = new LinkedList<PerSeedAnalysisContext<V>>();
	}

	public void run() {
		printOptions();
		WrappedSootField.TRACK_STMT = false;
		initialSeeds = computeSeeds();
		
		/*System.out.println("Initial seeds " + initialSeeds);
		for (IFactAtStatement iFactAtStatement : initialSeeds) {
			System.out.println("stmt " + iFactAtStatement.getStmt() + " AccessGraph " + iFactAtStatement.getFact());
		}*/
		
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
//		System.out.println("\nseed in analysisForSeed " + seed.getStmt());
		PerSeedAnalysisContext<V> currContext = new PerSeedAnalysisContext<>(analysisDefinition, seed);
		perSeedContexts.add(currContext);
		currContext.run();
	}
	
	public void update(AbstractUpdatableExtendedICFG<Unit, SootMethod> newCfg) {
		@SuppressWarnings("rawtypes")
		CFGChangeSet cfgChangeSet = new CFGChangeSet<>();
		System.out.println("updating " + perSeedContexts.size() + " perSeedContexts");
		for(PerSeedAnalysisContext<V> contextSolver: perSeedContexts) {
			contextSolver.updateSolverResults(newCfg, cfgChangeSet);
//			contextSolver.destroy();
		}
		
		/*Set<IFactAtStatement> seedsAfterUpdate = computeSeeds();
		seedsAfterUpdate.removeAll(initialSeeds);
		System.out.println("new seeds are " + seedsAfterUpdate);
		for (IFactAtStatement newSeed : seedsAfterUpdate) {
			analysisForSeed(newSeed);
		}*/
		
		Set<IFactAtStatement> newSeeds = getNewSeeds(computeSeeds());
		for (IFactAtStatement newSeed : newSeeds) {
			System.out.println("analysing new seed " + newSeed);
			analysisForSeed(newSeed);
		}
	}
	
	private Set<IFactAtStatement> getNewSeeds(Set<IFactAtStatement> newSeeds) {
		Set<IFactAtStatement> newSeedsInScene = new HashSet<>();
		int newSeedCount = newSeeds.size();
		int oldSeedCount = initialSeeds.size();
		for (IFactAtStatement newSeed : newSeeds) {
			for (IFactAtStatement initialSeed : initialSeeds) {
				if(newSeed.getStmt().getContents() != initialSeed.getStmt().getContents() && newSeedCount > oldSeedCount) {
					newSeedsInScene.add(newSeed);
				}
			}
		}
		return newSeedsInScene;
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
			Collection<UpdatableWrapper<Unit>> endPoints = icfg.getEndPointsOf(currMethod);
			for (UpdatableWrapper<Unit> endpoint : endPoints) {
				for(PerSeedAnalysisContext<V> context: perSeedContexts) {
					Map<UpdatableAccessGraph, V> resultAtEndPoint = context.getResultAt(endpoint);
					if(!resultAtEndPoint.isEmpty())
						results.put(context.getSeed().getStmt().getContents().toString() + " : " + currMethod.getContents().getSignature() + endpoint, resultAtEndPoint);
				}
			}
		}
		/*Map<String, Map<String, Map<String, Map<UpdatableAccessGraph, V>>>> results = new HashMap<>();
		for(PerSeedAnalysisContext<V> context: perSeedContexts) {
			results.put(context.getSeed().getStmt().getContents().toString(), context.getSummaryResults());
			Map<String, Map<String, Map<UpdatableAccessGraph, V>>> perSeedResult = context.getSummaryResults();
			for (IFactAtStatement iFactAtStatement : ) {
				
			}
		}
		return results;*/
		return results;
	}

}
