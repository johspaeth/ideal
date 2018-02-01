package ideal.incremental.accessgraph;

import java.util.Collection;
import java.util.HashSet;

import boomerang.accessgraph.AccessGraph;
import boomerang.accessgraph.IFieldGraph;
import boomerang.accessgraph.WrappedSootField;
import boomerang.cfg.IExtendedICFG;
import soot.Local;
import soot.SootMethod;
import soot.Unit;

public class Utils {
	
	public static UpdatableAccessGraph getUpdatableAccessGraph(AccessGraph ag, IExtendedICFG<Unit, SootMethod> icfg) {
		IFieldGraph fieldGraph = ag.getFieldGraph();
		Unit sourceStmt = ag.getSourceStmt();
		Local base = ag.getBase();
		if(null == fieldGraph && null == sourceStmt)
			return new UpdatableAccessGraph(base);
		else if(null == fieldGraph && null != sourceStmt)
			return new UpdatableAccessGraph(base, icfg.wrap(sourceStmt), ag.hasNullAllocationSite());
		else
			return new UpdatableAccessGraph(base, Utils.getUpdatableFieldGraph(fieldGraph.getFields(), icfg), icfg.wrap(sourceStmt), ag.hasNullAllocationSite());
	}
	
	public static Collection<UpdatableAccessGraph> getUpdatableAccessGraph(Collection<AccessGraph> accessGraphs, IExtendedICFG<Unit, SootMethod> icfg) {
		Collection<UpdatableAccessGraph> updatableAccessGraphs = new HashSet<>(accessGraphs.size());
		for (AccessGraph accessGraph : accessGraphs) {
			updatableAccessGraphs.add(Utils.getUpdatableAccessGraph(accessGraph, icfg));
		}
		return updatableAccessGraphs;
	}
	
	private static UpdatableFieldGraph getUpdatableFieldGraph(WrappedSootField[] wrappedSootFields, IExtendedICFG<Unit, SootMethod> icfg) {
		UpdatableWrappedSootField updatableWrappedSootFields[] = new UpdatableWrappedSootField[wrappedSootFields.length];
		int i = 0;
		for (WrappedSootField wrappedSootField : wrappedSootFields) {
			updatableWrappedSootFields[i++] = new UpdatableWrappedSootField(wrappedSootField.getField(), icfg.wrap(wrappedSootField.getStmt()));
		}
		return new UpdatableFieldGraph(updatableWrappedSootFields);
	}
}
