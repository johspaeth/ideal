package ideal.incremental.accessgraph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import com.google.common.base.Joiner;

import soot.Scene;
import soot.Type;

public class UpdatableSetBasedFieldGraph implements UpdatableIFieldGraph {

	private Set<UpdatableWrappedSootField> fields;
	public static Set<UpdatableWrappedSootField> allFields;

	public UpdatableSetBasedFieldGraph(Set<UpdatableWrappedSootField> fields) {
		this(fields, true);
	}

	public UpdatableSetBasedFieldGraph(Set<UpdatableWrappedSootField> fields, boolean type) {
		if (!type) {
			this.fields = new HashSet<>();
			for (UpdatableWrappedSootField f : fields) {
				this.fields.add(new UpdatableWrappedSootField(f.getField(), null));
			}
		} else {
			if (allFields == null)
				allFields = new HashSet<>();
			allFields.addAll(fields);
				this.fields =new HashSet<>(fields);// minimize(fields);
		}
		// assert fields.size() > 1;
	}


	private Type superType(Type a, Type b) {
		if (a.equals(b))
			return a;
		if (Scene.v().getOrMakeFastHierarchy().canStoreType(a, b)) {
			return b;
		} else if (Scene.v().getOrMakeFastHierarchy().canStoreType(b, a)) {
			return a;
		}
		return a;
		// throw new RuntimeException("Type mismatch?" + a +" and " + b);
	}
	@Override
	public Set<UpdatableIFieldGraph> popFirstField() {
		Set<UpdatableIFieldGraph> out = new HashSet<>();
		out.add(this);
		out.add(UpdatableFieldGraph.EMPTY_GRAPH);
		return out;
	}

	@Override
	public Set<UpdatableIFieldGraph> popLastField() {
		return popFirstField();
	}

	@Override
	public Collection<UpdatableWrappedSootField> getEntryNode() {
		return fields;
	}

	@Override
	public UpdatableWrappedSootField[] getFields() {
		return new UpdatableWrappedSootField[0];
	}

	@Override
	public UpdatableIFieldGraph appendFields(UpdatableWrappedSootField[] toAppend) {
		Set<UpdatableWrappedSootField> overapprox = new HashSet<>(fields);
		for (UpdatableWrappedSootField f : toAppend)
			overapprox.add(f);
		return new UpdatableSetBasedFieldGraph(overapprox);
	}

	@Override
	public UpdatableIFieldGraph append(UpdatableIFieldGraph graph) {
		return appendFields(graph.getFields());
	}

	@Override
	public UpdatableIFieldGraph prependField(UpdatableWrappedSootField f) {
		Set<UpdatableWrappedSootField> overapprox = new HashSet<>(fields);
		overapprox.add(f);
		return new UpdatableSetBasedFieldGraph(overapprox);
	}

	@Override
	public Collection<UpdatableWrappedSootField> getExitNode() {
		return fields;
	}

	@Override
	public boolean shouldOverApproximate() {
		return false;
	}

	@Override
	public UpdatableIFieldGraph overapproximation() {
		return this;
		// throw new RuntimeException("Cannot overapproximate the approxmiation
		// anymore");
	}

	public String toString() {
		return " {" + Joiner.on(",").join(fields) + "}";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fields == null) ? 0 : fields.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UpdatableSetBasedFieldGraph other = (UpdatableSetBasedFieldGraph) obj;
		if (fields == null) {
			if (other.fields != null)
				return false;
		} else if (fields.size() != other.fields.size() || !fields.equals(other.fields))
			return false;
		return true;
	}

}
