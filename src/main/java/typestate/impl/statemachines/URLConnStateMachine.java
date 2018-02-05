package typestate.impl.statemachines;

import java.util.Collection;
import java.util.List;
import java.util.Set;

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

public class URLConnStateMachine extends MatcherStateMachine<ConcreteState> implements TypestateChangeFunction<ConcreteState> {

	public static enum States implements ConcreteState {
		NONE, INIT, CONNECTED, ERROR;

		@Override
		public boolean isErrorState() {
			return this == ERROR;
		}

	}

	public URLConnStateMachine() {
		addTransition(new MatcherTransition<ConcreteState>(States.CONNECTED, illegalOpertaion(), Parameter.This, States.ERROR,
				Type.OnReturn));
		addTransition(
				new MatcherTransition<ConcreteState>(States.ERROR, illegalOpertaion(), Parameter.This, States.ERROR, Type.OnReturn));
	}

	private Set<SootMethod> connect() {
		return selectMethodByName(getSubclassesOf("java.net.URLConnection"), "connect");
	}

	private Set<SootMethod> illegalOpertaion() {
		List<SootClass> subclasses = getSubclassesOf("java.net.URLConnection");
		return selectMethodByName(subclasses,
				"setDoInput|setDoOutput|setAllowUserInteraction|setUseCaches|setIfModifiedSince|setRequestProperty|addRequestProperty|getRequestProperty|getRequestProperties");
	}

	@Override
	public Collection<UpdatableAccessGraph> generateSeed(UpdatableWrapper<SootMethod> m, UpdatableWrapper<Unit> unit,
			Collection<UpdatableWrapper<SootMethod>> calledMethod, IExtendedICFG<Unit, SootMethod> icfg) {
		return this.generateThisAtAnyCallSitesOf(unit, calledMethod, connect(), icfg);
	}

	@Override
	public TypestateDomainValue<ConcreteState> getBottomElement() {
		return new TypestateDomainValue<ConcreteState>(States.CONNECTED);
	}
}
