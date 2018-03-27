package ideal.pointsofaliasing;

import heros.incremental.UpdatableWrapper;
import ideal.incremental.accessgraph.UpdatableAccessGraph;
import soot.Unit;

public abstract class AbstractPointOfAlias<V> implements PointOfAlias<V>{
  protected UpdatableAccessGraph d2;
  protected UpdatableWrapper<Unit> curr;
  protected UpdatableWrapper<Unit> succ;
  protected UpdatableAccessGraph d1;

  public AbstractPointOfAlias(UpdatableAccessGraph d1, UpdatableWrapper<Unit> stmt, UpdatableAccessGraph d2, UpdatableWrapper<Unit> succ) {
    this.d1 = d1;
    this.curr = stmt;
    this.d2 = d2;
    this.succ = succ;
  }


  public String toString() {
    return "<" + d1.toString() + ">-<" + curr.getContents().toString() + "," + d2 + ">";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((curr == null) ? 0 : curr.getContents().hashCode());
    result = prime * result + ((d1 == null) ? 0 : d1.hashCode());
    result = prime * result + ((d2 == null) ? 0 : d2.hashCode());
    result = prime * result + ((succ == null) ? 0 : succ.getContents().hashCode());
//    return result;
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
    @SuppressWarnings("rawtypes")
    AbstractPointOfAlias other = (AbstractPointOfAlias) obj;
    if (curr == null) {
      if (other.curr != null)
        return false;
    } else if (!curr.getContents().equals(other.curr.getContents()))
      return false;
    if (d1 == null) {
      if (other.d1 != null)
        return false;
    } else if (!d1.equals(other.d1))
      return false;
    if (d2 == null) {
      if (other.d2 != null)
        return false;
    } else if (!d2.equals(other.d2))
      return false;
    if (succ == null) {
      if (other.succ != null)
        return false;
    } else if (!succ.getContents().equals(other.succ.getContents()))
      return false;
    return true;
  };

}
