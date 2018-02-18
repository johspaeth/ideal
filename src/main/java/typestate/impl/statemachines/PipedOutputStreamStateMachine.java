package typestate.impl.statemachines;

import java.util.Collection;
import java.util.Set;

import boomerang.cfg.IExtendedICFG;
import heros.incremental.UpdatableWrapper;
import ideal.incremental.accessgraph.UpdatableAccessGraph;
import soot.SootMethod;
import soot.Unit;
import test.ConcreteState;
import typestate.TypestateChangeFunction;
import typestate.TypestateDomainValue;
import typestate.finiteautomata.MatcherStateMachine;
import typestate.finiteautomata.MatcherTransition;
import typestate.finiteautomata.MatcherTransition.Parameter;
import typestate.finiteautomata.MatcherTransition.Type;

public class PipedOutputStreamStateMachine extends MatcherStateMachine<ConcreteState> implements TypestateChangeFunction<ConcreteState> {

	IExtendedICFG<Unit, SootMethod> icfg;

	public static enum States implements ConcreteState {
		 INIT, CONNECTED, ERROR;

		@Override
		public boolean isErrorState() {
			return this == ERROR;
		}

	}

	PipedOutputStreamStateMachine(IExtendedICFG<Unit, SootMethod> icfg) {
		this.icfg = icfg;
		addTransition(
				new MatcherTransition<ConcreteState>(States.INIT, connect(icfg), Parameter.This, States.CONNECTED, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.INIT, readMethods(icfg), Parameter.This, States.ERROR, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.CONNECTED, readMethods(icfg), Parameter.This, States.CONNECTED, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.ERROR, readMethods(icfg), Parameter.This, States.ERROR, Type.OnReturn, icfg));
	}
	private Set<UpdatableWrapper<SootMethod>> connect(IExtendedICFG<Unit, SootMethod> icfg) {
		return selectMethodByName(getSubclassesOf("java.io.PipedOutputStream", icfg), "connect", icfg);
	}


	private Set<UpdatableWrapper<SootMethod>> readMethods(IExtendedICFG<Unit, SootMethod> icfg) {
		return selectMethodByName(getSubclassesOf("java.io.PipedOutputStream", icfg), "write", icfg);
	}


	@Override
	public Collection<UpdatableAccessGraph> generateSeed(UpdatableWrapper<SootMethod> m, UpdatableWrapper<Unit> unit,
			Collection<UpdatableWrapper<SootMethod>> calledMethod, IExtendedICFG<Unit, SootMethod> icfg) {
		return generateAtAllocationSiteOf(unit, java.io.PipedOutputStream.class, icfg);
	}
	@Override
	public TypestateDomainValue<ConcreteState> getBottomElement() {
		return new TypestateDomainValue<ConcreteState>(States.INIT);
	}
	@Override
	public void updateTypeStateFunctions() {
		addTransition(
				new MatcherTransition<ConcreteState>(States.INIT, connect(icfg), Parameter.This, States.CONNECTED, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.INIT, readMethods(icfg), Parameter.This, States.ERROR, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.CONNECTED, readMethods(icfg), Parameter.This, States.CONNECTED, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.ERROR, readMethods(icfg), Parameter.This, States.ERROR, Type.OnReturn, icfg));
	}
}
