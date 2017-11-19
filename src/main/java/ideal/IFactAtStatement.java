package ideal;

import boomerang.accessgraph.AccessGraph;
import boomerang.incremental.UpdatableWrapper;
import soot.Unit;

public interface IFactAtStatement {
	public AccessGraph getFact();

	public UpdatableWrapper<Unit> getStmt();
}
