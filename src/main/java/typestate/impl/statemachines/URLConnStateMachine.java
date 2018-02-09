package typestate.impl.statemachines;

import java.util.Collection;
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

	IExtendedICFG<Unit, SootMethod> icfg;
	
	public static enum States implements ConcreteState {
		NONE, INIT, CONNECTED, ERROR;

		@Override
		public boolean isErrorState() {
			return this == ERROR;
		}

	}

	public URLConnStateMachine(IExtendedICFG<Unit, SootMethod> icfg) {
		this.icfg = icfg;
		addTransition(new MatcherTransition<ConcreteState>(States.CONNECTED, illegalOpertaion(icfg), Parameter.This, States.ERROR,
				Type.OnReturn, icfg));
		addTransition(
				new MatcherTransition<ConcreteState>(States.ERROR, illegalOpertaion(icfg), Parameter.This, States.ERROR, Type.OnReturn, icfg));
	}

	private Set<UpdatableWrapper<SootMethod>> connect(IExtendedICFG<Unit, SootMethod> icfg) {
		return selectMethodByName(getSubclassesOf("java.net.URLConnection", icfg), "connect", icfg);
	}

	private Set<UpdatableWrapper<SootMethod>> illegalOpertaion(IExtendedICFG<Unit, SootMethod> icfg) {
		Collection<UpdatableWrapper<SootClass>> subclasses = getSubclassesOf("java.net.URLConnection", icfg);
		return selectMethodByName(subclasses,
				"setDoInput|setDoOutput|setAllowUserInteraction|setUseCaches|setIfModifiedSince|setRequestProperty|addRequestProperty|getRequestProperty|getRequestProperties", icfg);
	}

	@Override
	public Collection<UpdatableAccessGraph> generateSeed(UpdatableWrapper<SootMethod> m, UpdatableWrapper<Unit> unit,
			Collection<UpdatableWrapper<SootMethod>> calledMethod, IExtendedICFG<Unit, SootMethod> icfg) {
		return this.generateThisAtAnyCallSitesOf(unit, calledMethod, connect(icfg), icfg);
	}

	@Override
	public TypestateDomainValue<ConcreteState> getBottomElement() {
		return new TypestateDomainValue<ConcreteState>(States.CONNECTED);
	}

	@Override
	public void getNewTansitions() {
		addTransition(new MatcherTransition<ConcreteState>(States.CONNECTED, illegalOpertaion(icfg), Parameter.This, States.ERROR,
				Type.OnReturn, icfg));
		addTransition(
				new MatcherTransition<ConcreteState>(States.ERROR, illegalOpertaion(icfg), Parameter.This, States.ERROR, Type.OnReturn, icfg));
	}
}
