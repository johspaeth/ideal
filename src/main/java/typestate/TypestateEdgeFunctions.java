package typestate;

import java.util.Set;

import heros.EdgeFunction;
import heros.edgefunc.EdgeIdentity;
import heros.incremental.UpdatableWrapper;
import ideal.edgefunction.AnalysisEdgeFunctions;
import ideal.incremental.accessgraph.UpdatableAccessGraph;
import soot.SootMethod;
import soot.Unit;
import typestate.finiteautomata.Transition;

public class TypestateEdgeFunctions<State> implements AnalysisEdgeFunctions<TypestateDomainValue<State>> {

	private TypestateChangeFunction<State> func;

	public TypestateEdgeFunctions(TypestateChangeFunction<State> func) {
		this.func = func;
	}

	@Override
	public EdgeFunction<TypestateDomainValue<State>> getNormalEdgeFunction(UpdatableAccessGraph d1, UpdatableWrapper<Unit> curr, UpdatableAccessGraph currNode,
			UpdatableWrapper<Unit> succ, UpdatableAccessGraph succNode) {
		return EdgeIdentity.v();
	}

	@Override
	public EdgeFunction<TypestateDomainValue<State>> getCallEdgeFunction(UpdatableAccessGraph callerD1, UpdatableWrapper<Unit> callSite,
			UpdatableAccessGraph srcNode, UpdatableWrapper<SootMethod> calleeMethod, UpdatableAccessGraph destNode) {
		Set<? extends Transition<State>> trans = func.getCallTransitionsFor(callerD1, callSite, calleeMethod, srcNode,
				destNode);
		if (trans.isEmpty())
			return EdgeIdentity.v();
		return new TransitionFunction<State>(trans);
	}

	@Override
	public EdgeFunction<TypestateDomainValue<State>> getReturnEdgeFunction(UpdatableAccessGraph callerD1, UpdatableWrapper<Unit> callSite,
			UpdatableWrapper<SootMethod> calleeMethod, UpdatableWrapper<Unit> exitStmt, UpdatableAccessGraph exitNode, UpdatableWrapper<Unit> returnSite, UpdatableAccessGraph retNode) {

		Set<? extends Transition<State>> trans = func.getReturnTransitionsFor(callerD1, callSite, calleeMethod, exitStmt,
				exitNode, returnSite, retNode);
		if (trans.isEmpty())
			return EdgeIdentity.v();
		return new TransitionFunction<State>(trans);
	}

	@Override
	public EdgeFunction<TypestateDomainValue<State>> getCallToReturnEdgeFunction(UpdatableAccessGraph d1, UpdatableWrapper<Unit> callSite, UpdatableAccessGraph d2,
			UpdatableWrapper<Unit> returnSite, UpdatableAccessGraph d3) {
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

	@Override
	public void updateEdgeFunction() {
		func.updateTypeStateFunctions();
	}

}
