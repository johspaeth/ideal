package test;

import boomerang.accessgraph.AccessGraph;
import heros.incremental.UpdatableWrapper;
import soot.Unit;
import typestate.TypestateDomainValue;

public interface ComparableResult<State> {

	public AccessGraph getAccessGraph();
	public UpdatableWrapper<Unit> getStmt();
	public void computedResults(TypestateDomainValue<State> val);
}
