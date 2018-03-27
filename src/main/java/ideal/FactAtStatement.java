package ideal;

import heros.incremental.UpdatableWrapper;
import ideal.incremental.accessgraph.UpdatableAccessGraph;
import soot.Unit;

public class FactAtStatement implements IFactAtStatement {

	private UpdatableAccessGraph fact;
	private UpdatableWrapper<Unit> u;

	public FactAtStatement(UpdatableWrapper<Unit> u, UpdatableAccessGraph fact) {
		this.u = u;
		this.fact = fact;
	}

	public UpdatableAccessGraph getFact() {
		return fact;
	}

	public UpdatableWrapper<Unit> getStmt() {
		return u;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fact == null) ? 0 : fact.hashCode());
		result = prime * result + ((u == null) ? 0 : u.getContents().hashCode());
//		return result;
		return 1;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FactAtStatement other = (FactAtStatement) obj;
		if (fact == null) {
			if (other.fact != null)
				return false;
		} else if (!fact.equals(other.fact))
			return false;
		if (u == null) {
			if (other.u != null)
				return false;
		} else if (!u.getContents().equals(other.u.getContents()))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return fact + " @ " + u;
	}
}
