package typestate.impl.statemachines;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import boomerang.cfg.IExtendedICFG;
import heros.incremental.UpdatableWrapper;
import ideal.incremental.accessgraph.UpdatableAccessGraph;
import ideal.incremental.accessgraph.Utils;
import soot.Local;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.AssignStmt;
import test.ConcreteState;
import typestate.TypestateChangeFunction;
import typestate.TypestateDomainValue;
import typestate.finiteautomata.MatcherStateMachine;
import typestate.finiteautomata.MatcherTransition;
import typestate.finiteautomata.MatcherTransition.Parameter;
import typestate.finiteautomata.MatcherTransition.Type;

public class KeyStoreStateMachine extends MatcherStateMachine<ConcreteState> implements TypestateChangeFunction<ConcreteState> {

	IExtendedICFG<Unit, SootMethod> icfg;

	public static enum States implements ConcreteState {
		NONE, INIT, LOADED, ERROR;

		@Override
		public boolean isErrorState() {
			return this == ERROR;
		}

	}

	public KeyStoreStateMachine(IExtendedICFG<Unit, SootMethod> icfg) {
		this.icfg = icfg;
		// addTransition(new MatcherTransition(States.NONE,
		// keyStoreConstructor(),Parameter.This, States.INIT, Type.OnReturn));
		addTransition(new MatcherTransition<ConcreteState>(States.INIT, loadMethods(icfg), Parameter.This, States.LOADED, Type.OnReturn, icfg));

		addTransition(new MatcherTransition<ConcreteState>(States.INIT, anyMethodOtherThanLoad(icfg), Parameter.This, States.ERROR,
				Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.ERROR, anyMethodOtherThanLoad(icfg), Parameter.This, States.ERROR,
				Type.OnReturn, icfg));

	}

	private Set<UpdatableWrapper<SootMethod>> anyMethodOtherThanLoad(IExtendedICFG<Unit, SootMethod> icfg) {
		Collection<UpdatableWrapper<SootClass>> subclasses = getSubclassesOf("java.security.KeyStore", icfg);
		Set<UpdatableWrapper<SootMethod>> loadMethods = loadMethods(icfg);
		Set<SootMethod> out = new HashSet<>();
		for (UpdatableWrapper<SootClass> c : subclasses) {
			for (SootMethod m : c.getContents().getMethods())
				if (m.isPublic() && !Utils.getSootMethods(loadMethods).contains(m) && !m.isStatic())
					out.add(m);
		}
		return icfg.wrap(out);
	}

	private Set<UpdatableWrapper<SootMethod>> loadMethods(IExtendedICFG<Unit, SootMethod> icfg) {
		return selectMethodByName(getSubclassesOf("java.security.KeyStore", icfg), "load", icfg);
	}

	private Set<UpdatableWrapper<SootMethod>> keyStoreConstructor(IExtendedICFG<Unit, SootMethod> icfg) {
		Collection<UpdatableWrapper<SootClass>> subclasses = getSubclassesOf("java.security.KeyStore", icfg);
		Set<SootMethod> out = new HashSet<>();
		for (UpdatableWrapper<SootClass> c : subclasses) {
			for (SootMethod m : c.getContents().getMethods())
				if (m.getName().equals("getInstance") && m.isStatic())
					out.add(m);
		}
		return icfg.wrap(out);
	}

	@Override
	public Collection<UpdatableAccessGraph> generateSeed(UpdatableWrapper<SootMethod> m, UpdatableWrapper<Unit> unit, Collection<UpdatableWrapper<SootMethod>> calledMethod, IExtendedICFG<Unit, SootMethod> icfg) {
		if (unit.getContents() instanceof AssignStmt) {
			AssignStmt stmt = (AssignStmt) unit;
			if(stmt.containsInvokeExpr()){
				if(Utils.getSootMethods(keyStoreConstructor(icfg)).contains(stmt.getInvokeExpr().getMethod())){
					Set<UpdatableAccessGraph> out = new HashSet<>();
					out.add(new UpdatableAccessGraph(icfg.wrap((Local) stmt.getLeftOp())));
					return out;
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public TypestateDomainValue<ConcreteState> getBottomElement() {
		return new TypestateDomainValue<ConcreteState>(States.INIT);
	}

	@Override
	public void updateTypeStateFunctions() {
		// addTransition(new MatcherTransition(States.NONE,
		// keyStoreConstructor(),Parameter.This, States.INIT, Type.OnReturn));
		addTransition(new MatcherTransition<ConcreteState>(States.INIT, loadMethods(icfg), Parameter.This, States.LOADED, Type.OnReturn, icfg));

		addTransition(new MatcherTransition<ConcreteState>(States.INIT, anyMethodOtherThanLoad(icfg), Parameter.This, States.ERROR,
				Type.OnReturn, icfg));
		addTransition(new MatcherTransition<ConcreteState>(States.ERROR, anyMethodOtherThanLoad(icfg), Parameter.This, States.ERROR,
				Type.OnReturn, icfg));		
	}

}
