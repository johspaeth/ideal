package ideal;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import boomerang.accessgraph.AccessGraph;
import boomerang.accessgraph.WrappedSootField;
import boomerang.cfg.IExtendedICFG;
import boomerang.incremental.UpdatableWrapper;
import ideal.debug.IDebugger;
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

	public Analysis(IDEALAnalysisDefinition<V> analysisDefinition) {
		this.analysisDefinition = analysisDefinition;
		this.icfg = analysisDefinition.eIcfg();
		this.debugger = analysisDefinition.debugger();
		this.perSeedContexts = new LinkedList<PerSeedAnalysisContext<V>>();
	}

	public void run() {
		printOptions();
		WrappedSootField.TRACK_STMT = false;
		Set<IFactAtStatement> initialSeeds = computeSeeds();
		
		System.out.println("Initial seeds " + initialSeeds);
		for (IFactAtStatement iFactAtStatement : initialSeeds) {
			System.out.println("stmt " + iFactAtStatement.getStmt() + " AccessGraph " + iFactAtStatement.getFact());
		}
		
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
		System.out.println("\nseed in analysisForSeed " + seed.getStmt());
		perSeedContexts.add(new PerSeedAnalysisContext<>(analysisDefinition, seed));
		perSeedContexts.get(perSeedContexts.size()-1).run();
	}
	
	public void update() {
		for(PerSeedAnalysisContext<V> contextSolver: perSeedContexts) {
			contextSolver.updateSolverResults();
			contextSolver.destroy();
		}
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
			for (AccessGraph fact : analysisDefinition.generate(method, icfg.wrap(u), calledMethods)) {
				seeds.add(new FactAtStatement(icfg.wrap(u),fact));
			}
		}
		return seeds;
	}

}
