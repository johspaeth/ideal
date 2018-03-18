package ideal.pointsofaliasing;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import heros.incremental.UpdatableWrapper;
import heros.solver.PathEdge;
import ideal.PerSeedAnalysisContext;
import ideal.incremental.accessgraph.UpdatableAccessGraph;
import ideal.incremental.accessgraph.Utils;
import soot.Local;
import soot.Unit;

public class InstanceFieldWrite<V> extends AbstractPointOfAlias<V> {

	private UpdatableWrapper<Local> base;

	public InstanceFieldWrite(UpdatableAccessGraph d1, UpdatableWrapper<Unit> stmt, UpdatableWrapper<Local> base, UpdatableAccessGraph d2, UpdatableWrapper<Unit> succ) {
		super(d1, stmt, d2, succ);
		this.base = base;
	}

	@Override
	public Collection<PathEdge<UpdatableWrapper<Unit>, UpdatableAccessGraph>> getPathEdges(PerSeedAnalysisContext<V> tsanalysis) {
		Set<PathEdge<UpdatableWrapper<Unit>, UpdatableAccessGraph>> res = new HashSet<>();

		Set<UpdatableAccessGraph> outFlows = new HashSet<>();
		for (UpdatableAccessGraph mayAliasingAccessGraph : this.getIndirectFlowTargets(tsanalysis)) {
			UpdatableAccessGraph withFields = mayAliasingAccessGraph.appendGraph(d2.getFieldGraph());
			outFlows.add(withFields);
			tsanalysis.debugger().indirectFlowAtWrite(d2, curr.getContents(), withFields);
			res.add(new PathEdge<UpdatableWrapper<Unit>, UpdatableAccessGraph>(d1, succ, withFields));
		}
		tsanalysis.storeFlowAtPointOfAlias(this, outFlows);
		return res;
	}

	public Collection<UpdatableAccessGraph> getIndirectFlowTargets(PerSeedAnalysisContext<V> tsanalysis) {
		UpdatableAccessGraph accessGraph = new UpdatableAccessGraph(base);
		Collection<UpdatableAccessGraph> results = Utils.getUpdatableAccessGraph(tsanalysis.aliasesFor(accessGraph, curr, d1).mayAliasSet(), tsanalysis.icfg());
		return results;
	}

	public UpdatableWrapper<Unit> getCallSite() {
		return curr;
	}
}
