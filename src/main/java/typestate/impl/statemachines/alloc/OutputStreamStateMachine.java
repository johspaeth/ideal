package typestate.impl.statemachines.alloc;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import boomerang.cfg.IExtendedICFG;
import heros.incremental.UpdatableWrapper;
import ideal.incremental.accessgraph.UpdatableAccessGraph;
import soot.Scene;
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

public class OutputStreamStateMachine extends MatcherStateMachine<ConcreteState> implements TypestateChangeFunction<ConcreteState> {

	IExtendedICFG<Unit, SootMethod> icfg;
	
	public static enum States implements ConcreteState {
		OPEN, CLOSED, ERROR;

		@Override
		public boolean isErrorState() {
			return this == ERROR;
		}

	}

	OutputStreamStateMachine(IExtendedICFG<Unit, SootMethod> icfg) {
		this.icfg = icfg;
		addTransition(
				new MatcherTransition<ConcreteState>(States.OPEN, closeMethods(icfg), Parameter.This, States.CLOSED, Type.OnReturn, icfg));
		addTransition(
				new MatcherTransition<ConcreteState>(States.CLOSED, closeMethods(icfg), Parameter.This, States.CLOSED, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.OPEN, writeMethods(icfg), Parameter.This, States.OPEN, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.CLOSED, writeMethods(icfg), Parameter.This, States.ERROR, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.ERROR, writeMethods(icfg), Parameter.This, States.ERROR, Type.OnReturn, icfg));
	}


	private Set<UpdatableWrapper<SootMethod>> constructors(IExtendedICFG<Unit, SootMethod> icfg) {
		Collection<UpdatableWrapper<SootClass>> subclasses = getSubclassesOf("java.io.OutputStream", icfg);
		Set<SootMethod> out = new HashSet<>();
		for (UpdatableWrapper<SootClass> c : subclasses) {
			for (SootMethod m : c.getContents().getMethods())
				if (m.isConstructor())
					out.add(m);
		}
		return icfg.wrap(out);
	}
	private Set<UpdatableWrapper<SootMethod>> closeMethods(IExtendedICFG<Unit, SootMethod> icfg) {
		return selectMethodByName(getImplementersOf("java.io.OutputStream", icfg), "close", icfg);
	}

	private Set<UpdatableWrapper<SootMethod>> writeMethods(IExtendedICFG<Unit, SootMethod> icfg) {
		return selectMethodByName(getImplementersOf("java.io.OutputStream", icfg), "write", icfg);
	}


	private Collection<UpdatableWrapper<SootClass>> getImplementersOf(String className, IExtendedICFG<Unit, SootMethod> icfg) {
		SootClass sootClass = Scene.v().getSootClass(className);
		List<SootClass> list = Scene.v().getActiveHierarchy().getSubclassesOfIncluding(sootClass);
		List<SootClass> res = new LinkedList<>();
		for (SootClass c : list) {
			res.add(c);
		}
		return icfg.wrap(res);
	}

	@Override
	public Collection<UpdatableAccessGraph> generateSeed(UpdatableWrapper<SootMethod> m, UpdatableWrapper<Unit> unit,
			Collection<UpdatableWrapper<SootMethod>> calledMethod, IExtendedICFG<Unit, SootMethod> icfg) {
		return generateThisAtAnyCallSitesOf(unit,calledMethod,constructors(icfg), icfg);
	}


	@Override
	public TypestateDomainValue<ConcreteState> getBottomElement() {
		return new TypestateDomainValue<ConcreteState>(States.OPEN);
	}


	@Override
	public void updateTypeStateFunctions() {
		addTransition(
				new MatcherTransition<ConcreteState>(States.OPEN, closeMethods(icfg), Parameter.This, States.CLOSED, Type.OnReturn, icfg));
		addTransition(
				new MatcherTransition<ConcreteState>(States.CLOSED, closeMethods(icfg), Parameter.This, States.CLOSED, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.OPEN, writeMethods(icfg), Parameter.This, States.OPEN, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.CLOSED, writeMethods(icfg), Parameter.This, States.ERROR, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.ERROR, writeMethods(icfg), Parameter.This, States.ERROR, Type.OnReturn, icfg));
	}


}
