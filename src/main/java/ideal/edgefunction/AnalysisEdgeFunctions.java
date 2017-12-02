package ideal.edgefunction;

import boomerang.accessgraph.AccessGraph;
import heros.EdgeFunctions;
import heros.incremental.UpdatableWrapper;
import soot.SootMethod;
import soot.Unit;

/**
 * This class just lifts the regular JoinLattice from the Heros solver to the EdgeFunction.
 *
 */
public interface AnalysisEdgeFunctions<V> extends EdgeFunctions<UpdatableWrapper<Unit>, AccessGraph, UpdatableWrapper<SootMethod>, V> {
  V bottom();

  V top();

  V join(V left, V right);
}
