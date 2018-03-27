package ideal.incremental.accessgraph;

import boomerang.accessgraph.WrappedSootField;
import heros.incremental.UpdatableWrapper;

import soot.SootField;
import soot.Unit;

/**
 * A wrapped SootField. It can have a more precise type information than the original SootField.
 * 
 * @author spaeth
 *
 */
public class UpdatableWrappedSootField {
  private UpdatableWrapper<SootField> field;
  private UpdatableWrapper<Unit> stmt;
  public static boolean TRACK_STMT = true;

  public UpdatableWrappedSootField(UpdatableWrapper<SootField> f, UpdatableWrapper<Unit> s) {
    this.field = f;
    this.stmt = (TRACK_STMT ? s : null);
  }

public UpdatableWrapper<SootField> getField() {
    return field;
  }
  
  public WrappedSootField getWrappedSootField() {
	  /*if(stmt != null)
		  return new WrappedSootField(this.field.getContents(), this.stmt.getContents());
	  else
		  return new WrappedSootField(this.field.getContents(), null);*/
	  return new WrappedSootField(field.getContents(), (TRACK_STMT ? stmt.getContents() : null));
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((field == null) ? 0 : field.getContents().hashCode());
    result = prime * result + ((stmt == null) ? 0 : stmt.getContents().hashCode());
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
    UpdatableWrappedSootField other = (UpdatableWrappedSootField) obj;
    if (field == null) {
      if (other.field != null)
        return false;
    } else if (!field.getContents().equals(other.field.getContents()))
      return false;
    if (stmt == null) {
      if (other.stmt != null)
        return false;
    } else if (!stmt.getContents().equals(other.stmt.getContents()))
      return false;
    return true;
  }

  public String toString() {
    return field.getContents().getName().toString();
  }

}
