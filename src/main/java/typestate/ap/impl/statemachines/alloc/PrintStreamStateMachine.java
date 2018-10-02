package typestate.ap.impl.statemachines.alloc;

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

public class PrintStreamStateMachine extends MatcherStateMachine<ConcreteState>
		implements TypestateChangeFunction<ConcreteState> {

	public static enum States implements ConcreteState {
		OPEN, CLOSED, ERROR;

		@Override
		public boolean isErrorState() {
			return this == ERROR || this == OPEN;
		}
	}

	public PrintStreamStateMachine() {
		addTransition(new MatcherTransition<ConcreteState>(States.CLOSED, closeMethods(), Parameter.This, States.CLOSED,
				Type.OnReturn));
		addTransition(new MatcherTransition<ConcreteState>(States.OPEN, readMethods(), Parameter.This, States.OPEN,
				Type.OnReturn));
		addTransition(new MatcherTransition<ConcreteState>(States.OPEN, closeMethods(), Parameter.This, States.CLOSED,
				Type.OnReturn));
		addTransition(new MatcherTransition<ConcreteState>(States.CLOSED, readMethods(), Parameter.This, States.ERROR,
				Type.OnReturn));
		addTransition(new MatcherTransition<ConcreteState>(States.ERROR, readMethods(), Parameter.This, States.ERROR,
				Type.OnReturn));
		addTransition(new MatcherTransition<ConcreteState>(States.ERROR, closeMethods(), Parameter.This, States.ERROR,
				Type.OnReturn));
	}

	private Set<SootMethod> closeMethods() {
		return selectMethodByName(getSubclassesOf("java.io.PrintStream"), "close");
	}

	private Set<SootMethod> readMethods() {
		List<SootClass> subclasses = getSubclassesOf("java.io.PrintStream");
		Set<SootMethod> closeMethods = closeMethods();
		Set<SootMethod> out = new HashSet<>();
		for (SootClass c : subclasses) {
			for (SootMethod m : c.getMethods())
				if (m.isPublic() && !closeMethods.contains(m) && !m.isStatic() && !m.isConstructor())
					out.add(m);
		}
		return out;
	}

	@Override
	public Collection<AccessGraph> generateSeed(SootMethod m, Unit unit, Collection<SootMethod> calledMethod) {
		return generateAtAllocationSiteOf(unit, java.io.PrintStream.class);
	}

	@Override
	public TypestateDomainValue<ConcreteState> getBottomElement() {
		return new TypestateDomainValue<ConcreteState>(States.OPEN);
	}

}
