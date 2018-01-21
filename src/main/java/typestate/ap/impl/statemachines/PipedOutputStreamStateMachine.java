package typestate.ap.impl.statemachines;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import boomerang.accessgraph.AccessGraph;
import boomerang.cfg.ExtendedICFG;
import heros.EdgeFunction;
import heros.solver.Pair;
import ideal.ap.Analysis;
import soot.Local;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.Stmt;
import typestate.ap.ConcreteState;
import typestate.ap.TransitionFunction;
import typestate.ap.TypestateChangeFunction;
import typestate.ap.TypestateDomainValue;
import typestate.ap.finiteautomata.MatcherStateMachine;
import typestate.ap.finiteautomata.MatcherTransition;
import typestate.ap.finiteautomata.State;
import typestate.ap.finiteautomata.Transition;
import typestate.ap.finiteautomata.MatcherTransition.Parameter;
import typestate.ap.finiteautomata.MatcherTransition.Type;

public class PipedOutputStreamStateMachine extends MatcherStateMachine<ConcreteState> implements TypestateChangeFunction<ConcreteState> {


	public static enum States implements ConcreteState {
		 INIT, CONNECTED, ERROR;

		@Override
		public boolean isErrorState() {
			return this == ERROR;
		}

	}

	public PipedOutputStreamStateMachine() {
		addTransition(
				new MatcherTransition<ConcreteState>(States.INIT, connect(), Parameter.This, States.CONNECTED, Type.OnReturn));
		addTransition(new MatcherTransition<ConcreteState>(States.INIT, readMethods(), Parameter.This, States.ERROR, Type.OnReturn));
		addTransition(new MatcherTransition<ConcreteState>(States.CONNECTED, readMethods(), Parameter.This, States.CONNECTED, Type.OnReturn));
		addTransition(new MatcherTransition<ConcreteState>(States.ERROR, readMethods(), Parameter.This, States.ERROR, Type.OnReturn));
	}
	private Set<SootMethod> connect() {
		return selectMethodByName(getSubclassesOf("java.io.PipedOutputStream"), "connect");
	}


	private Set<SootMethod> readMethods() {
		return selectMethodByName(getSubclassesOf("java.io.PipedOutputStream"), "write");
	}


	@Override
	public Collection<AccessGraph> generateSeed(SootMethod m, Unit unit,
			Collection<SootMethod> calledMethod) {
		return generateAtAllocationSiteOf(unit, java.io.PipedOutputStream.class);
	}
	@Override
	public TypestateDomainValue<ConcreteState> getBottomElement() {
		return new TypestateDomainValue<ConcreteState>(States.INIT);
	}
}
