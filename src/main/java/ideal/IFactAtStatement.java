package ideal;

import boomerang.accessgraph.AccessGraph;
import heros.incremental.UpdatableWrapper;
import soot.Unit;

public interface IFactAtStatement {
	public AccessGraph getFact();

	public UpdatableWrapper<Unit> getStmt();
}
