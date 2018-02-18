package typestate;

import java.util.Collection;
import java.util.Set;

import boomerang.cfg.IExtendedICFG;
import heros.incremental.UpdatableWrapper;
import ideal.incremental.accessgraph.UpdatableAccessGraph;
import soot.SootMethod;
import soot.Unit;
import typestate.finiteautomata.Transition;

public interface TypestateChangeFunction<State> {
	Set<? extends Transition<State>> getReturnTransitionsFor(UpdatableAccessGraph callerD1, UpdatableWrapper<Unit> callSite, UpdatableWrapper<SootMethod> calleeMethod,
			UpdatableWrapper<Unit> exitStmt, UpdatableAccessGraph exitNode, UpdatableWrapper<Unit> returnSite, UpdatableAccessGraph retNode);

	Collection<UpdatableAccessGraph> generate(UpdatableWrapper<SootMethod> method, UpdatableWrapper<Unit> stmt, Collection<UpdatableWrapper<SootMethod>> optional, IExtendedICFG<Unit, SootMethod> icfg);

	Set<? extends Transition<State>> getCallTransitionsFor(UpdatableAccessGraph callerD1, UpdatableWrapper<Unit> callSite, UpdatableWrapper<SootMethod> calleeMethod,
			UpdatableAccessGraph srcNode, UpdatableAccessGraph destNode);

	Set<? extends Transition<State>> getCallToReturnTransitionsFor(UpdatableAccessGraph d1, UpdatableWrapper<Unit> callSite, UpdatableAccessGraph d2,
			UpdatableWrapper<Unit> returnSite, UpdatableAccessGraph d3);

	TypestateDomainValue<State> getBottomElement();
	
	void updateTypeStateFunctions();

}
