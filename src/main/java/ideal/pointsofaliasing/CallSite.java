package ideal.pointsofaliasing;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import boomerang.AliasResults;
import boomerang.accessgraph.WrappedSootField;
import heros.incremental.UpdatableWrapper;
import heros.solver.PathEdge;
import ideal.PerSeedAnalysisContext;
import ideal.incremental.accessgraph.UpdatableAccessGraph;
import ideal.incremental.accessgraph.UpdatableWrappedSootField;
import ideal.incremental.accessgraph.Utils;
import soot.Unit;

public class CallSite<V> extends AbstractPointOfAlias<V> {

	private UpdatableAccessGraph callerCallSiteFact;

	public CallSite(UpdatableAccessGraph callerD1, UpdatableWrapper<Unit> stmt, UpdatableAccessGraph callerCallSiteFact, UpdatableAccessGraph callerD2,
			UpdatableWrapper<Unit> returnSite) {
		super(callerD1, stmt, callerD2, returnSite);
		this.callerCallSiteFact = callerCallSiteFact;
	}

	@Override
	public Collection<PathEdge<UpdatableWrapper<Unit>, UpdatableAccessGraph>> getPathEdges(PerSeedAnalysisContext<V> tsanalysis) {
		Collection<PathEdge<UpdatableWrapper<Unit>, UpdatableAccessGraph>> res = new HashSet<>();

		if (d2.getFieldCount() > 0 && !callerCallSiteFact.equals(d2)) {
			res.addAll(unbalancedReturn(tsanalysis));
		}
		return res;
	}

	public Collection<UpdatableAccessGraph> getIndirectFlowTargets(PerSeedAnalysisContext<V> tsanalysis) {
		Collection<UpdatableWrappedSootField> lastFields = d2.getLastField();
		Set<UpdatableAccessGraph> popLastField = d2.popLastField();
		Set<UpdatableAccessGraph> res = new HashSet<>();
		for (UpdatableAccessGraph withoutLast : popLastField) {
			AliasResults results = tsanalysis.aliasesFor(withoutLast, curr, d1);
			for (UpdatableAccessGraph mayAliasingAccessGraph : Utils.getUpdatableAccessGraph(results.mayAliasSet(), tsanalysis.icfg())) {
				for (UpdatableWrappedSootField lastField : lastFields) {
					UpdatableAccessGraph g = mayAliasingAccessGraph.appendFields(new UpdatableWrappedSootField[] { lastField });
					res.add(g);
					tsanalysis.debugger().indirectFlowAtCall(withoutLast, curr.getContents(), g);
				}
			}
		}
		tsanalysis.storeFlowAtPointOfAlias(this, res);
		return res;
	}

	private Collection<PathEdge<UpdatableWrapper<Unit>, UpdatableAccessGraph>> unbalancedReturn(PerSeedAnalysisContext<V> tsanalysis) {
		Set<PathEdge<UpdatableWrapper<Unit>, UpdatableAccessGraph>> res = new HashSet<>();
		for (UpdatableAccessGraph g : getIndirectFlowTargets(tsanalysis)) {
			res.add(new PathEdge<UpdatableWrapper<Unit>, UpdatableAccessGraph>(d1, succ, g));
		}
		return res;
	}

	public UpdatableWrapper<Unit> getCallSite() {
		return curr;
	}

	@Override
	public String toString() {
		return "[CallSite " + super.toString() + " returns to: " + succ + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((callerCallSiteFact == null) ? 0 : callerCallSiteFact.hashCode());
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
		CallSite other = (CallSite) obj;
		if (callerCallSiteFact == null) {
			if (other.callerCallSiteFact != null)
				return false;
		} else if (!callerCallSiteFact.equals(other.callerCallSiteFact))
			return false;
		return true;
	}

}
