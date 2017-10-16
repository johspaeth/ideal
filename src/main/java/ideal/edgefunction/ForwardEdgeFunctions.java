package ideal.edgefunction;

import boomerang.accessgraph.AccessGraph;
import boomerang.incremental.UpdatableWrapper;
import heros.EdgeFunction;
import heros.EdgeFunctions;
import heros.edgefunc.AllTop;
import ideal.PerSeedAnalysisContext;
import soot.SootMethod;
import soot.Unit;

public class ForwardEdgeFunctions<V> implements EdgeFunctions<UpdatableWrapper<Unit>, AccessGraph, UpdatableWrapper<SootMethod>, V> {

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
  public EdgeFunction<V> getNormalEdgeFunction(AccessGraph d1, UpdatableWrapper<Unit> curr,
      AccessGraph currNode, UpdatableWrapper<Unit> succ, AccessGraph succNode) {

    if (context.isNullnessBranch(curr.getContents(), succ.getContents(), currNode)) {
      return ALL_TOP;
    }
    return edgeFunctions.getNormalEdgeFunction(d1, curr, currNode, succ, succNode);
  }

  @Override
  public EdgeFunction<V> getCallEdgeFunction(AccessGraph callerD1, UpdatableWrapper<Unit> callSite,
      AccessGraph srcNode, UpdatableWrapper<SootMethod> calleeMethod, AccessGraph destNode) {
    return edgeFunctions.getCallEdgeFunction(callerD1, callSite, srcNode, calleeMethod, destNode);
  }

  @Override
  public EdgeFunction<V> getReturnEdgeFunction(AccessGraph callerD1,
      UpdatableWrapper<Unit> callSite, UpdatableWrapper<SootMethod> calleeMethod, UpdatableWrapper<Unit> exitStmt, AccessGraph exitNode, UpdatableWrapper<Unit> returnSite,
      AccessGraph retNode) {
    return edgeFunctions.getReturnEdgeFunction(callerD1, callSite, calleeMethod, exitStmt, exitNode,
        returnSite,
        retNode);
  }

  @Override
  public EdgeFunction<V> getCallToReturnEdgeFunction(AccessGraph d1,
      UpdatableWrapper<Unit> callSite, AccessGraph callNode, UpdatableWrapper<Unit> returnSite, AccessGraph returnSiteNode) {
    // Assign the top function to call-to-return flows where we know about a strong update.
    if (context.isStrongUpdate(callSite.getContents(), returnSiteNode)) {
      return ALL_TOP;
    }
    return edgeFunctions.getCallToReturnEdgeFunction(d1, callSite, callNode, returnSite,
        returnSiteNode);
  }

}
