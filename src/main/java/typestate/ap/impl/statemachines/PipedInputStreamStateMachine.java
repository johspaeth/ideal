package typestate.ap.impl.statemachines;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import boomerang.accessgraph.AccessGraph;
import boomerang.cfg.ExtendedICFG;
import heros.EdgeFunction;
import heros.solver.Pair;
import ideal.ap.Analysis;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import typestate.ap.ConcreteState;
import typestate.ap.TypestateChangeFunction;
import typestate.ap.TypestateDomainValue;
import typestate.ap.finiteautomata.MatcherStateMachine;
import typestate.ap.finiteautomata.MatcherTransition;
import typestate.ap.finiteautomata.State;
import typestate.ap.finiteautomata.MatcherTransition.Parameter;
import typestate.ap.finiteautomata.MatcherTransition.Type;

public class PipedInputStreamStateMachine extends MatcherStateMachine<ConcreteState> implements TypestateChangeFunction<ConcreteState> {

	public static enum States implements ConcreteState {
		INIT, CONNECTED, ERROR;

		@Override
		public boolean isErrorState() {
			return this == ERROR;
		}
	}

	public PipedInputStreamStateMachine() {
		addTransition(
				new MatcherTransition<ConcreteState>(States.INIT, connect(), Parameter.This, States.CONNECTED, Type.OnReturn));
		addTransition(new MatcherTransition<ConcreteState>(States.INIT, readMethods(), Parameter.This, States.ERROR, Type.OnReturn));
		addTransition(new MatcherTransition<ConcreteState>(States.CONNECTED, readMethods(), Parameter.This, States.CONNECTED, Type.OnReturn));
		addTransition(new MatcherTransition<ConcreteState>(States.ERROR, readMethods(), Parameter.This, States.ERROR, Type.OnReturn));
	}

	private Set<SootMethod> connect() {
		return selectMethodByName(getSubclassesOf("java.io.PipedInputStream"), "connect");
	}


	private Set<SootMethod> readMethods() {
		return selectMethodByName(getSubclassesOf("java.io.PipedInputStream"), "read");
	}


	@Override
	public Collection<AccessGraph> generateSeed(SootMethod m, Unit unit,
			Collection<SootMethod> calledMethod) {
		return generateAtAllocationSiteOf(unit, java.io.PipedInputStream.class);
	}

	@Override
	public TypestateDomainValue<ConcreteState> getBottomElement() {
		return new TypestateDomainValue<ConcreteState>(States.INIT);
	}
}
