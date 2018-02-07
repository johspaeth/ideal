package ideal.incremental.accessgraph;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import boomerang.accessgraph.AccessGraph;
import heros.incremental.UpdatableWrapper;
import ideal.InternalAnalysisProblem;
import soot.Local;
import soot.SootField;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.NewExpr;

/**
 * An AccessGraph is represented by a local variable and a {@link FieldGraph}
 * representing multiple field accesses.
 * 
 * @author spaeth
 *
 */
public class UpdatableAccessGraph {

	/**
	 * The local variable at which the field graph is rooted.
	 */
	private final UpdatableWrapper<Local> value;


	/**
	 * The {@link FieldGraph} representing the accesses which yield to the
	 * allocation site.
	 */
	private final UpdatableIFieldGraph fieldGraph;

	private int hashCode = 0;

	/**
	 * The allocation site to which this access graph points-to.
	 */
	private UpdatableWrapper<Unit> allocationSite;

	private boolean isNullAllocsite;

	public AccessGraph getAccessGraph() {
		if(null == fieldGraph && null == getSourceStmt() && null == value) {
			return InternalAnalysisProblem.ZERO1;
		}
		if(null == fieldGraph && null == getSourceStmt())
			return new AccessGraph(value.getContents());
		else if(null == fieldGraph && null != getSourceStmt())
			return new AccessGraph(value.getContents(), getSourceStmt().getContents(), hasAllocationSite());
		else if(getSourceStmt() == null)
			return new AccessGraph(value.getContents(), fieldGraph.getFieldGraph(), null, hasAllocationSite());
		else
			return new AccessGraph(value.getContents(), fieldGraph.getFieldGraph(), getSourceStmt().getContents(), hasAllocationSite());
	}
	
	/*public UpdatableAccessGraph(UpdatableWrapper<Unit> base, UpdatableIFieldGraph IFieldGraph, UpdatableWrapper<Unit> sourceStmt, boolean hasNullAllocationSite) {
		this(base, IFieldGraph, sourceStmt, hasNullAllocationSite);
	}*/
	
	/**
	 * Constructs an access graph with empty field graph, but specified base
	 * (local) variable.
	 * 
	 * @param val
	 *            The local to be the base of the access graph.
	 * @param t
	 *            The type of the base
	 */
	public UpdatableAccessGraph(UpdatableWrapper<Local> val) {
		this(val,  null, null, false);
	}

	public UpdatableAccessGraph(UpdatableWrapper<Local> val, UpdatableWrapper<Unit> allocsite,boolean isNullAllocsite) {
		this(val, null, allocsite, isNullAllocsite);
	}

	/**
	 * Constructs an access graph with base variable and field graph consisting
	 * of exactly one field (write) access.
	 * 
	 * @param val
	 *            the local base variable
	 * @param t
	 *            the type of the local variable
	 * @param field
	 *            the first field access
	 */
	public UpdatableAccessGraph(UpdatableWrapper<Local> val,  UpdatableWrappedSootField field) {
		this(val, new UpdatableFieldGraph(field), null,false);
	}

	/**
	 * Constructs an access graph with base variable and field graph consisting
	 * of the sequence of supplied field (write) accesses.
	 * 
	 * @param val
	 *            the local base variable
	 * @param t
	 *            the type of the local variable
	 * @param field
	 *            An array of field accesses
	 */
	public UpdatableAccessGraph(UpdatableWrapper<Local> val, UpdatableWrappedSootField[] f) {
		this(val, (f == null || f.length == 0 ? null : new UpdatableFieldGraph(f)), null, false);
	}

	public UpdatableAccessGraph(UpdatableWrapper<Local> value, UpdatableIFieldGraph fieldGraph, UpdatableWrapper<Unit> sourceStmt, boolean isNullAllocsite) {
		this.value = value;
		this.isNullAllocsite = isNullAllocsite;
//		if(apgs == null){
//			apgs = new LinkedList<IFieldGraph>();
//		}
		if(fieldGraph != null && fieldGraph.equals(UpdatableFieldGraph.EMPTY_GRAPH))
			fieldGraph = null;
//		int index = apgs.indexOf(fieldGraph);
//		if(index >= 0){
//			this.fieldGraph = apgs.get(index);
//		} else{
//			apgs.add(fieldGraph);
//			System.out.println("APG" + apgs.size());
//			System.out.println(fieldGraph);
			this.fieldGraph = fieldGraph;
//		}
		this.allocationSite = sourceStmt;
	}

	/**
	 * Get the base/local variable of the access graph.
	 * 
	 * @return The local variable at which the graph is rooted.
	 */
	public UpdatableWrapper<Local> getBase() {
		return value;
	}

	/**
	 * If the access graph is not null, the first field access is returned.
	 * 
	 * @return The first field of the access graph (might return
	 *         <code>null</code>)
	 */
	public Collection<UpdatableWrappedSootField> getFirstField() {
		if (fieldGraph == null)
			return Collections.emptySet();
		return fieldGraph.getEntryNode();
	}

	/**
	 * Checks if the first field of the access graph matches the given field.
	 * 
	 * @param field
	 *            The field to check against
	 * @return {@link Boolean} whether the field matches or not.
	 */
	public boolean firstFieldMustMatch(SootField field) {
		if (fieldGraph == null)
			return false;
		if(fieldGraph instanceof UpdatableSetBasedFieldGraph)
			return false;
		for(UpdatableWrappedSootField f: getFirstField())
			return f.getField().equals(field);
		throw new RuntimeException("Unreachable Code");
	}
	
	public boolean firstFirstFieldMayMatch(SootField field) {
		for(UpdatableWrappedSootField f: getFirstField())
			if(f.getField().equals(field))
				return true;
		return false;
	}
	/**
	 * Returns the number of field accesses (in cases where the field graph has
	 * loops, the shortest version is picked.)
	 * 
	 * @return The length of the shortest sequence of field accesses.
	 */
	public int getFieldCount() {
		return (fieldGraph == null ? 0 : (getRepresentative() == null ? 0 : getRepresentative().length));
	}

	/**
	 * One representative of the accesses described by this access graph. In
	 * cases of loops, it will only pick the shortest sequence.
	 * 
	 * @return An array of SootField paired with the statement from which they
	 *         originate (the field write statements)
	 */
	public UpdatableWrappedSootField[] getRepresentative() {
		if (fieldGraph == null)
			return null;

		return fieldGraph.getFields();
	}

	@Override
	public String toString() {
		String str = "";
		if (value != null){
			str += value.toString() ;
		}
		if (fieldGraph != null) {
			
			 str += fieldGraph.toString();
		}
		if (allocationSite != null) {
			str += " at " +allocationSite.toString();
		}
		return str;
	}

	/**
	 * Keeps the accesses as they are, and changes the local variable with the
	 * given type.
	 * 
	 * @param local
	 *            The local variable of the returned access graph
	 * @param type
	 *            The new type to be used.
	 * @return The access graph
	 */
	public UpdatableAccessGraph deriveWithNewLocal(UpdatableWrapper<Local> local) {
		return new UpdatableAccessGraph(local, fieldGraph, allocationSite,isNullAllocsite);
	}

	/**
	 * Appends a sequence of SootFields (wrapped inside {@link UpdatableWrappedSootField}
	 * ) to the current access graph.
	 * 
	 * @param toAppend
	 *            Sequence of fields to append.
	 * @return the access graph derived with the appended fields.
	 */
	public UpdatableAccessGraph appendFields(UpdatableWrappedSootField[] toAppend) {
		UpdatableIFieldGraph newapg = (fieldGraph != null ? fieldGraph.appendFields(toAppend) : new UpdatableFieldGraph(toAppend));
		if(newapg.shouldOverApproximate()){
			newapg = newapg.overapproximation();
		}
		return new UpdatableAccessGraph(value,  newapg, allocationSite,isNullAllocsite);
	}

	/**
	 * Appends a complete field graph to the current access graph.
	 * 
	 * @param toAppend
	 *            The field graph to append
	 * @return the access graph derived with the appended fields.
	 */
	public UpdatableAccessGraph appendGraph(UpdatableIFieldGraph graph) {
		if (graph == null)
			return this;
		UpdatableIFieldGraph newapg = (fieldGraph != null ? fieldGraph.append(graph) : graph);
		if(newapg.shouldOverApproximate()){
			newapg = newapg.overapproximation();
		}
		return new UpdatableAccessGraph(value,  newapg, allocationSite,isNullAllocsite);
	}
	
	/**
	 * Add the provided field to the beginning of the field graph. This is
	 * typically called at field write statements.
	 * 
	 * @param f
	 *            The field to prepend
	 * @return A copy of the current access graph with the field appended
	 */
	public UpdatableAccessGraph prependField(UpdatableWrappedSootField f) {
		UpdatableIFieldGraph newapg = (fieldGraph != null ? fieldGraph.prependField(f) : new UpdatableFieldGraph(f));
		if(newapg.shouldOverApproximate()){
			newapg = newapg.overapproximation();
		}
		return new UpdatableAccessGraph(value, newapg, allocationSite,isNullAllocsite);
	}

	/**
	 * Checks if the base of this access graph matches the local given as
	 * argument.
	 * 
	 * @param local
	 *            The value to check against.
	 * @return {@link Boolean} depending if the base matches the argument.
	 */
	public boolean baseMatches(Value local) {
		assert local != null;
		return value != null ? value.getContents() == local : false;
//		return value.getContents() == local;
	}

	/**
	 * Checks if the base variable and the first field matches the given
	 * argument.
	 * 
	 * @param local
	 *            The base variable to check against.
	 * @param field
	 *            The first field to check.
	 * @return {@link Boolean} depending if it matches.
	 */
	public boolean baseAndFirstFieldMatches(Value local, SootField field) {
		if (!baseMatches(local)) {
			return false;
		}
		return firstFieldMustMatch(field);
	}

	/**
	 * Removes the first field from this access graph. (Typically invoked at
	 * field read statements.) As the the first field access might have multiple
	 * successors, the output of this method is a set, and not a single access
	 * graph.
	 * 
	 * @return A set of access graph which are derived by the removal of the
	 *         first field of the current graph.
	 */
	public Set<UpdatableAccessGraph> popFirstField() {
		if (fieldGraph == null)
			throw new RuntimeException("Try to remove the first field from an access graph which has no field" + this);

		Set<UpdatableIFieldGraph> newapg = fieldGraph.popFirstField();
		if (newapg.isEmpty())
			return Collections.singleton(new UpdatableAccessGraph(value, null, allocationSite,isNullAllocsite));
		Set<UpdatableAccessGraph> out = new HashSet<>();
		for (UpdatableIFieldGraph a : newapg) {
				out.add(new UpdatableAccessGraph(value, a, allocationSite,isNullAllocsite));
		}
		return out;
	}

	/**
	 * Similar to {@link #popFirstField()} but instead removes the last field.
	 * As the last field might have multiple predecessors, a set of access graph
	 * is returned.
	 * 
	 * @return Set of graphs without the last access.
	 */
	public Set<UpdatableAccessGraph> popLastField() {
		if (fieldGraph == null)
			throw new RuntimeException("Try to remove the first field from an access graph which has no field" + this);

		Set<UpdatableIFieldGraph> newapg = fieldGraph.popLastField();

		Set<UpdatableAccessGraph> out = new HashSet<>();
		if (newapg.isEmpty())
			return Collections.singleton(new UpdatableAccessGraph(value, null, allocationSite,isNullAllocsite));
		for (UpdatableIFieldGraph a : newapg) {
			out.add(new UpdatableAccessGraph(value,  a, allocationSite,isNullAllocsite));
		}
		return out;
	}

	/**
	 * Returns the allocation site of this access graph. Might be null.
	 * 
	 * @return The allocation site, this access graph points to. Can be null, if
	 *         the base variable is a parameter of the current method.
	 */
	public UpdatableWrapper<Unit> getSourceStmt() {
		return allocationSite;
	}

	/**
	 * Derives an access graph with the given statement as allocation site to
	 * that access graph.
	 * 
	 * @param stmt
	 *            The statement, typically the allocation site.
	 * @return The derived access graph
	 */
	public UpdatableAccessGraph deriveWithAllocationSite(UpdatableWrapper<Unit> stmt, boolean isNullAllocsite) {
		return new UpdatableAccessGraph(value, fieldGraph, stmt, isNullAllocsite);
	}

	/**
	 * Check whether this access graph points-to an allocation site.
	 * 
	 * @return <code>true</code> if it has an allocation site associated.
	 */
	public boolean hasAllocationSite() {
		return allocationSite != null;
	}

	/**
	 * Sets the allocation to null. This is called whenever a flow enters a call
	 * with an allocation site. Now the allocation site is removed, as it does
	 * not hold within the method. In that way we receive more reusable
	 * summaries.
	 * 
	 * @return The derived access graph
	 */
	public UpdatableAccessGraph deriveWithoutAllocationSite() {
		return new UpdatableAccessGraph(value, fieldGraph, null, false);
	}

	/**
	 * Removes the complete field graph from the access graph.
	 * 
	 * @return The derived access graph
	 */
	public UpdatableAccessGraph dropTail() {
		return new UpdatableAccessGraph(value, null, allocationSite,isNullAllocsite);
	}

	/**
	 * Derives a static access graph. A static access graph has
	 * <code>null</code> as local variable. The first field automatically
	 * determines the base class of the static field of the access graph.
	 * 
	 * @return The derived access graph.
	 */
	public UpdatableAccessGraph makeStatic() {
		return new UpdatableAccessGraph(null, fieldGraph, allocationSite,isNullAllocsite);
	}

	/**
	 * Checks if this access graph represents a static field.
	 * 
	 * @return <code>true</code> if it is static.
	 */
	public boolean isStatic() {
		return value == null && (getFieldCount() > 0);
	}

	/**
	 * Retrieve the last field access of the graph.
	 * 
	 * @return Last field, might be null.
	 */
	public Collection<UpdatableWrappedSootField> getLastField() {
		if (fieldGraph == null)
			return null;

		return fieldGraph.getExitNode();
	}

	/**
	 * Retrieve a clone of the field graph of that current access graph.
	 * 
	 * @return The field graph.
	 */
	public UpdatableIFieldGraph getFieldGraph() {
		return fieldGraph;
	}
	

	@Override
	public int hashCode() {
//		if (hashCode != 0)
//			return hashCode;

		/*final int prime = 31;
		int result = 1;
		result = prime * result + ((fieldGraph == null) ? 0 : fieldGraph.getFieldGraph().hashCode());
		result = prime * result + ((value == null) ? 0 : value.getContents().hashCode());
		result = prime * result + ((allocationSite == null) ? 0 : allocationSite.getContents().hashCode());
		this.hashCode = result;

		return this.hashCode;*/
//		return this.getAccessGraph().hashCode();
		
		return 1;
	}

	@Override
	public boolean equals(Object obj) {
		/*if (obj == this || super.equals(obj))
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;

		UpdatableAccessGraph other = (UpdatableAccessGraph) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.getContents().equals(other.value.getContents()))
			return false;
		if (allocationSite == null) {
			if (other.allocationSite != null)
				return false;
		} else if (!allocationSite.getContents().equals(other.allocationSite.getContents()))
			return false;
		if (fieldGraph == null) {
			if (other.fieldGraph != null)
				return false;
		} else if (!fieldGraph.getFieldGraph().equals(other.fieldGraph.getFieldGraph()))
			return false;
		assert this.hashCode() == obj.hashCode();
		return true;*/
		return ((UpdatableAccessGraph) obj).getAccessGraph().equals(this.getAccessGraph());
	}

	public boolean hasSetBasedFieldGraph() {
		return fieldGraph instanceof UpdatableSetBasedFieldGraph;
	}

	public UpdatableAccessGraph overApproximate() {
		return new UpdatableAccessGraph(value,fieldGraph == null ? null : fieldGraph.overapproximation(), allocationSite,isNullAllocsite);
	}


	public boolean hasNullAllocationSite() {
		return isNullAllocsite;
	}
	

	public Type getAllocationType() {
		if(!hasAllocationSite())
			throw new RuntimeException("Wrong state");
		if(allocationSite instanceof AssignStmt){
			AssignStmt as = (AssignStmt) allocationSite;
			Value rightOp = as.getRightOp();
			if(rightOp instanceof NewExpr){
				NewExpr newExpr = (NewExpr) rightOp;
				return newExpr.getBaseType();
			}
			Value leftOp = as.getLeftOp();
			return leftOp.getType();
		}
		throw new RuntimeException("Allocation site not an Assign Stmt" + allocationSite);
	}
}
