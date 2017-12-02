package ideal.pointsofaliasing;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import boomerang.accessgraph.AccessGraph;
import heros.incremental.UpdatableWrapper;
import heros.solver.PathEdge;
import ideal.PerSeedAnalysisContext;
import soot.Local;
import soot.Unit;

public class InstanceFieldWrite<V> extends AbstractPointOfAlias<V> {

	private Local base;

	public InstanceFieldWrite(AccessGraph d1, UpdatableWrapper<Unit> stmt, Local base, AccessGraph d2, UpdatableWrapper<Unit> succ) {
		super(d1, stmt, d2, succ);
		this.base = base;
	}

	@Override
	public Collection<PathEdge<UpdatableWrapper<Unit>, AccessGraph>> getPathEdges(PerSeedAnalysisContext<V> tsanalysis) {
		Set<PathEdge<UpdatableWrapper<Unit>, AccessGraph>> res = new HashSet<>();

		Set<AccessGraph> outFlows = new HashSet<>();
		for (AccessGraph mayAliasingAccessGraph : this.getIndirectFlowTargets(tsanalysis)) {
			AccessGraph withFields = mayAliasingAccessGraph.appendGraph(d2.getFieldGraph());
			outFlows.add(withFields);
			tsanalysis.debugger().indirectFlowAtWrite(d2, curr.getContents(), withFields);
			res.add(new PathEdge<UpdatableWrapper<Unit>, AccessGraph>(d1, succ, withFields));
		}
		tsanalysis.storeFlowAtPointOfAlias(this, outFlows);
		return res;
	}

	public Collection<AccessGraph> getIndirectFlowTargets(PerSeedAnalysisContext<V> tsanalysis) {
		AccessGraph accessGraph = new AccessGraph(base);
		Collection<AccessGraph> results = tsanalysis.aliasesFor(accessGraph, curr.getContents(), d1).mayAliasSet();
		return results;
	}

	public UpdatableWrapper<Unit> getCallSite() {
		return curr;
	}
}
