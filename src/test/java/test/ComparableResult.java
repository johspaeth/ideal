package test;

import heros.incremental.UpdatableWrapper;
import ideal.incremental.accessgraph.UpdatableAccessGraph;
import soot.Unit;
import typestate.TypestateDomainValue;

public interface ComparableResult<State> {

	public UpdatableAccessGraph getAccessGraph();
	public UpdatableWrapper<Unit> getStmt();
	public void computedResults(TypestateDomainValue<State> val);
}
