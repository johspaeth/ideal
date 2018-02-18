package ideal.edgefunction;

import heros.EdgeFunction;
import heros.EdgeFunctions;
import heros.edgefunc.AllTop;
import heros.incremental.UpdatableWrapper;
import ideal.PerSeedAnalysisContext;
import ideal.incremental.accessgraph.UpdatableAccessGraph;
import soot.SootMethod;
import soot.Unit;

public class ForwardEdgeFunctions<V> implements EdgeFunctions<UpdatableWrapper<Unit>, UpdatableAccessGraph, UpdatableWrapper<SootMethod>, V> {

	private PerSeedAnalysisContext<V> context;
	private final EdgeFunction<V> ALL_TOP;
	private final AnalysisEdgeFunctions<V> edgeFunctions;

	public ForwardEdgeFunctions(PerSeedAnalysisContext<V> context,
			AnalysisEdgeFunctions<V> edgeFunctions) {
		this.context = context;
		this.edgeFunctions = edgeFunctions;
		this.ALL_TOP = new AllTop<V>(edgeFunctions.top());
	}

	@Override
	public EdgeFunction<V> getNormalEdgeFunction(UpdatableAccessGraph d1, UpdatableWrapper<Unit> curr,
			UpdatableAccessGraph currNode, UpdatableWrapper<Unit> succ, UpdatableAccessGraph succNode) {

		if (context.isNullnessBranch(curr, succ, currNode)) {
			return ALL_TOP;
		}
		return edgeFunctions.getNormalEdgeFunction(d1, curr, currNode, succ, succNode);
	}

	@Override
	public EdgeFunction<V> getCallEdgeFunction(UpdatableAccessGraph callerD1, UpdatableWrapper<Unit> callSite,
			UpdatableAccessGraph srcNode, UpdatableWrapper<SootMethod> calleeMethod, UpdatableAccessGraph destNode) {
		return edgeFunctions.getCallEdgeFunction(callerD1, callSite, srcNode, calleeMethod, destNode);
	}

	@Override
	public EdgeFunction<V> getReturnEdgeFunction(UpdatableAccessGraph callerD1,
			UpdatableWrapper<Unit> callSite, UpdatableWrapper<SootMethod> calleeMethod, UpdatableWrapper<Unit> exitStmt, UpdatableAccessGraph exitNode, UpdatableWrapper<Unit> returnSite,
			UpdatableAccessGraph retNode) {
		return edgeFunctions.getReturnEdgeFunction(callerD1, callSite, calleeMethod, exitStmt, exitNode,
				returnSite,
				retNode);
	}

	@Override
	public EdgeFunction<V> getCallToReturnEdgeFunction(UpdatableAccessGraph d1,
			UpdatableWrapper<Unit> callSite, UpdatableAccessGraph callNode, UpdatableWrapper<Unit> returnSite, UpdatableAccessGraph returnSiteNode) {
		// Assign the top function to call-to-return flows where we know about a strong update.
		if (context.isStrongUpdate(callSite, returnSiteNode)) {
			return ALL_TOP;
		}
		return edgeFunctions.getCallToReturnEdgeFunction(d1, callSite, callNode, returnSite,
				returnSiteNode);
	}

	@Override
	public void updateEdgeFunction() {
		edgeFunctions.updateEdgeFunction();
	}

}
