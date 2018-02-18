package typestate.impl.statemachines;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import boomerang.cfg.ExtendedICFG;
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

public class SignatureStateMachine extends MatcherStateMachine<ConcreteState>
		implements TypestateChangeFunction<ConcreteState> {

	IExtendedICFG<Unit, SootMethod> icfg;
	
	public static enum States implements ConcreteState {
		NONE, UNITIALIZED, SIGN_CHECK, VERIFY_CHECK, ERROR;

		@Override
		public boolean isErrorState() {
			return this == ERROR;
		}
	}

	SignatureStateMachine(ExtendedICFG icfg) {
		this.icfg = icfg;
		addTransition(new MatcherTransition<ConcreteState>(States.NONE, constructor(icfg), Parameter.This,
				States.UNITIALIZED, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.UNITIALIZED, initSign(icfg), Parameter.This,
				States.SIGN_CHECK, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.UNITIALIZED, initVerify(icfg), Parameter.This,
				States.VERIFY_CHECK, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.UNITIALIZED, sign(icfg), Parameter.This, States.ERROR,
				Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.UNITIALIZED, verify(icfg), Parameter.This, States.ERROR,
				Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.UNITIALIZED, update(icfg), Parameter.This, States.ERROR,
				Type.OnReturn, icfg));

		addTransition(new MatcherTransition<ConcreteState>(States.SIGN_CHECK, initSign(icfg), Parameter.This,
				States.SIGN_CHECK, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.SIGN_CHECK, initVerify(icfg), Parameter.This,
				States.VERIFY_CHECK, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.SIGN_CHECK, sign(icfg), Parameter.This, States.SIGN_CHECK,
				Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.SIGN_CHECK, verify(icfg), Parameter.This, States.ERROR,
				Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.SIGN_CHECK, update(icfg), Parameter.This,
				States.SIGN_CHECK, Type.OnReturn, icfg));

		addTransition(new MatcherTransition<ConcreteState>(States.VERIFY_CHECK, initSign(icfg), Parameter.This,
				States.SIGN_CHECK, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.VERIFY_CHECK, initVerify(icfg), Parameter.This,
				States.VERIFY_CHECK, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.VERIFY_CHECK, sign(icfg), Parameter.This, States.ERROR,
				Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.VERIFY_CHECK, verify(icfg), Parameter.This,
				States.VERIFY_CHECK, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.VERIFY_CHECK, update(icfg), Parameter.This,
				States.VERIFY_CHECK, Type.OnReturn, icfg));

		addTransition(new MatcherTransition<ConcreteState>(States.ERROR, initSign(icfg), Parameter.This, States.ERROR,
				Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.ERROR, initVerify(icfg), Parameter.This, States.ERROR,
				Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.ERROR, sign(icfg), Parameter.This, States.ERROR,
				Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.ERROR, verify(icfg), Parameter.This, States.ERROR,
				Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.ERROR, update(icfg), Parameter.This, States.ERROR,
				Type.OnReturn, icfg));
	}

	private Set<UpdatableWrapper<SootMethod>> constructor(IExtendedICFG<Unit, SootMethod> icfg) {
		Collection<UpdatableWrapper<SootClass>> subclasses = getSubclassesOf("java.security.Signature", icfg);
		Set<SootMethod> out = new HashSet<>();
		for (UpdatableWrapper<SootClass> c : subclasses) {
			for (SootMethod m : c.getContents().getMethods())
				if (m.isPublic() && m.getName().equals("getInstance"))
					out.add(m);
		}
		return icfg.wrap(out);
	}

	private Set<UpdatableWrapper<SootMethod>> verify(IExtendedICFG<Unit, SootMethod> icfg) {
		return selectMethodByName(getSubclassesOf("java.security.Signature", icfg), "verify", icfg);
	}

	private Set<UpdatableWrapper<SootMethod>> update(IExtendedICFG<Unit, SootMethod> icfg) {
		return selectMethodByName(getSubclassesOf("java.security.Signature", icfg), "update", icfg);
	}

	private Set<UpdatableWrapper<SootMethod>> sign(IExtendedICFG<Unit, SootMethod> icfg) {
		return selectMethodByName(getSubclassesOf("java.security.Signature", icfg), "sign", icfg);
	}

	private Set<UpdatableWrapper<SootMethod>> initSign(IExtendedICFG<Unit, SootMethod> icfg) {
		return selectMethodByName(getSubclassesOf("java.security.Signature", icfg), "initSign", icfg);
	}

	private Set<UpdatableWrapper<SootMethod>> initVerify(IExtendedICFG<Unit, SootMethod> icfg) {
		return selectMethodByName(getSubclassesOf("java.security.Signature", icfg), "initVerify", icfg);
	}

	@Override
	public Collection<UpdatableAccessGraph> generateSeed(UpdatableWrapper<SootMethod> m, UpdatableWrapper<Unit> unit,
			Collection<UpdatableWrapper<SootMethod>> calledMethod, IExtendedICFG<Unit, SootMethod> icfg) {
		for (UpdatableWrapper<SootMethod> cons : constructor(icfg)) {
			//TODO GetContents in order to compare
			if (Utils.getSootMethods(calledMethod).contains(cons.getContents())) {
				return getLeftSideOf(unit, icfg);
			}
		}
		return Collections.emptySet();
	}

	@Override
	public TypestateDomainValue<ConcreteState> getBottomElement() {
		return new TypestateDomainValue<ConcreteState>(States.NONE);
	}

	@Override
	public void updateTypeStateFunctions() {
		addTransition(new MatcherTransition<ConcreteState>(States.NONE, constructor(icfg), Parameter.This,
				States.UNITIALIZED, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.UNITIALIZED, initSign(icfg), Parameter.This,
				States.SIGN_CHECK, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.UNITIALIZED, initVerify(icfg), Parameter.This,
				States.VERIFY_CHECK, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.UNITIALIZED, sign(icfg), Parameter.This, States.ERROR,
				Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.UNITIALIZED, verify(icfg), Parameter.This, States.ERROR,
				Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.UNITIALIZED, update(icfg), Parameter.This, States.ERROR,
				Type.OnReturn, icfg));

		addTransition(new MatcherTransition<ConcreteState>(States.SIGN_CHECK, initSign(icfg), Parameter.This,
				States.SIGN_CHECK, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.SIGN_CHECK, initVerify(icfg), Parameter.This,
				States.VERIFY_CHECK, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.SIGN_CHECK, sign(icfg), Parameter.This, States.SIGN_CHECK,
				Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.SIGN_CHECK, verify(icfg), Parameter.This, States.ERROR,
				Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.SIGN_CHECK, update(icfg), Parameter.This,
				States.SIGN_CHECK, Type.OnReturn, icfg));

		addTransition(new MatcherTransition<ConcreteState>(States.VERIFY_CHECK, initSign(icfg), Parameter.This,
				States.SIGN_CHECK, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.VERIFY_CHECK, initVerify(icfg), Parameter.This,
				States.VERIFY_CHECK, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.VERIFY_CHECK, sign(icfg), Parameter.This, States.ERROR,
				Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.VERIFY_CHECK, verify(icfg), Parameter.This,
				States.VERIFY_CHECK, Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.VERIFY_CHECK, update(icfg), Parameter.This,
				States.VERIFY_CHECK, Type.OnReturn, icfg));

		addTransition(new MatcherTransition<ConcreteState>(States.ERROR, initSign(icfg), Parameter.This, States.ERROR,
				Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.ERROR, initVerify(icfg), Parameter.This, States.ERROR,
				Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.ERROR, sign(icfg), Parameter.This, States.ERROR,
				Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.ERROR, verify(icfg), Parameter.This, States.ERROR,
				Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.ERROR, update(icfg), Parameter.This, States.ERROR,
				Type.OnReturn, icfg));
	}
}
