package ideal.edgefunction;

import heros.EdgeFunctions;
import heros.incremental.UpdatableWrapper;
import ideal.incremental.accessgraph.UpdatableAccessGraph;
import soot.SootMethod;
import soot.Unit;

/**
 * This class just lifts the regular JoinLattice from the Heros solver to the EdgeFunction.
 *
 */
public interface AnalysisEdgeFunctions<V> extends EdgeFunctions<UpdatableWrapper<Unit>, UpdatableAccessGraph, UpdatableWrapper<SootMethod>, V> {
  V bottom();

  V top();

  V join(V left, V right);
}
