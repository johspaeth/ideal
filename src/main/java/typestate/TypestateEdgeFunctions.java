package typestate;

import java.util.Set;

import boomerang.accessgraph.AccessGraph;
import heros.EdgeFunction;
import heros.edgefunc.EdgeIdentity;
import heros.incremental.UpdatableWrapper;
import ideal.edgefunction.AnalysisEdgeFunctions;
import soot.SootMethod;
import soot.Unit;
import typestate.finiteautomata.Transition;

public class TypestateEdgeFunctions<State> implements AnalysisEdgeFunctions<TypestateDomainValue<State>> {

	private TypestateChangeFunction<State> func;

	public TypestateEdgeFunctions(TypestateChangeFunction<State> func) {
		this.func = func;
	}

	@Override
	public EdgeFunction<TypestateDomainValue<State>> getNormalEdgeFunction(AccessGraph d1, UpdatableWrapper<Unit> curr, AccessGraph currNode,
			UpdatableWrapper<Unit> succ, AccessGraph succNode) {
		return EdgeIdentity.v();
	}

	@Override
	public EdgeFunction<TypestateDomainValue<State>> getCallEdgeFunction(AccessGraph callerD1, UpdatableWrapper<Unit> callSite,
			AccessGraph srcNode, UpdatableWrapper<SootMethod> calleeMethod, AccessGraph destNode) {
		Set<? extends Transition<State>> trans = func.getCallTransitionsFor(callerD1, callSite, calleeMethod, srcNode,
				destNode);
		if (trans.isEmpty())
			return EdgeIdentity.v();
		return new TransitionFunction<State>(trans);
	}

	@Override
	public EdgeFunction<TypestateDomainValue<State>> getReturnEdgeFunction(AccessGraph callerD1, UpdatableWrapper<Unit> callSite,
			UpdatableWrapper<SootMethod> calleeMethod, UpdatableWrapper<Unit> exitStmt, AccessGraph exitNode, UpdatableWrapper<Unit> returnSite, AccessGraph retNode) {

		Set<? extends Transition<State>> trans = func.getReturnTransitionsFor(callerD1, callSite, calleeMethod, exitStmt,
				exitNode, returnSite, retNode);
		if (trans.isEmpty())
			return EdgeIdentity.v();
		return new TransitionFunction<State>(trans);
	}

	@Override
	public EdgeFunction<TypestateDomainValue<State>> getCallToReturnEdgeFunction(AccessGraph d1, UpdatableWrapper<Unit> callSite, AccessGraph d2,
			UpdatableWrapper<Unit> returnSite, AccessGraph d3) {
		Set<? extends Transition<State>> trans = func.getCallToReturnTransitionsFor(d1, callSite, d2, returnSite, d3);
		if (trans.isEmpty())
			return EdgeIdentity.v();
		return new TransitionFunction<State>(trans);
	}

	@Override
	public TypestateDomainValue<State> bottom() {
		return func.getBottomElement();
	}

	@Override
	public TypestateDomainValue<State> top() {
		return TypestateDomainValue.top();
	}

	@Override
	public TypestateDomainValue<State> join(TypestateDomainValue<State> left, TypestateDomainValue<State> right) {
		if (left.equals(top()))
			return right;
		if (right.equals(top()))
			return left;
		Set<State> transitions = left.getStates();
		transitions.addAll(right.getStates());
		return new TypestateDomainValue<State>(transitions);
	}
	
	@Override
	public String toString() {
		return func.toString();
	}
}
