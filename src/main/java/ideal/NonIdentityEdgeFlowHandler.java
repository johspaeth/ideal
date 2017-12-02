package ideal;

import boomerang.accessgraph.AccessGraph;
import heros.EdgeFunction;
import heros.incremental.UpdatableWrapper;
import soot.Unit;

public interface NonIdentityEdgeFlowHandler<V> {

	public void onCallToReturnFlow(AccessGraph d2, UpdatableWrapper<Unit> callSite, AccessGraph d3, UpdatableWrapper<Unit> returnSite, AccessGraph d1,
			EdgeFunction<V> func);

	public void onReturnFlow(AccessGraph d2, UpdatableWrapper<Unit> callSite, AccessGraph d3, UpdatableWrapper<Unit> returnSite, AccessGraph d1,
			EdgeFunction<V> func);

}
