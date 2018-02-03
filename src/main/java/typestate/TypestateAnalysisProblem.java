package typestate;

import java.util.Collection;

import boomerang.cfg.IExtendedICFG;
import heros.incremental.UpdatableWrapper;
import ideal.DefaultIDEALAnalysisDefinition;
import ideal.edgefunction.AnalysisEdgeFunctions;
import ideal.incremental.accessgraph.UpdatableAccessGraph;
import soot.SootMethod;
import soot.Unit;

public abstract class TypestateAnalysisProblem<State> extends DefaultIDEALAnalysisDefinition<TypestateDomainValue<State>> {
	private TypestateChangeFunction<State> func;

	@Override
	public AnalysisEdgeFunctions<TypestateDomainValue<State>> edgeFunctions() {
		return new TypestateEdgeFunctions<State>(getOrCreateTransitionFunctions());
	}

	private TypestateChangeFunction<State> getOrCreateTransitionFunctions() {
		if(func == null)
			func = createTypestateChangeFunction();
		return func;
	}

	public abstract TypestateChangeFunction<State> createTypestateChangeFunction();

	@Override
	public Collection<UpdatableAccessGraph> generate(UpdatableWrapper<SootMethod> method, UpdatableWrapper<Unit> stmt, Collection<UpdatableWrapper<SootMethod>> optional, IExtendedICFG<Unit, SootMethod> icfg) {
		return getOrCreateTransitionFunctions().generate(method, stmt, optional, icfg);
	}

}
