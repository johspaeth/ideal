package ideal;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table.Cell;

import boomerang.accessgraph.AccessGraph;
import boomerang.context.IContextRequester;
import heros.EdgeFunction;
import heros.InterproceduralCFG;
import heros.incremental.UpdatableWrapper;
import heros.solver.IDESolver;
import heros.solver.Pair;
import heros.solver.PathEdge;
import soot.SootMethod;
import soot.Unit;

public class AnalysisSolver<V>
		extends IDESolver<UpdatableWrapper<Unit>, AccessGraph, UpdatableWrapper<SootMethod>, V, InterproceduralCFG<UpdatableWrapper<Unit>, UpdatableWrapper<SootMethod>>> {

	private PerSeedAnalysisContext<V> context;
	
	protected static final Logger logger = LoggerFactory.getLogger(AnalysisSolver.class);

	public AnalysisSolver(IDEALAnalysisDefinition<V> analysisDefinition, PerSeedAnalysisContext<V> context) {
		super(new InternalAnalysisProblem<V>(analysisDefinition, context));
		this.context = context;
	}
	
	/*public UpdatableWrapper<Unit> wrapUnit(Unit unit)
	{
		return this.context.icfg().wrap(unit);
	}*/

	/**
	 * Starts the IFDS phase with the given path edge <d1>-><curr,d2>
	 * 
	 * @param d1
	 * @param curr
	 * @param d2
	 */
	public void injectPhase1Seed(AccessGraph d1, UpdatableWrapper<Unit> curr, AccessGraph d2, EdgeFunction<V> func) {
//		logger.debug("propagating UpdatableWrapper<Unit> " + curr);
		System.out.println("--------------------------------------IDESolver run()--------------------------------------------------------------------------------");
		super.propagate(d1, curr, d2, func, null, true);
		runExecutorAndAwaitCompletion();
		System.out.println("--------------------------------------IDESolver run()--------------------------------------------------------------------------------");
	}

	@Override
	protected void scheduleEdgeProcessing(PathEdge<UpdatableWrapper<Unit>, AccessGraph> edge) {
		worklist.add(new PathEdgeProcessingTask(edge));
		propagationCount++;
	}


	@Override
	protected void scheduleValueProcessing(ValuePropagationTask vpt) {
		context.checkTimeout();
		super.scheduleValueProcessing(vpt);
	}

	@Override
	protected void scheduleValueComputationTask(ValueComputationTask task) {
		context.checkTimeout();
		super.scheduleValueComputationTask(task);
	}

	public IContextRequester getContextRequestorFor(final AccessGraph d1, final UpdatableWrapper<Unit> stmt) {
		return new ContextRequester(d1, stmt);
	}

	private class ContextRequester implements IContextRequester {
		Multimap<SootMethod, AccessGraph> methodToStartFact = HashMultimap.create();
		private AccessGraph d1;

		public ContextRequester(AccessGraph d1, UpdatableWrapper<Unit> stmt) {
			this.d1 = d1;
			methodToStartFact.put(icfg.getMethodOf(stmt).getContents(), d1);
		}

		@Override
		public boolean continueAtCallSite(Unit callSite, SootMethod callee) {
			if (d1.equals(zeroValue)) {
				return true;
			}
			Collection<UpdatableWrapper<Unit>> startPoints = icfg.getStartPointsOf(context.icfg().wrap(callee));

			for (UpdatableWrapper<Unit> sp : startPoints) {
				for (AccessGraph g : new HashSet<>(methodToStartFact.get(callee))) {
					Map<UpdatableWrapper<Unit>, Set<Pair<AccessGraph, AccessGraph>>> inc = incoming(g, sp);
					for (Set<Pair<AccessGraph, AccessGraph>> in : inc.values()) {
						for (Pair<AccessGraph, AccessGraph> e : in) {
							methodToStartFact.put(icfg.getMethodOf(context.icfg().wrap(callSite)).getContents(), e.getO2());
						}
					}
					if (inc.containsKey(context.icfg().wrap(callSite)))
						return true;
				}
			}
			return false;
		}

		@Override
		public boolean isEntryPointMethod(SootMethod method) {
			return false;
		}
	}

	public void destroy() {
		jumpFn.clear();
		incoming.clear();
		endSummary.clear();
		incoming.clear();
	}

	public Set<Cell<AccessGraph, AccessGraph, EdgeFunction<V>>> getPathEdgesAt(UpdatableWrapper<Unit> statement) {
		return jumpFn.lookupByTarget(statement);
	}
	
	public void updateAnalysis() {
		this.update();
	}
}
