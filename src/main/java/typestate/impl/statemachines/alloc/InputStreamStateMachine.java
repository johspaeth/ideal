package typestate.impl.statemachines.alloc;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import boomerang.cfg.IExtendedICFG;
import heros.incremental.UpdatableWrapper;
import ideal.incremental.accessgraph.UpdatableAccessGraph;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import test.ConcreteState;
import typestate.TypestateChangeFunction;
import typestate.TypestateDomainValue;
import typestate.finiteautomata.MatcherStateMachine;
import typestate.finiteautomata.MatcherTransition;
import typestate.finiteautomata.MatcherTransition.Parameter;
import typestate.finiteautomata.MatcherTransition.Type;

public class InputStreamStateMachine extends MatcherStateMachine<ConcreteState> implements TypestateChangeFunction<ConcreteState> {

	IExtendedICFG<Unit, SootMethod> icfg;
	
	public static enum States implements ConcreteState {
		 OPEN, CLOSED, ERROR;

		@Override
		public boolean isErrorState() {
			return this == ERROR;
		}
	}

	InputStreamStateMachine(IExtendedICFG<Unit, SootMethod> icfg) {
		this.icfg = icfg;
		addTransition(
				new MatcherTransition<ConcreteState>(States.OPEN, closeMethods(icfg), Parameter.This, States.CLOSED, Type.OnReturn, icfg));
		addTransition(
				new MatcherTransition<ConcreteState>(States.CLOSED, closeMethods(icfg), Parameter.This, States.CLOSED, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.OPEN, readMethods(icfg), Parameter.This, States.OPEN, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.ERROR, readMethods(icfg), Parameter.This, States.ERROR, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.CLOSED, readMethods(icfg), Parameter.This, States.ERROR, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.ERROR, closeMethods(icfg), Parameter.This, States.ERROR, Type.OnReturn, icfg));

		addTransition(new MatcherTransition<ConcreteState>(States.CLOSED, nativeReadMethods(icfg), Parameter.This, States.ERROR, Type.OnCallToReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.ERROR, nativeReadMethods(icfg), Parameter.This, States.ERROR, Type.OnCallToReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.OPEN, nativeReadMethods(icfg), Parameter.This, States.OPEN, Type.OnCallToReturn, icfg));
	}


	private Set<UpdatableWrapper<SootMethod>> nativeReadMethods(IExtendedICFG<Unit, SootMethod> icfg) {
		Collection<UpdatableWrapper<SootClass>> subclasses = getSubclassesOf("java.io.InputStream", icfg);
		Set<SootMethod> out = new HashSet<>();
		for (UpdatableWrapper<SootClass> c : subclasses) {
			for (SootMethod m : c.getContents().getMethods())
				if (m.isNative() && m.toString().contains("read()"))
					out.add(m);
		}
		return icfg.wrap(out);
	}


	private Set<UpdatableWrapper<SootMethod>> constructors(IExtendedICFG<Unit, SootMethod> icfg) {
		Collection<UpdatableWrapper<SootClass>> subclasses = getSubclassesOf("java.io.InputStream", icfg);
		Set<SootMethod> out = new HashSet<>();
		for (UpdatableWrapper<SootClass> c : subclasses) {
			for (SootMethod m : c.getContents().getMethods())
				if (m.isConstructor())
					out.add(m);
		}
		return icfg.wrap(out);
	}

	private Set<UpdatableWrapper<SootMethod>> closeMethods(IExtendedICFG<Unit, SootMethod> icfg) {
		return selectMethodByName(getImplementersOf("java.io.InputStream", icfg), "close", icfg);
	}

	private Set<UpdatableWrapper<SootMethod>> readMethods(IExtendedICFG<Unit, SootMethod> icfg) {
		return selectMethodByName(getImplementersOf("java.io.InputStream", icfg), "read", icfg);
	}


	private Collection<UpdatableWrapper<SootClass>> getImplementersOf(String className, IExtendedICFG<Unit, SootMethod> icfg) {
		SootClass sootClass = Scene.v().getSootClass(className);
		List<SootClass> list = Scene.v().getActiveHierarchy().getSubclassesOfIncluding(sootClass);
		List<SootClass> res = new LinkedList<>();
		for (SootClass c : list) {
			res.add(c);
		}
		return icfg.wrap(res);
	}

	@Override
	public Collection<UpdatableAccessGraph> generateSeed(UpdatableWrapper<SootMethod> m, UpdatableWrapper<Unit> unit,
			Collection<UpdatableWrapper<SootMethod>> calledMethod, IExtendedICFG<Unit, SootMethod> icfg) {
		
		return this.generateThisAtAnyCallSitesOf(unit, calledMethod, constructors(icfg), icfg);
	}


	@Override
	public TypestateDomainValue<ConcreteState> getBottomElement() {
		return new TypestateDomainValue<ConcreteState>(States.OPEN);
	}


	@Override
	public void getNewTansitions() {
		addTransition(
				new MatcherTransition<ConcreteState>(States.OPEN, closeMethods(icfg), Parameter.This, States.CLOSED, Type.OnReturn, icfg));
		addTransition(
				new MatcherTransition<ConcreteState>(States.CLOSED, closeMethods(icfg), Parameter.This, States.CLOSED, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.OPEN, readMethods(icfg), Parameter.This, States.OPEN, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.ERROR, readMethods(icfg), Parameter.This, States.ERROR, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.CLOSED, readMethods(icfg), Parameter.This, States.ERROR, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.ERROR, closeMethods(icfg), Parameter.This, States.ERROR, Type.OnReturn, icfg));

		addTransition(new MatcherTransition<ConcreteState>(States.CLOSED, nativeReadMethods(icfg), Parameter.This, States.ERROR, Type.OnCallToReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.ERROR, nativeReadMethods(icfg), Parameter.This, States.ERROR, Type.OnCallToReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.OPEN, nativeReadMethods(icfg), Parameter.This, States.OPEN, Type.OnCallToReturn, icfg));
	}
}
