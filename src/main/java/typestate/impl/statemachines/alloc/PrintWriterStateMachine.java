package typestate.impl.statemachines.alloc;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import boomerang.cfg.IExtendedICFG;
import heros.incremental.UpdatableWrapper;
import ideal.incremental.accessgraph.UpdatableAccessGraph;
import ideal.incremental.accessgraph.Utils;
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

	IExtendedICFG<Unit, SootMethod> icfg;
	
	public static enum States implements ConcreteState {
		OPEN, CLOSED, ERROR;

		@Override
		public boolean isErrorState() {
			return this == ERROR;
		}
	}

	PrintWriterStateMachine(IExtendedICFG<Unit, SootMethod> icfg) {
		this.icfg = icfg;
		addTransition(new MatcherTransition<ConcreteState>(States.OPEN, closeMethods(icfg), Parameter.This, States.CLOSED,
				Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.CLOSED, closeMethods(icfg), Parameter.This, States.CLOSED,
				Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.CLOSED, readMethods(icfg), Parameter.This, States.ERROR,
				Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.ERROR, readMethods(icfg), Parameter.This, States.ERROR,
				Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.ERROR, closeMethods(icfg), Parameter.This, States.ERROR,
				Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.OPEN, readMethods(icfg), Parameter.This, States.OPEN,
				Type.OnReturn, icfg));

	}

	private Set<UpdatableWrapper<SootMethod>> closeMethods(IExtendedICFG<Unit, SootMethod> icfg) {
		return selectMethodByName(getSubclassesOf("java.io.PrintWriter", icfg), "close", icfg);
	}

	private Set<UpdatableWrapper<SootMethod>> readMethods(IExtendedICFG<Unit, SootMethod> icfg) {
		Collection<UpdatableWrapper<SootClass>> subclasses = getSubclassesOf("java.io.PrintWriter", icfg);
		Set<UpdatableWrapper<SootMethod>> closeMethods = closeMethods(icfg);
		Set<SootMethod> out = new HashSet<>();
		for (UpdatableWrapper<SootClass> c : subclasses) {
			for (SootMethod m : c.getContents().getMethods())
				if (m.isPublic() && !Utils.getSootMethods(closeMethods).contains(m) && !m.isStatic())
					out.add(m);
		}
		return icfg.wrap(out);
	}

	@Override
	public Collection<UpdatableAccessGraph> generateSeed(UpdatableWrapper<SootMethod> m, UpdatableWrapper<Unit> unit,
			Collection<UpdatableWrapper<SootMethod>> calledMethod, IExtendedICFG<Unit, SootMethod> icfg) {
		return generateAtAllocationSiteOf(unit, java.io.PrintWriter.class, icfg);
	}

	@Override
	public TypestateDomainValue<ConcreteState> getBottomElement() {
		return new TypestateDomainValue<ConcreteState>(States.OPEN);
	}

	@Override
	public void getNewTansitions() {
		addTransition(new MatcherTransition<ConcreteState>(States.OPEN, closeMethods(icfg), Parameter.This, States.CLOSED,
				Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.CLOSED, closeMethods(icfg), Parameter.This, States.CLOSED,
				Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.CLOSED, readMethods(icfg), Parameter.This, States.ERROR,
				Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.ERROR, readMethods(icfg), Parameter.This, States.ERROR,
				Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.ERROR, closeMethods(icfg), Parameter.This, States.ERROR,
				Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.OPEN, readMethods(icfg), Parameter.This, States.OPEN,
				Type.OnReturn, icfg));
	}

}
