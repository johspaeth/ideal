package typestate.finiteautomata;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import boomerang.BoomerangContext;
import heros.incremental.UpdatableWrapper;
import ideal.incremental.accessgraph.UpdatableAccessGraph;
import soot.Local;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.NewExpr;
import soot.jimple.Stmt;
import typestate.TypestateChangeFunction;
import typestate.finiteautomata.MatcherTransition.Parameter;
import typestate.finiteautomata.MatcherTransition.Type;

public abstract class MatcherStateMachine<State> implements TypestateChangeFunction<State> {
	public Set<MatcherTransition<State>> transition = new HashSet<>();

	public void addTransition(MatcherTransition<State> trans) {
		transition.add(trans);
	}

	public Set<Transition<State>> getReturnTransitionsFor(UpdatableAccessGraph callerD1, UpdatableWrapper<Unit> callSite, UpdatableWrapper<SootMethod> calleeMethod,
			UpdatableWrapper<Unit> exitStmt, UpdatableAccessGraph exitNode, UpdatableWrapper<Unit> returnSite, UpdatableAccessGraph retNode) {
		return getMatchingTransitions(calleeMethod, exitNode, Type.OnReturn);
	}

	public Set<Transition<State>> getCallTransitionsFor(UpdatableAccessGraph callerD1, UpdatableWrapper<Unit> callSite, UpdatableWrapper<SootMethod> callee,
			UpdatableAccessGraph srcNode, UpdatableAccessGraph destNode) {
		return getMatchingTransitions(callee, destNode, Type.OnCall);
	}

	public Set<Transition<State>> getCallToReturnTransitionsFor(UpdatableAccessGraph d1, UpdatableWrapper<Unit> callSite, UpdatableAccessGraph d2,
			UpdatableWrapper<Unit> returnSite, UpdatableAccessGraph d3) {
		Set<Transition<State>> res = new HashSet<>();
		if(callSite.getContents() instanceof Stmt){
			Stmt stmt = (Stmt) callSite.getContents();
			if(stmt.containsInvokeExpr() && stmt.getInvokeExpr() instanceof InstanceInvokeExpr){
				SootMethod method = stmt.getInvokeExpr().getMethod();
				InstanceInvokeExpr e = (InstanceInvokeExpr)stmt.getInvokeExpr();
				if(e.getBase().equals(d2.getBase().getContents())){
					for (MatcherTransition<State> trans : transition) {
						if(trans.matches(method) && trans.getType().equals(Type.OnCallToReturn)){
							res.add(trans);
						}
					}	
				}
			}
		}
		return res;
	}

	private Set<Transition<State>> getMatchingTransitions(UpdatableWrapper<SootMethod> method, UpdatableAccessGraph node, Type type) {
		Set<Transition<State>> res = new HashSet<>();
		if (node.getFieldCount() == 0) {
			for (MatcherTransition<State> trans : transition) {
				if (trans.matches(method.getContents()) && trans.getType().equals(type)) {
					Parameter param = trans.getParam();
					if (param.equals(Parameter.This) && BoomerangContext.isThisValue(method.getContents(), node.getBase().getContents()))
						res.add(new Transition<State>(trans.from(), trans.to()));
					if (param.equals(Parameter.Param1)
							&& method.getContents().getActiveBody().getParameterLocal(0).equals(node.getBase()))
						res.add(new Transition<State>(trans.from(), trans.to()));
					if (param.equals(Parameter.Param2)
							&& method.getContents().getActiveBody().getParameterLocal(1).equals(node.getBase()))
						res.add(new Transition<State>(trans.from(), trans.to()));
				}
			}
		}

		return res;
	}

	protected Set<SootMethod> selectMethodByName(Collection<SootClass> classes, String pattern) {
		Set<SootMethod> res = new HashSet<>();
		for (SootClass c : classes) {
			for (SootMethod m : c.getMethods()) {
				if (Pattern.matches(pattern, m.getName()))
					res.add(m);
			}
		}
		return res;
	}

	protected List<SootClass> getSubclassesOf(String className) {
		SootClass sootClass = Scene.v().getSootClass(className);
		List<SootClass> list = Scene.v().getActiveHierarchy().getSubclassesOfIncluding(sootClass);
		List<SootClass> res = new LinkedList<>();
		for (SootClass c : list) {
			res.add(c);
		}
		return res;
	}

	protected Collection<UpdatableAccessGraph> generateAtConstructor(UpdatableWrapper<Unit> unit,
			Collection<UpdatableWrapper<SootMethod>> calledMethod, MatcherTransition<State> initialTrans) {
		boolean matches = false;
		for (UpdatableWrapper<SootMethod> method : calledMethod) {
			if (initialTrans.matches(method.getContents())) {
				matches = true;
			}
		}
		if (!matches)
			return Collections.emptySet();
		if (unit.getContents() instanceof Stmt) {
			Stmt stmt = (Stmt) unit.getContents();
			if (stmt.containsInvokeExpr())
				if (stmt.getInvokeExpr() instanceof InstanceInvokeExpr) {
					InstanceInvokeExpr iie = (InstanceInvokeExpr) stmt.getInvokeExpr();
					if (iie.getBase() instanceof Local) {
						Local l = (Local) iie.getBase();
						Set<UpdatableAccessGraph> out = new HashSet<>();
						out.add(new UpdatableAccessGraph(l));
						return out;
					}
				}
		}
		return Collections.emptySet();
	}

	protected Collection<UpdatableAccessGraph> getLeftSideOf(UpdatableWrapper<Unit> unit) {
		if (unit.getContents() instanceof AssignStmt) {
			Set<UpdatableAccessGraph> out = new HashSet<>();
			AssignStmt stmt = (AssignStmt) unit.getContents();
			out.add(
					new UpdatableAccessGraph((Local) stmt.getLeftOp()));
			return out;
		}
		return Collections.emptySet();
	}
	
	protected Collection<UpdatableAccessGraph> generateThisAtAnyCallSitesOf(UpdatableWrapper<Unit> unit,
			Collection<UpdatableWrapper<SootMethod>> calledMethod, Set<SootMethod> set) {
		for (UpdatableWrapper<SootMethod> callee : calledMethod) {
			if (set.contains(callee.getContents())) {
				if (unit.getContents() instanceof Stmt) {
					if (((Stmt) unit.getContents()).getInvokeExpr() instanceof InstanceInvokeExpr) {
						InstanceInvokeExpr iie = (InstanceInvokeExpr) ((Stmt) unit.getContents()).getInvokeExpr();
						Local thisLocal = (Local) iie.getBase();
						Set<UpdatableAccessGraph> out = new HashSet<>();
						out.add(new UpdatableAccessGraph(thisLocal));
						return out;
						
					}
				}

			}
		}
		return Collections.emptySet();
	}
	

	protected Collection<UpdatableAccessGraph> generateAtAllocationSiteOf(UpdatableWrapper<Unit> unit, Class allocationSuperType) {
		if(unit.getContents() instanceof AssignStmt){
			AssignStmt assignStmt = (AssignStmt) unit.getContents();
			if(assignStmt.getRightOp() instanceof NewExpr){
				NewExpr newExpr = (NewExpr) assignStmt.getRightOp();
				Value leftOp = assignStmt.getLeftOp();
				soot.Type type = newExpr.getType();
				if(Scene.v().getOrMakeFastHierarchy().canStoreType(type, Scene.v().getType(allocationSuperType.getName()))){
					return Collections.singleton(new UpdatableAccessGraph((Local)leftOp));
				}
			}
		}
		return Collections.emptySet();
	}
	@Override
	public Collection<UpdatableAccessGraph> generate(UpdatableWrapper<SootMethod> method, UpdatableWrapper<Unit> stmt,
			Collection<UpdatableWrapper<SootMethod>> calledMethods) {
		return generateSeed(method, stmt, calledMethods);
	}
	
	public abstract Collection<UpdatableAccessGraph> generateSeed(UpdatableWrapper<SootMethod> method, UpdatableWrapper<Unit> stmt,
			Collection<UpdatableWrapper<SootMethod>> calledMethods);
}
	
