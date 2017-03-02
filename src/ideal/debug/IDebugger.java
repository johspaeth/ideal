package ideal.debug;

import java.util.Map;
import java.util.Set;

import boomerang.accessgraph.AccessGraph;
import boomerang.AliasResults;
import heros.EdgeFunction;
import heros.InterproceduralCFG;
import heros.solver.IDEDebugger;
import heros.solver.PathEdge;
import ideal.AnalysisSolver;
import ideal.flowfunctions.WrappedAccessGraph;
import ideal.pointsofaliasing.PointOfAlias;
import soot.SootMethod;
import soot.Unit;

public interface IDebugger<V> extends IDEDebugger<Unit, WrappedAccessGraph, SootMethod, V, InterproceduralCFG<Unit, SootMethod>> {

  void computedSeeds(Map<PathEdge<Unit, WrappedAccessGraph>, EdgeFunction<V>> seedToInitivalValue);

  void beforeAnalysis();

  void startWithSeed(PathEdge<Unit, WrappedAccessGraph> seed);

  void startPhase1WithSeed(PathEdge<Unit, WrappedAccessGraph> seed, AnalysisSolver<V> solver);


  void startPhase2WithSeed(PathEdge<Unit, WrappedAccessGraph> s, AnalysisSolver<V> solver);

  void finishPhase1WithSeed(PathEdge<Unit, WrappedAccessGraph> seed, AnalysisSolver<V> solver);

  void finishPhase2WithSeed(PathEdge<Unit, WrappedAccessGraph> s, AnalysisSolver<V> solver);

  void finishWithSeed(PathEdge<Unit, WrappedAccessGraph> seed, boolean timeout, boolean isInErrorState,
			AnalysisSolver<V> solver);

  void afterAnalysis();

  void startAliasPhase(Set<PointOfAlias<V>> pointsOfAlias);

  void startForwardPhase(Set<PathEdge<Unit, WrappedAccessGraph>> worklist);

  void onAliasesComputed(WrappedAccessGraph boomerangAccessGraph, Unit curr, WrappedAccessGraph d1,
      AliasResults res);

  void onAliasTimeout(WrappedAccessGraph boomerangAccessGraph, Unit curr, WrappedAccessGraph d1);

  void beforeAlias(WrappedAccessGraph boomerangAccessGraph, Unit curr, WrappedAccessGraph d1);

  void killAsOfStrongUpdate(WrappedAccessGraph d1, Unit callSite, WrappedAccessGraph callNode, Unit returnSite,
      WrappedAccessGraph returnSideNode2);

  void detectedStrongUpdate(Unit callSite, WrappedAccessGraph receivesUpdate);

  void onAnalysisTimeout(PathEdge<Unit, WrappedAccessGraph> seed);

  void solvePOA(PointOfAlias<V> p);

	void onNormalPropagation(WrappedAccessGraph d1, Unit curr, Unit succ, WrappedAccessGraph source);

	void indirectFlowAtWrite(WrappedAccessGraph source, Unit curr, WrappedAccessGraph target);

	void indirectFlowAtCall(WrappedAccessGraph source, Unit curr, WrappedAccessGraph target);


}
