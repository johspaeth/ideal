package typestate.impl.statemachines;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.Vector;

import boomerang.cfg.IExtendedICFG;
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

public class VectorStateMachine extends MatcherStateMachine<ConcreteState> implements TypestateChangeFunction<ConcreteState> {

	public static enum States implements ConcreteState {
		INIT, NOT_EMPTY, ACCESSED_EMPTY;

		@Override
		public boolean isErrorState() {
			return this == ACCESSED_EMPTY;
		}

	}

	public VectorStateMachine(IExtendedICFG<Unit, SootMethod> icfg) {
		addTransition(
				new MatcherTransition<ConcreteState>(States.INIT, addElement(icfg), Parameter.This, States.NOT_EMPTY, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.INIT, accessElement(icfg), Parameter.This, States.ACCESSED_EMPTY,
				Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.NOT_EMPTY, accessElement(icfg), Parameter.This, States.NOT_EMPTY,
				Type.OnReturn, icfg));

		addTransition(new MatcherTransition<ConcreteState>(States.NOT_EMPTY, removeAllElements(icfg), Parameter.This, States.INIT,
				Type.OnReturn, icfg));
		addTransition(
				new MatcherTransition<ConcreteState>(States.INIT, removeAllElements(icfg), Parameter.This, States.INIT, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.ACCESSED_EMPTY, accessElement(icfg), Parameter.This,
				States.ACCESSED_EMPTY, Type.OnReturn, icfg));
	}

	private Set<UpdatableWrapper<SootMethod>> removeAllElements(IExtendedICFG<Unit, SootMethod> icfg) {
		Collection<UpdatableWrapper<SootClass>> vectorClasses = getSubclassesOf("java.util.Vector", icfg);
		Set<UpdatableWrapper<SootMethod>> selectMethodByName = selectMethodByName(vectorClasses, "removeAllElements", icfg);
		return selectMethodByName;
	}

	private Set<UpdatableWrapper<SootMethod>> addElement(IExtendedICFG<Unit, SootMethod> icfg) {
		Collection<UpdatableWrapper<SootClass>> vectorClasses = getSubclassesOf("java.util.Vector", icfg);
		Set<UpdatableWrapper<SootMethod>> selectMethodByName = selectMethodByName(vectorClasses,
				"add|addAll|addElement|insertElementAt|set|setElementAt", icfg);
		return selectMethodByName;
	}

	private Set<UpdatableWrapper<SootMethod>> accessElement(IExtendedICFG<Unit, SootMethod> icfg) {
		Collection<UpdatableWrapper<SootClass>> vectorClasses = getSubclassesOf("java.util.Vector", icfg);
		Set<UpdatableWrapper<SootMethod>> selectMethodByName = selectMethodByName(vectorClasses,
				"elementAt|firstElement|lastElement|get", icfg);
		return selectMethodByName;
	}

	@Override
	public Collection<UpdatableAccessGraph> generateSeed(UpdatableWrapper<SootMethod> m, UpdatableWrapper<Unit> unit,
			Collection<UpdatableWrapper<SootMethod>> calledMethod, IExtendedICFG<Unit, SootMethod> icfg) {
		if(m.toString().contains("<clinit>"))
			return Collections.emptySet();
		return generateAtAllocationSiteOf(unit,Vector.class, icfg);
	}
	
	@Override
	public TypestateDomainValue<ConcreteState> getBottomElement() {
		return new TypestateDomainValue<ConcreteState>(States.INIT);
	}

}
