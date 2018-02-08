package typestate.impl.statemachines;

import java.net.Socket;
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

public class SocketStateMachine extends MatcherStateMachine<ConcreteState> implements TypestateChangeFunction<ConcreteState> {

	public static enum States implements ConcreteState {
		NONE, INIT, CONNECTED, ERROR;

		@Override
		public boolean isErrorState() {
			return this == ERROR;
		}
	}
	public SocketStateMachine(IExtendedICFG<Unit, SootMethod> icfg) {
		addTransition(
				new MatcherTransition<ConcreteState>(States.NONE, socketConstructor(icfg), Parameter.This, States.INIT, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.INIT, connect(icfg), Parameter.This, States.CONNECTED, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.INIT, useMethods(icfg), Parameter.This, States.ERROR, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.ERROR, useMethods(icfg), Parameter.This, States.ERROR, Type.OnReturn, icfg));
	}

	private Set<UpdatableWrapper<SootMethod>> socketConstructor(IExtendedICFG<Unit, SootMethod> icfg) {
		Collection<UpdatableWrapper<SootClass>> subclasses = getSubclassesOf("java.net.Socket", icfg);
		Set<SootMethod> out = new HashSet<>();
		for (UpdatableWrapper<SootClass> c : subclasses) {
			for (SootMethod m : c.getContents().getMethods())
				if (m.isConstructor())
					out.add(m);
		}
		return icfg.wrap(out);
	}

	private Set<UpdatableWrapper<SootMethod>> connect(IExtendedICFG<Unit, SootMethod> icfg) {
		return selectMethodByName(getSubclassesOf("java.net.Socket", icfg), "connect", icfg);
	}

	private Set<UpdatableWrapper<SootMethod>> useMethods(IExtendedICFG<Unit, SootMethod> icfg) {
		Collection<UpdatableWrapper<SootClass>> subclasses = getSubclassesOf("java.net.Socket", icfg);
		Set<UpdatableWrapper<SootMethod>> connectMethod = connect(icfg);
		Set<SootMethod> out = new HashSet<>();
		for (UpdatableWrapper<SootClass> c : subclasses) {
			for (SootMethod m : c.getContents().getMethods())
				if (m.isPublic() && !Utils.getSootMethods(connectMethod).contains(m) && !m.isStatic())
					out.add(m);
		}
		return icfg.wrap(out);
	}

	@Override
	public Collection<UpdatableAccessGraph> generateSeed(UpdatableWrapper<SootMethod> m, UpdatableWrapper<Unit> unit,
			Collection<UpdatableWrapper<SootMethod>> calledMethod, IExtendedICFG<Unit, SootMethod> icfg) {
		return generateAtAllocationSiteOf(unit, Socket.class, icfg);
	}

	@Override
	public TypestateDomainValue<ConcreteState> getBottomElement() {
		return new TypestateDomainValue<ConcreteState>(States.NONE);
	}

}
