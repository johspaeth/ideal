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

import boomerang.context.IContextRequester;
import heros.EdgeFunction;
import heros.InterproceduralCFG;
import heros.incremental.UpdatableWrapper;
import heros.solver.IDESolver;
import heros.solver.Pair;
import heros.solver.PathEdge;
import ideal.incremental.accessgraph.UpdatableAccessGraph;
import soot.SootMethod;
import soot.Unit;

public class AnalysisSolver<V>
		extends IDESolver<UpdatableWrapper<Unit>, UpdatableAccessGraph, UpdatableWrapper<SootMethod>, V, InterproceduralCFG<UpdatableWrapper<Unit>, UpdatableWrapper<SootMethod>>> {

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
	public void injectPhase1Seed(UpdatableAccessGraph d1, UpdatableWrapper<Unit> curr, UpdatableAccessGraph d2, EdgeFunction<V> func) {
		super.propagate(d1, curr, d2, func, null, true);
		runExecutorAndAwaitCompletion();
	}

	@Override
	protected void scheduleEdgeProcessing(PathEdge<UpdatableWrapper<Unit>, UpdatableAccessGraph> edge) {
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

	public IContextRequester getContextRequestorFor(final UpdatableAccessGraph d1, final UpdatableWrapper<Unit> stmt) {
		return new ContextRequester(d1, stmt);
	}

	private class ContextRequester implements IContextRequester {
		Multimap<SootMethod, UpdatableAccessGraph> methodToStartFact = HashMultimap.create();
		private UpdatableAccessGraph d1;

		public ContextRequester(UpdatableAccessGraph d1, UpdatableWrapper<Unit> stmt) {
			this.d1 = d1;
			methodToStartFact.put(context.icfg().getMethodOf(stmt).getContents(), d1);
		}

		@Override
		public boolean continueAtCallSite(Unit callSite, SootMethod callee) {
			if (d1.equals(zeroValue)) {
				return true;
			}
			Collection<UpdatableWrapper<Unit>> startPoints = icfg().getStartPointsOf(context.icfg().wrap(callee));

			for (UpdatableWrapper<Unit> sp : startPoints) {
				for (UpdatableAccessGraph g : new HashSet<>(methodToStartFact.get(callee))) {
					Map<UpdatableWrapper<Unit>, Set<Pair<UpdatableAccessGraph, UpdatableAccessGraph>>> inc = incoming(g, sp);
					for (Set<Pair<UpdatableAccessGraph, UpdatableAccessGraph>> in : inc.values()) {
						for (Pair<UpdatableAccessGraph, UpdatableAccessGraph> e : in) {
							methodToStartFact.put(icfg().getMethodOf(context.icfg().wrap(callSite)).getContents(), e.getO2());
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

	public Set<Cell<UpdatableAccessGraph, UpdatableAccessGraph, EdgeFunction<V>>> getPathEdgesAt(UpdatableWrapper<Unit> statement) {
		return jumpFn.lookupByTarget(statement);
	}
}
