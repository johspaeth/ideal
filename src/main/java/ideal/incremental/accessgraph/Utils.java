package ideal.incremental.accessgraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import boomerang.accessgraph.AccessGraph;
import boomerang.accessgraph.WrappedSootField;
import boomerang.cfg.IExtendedICFG;
import heros.incremental.UpdatableWrapper;
import ideal.InternalAnalysisProblem;
import soot.SootMethod;
import soot.Unit;

public class Utils {
	
	public static UpdatableAccessGraph getUpdatableAccessGraph(AccessGraph ag, IExtendedICFG<Unit, SootMethod> icfg) {
//		IFieldGraph fieldGraph = ag.getFieldGraph();
//		UpdatableWrapper<Unit> sourceStmt = icfg.wrap(ag.getSourceStmt());
//		UpdatableWrapper<Local> base = icfg.wrap(ag.getBase());
		if(null == ag.getFieldGraph() && null == ag.getSourceStmt() && null == ag.getBase() && ag.toString().contains("{ZERO}"))
			return InternalAnalysisProblem.ZERO;
		if(null == ag.getFieldGraph() && null == ag.getSourceStmt())
			return new UpdatableAccessGraph(icfg.wrap(ag.getBase()));
		else if(null == ag.getFieldGraph() && null != ag.getSourceStmt())
			return new UpdatableAccessGraph(icfg.wrap(ag.getBase()), icfg.wrap(ag.getSourceStmt()), ag.hasAllocationSite());
		else if(null == ag.getSourceStmt() && null != ag.getFieldGraph())
			return new UpdatableAccessGraph(icfg.wrap(ag.getBase()), Utils.getUpdatableFieldGraph(ag.getFieldGraph().getFields(), icfg), null, ag.hasAllocationSite());
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
			if(wrappedSootField.getField() == null)
				updatableWrappedSootFields[i++] = new UpdatableWrappedSootField(null, icfg.wrap(wrappedSootField.getStmt()));
			else if(null == wrappedSootField.getStmt())
				updatableWrappedSootFields[i++] = new UpdatableWrappedSootField(icfg.wrap(wrappedSootField.getField()), null);
			else if(null == wrappedSootField || (wrappedSootField.getField() == null && wrappedSootField.getStmt() == null))
				updatableWrappedSootFields[i++] = new UpdatableWrappedSootField(null, null);
			else
				updatableWrappedSootFields[i++] = new UpdatableWrappedSootField(icfg.wrap(wrappedSootField.getField()), icfg.wrap(wrappedSootField.getStmt()));
		}
		return new UpdatableFieldGraph(updatableWrappedSootFields);
	}
	
	public static Collection<SootMethod> getSootMethods(Collection<UpdatableWrapper<SootMethod>> updatableMethods) {
		Collection<SootMethod> sootMethods = new HashSet<>(updatableMethods.size());
		for (UpdatableWrapper<SootMethod> updatableSootMethod : updatableMethods) {
			sootMethods.add(updatableSootMethod.getContents());
		}
		return sootMethods;
	}
	
	public static WrappedSootField[] getWrappedSootField(UpdatableWrappedSootField[] updatableFields) {
		WrappedSootField[] fields = new WrappedSootField[updatableFields.length];
		int i = 0;
		for (UpdatableWrappedSootField updatableWrappedSootField : updatableFields) {
			fields[i++] = updatableWrappedSootField.getWrappedSootField();
		}
		return fields;
	}
	
	public static Set<WrappedSootField> getWrappedSootField(Set<UpdatableWrappedSootField> updatableFields) {
		Set<WrappedSootField> fields = new HashSet<>();
		for (UpdatableWrappedSootField updatableWrappedSootField : updatableFields) {
			fields.add(updatableWrappedSootField.getWrappedSootField());
		}
		return fields;
	}
	
	public static List<WrappedSootField> getWrappedSootField(List<UpdatableWrappedSootField> updatableFields) {
		List<WrappedSootField> fields = new LinkedList<>();
		for (UpdatableWrappedSootField updatableWrappedSootField : updatableFields) {
			fields.add(updatableWrappedSootField.getWrappedSootField());
		}
		return fields;
	}
	
	public static List<Unit> getUnits(List<UpdatableWrapper<Unit>> wrappedUnits) {
		List<Unit> units = new ArrayList<>();
		for (UpdatableWrapper<Unit> wrappedUnit : wrappedUnits) {
			units.add(wrappedUnit.getContents());
		}
		return units;
	}
	
}
