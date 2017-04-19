package typestate;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Table.Cell;

import boomerang.BoomerangContext;
import boomerang.accessgraph.AccessGraph;
import heros.EdgeFunction;
import heros.solver.Pair;
import heros.solver.PathEdge;
import ideal.AnalysisProblem;
import ideal.AnalysisSolver;
import ideal.InternalAnalysisProblem;
import ideal.edgefunction.AnalysisEdgeFunctions;
import soot.MethodOrMethodContext;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.toolkits.callgraph.ReachableMethods;
import soot.util.queue.QueueReader;
import typestate.finiteautomata.State;
import typestate.finiteautomata.Transition;

public class TypestateAnalysisProblem implements AnalysisProblem<TypestateDomainValue> {

  private TypestateChangeFunction func;
  private Set<Cell<SootMethod,AccessGraph,TypestateDomainValue>> errorPathEdges = new HashSet<>();
  private ResultCollection<TypestateDomainValue> endingPathsOfPropagation = new ResultCollection<>(new Join<TypestateDomainValue>(){

	@Override
	public TypestateDomainValue join(TypestateDomainValue t1, TypestateDomainValue t2) {
		Set<State> transitions = t1.getStates();
		Set<State> transitions2 = t2.getStates();
		transitions.addAll(transitions2);
		return new TypestateDomainValue(transitions);
	}});

  public TypestateAnalysisProblem(TypestateChangeFunction func) {
    this.func = func;
  }

  @Override
  public AnalysisEdgeFunctions<TypestateDomainValue> edgeFunctions() {
    return new TypestateEdgeFunctions(func);
  }
 
  @Override
	public void onAnalysisFinished(PathEdge<Unit, AccessGraph> seed,
			AnalysisSolver<TypestateDomainValue> solver) {
	errorPathEdges.clear();
	endingPathsOfPropagation.clear();
    ReachableMethods rm = Scene.v().getReachableMethods();
    QueueReader<MethodOrMethodContext> listener = rm.listener();
    while (listener.hasNext()) {
      MethodOrMethodContext next = listener.next();
      SootMethod method = next.method();
      if (!method.hasActiveBody())
        continue;

      
      Collection<Unit> endPointsOf = solver.icfg().getEndPointsOf(method);

      for (Unit eP : endPointsOf) {
        Set<AccessGraph> localsAtEndPoint = new HashSet<>();
        for (Cell<AccessGraph, AccessGraph, EdgeFunction<TypestateDomainValue>> cell : solver
            .getPathEdgesAt(eP)) {
          if (!cell.getRowKey().equals(InternalAnalysisProblem.ZERO)) {
            continue;
          }
          localsAtEndPoint.add(cell.getColumnKey());
        }
        boolean escapes = false;
        for (AccessGraph ag : localsAtEndPoint) {
          if (BoomerangContext.isParameterOrThisValue(method, ag.getBase())) {
            escapes = true;
          }
        }
        if (!escapes) {
          Map<AccessGraph, TypestateDomainValue> resultAt = solver.resultsAt(eP);
          for (Entry<AccessGraph, TypestateDomainValue> fact : resultAt.entrySet()) {
            if (localsAtEndPoint.contains(fact.getKey())) {
              if (!fact.getValue().equals(solver.bottom()))
                endingPathsOfPropagation
                    .put(method, fact.getKey(), fact.getValue());
            }
          }
        }

      }
    }
    for (Cell<SootMethod, AccessGraph, TypestateDomainValue> res : endingPathsOfPropagation) {
      if (res.getValue().endsInErrorState()) {
        errorPathEdges.add(res);
      }
    }
  }

  @Override
  public Collection<Pair<AccessGraph, EdgeFunction<TypestateDomainValue>>> generate(SootMethod method, Unit stmt,
      Collection<SootMethod> optional) {
    return func.generate(method,stmt, optional);
  }

  public Set<Cell<SootMethod, AccessGraph, TypestateDomainValue>> getErrors() {
    return errorPathEdges;
  }

  public ResultCollection<TypestateDomainValue> getPathEdgesAtEndOfMethods() {
    return endingPathsOfPropagation;
  }

  @Override
  public boolean isInErrorState() {
	  System.out.println(errorPathEdges);
    return !errorPathEdges.isEmpty();
  }

}
