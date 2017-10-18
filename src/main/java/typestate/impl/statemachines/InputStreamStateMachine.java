package typestate.impl.statemachines;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import boomerang.accessgraph.AccessGraph;
import boomerang.incremental.UpdatableWrapper;
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
import typestate.impl.statemachines.InputStreamStateMachine.States;

public class InputStreamStateMachine extends MatcherStateMachine<ConcreteState> implements TypestateChangeFunction<ConcreteState> {

	public static enum States implements ConcreteState {
		NONE, CLOSED, ERROR;

		@Override
		public boolean isErrorState() {
			return this == ERROR;
		}

	}

	public InputStreamStateMachine() {
		addTransition(new MatcherTransition<ConcreteState>(States.NONE, closeMethods(), Parameter.This, States.CLOSED, Type.OnReturn));
		addTransition(
				new MatcherTransition<ConcreteState>(States.CLOSED, closeMethods(), Parameter.This, States.CLOSED, Type.OnReturn));
		addTransition(new MatcherTransition<ConcreteState>(States.CLOSED, readMethods(), Parameter.This, States.ERROR, Type.OnReturn));
		addTransition(new MatcherTransition<ConcreteState>(States.ERROR, readMethods(), Parameter.This, States.ERROR, Type.OnReturn));

		addTransition(new MatcherTransition<ConcreteState>(States.CLOSED, Collections.singleton(Scene.v().getMethod("<java.io.InputStream: int read()>")), Parameter.This, States.ERROR, Type.OnCallToReturn));
		addTransition(new MatcherTransition<ConcreteState>(States.ERROR, Collections.singleton(Scene.v().getMethod("<java.io.InputStream: int read()>")), Parameter.This, States.ERROR, Type.OnCallToReturn));
	}
	private Set<SootMethod> closeMethods() {
		return selectMethodByName(getImplementersOf("java.io.InputStream"), "close");
	}

	private Set<SootMethod> readMethods() {
		return selectMethodByName(getImplementersOf("java.io.InputStream"), "read");
	}


	private List<SootClass> getImplementersOf(String className) {
		SootClass sootClass = Scene.v().getSootClass(className);
		List<SootClass> list = Scene.v().getActiveHierarchy().getSubclassesOfIncluding(sootClass);
		List<SootClass> res = new LinkedList<>();
		for (SootClass c : list) {
			res.add(c);
		}
		return res;
	}

	@Override
	public Collection<AccessGraph> generateSeed(UpdatableWrapper<SootMethod> method, UpdatableWrapper<Unit> unit,
			Collection<UpdatableWrapper<SootMethod>> calledMethod) {
		return this.generateThisAtAnyCallSitesOf(unit, calledMethod, closeMethods());
	}
	@Override
	public TypestateDomainValue getBottomElement() {
		return new TypestateDomainValue<States>(States.NONE);
	}
}