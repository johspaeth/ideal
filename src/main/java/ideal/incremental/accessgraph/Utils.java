package ideal.incremental.accessgraph;

import java.util.Collection;
import java.util.HashSet;

import boomerang.accessgraph.AccessGraph;
import boomerang.accessgraph.WrappedSootField;
import boomerang.cfg.IExtendedICFG;
import soot.SootMethod;
import soot.Unit;

public class Utils {
	
	public static UpdatableAccessGraph getUpdatableAccessGraph(AccessGraph ag, IExtendedICFG<Unit, SootMethod> icfg) {
//		IFieldGraph fieldGraph = ag.getFieldGraph();
//		UpdatableWrapper<Unit> sourceStmt = icfg.wrap(ag.getSourceStmt());
//		UpdatableWrapper<Local> base = icfg.wrap(ag.getBase());
		/*if(null == ag.getFieldGraph() && null == ag.getSourceStmt() && null == ag.getBase())
			return InternalAnalysisProblem.ZERO;*/
		if(null == ag.getFieldGraph() && null == ag.getSourceStmt())
			return new UpdatableAccessGraph(icfg.wrap(ag.getBase()));
		else if(null == ag.getFieldGraph() && null != ag.getSourceStmt())
			return new UpdatableAccessGraph(icfg.wrap(ag.getBase()), icfg.wrap(ag.getSourceStmt()), ag.hasAllocationSite());
		else
			return new UpdatableAccessGraph(icfg.wrap(ag.getBase()), Utils.getUpdatableFieldGraph(ag.getFieldGraph().getFields(), icfg), icfg.wrap(ag.getSourceStmt()), ag.hasAllocationSite());
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
