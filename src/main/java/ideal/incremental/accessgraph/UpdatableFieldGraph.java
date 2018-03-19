package ideal.incremental.accessgraph;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import boomerang.accessgraph.FieldGraph;
import boomerang.accessgraph.WrappedSootField;
import heros.incremental.UpdatableWrapper;
import soot.SootField;

/**
 * A field graph represents only the of the access graph field accesses. It is a
 * directed graph. Two nodes of the graph are special, the entry and exit node.
 * One can also see the field graph as a Finite State Machine. The inital state
 * is the entry node and the accepting state is the target node. As the Grph
 * Library represents nodes within the graph as integers, we keep a mapping from
 * fields to integer.
 * 
 * @author spaeth
 *
 */
public class UpdatableFieldGraph implements UpdatableIFieldGraph {

	final LinkedList<UpdatableWrappedSootField> fields;
	static UpdatableFieldGraph EMPTY_GRAPH = new UpdatableFieldGraph() {
		public String toString() {
			return "EMPTY_GRAPH";
		};
	};
	

	public UpdatableFieldGraph(UpdatableWrappedSootField[] fields) {
		assert fields != null && fields.length > 0;
		this.fields = new LinkedList<>(Arrays.asList(fields));
	}

	public UpdatableFieldGraph(UpdatableWrappedSootField f) {
		assert f != null;
		this.fields = new LinkedList<>();
		this.fields.add(f);
	}

	private UpdatableFieldGraph(LinkedList<UpdatableWrappedSootField> fields) {
		this.fields = fields;
	}

	private UpdatableFieldGraph() {
		this.fields = new LinkedList<>();
	}
	
	/*public UpdatableFieldGraph(WrappedSootField[] fields) {
		assert fields != null && fields.length > 0;
		
		UpdatableWrappedSootField[] updatableFields = new UpdatableWrappedSootField[fields.length];
		int i = 0;
		for (WrappedSootField wrappedSootField : fields) {
			updatableFields[i++] = new UpdatableWrappedSootField(fields[i].getField(), fields[i].getStmt());
		}
		
		this.fields = new LinkedList<>(Arrays.asList(fields));
	}*/

	public FieldGraph getFieldGraph() {
		WrappedSootField[] fields = new WrappedSootField[this.fields.size()];
		int i = 0;
		for (UpdatableWrappedSootField updatableWrappedSootField : this.fields) {
			fields[i++] = updatableWrappedSootField.getWrappedSootField();
		}
		return fields.length > 0 ? new FieldGraph(fields) :  FieldGraph.EMPTY_GRAPH;
//		return new FieldGraph(fields);
	}

	/**
	 * 
	 * @return
	 */
	public Set<UpdatableIFieldGraph> popFirstField() {
		if (fields.isEmpty())
			return new HashSet<>();
		Set<UpdatableIFieldGraph> out = new HashSet<>();
		LinkedList<UpdatableWrappedSootField> newFields = new LinkedList<>(fields);
		newFields.removeFirst();
		if(newFields.isEmpty())
			out.add(UpdatableFieldGraph.EMPTY_GRAPH);
		else
			out.add(new UpdatableFieldGraph(newFields));
		return out;
	}

	public UpdatableWrappedSootField[] getFields() {
		return fields.toArray(new UpdatableWrappedSootField[] {});
	}

	public UpdatableIFieldGraph prependField(UpdatableWrappedSootField f) {
		LinkedList<UpdatableWrappedSootField> newFields = new LinkedList<>(fields);
		newFields.addFirst(f);
		return new UpdatableFieldGraph(newFields);
	}

	public Set<UpdatableIFieldGraph> popLastField() {
		Set<UpdatableIFieldGraph> out = new HashSet<>();
		if (fields.isEmpty())
			return out;
		LinkedList<UpdatableWrappedSootField> newFields = new LinkedList<>(fields);
		newFields.removeLast();
		if(newFields.isEmpty())
			out.add(UpdatableFieldGraph.EMPTY_GRAPH);
		else
			out.add(new UpdatableFieldGraph(newFields));
		return out;
	}

	public UpdatableIFieldGraph append(UpdatableIFieldGraph o) {
		if (o instanceof UpdatableSetBasedFieldGraph) {
			UpdatableSetBasedFieldGraph setBasedFieldGraph = (UpdatableSetBasedFieldGraph) o;
			return setBasedFieldGraph.append(this);
		} else if (o instanceof UpdatableFieldGraph) {
			UpdatableFieldGraph other = (UpdatableFieldGraph) o;
			LinkedList<UpdatableWrappedSootField> fields2 = other.fields;
			LinkedList<UpdatableWrappedSootField> newFields = new LinkedList<>(fields);
			newFields.addAll(fields2);
			return new UpdatableFieldGraph(newFields);
		}
		throw new RuntimeException("Not yet implemented!");
	}

	public UpdatableIFieldGraph appendFields(UpdatableWrappedSootField[] toAppend) {
		return append(new UpdatableFieldGraph(toAppend));
	}

	public Set<UpdatableWrappedSootField> getEntryNode() {
		Set<UpdatableWrappedSootField> out = new HashSet<>();
		out.add(fields.get(0));
		return out;
	}

	boolean hasLoops() {
		Set<UpdatableWrapper<SootField>> sootFields = new HashSet<>();
		for (UpdatableWrappedSootField f : this.fields) {
			if (sootFields.contains(f.getField()))
				return true;
			sootFields.add(f.getField());
		}
		return false;
	}

	public Collection<UpdatableWrappedSootField> getExitNode() {
		return Collections.singleton(fields.getLast());
	}

	public String toString() {
		String str = "";
		str += fields.toString();
		return str;
	}

	@Override
	public boolean shouldOverApproximate() {
		return hasLoops();
	}

	@Override
	public UpdatableIFieldGraph overapproximation() {
		return new UpdatableSetBasedFieldGraph(new HashSet<>(fields));
	}

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fields == null) ? 0 : fields.hashCode());
		return result;
//		return getFieldGraph().hashCode();
//		return 1;
	}

	@Override
	public boolean equals(Object obj) {
		/*if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UpdatableFieldGraph other = (UpdatableFieldGraph) obj;
		if (fields == null) {
			if (other.getFieldGraph().getFields() != null)
				return false;
		} else if (!this.getFieldGraph().getFields().equals(other.getFieldGraph().getFields()))
			return false;
		return true;*/
		return ((UpdatableFieldGraph) obj).getFieldGraph().equals(this.getFieldGraph());
	}


}
