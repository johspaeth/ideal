package typestate.impl.statemachines;

import java.util.Collection;

import boomerang.cfg.IExtendedICFG;
import heros.incremental.UpdatableWrapper;
import ideal.incremental.accessgraph.UpdatableAccessGraph;
import soot.SootMethod;
import soot.Unit;
import test.ConcreteState;
import typestate.TypestateDomainValue;
import typestate.finiteautomata.MatcherStateMachine;
import typestate.finiteautomata.MatcherTransition;
import typestate.finiteautomata.MatcherTransition.Parameter;
import typestate.finiteautomata.MatcherTransition.Type;
import typestate.test.helper.File;

public class FileMustBeClosedStateMachine extends MatcherStateMachine<ConcreteState>{

	public static enum States implements ConcreteState {
		NONE, INIT, OPENED, CLOSED;

		@Override
		public boolean isErrorState() {
			return this == OPENED;
		}

	}



	private IExtendedICFG<Unit, SootMethod> icfg;

	public FileMustBeClosedStateMachine(IExtendedICFG<Unit, SootMethod> icfg) {
		this.icfg = icfg;
		addTransition(new MatcherTransition<ConcreteState>(States.INIT, ".*open.*",Parameter.This, States.OPENED, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.INIT, ".*close.*",Parameter.This, States.CLOSED, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.OPENED, ".*close.*",Parameter.This, States.CLOSED, Type.OnReturn, icfg));
	}



	@Override
	public Collection<UpdatableAccessGraph> generateSeed(UpdatableWrapper<SootMethod> method,UpdatableWrapper<Unit> unit,
			Collection<UpdatableWrapper<SootMethod>> calledMethod, IExtendedICFG<Unit, SootMethod> icfg) {
		return generateAtAllocationSiteOf(unit, File.class, icfg);
	}



	public TypestateDomainValue<ConcreteState> getBottomElement() {
		return new TypestateDomainValue<ConcreteState>(States.INIT);
	}

	@Override
	public void updateTypeStateFunctions() {
		addTransition(new MatcherTransition<ConcreteState>(States.INIT, ".*open.*",Parameter.This, States.OPENED, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.INIT, ".*close.*",Parameter.This, States.CLOSED, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.OPENED, ".*close.*",Parameter.This, States.CLOSED, Type.OnReturn, icfg));
	}



}
