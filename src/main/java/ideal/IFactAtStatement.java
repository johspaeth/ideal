package ideal;

import heros.incremental.UpdatableWrapper;
import ideal.incremental.accessgraph.UpdatableAccessGraph;
import soot.Unit;

public interface IFactAtStatement {
	public UpdatableAccessGraph getFact();

	public UpdatableWrapper<Unit> getStmt();
}
