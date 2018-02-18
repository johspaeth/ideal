package typestate.impl.statemachines;

import java.util.Collection;
import java.util.Collections;
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
		NONE, CLOSED, ERROR;

		@Override
		public boolean isErrorState() {
			return this == ERROR;
		}

	}

	public InputStreamStateMachine(IExtendedICFG<Unit, SootMethod> icfg) {
		this.icfg = icfg;
		addTransition(new MatcherTransition<ConcreteState>(States.NONE, closeMethods(icfg), Parameter.This, States.CLOSED, Type.OnReturn, icfg));
		addTransition(
				new MatcherTransition<ConcreteState>(States.CLOSED, closeMethods(icfg), Parameter.This, States.CLOSED, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.CLOSED, readMethods(icfg), Parameter.This, States.ERROR, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.ERROR, readMethods(icfg), Parameter.This, States.ERROR, Type.OnReturn, icfg));

		addTransition(new MatcherTransition<ConcreteState>(States.CLOSED, icfg.wrap(Collections.singleton(Scene.v().getMethod("<java.io.InputStream: int read()>"))), Parameter.This, States.ERROR, Type.OnCallToReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.ERROR, icfg.wrap(Collections.singleton(Scene.v().getMethod("<java.io.InputStream: int read()>"))), Parameter.This, States.ERROR, Type.OnCallToReturn, icfg));
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
	public Collection<UpdatableAccessGraph> generateSeed(UpdatableWrapper<SootMethod> method, UpdatableWrapper<Unit> unit,
			Collection<UpdatableWrapper<SootMethod>> calledMethod, IExtendedICFG<Unit, SootMethod> icfg) {
		return this.generateThisAtAnyCallSitesOf(unit, calledMethod, closeMethods(icfg), icfg);
	}
	@Override
	public TypestateDomainValue getBottomElement() {
		return new TypestateDomainValue<States>(States.NONE);
	}
	@Override
	public void updateTypeStateFunctions() {
		addTransition(new MatcherTransition<ConcreteState>(States.NONE, closeMethods(icfg), Parameter.This, States.CLOSED, Type.OnReturn, icfg));
		addTransition(
				new MatcherTransition<ConcreteState>(States.CLOSED, closeMethods(icfg), Parameter.This, States.CLOSED, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.CLOSED, readMethods(icfg), Parameter.This, States.ERROR, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.ERROR, readMethods(icfg), Parameter.This, States.ERROR, Type.OnReturn, icfg));

		addTransition(new MatcherTransition<ConcreteState>(States.CLOSED, icfg.wrap(Collections.singleton(Scene.v().getMethod("<java.io.InputStream: int read()>"))), Parameter.This, States.ERROR, Type.OnCallToReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.ERROR, icfg.wrap(Collections.singleton(Scene.v().getMethod("<java.io.InputStream: int read()>"))), Parameter.This, States.ERROR, Type.OnCallToReturn, icfg));
	}
}
