package ideal.pointsofaliasing;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import boomerang.AliasResults;
import heros.EdgeFunction;
import heros.incremental.UpdatableWrapper;
import heros.solver.PathEdge;
import ideal.PerSeedAnalysisContext;
import ideal.incremental.accessgraph.UpdatableAccessGraph;
import ideal.incremental.accessgraph.Utils;
import soot.Unit;

public class ReturnEvent<V> extends Event<V> {

	private boolean isStrongUpdate;
	private UpdatableWrapper<Unit> exitStmt;
	private UpdatableAccessGraph d2;
	private UpdatableWrapper<Unit> callSite;
	private UpdatableAccessGraph d3;
	private UpdatableWrapper<Unit> returnSite;
	private UpdatableAccessGraph d1;
	private EdgeFunction<V> func;

	public ReturnEvent(UpdatableWrapper<Unit> exitStmt, UpdatableAccessGraph d2, UpdatableWrapper<Unit> callSite, UpdatableAccessGraph d3, UpdatableWrapper<Unit> returnSite, UpdatableAccessGraph d1, EdgeFunction<V> func) {
		this.exitStmt = exitStmt;
		this.d2 = d2;
		this.callSite = callSite;
		this.d3 = d3;
		this.returnSite = returnSite;
		this.d1 = d1;
		this.func = func;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((callSite.getContents() == null) ? 0 : callSite.getContents().hashCode());
		result = prime * result + ((d2 == null) ? 0 : d2.getAccessGraph().hashCode());
		result = prime * result + ((d3 == null) ? 0 : d3.getAccessGraph().hashCode());
		result = prime * result + ((exitStmt.getContents() == null) ? 0 : exitStmt.getContents().hashCode());
		result = prime * result + ((returnSite.getContents() == null) ? 0 : returnSite.getContents().hashCode());
		return result;
//		return 1;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReturnEvent other = (ReturnEvent) obj;
		if (callSite == null) {
			if (other.callSite != null)
				return false;
		} else if (!callSite.getContents().equals(other.callSite.getContents()))
			return false;
		if (d2 == null) {
			if (other.d2 != null)
				return false;
		} else if (!d2.getAccessGraph().equals(other.d2.getAccessGraph()))
			return false;
		if (d3 == null) {
			if (other.d3 != null)
				return false;
		} else if (!d3.getAccessGraph().equals(other.d3.getAccessGraph()))
			return false;
		if (exitStmt == null) {
			if (other.exitStmt != null)
				return false;
		} else if (!exitStmt.getContents().equals(other.exitStmt.getContents()))
			return false;
		if (returnSite == null) {
			if (other.returnSite != null)
				return false;
		} else if (!returnSite.getContents().equals(other.returnSite.getContents()))
			return false;
		return true;
	}

	@Override
	public Collection<PathEdge<UpdatableWrapper<Unit>, UpdatableAccessGraph>> getPathEdges(PerSeedAnalysisContext<V> tsanalysis) {
		Set<PathEdge<UpdatableWrapper<Unit>, UpdatableAccessGraph>> res = new HashSet<>();
		for (UpdatableAccessGraph mayAliasingAccessGraph : getIndirectFlowTargets(tsanalysis)) {
			res.add(new PathEdge<UpdatableWrapper<Unit>, UpdatableAccessGraph>(d1, returnSite,mayAliasingAccessGraph));
		}
		return res;
	}

	@Override
	public Collection<UpdatableAccessGraph> getIndirectFlowTargets(PerSeedAnalysisContext<V> tsanalysis) {
		AliasResults results = tsanalysis.aliasesFor(d3, callSite, d1);
		checkMustAlias(results,tsanalysis);
		Collection<UpdatableAccessGraph> mayAliasSet = Utils.getUpdatableAccessGraph(results.mayAliasSet(), tsanalysis.icfg());
		tsanalysis.storeFlowAtPointOfAlias(this, mayAliasSet);
		return mayAliasSet;
	}

	private void checkMustAlias(AliasResults results,
			PerSeedAnalysisContext<V> context) {
		boolean isStrongUpdate = !results.queryTimedout() && results.keySet().size() == 1;
		if(isStrongUpdate)
			context.storeStrongUpdateAtCallSite(callSite, Utils.getUpdatableAccessGraph(results.mayAliasSet(), context.icfg()));
	}

	@Override
	public String toString() {
		return "[Event " + super.toString() + " returns to: " + returnSite + "]";
	}

	@Override
	UpdatableWrapper<Unit> getCallsite() {
		return callSite;
	}

	public EdgeFunction<V> getEdgeFunction() {
		return func;
	}

}
