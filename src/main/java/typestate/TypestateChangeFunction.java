package typestate;

import java.util.Collection;
import java.util.Set;

import boomerang.accessgraph.AccessGraph;
import boomerang.incremental.UpdatableWrapper;
import soot.SootMethod;
import soot.Unit;
import typestate.finiteautomata.Transition;

public interface TypestateChangeFunction<State> {
	Set<? extends Transition<State>> getReturnTransitionsFor(AccessGraph callerD1, UpdatableWrapper<Unit> callSite, UpdatableWrapper<SootMethod> calleeMethod,
			UpdatableWrapper<Unit> exitStmt, AccessGraph exitNode, UpdatableWrapper<Unit> returnSite, AccessGraph retNode);

	Collection<AccessGraph> generate(UpdatableWrapper<SootMethod> method, UpdatableWrapper<Unit> stmt, Collection<UpdatableWrapper<SootMethod>> optional);

	Set<? extends Transition<State>> getCallTransitionsFor(AccessGraph callerD1, UpdatableWrapper<Unit> callSite, UpdatableWrapper<SootMethod> calleeMethod,
			AccessGraph srcNode, AccessGraph destNode);

	Set<? extends Transition<State>> getCallToReturnTransitionsFor(AccessGraph d1, UpdatableWrapper<Unit> callSite, AccessGraph d2,
			UpdatableWrapper<Unit> returnSite, AccessGraph d3);

	TypestateDomainValue<State> getBottomElement();

}
