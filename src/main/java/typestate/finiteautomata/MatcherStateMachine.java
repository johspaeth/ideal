package typestate.finiteautomata;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import boomerang.BoomerangContext;
import boomerang.cfg.IExtendedICFG;
import heros.incremental.UpdatableWrapper;
import ideal.incremental.accessgraph.UpdatableAccessGraph;
import ideal.incremental.accessgraph.Utils;
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
							&& method.getContents().getActiveBody().getParameterLocal(0).equals(node.getBase().getContents()))
						res.add(new Transition<State>(trans.from(), trans.to()));
					if (param.equals(Parameter.Param2)
							&& method.getContents().getActiveBody().getParameterLocal(1).equals(node.getBase().getContents()))
						res.add(new Transition<State>(trans.from(), trans.to()));
				}
			}
		}

		return res;
	}

	protected Set<UpdatableWrapper<SootMethod>> selectMethodByName(Collection<UpdatableWrapper<SootClass>> collection, String pattern, IExtendedICFG<Unit, SootMethod> icfg) {
		Set<SootMethod> res = new HashSet<>();
		for (UpdatableWrapper<SootClass> c : collection) {
			for (SootMethod m : c.getContents().getMethods()) {
				if (Pattern.matches(pattern, m.getName()))
					res.add(m);
			}
		}
		return icfg.wrap(res);
	}

	protected Collection<UpdatableWrapper<SootClass>> getSubclassesOf(String className, IExtendedICFG<Unit, SootMethod> icfg) {
		SootClass sootClass = Scene.v().getSootClass(className);
		List<SootClass> list = Scene.v().getActiveHierarchy().getSubclassesOfIncluding(sootClass);
		List<SootClass> res = new LinkedList<>();
		for (SootClass c : list) {
			res.add(c);
		}
		return icfg.wrap(res);
	}

	protected Collection<UpdatableAccessGraph> generateAtConstructor(UpdatableWrapper<Unit> unit,
			Collection<UpdatableWrapper<SootMethod>> calledMethod, MatcherTransition<State> initialTrans, IExtendedICFG<Unit, SootMethod> icfg) {
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
						out.add(new UpdatableAccessGraph(icfg.wrap(l)));
						return out;
					}
				}
		}
		return Collections.emptySet();
	}

	protected Collection<UpdatableAccessGraph> getLeftSideOf(UpdatableWrapper<Unit> unit, IExtendedICFG<Unit, SootMethod> icfg) {
		if (unit.getContents() instanceof AssignStmt) {
			Set<UpdatableAccessGraph> out = new HashSet<>();
			AssignStmt stmt = (AssignStmt) unit.getContents();
			out.add(
					new UpdatableAccessGraph(icfg.wrap((Local) stmt.getLeftOp())));
			return out;
		}
		return Collections.emptySet();
	}
	
	protected Collection<UpdatableAccessGraph> generateThisAtAnyCallSitesOf(UpdatableWrapper<Unit> unit,
			Collection<UpdatableWrapper<SootMethod>> calledMethod, Set<UpdatableWrapper<SootMethod>> set, IExtendedICFG<Unit, SootMethod> icfg) {
		for (UpdatableWrapper<SootMethod> callee : calledMethod) {
			if (Utils.getSootMethods(set).contains(callee.getContents())) {
				if (unit.getContents() instanceof Stmt) {
					if (((Stmt) unit.getContents()).getInvokeExpr() instanceof InstanceInvokeExpr) {
						InstanceInvokeExpr iie = (InstanceInvokeExpr) ((Stmt) unit.getContents()).getInvokeExpr();
						Local thisLocal = (Local) iie.getBase();
						Set<UpdatableAccessGraph> out = new HashSet<>();
						out.add(new UpdatableAccessGraph(icfg.wrap(thisLocal)));
						return out;
						
					}
				}

			}
		}
		return Collections.emptySet();
	}
	

	protected Collection<UpdatableAccessGraph> generateAtAllocationSiteOf(UpdatableWrapper<Unit> unit, Class allocationSuperType, IExtendedICFG<Unit, SootMethod> icfg) {
		if(unit.getContents() instanceof AssignStmt){
			AssignStmt assignStmt = (AssignStmt) unit.getContents();
			if(assignStmt.getRightOp() instanceof NewExpr){
				NewExpr newExpr = (NewExpr) assignStmt.getRightOp();
				Value leftOp = assignStmt.getLeftOp();
				soot.Type type = newExpr.getType();
				if(Scene.v().getOrMakeFastHierarchy().canStoreType(type, Scene.v().getType(allocationSuperType.getName()))){
					return Collections.singleton(new UpdatableAccessGraph(icfg.wrap((Local)leftOp)));
				}
			}
		}
		return Collections.emptySet();
	}
	@Override
	public Collection<UpdatableAccessGraph> generate(UpdatableWrapper<SootMethod> method, UpdatableWrapper<Unit> stmt,
			Collection<UpdatableWrapper<SootMethod>> calledMethods, IExtendedICFG<Unit, SootMethod> icfg) {
		return generateSeed(method, stmt, calledMethods, icfg);
	}
	
	public abstract Collection<UpdatableAccessGraph> generateSeed(UpdatableWrapper<SootMethod> method, UpdatableWrapper<Unit> stmt,
			Collection<UpdatableWrapper<SootMethod>> calledMethods, IExtendedICFG<Unit, SootMethod> icfg);
	
	public abstract void updateTypeStateFunctions();
	
	/*public void updateTypeStateFunctions() {
		this.transition = new HashSet<>();
		this.updateTransitionFunctions();
	}*/
}
	
