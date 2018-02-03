package typestate.impl.statemachines.alloc;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import heros.incremental.UpdatableWrapper;
import ideal.incremental.accessgraph.UpdatableAccessGraph;
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

public class PrintWriterStateMachine extends MatcherStateMachine<ConcreteState>
		implements TypestateChangeFunction<ConcreteState> {

	public static enum States implements ConcreteState {
		OPEN, CLOSED, ERROR;

		@Override
		public boolean isErrorState() {
			return this == ERROR;
		}
	}

	PrintWriterStateMachine() {
		addTransition(new MatcherTransition<ConcreteState>(States.OPEN, closeMethods(), Parameter.This, States.CLOSED,
				Type.OnReturn));
		addTransition(new MatcherTransition<ConcreteState>(States.CLOSED, closeMethods(), Parameter.This, States.CLOSED,
				Type.OnReturn));
		addTransition(new MatcherTransition<ConcreteState>(States.CLOSED, readMethods(), Parameter.This, States.ERROR,
				Type.OnReturn));
		addTransition(new MatcherTransition<ConcreteState>(States.ERROR, readMethods(), Parameter.This, States.ERROR,
				Type.OnReturn));
		addTransition(new MatcherTransition<ConcreteState>(States.ERROR, closeMethods(), Parameter.This, States.ERROR,
				Type.OnReturn));
		addTransition(new MatcherTransition<ConcreteState>(States.OPEN, readMethods(), Parameter.This, States.OPEN,
				Type.OnReturn));

	}

	private Set<SootMethod> closeMethods() {
		return selectMethodByName(getSubclassesOf("java.io.PrintWriter"), "close");
	}

	private Set<SootMethod> readMethods() {
		List<SootClass> subclasses = getSubclassesOf("java.io.PrintWriter");
		Set<SootMethod> closeMethods = closeMethods();
		Set<SootMethod> out = new HashSet<>();
		for (SootClass c : subclasses) {
			for (SootMethod m : c.getMethods())
				if (m.isPublic() && !closeMethods.contains(m) && !m.isStatic())
					out.add(m);
		}
		return out;
	}

	@Override
	public Collection<UpdatableAccessGraph> generateSeed(UpdatableWrapper<SootMethod> m, UpdatableWrapper<Unit> unit,
			Collection<UpdatableWrapper<SootMethod>> calledMethod) {
		return generateAtAllocationSiteOf(unit, java.io.PrintWriter.class);
	}

	@Override
	public TypestateDomainValue<ConcreteState> getBottomElement() {
		return new TypestateDomainValue<ConcreteState>(States.OPEN);
	}

}