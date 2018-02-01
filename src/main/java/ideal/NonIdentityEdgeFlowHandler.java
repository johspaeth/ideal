package ideal;

import heros.EdgeFunction;
import heros.incremental.UpdatableWrapper;
import ideal.incremental.accessgraph.UpdatableAccessGraph;
import soot.Unit;

public interface NonIdentityEdgeFlowHandler<V> {

	public void onCallToReturnFlow(UpdatableAccessGraph d2, UpdatableWrapper<Unit> callSite, UpdatableAccessGraph d3, UpdatableWrapper<Unit> returnSite, UpdatableAccessGraph d1,
			EdgeFunction<V> func);

	public void onReturnFlow(UpdatableAccessGraph d2, UpdatableWrapper<Unit> callSite, UpdatableAccessGraph d3, UpdatableWrapper<Unit> returnSite, UpdatableAccessGraph d1,
			EdgeFunction<V> func);

}
