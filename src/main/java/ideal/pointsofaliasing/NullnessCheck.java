package ideal.pointsofaliasing;

import java.util.Collection;
import java.util.Collections;

import boomerang.AliasResults;
import boomerang.accessgraph.AccessGraph;
import boomerang.incremental.UpdatableWrapper;
import heros.solver.PathEdge;
import ideal.PerSeedAnalysisContext;
import soot.Unit;

public class NullnessCheck<V> extends AbstractPointOfAlias<V> {
	public NullnessCheck(AccessGraph callerD1, UpdatableWrapper<Unit> stmt, AccessGraph callerD2, UpdatableWrapper<Unit> returnSite) {
		super(callerD1, stmt, callerD2, returnSite);
	}

	@Override
	public Collection<PathEdge<UpdatableWrapper<Unit>, AccessGraph>> getPathEdges(PerSeedAnalysisContext<V> tsanalysis) {
		AliasResults results = tsanalysis.aliasesFor(d2, curr.getContents(), d1);
		if (results.withoutNullAllocationSites().keySet().size() <= 1) {
			tsanalysis.storeComputedNullnessFlow(this, results.withoutNullAllocationSites());
		}
		return Collections.emptySet();
	}

	public UpdatableWrapper<Unit> getCurr() {
		return curr;
	}

	public UpdatableWrapper<Unit> getSucc() {
		return succ;
	}

	@Override
	public String toString() {
		return "[Nullness " + super.toString() + " ifs to: " + succ + "]";
	}

	@Override
	public Collection<AccessGraph> getIndirectFlowTargets(PerSeedAnalysisContext<V> tsanalysis) {
		return Collections.emptySet();
	}

}
