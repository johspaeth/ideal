package ideal.incremental.accessgraph;

import java.util.Collection;
import java.util.Set;

public interface UpdatableIFieldGraph {
	Set<UpdatableIFieldGraph> popFirstField();
	Set<UpdatableIFieldGraph> popLastField();
	Collection<UpdatableWrappedSootField> getEntryNode();
	UpdatableWrappedSootField[] getFields();
	UpdatableIFieldGraph appendFields(UpdatableWrappedSootField[] toAppend);
	UpdatableIFieldGraph append(UpdatableIFieldGraph graph);
	UpdatableIFieldGraph prependField(UpdatableWrappedSootField f);
	Collection<UpdatableWrappedSootField> getExitNode();
	boolean shouldOverApproximate();
	UpdatableIFieldGraph overapproximation();
}
