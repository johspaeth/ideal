package typestate.finiteautomata;

import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Pattern;

import boomerang.cfg.IExtendedICFG;
import heros.incremental.UpdatableWrapper;
import ideal.incremental.accessgraph.Utils;
import soot.MethodOrMethodContext;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.toolkits.callgraph.ReachableMethods;
import soot.util.queue.QueueReader;

public class MatcherTransition<State> extends Transition<State> {
	private Collection<UpdatableWrapper<SootMethod>> matchingMethods = new HashSet<>();
	private Type type;
	private Parameter param;

	public enum Type {
		OnCall, OnReturn, None, OnCallToReturn
	}

	public enum Parameter {
		This, Param1, Param2;
	}

	public MatcherTransition(State from, String methodMatcher, Parameter param, State to, Type type, IExtendedICFG<Unit, SootMethod> icfg) {
		super(from, to);
		this.type = type;
		this.param = param;
		ReachableMethods methods = Scene.v().getReachableMethods();
		QueueReader<MethodOrMethodContext> listener = methods.listener();
		Collection<SootMethod> tempMatchingMethods = new HashSet<>();
		while (listener.hasNext()) {
			MethodOrMethodContext next = listener.next();
			SootMethod method = next.method();
			if (Pattern.matches(methodMatcher, method.getSignature())) {
				tempMatchingMethods.add(method);
			}
		}
		matchingMethods = icfg.wrap(tempMatchingMethods);
	}

	public MatcherTransition(State from, Collection<UpdatableWrapper<SootMethod>> matchingMethods, Parameter param, State to, Type type, IExtendedICFG<Unit, SootMethod> icfg) {
		super(from, to);
		this.type = type;
		this.param = param;
		this.matchingMethods = matchingMethods;
	}

	public boolean matches(SootMethod method) {
		return Utils.getSootMethods(matchingMethods).contains(method);
	}

	public Type getType() {
		return type;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((matchingMethods == null) ? 0 : matchingMethods.hashCode());
		result = prime * result + ((param == null) ? 0 : param.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		MatcherTransition other = (MatcherTransition) obj;
		if (matchingMethods == null) {
			if (other.matchingMethods != null)
				return false;
		} else if (!matchingMethods.equals(other.matchingMethods))
			return false;
		if (param != other.param)
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	public Parameter getParam() {
		return param;
	}
}
