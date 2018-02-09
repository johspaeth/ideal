package typestate.impl.statemachines;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import boomerang.cfg.IExtendedICFG;
import heros.incremental.UpdatableWrapper;
import ideal.incremental.accessgraph.UpdatableAccessGraph;
import soot.Local;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.AssignStmt;
import test.ConcreteState;
import typestate.TypestateChangeFunction;
import typestate.TypestateDomainValue;
import typestate.finiteautomata.MatcherStateMachine;
import typestate.finiteautomata.MatcherTransition;
import typestate.finiteautomata.MatcherTransition.Parameter;
import typestate.finiteautomata.MatcherTransition.Type;
import typestate.finiteautomata.Transition;

public class HasNextStateMachine extends MatcherStateMachine<ConcreteState>  implements TypestateChangeFunction<ConcreteState> {

	private Set<UpdatableWrapper<SootMethod>> hasNextMethods;
	IExtendedICFG<Unit, SootMethod> icfg;

	public static enum States implements ConcreteState {
		NONE, INIT, HASNEXT, ERROR;

		@Override
		public boolean isErrorState() {
			return this == ERROR;
		}

	}

	public HasNextStateMachine(IExtendedICFG<Unit, SootMethod> icfg) {
		this.icfg = icfg;
		addTransition(new MatcherTransition<ConcreteState>(States.NONE, retrieveIteratorConstructors(icfg), Parameter.This, States.INIT,
				Type.None, icfg));
		addTransition(
				new MatcherTransition<ConcreteState>(States.INIT, retrieveNextMethods(icfg), Parameter.This, States.ERROR, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.ERROR, retrieveNextMethods(icfg), Parameter.This, States.ERROR,
				Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.HASNEXT, retrieveNextMethods(icfg), Parameter.This, States.INIT,
				Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.INIT, retrieveHasNextMethods(icfg), Parameter.This, States.HASNEXT,
				Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.HASNEXT, retrieveHasNextMethods(icfg), Parameter.This, States.HASNEXT,
				Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.ERROR, retrieveHasNextMethods(icfg), Parameter.This, States.ERROR,
				Type.OnReturn, icfg));
	}

	private Set<UpdatableWrapper<SootMethod>> retrieveHasNextMethods(IExtendedICFG<Unit, SootMethod> icfg) {
		if (hasNextMethods == null)
			hasNextMethods = selectMethodByName(getImplementersOfIterator("java.util.Iterator", icfg), "hasNext", icfg);
		return hasNextMethods;
	}

	private Set<UpdatableWrapper<SootMethod>> retrieveNextMethods(IExtendedICFG<Unit, SootMethod> icfg) {
		return selectMethodByName(getImplementersOfIterator("java.util.Iterator", icfg), "next", icfg);
	}

	private Set<UpdatableWrapper<SootMethod>> retrieveIteratorConstructors(IExtendedICFG<Unit, SootMethod> icfg) {
		Set<UpdatableWrapper<SootMethod>> selectMethodByName = selectMethodByName(icfg.wrap(Scene.v().getClasses()), "iterator", icfg);
		Set<SootMethod> res = new HashSet<>();
		for (UpdatableWrapper<SootMethod> m : selectMethodByName) {
			if (m.getContents().getReturnType() instanceof RefType) {
				RefType refType = (RefType) m.getContents().getReturnType();
				SootClass classs = refType.getSootClass();
				if (classs.equals(Scene.v().getSootClass("java.util.Iterator")) || Scene.v().getActiveHierarchy()
						.getImplementersOf(Scene.v().getSootClass("java.util.Iterator")).contains(classs)) {
					res.add(m.getContents());
				}
			}
		}
		return icfg.wrap(res);
	}

	private Collection<UpdatableWrapper<SootClass>> getImplementersOfIterator(String className, IExtendedICFG<Unit, SootMethod> icfg) {
		SootClass sootClass = Scene.v().getSootClass(className);
		List<SootClass> list = Scene.v().getActiveHierarchy().getImplementersOf(sootClass);
		List<SootClass> res = new LinkedList<>();
		for (SootClass c : list) {
			res.add(c);
		}
		return icfg.wrap(res);
	}

	@Override
	public Collection<UpdatableAccessGraph> generateSeed(UpdatableWrapper<SootMethod> method, UpdatableWrapper<Unit> unit, Collection<UpdatableWrapper<SootMethod>> calledMethod, IExtendedICFG<Unit, SootMethod> icfg) {
		for (UpdatableWrapper<SootMethod> m : calledMethod) {
			if (retrieveIteratorConstructors(icfg).contains(m)) {
				if (unit.getContents() instanceof AssignStmt) {
					Set<UpdatableAccessGraph> out = new HashSet<>();
					AssignStmt stmt = (AssignStmt) unit;
					out.add(new UpdatableAccessGraph(icfg.wrap((Local) stmt.getLeftOp())));
					return out;
				}
			}
		}

		return Collections.emptySet();
	}

	@Override
	public Set<Transition<ConcreteState>> getReturnTransitionsFor(UpdatableAccessGraph callerD1, UpdatableWrapper<Unit> callSite, UpdatableWrapper<SootMethod> calleeMethod,
			UpdatableWrapper<Unit> exitStmt, UpdatableAccessGraph exitNode, UpdatableWrapper<Unit> returnSite, UpdatableAccessGraph retNode) {
//		if (retrieveHasNextMethods().contains(calleeMethod)) {
//			if (icfg.getMethodOf(callSite).getSignature().contains("java.lang.Object next()"))
//				return Collections.emptySet();
//		}

		return super.getReturnTransitionsFor(callerD1, callSite, calleeMethod, exitStmt, exitNode, returnSite, retNode);
	}

	@Override
	public TypestateDomainValue<ConcreteState> getBottomElement() {
		return new TypestateDomainValue<ConcreteState>(States.INIT);
	}

	@Override
	public void getNewTansitions() {
		addTransition(new MatcherTransition<ConcreteState>(States.NONE, retrieveIteratorConstructors(icfg), Parameter.This, States.INIT,
				Type.None, icfg));
		addTransition(
				new MatcherTransition<ConcreteState>(States.INIT, retrieveNextMethods(icfg), Parameter.This, States.ERROR, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.ERROR, retrieveNextMethods(icfg), Parameter.This, States.ERROR,
				Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.HASNEXT, retrieveNextMethods(icfg), Parameter.This, States.INIT,
				Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.INIT, retrieveHasNextMethods(icfg), Parameter.This, States.HASNEXT,
				Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.HASNEXT, retrieveHasNextMethods(icfg), Parameter.This, States.HASNEXT,
				Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.ERROR, retrieveHasNextMethods(icfg), Parameter.This, States.ERROR,
				Type.OnReturn, icfg));
	}
}
