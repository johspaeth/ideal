package typestate.ap.impl.statemachines;

import java.net.Socket;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import boomerang.accessgraph.AccessGraph;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import typestate.ap.ConcreteState;
import typestate.ap.TypestateChangeFunction;
import typestate.ap.TypestateDomainValue;
import typestate.ap.finiteautomata.MatcherStateMachine;
import typestate.ap.finiteautomata.MatcherTransition;
import typestate.ap.finiteautomata.MatcherTransition.Parameter;
import typestate.ap.finiteautomata.MatcherTransition.Type;

public class SocketStateMachine extends MatcherStateMachine<ConcreteState> implements TypestateChangeFunction<ConcreteState> {

	public static enum States implements ConcreteState {
		NONE, INIT, CONNECTED, ERROR;

		@Override
		public boolean isErrorState() {
			return this == ERROR;
		}
	}
	public SocketStateMachine() {
		addTransition(
				new MatcherTransition<ConcreteState>(States.NONE, socketConstructor(), Parameter.This, States.INIT, Type.OnReturn));
		addTransition(new MatcherTransition<ConcreteState>(States.INIT, connect(), Parameter.This, States.CONNECTED, Type.OnReturn));
		addTransition(new MatcherTransition<ConcreteState>(States.INIT, useMethods(), Parameter.This, States.ERROR, Type.OnReturn));
		addTransition(new MatcherTransition<ConcreteState>(States.ERROR, useMethods(), Parameter.This, States.ERROR, Type.OnReturn));
	}

	private Set<SootMethod> socketConstructor() {
		List<SootClass> subclasses = getSubclassesOf("java.net.Socket");
		Set<SootMethod> out = new HashSet<>();
		for (SootClass c : subclasses) {
			for (SootMethod m : c.getMethods())
				if (m.isConstructor())
					out.add(m);
		}
		return out;
	}

	private Set<SootMethod> connect() {
		return selectMethodByName(getSubclassesOf("java.net.Socket"), "connect");
	}

	private Set<SootMethod> useMethods() {
		List<SootClass> subclasses = getSubclassesOf("java.net.Socket");
		Set<SootMethod> connectMethod = connect();
		Set<SootMethod> out = new HashSet<>();
		for (SootClass c : subclasses) {
			for (SootMethod m : c.getMethods())
				if (m.isPublic() && !connectMethod.contains(m) && !m.isStatic())
					out.add(m);
		}
		return out;
	}

	@Override
	public Collection<AccessGraph> generateSeed(SootMethod m, Unit unit, Collection<SootMethod> calledMethod) {
		return generateAtAllocationSiteOf(unit, Socket.class);
	}

	@Override
	public TypestateDomainValue<ConcreteState> getBottomElement() {
		return new TypestateDomainValue<ConcreteState>(States.NONE);
	}

}
