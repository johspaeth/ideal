package ideal.debug;

import java.util.Set;

import boomerang.AliasResults;
import boomerang.accessgraph.AccessGraph;
import heros.InterproceduralCFG;
import heros.solver.IDEDebugger;
import heros.solver.PathEdge;
import ideal.AnalysisSolver;
import ideal.IFactAtStatement;
import ideal.incremental.accessgraph.UpdatableAccessGraph;
import ideal.pointsofaliasing.PointOfAlias;
import soot.SootMethod;
import soot.Unit;

public interface IDebugger<V>
		extends IDEDebugger<Unit, UpdatableAccessGraph, SootMethod, V, InterproceduralCFG<Unit, SootMethod>> {

	void beforeAnalysis();

	void startWithSeed(IFactAtStatement seed);

	void startPhase1WithSeed(IFactAtStatement seed, AnalysisSolver<V> solver);

	void startPhase2WithSeed(IFactAtStatement seed, AnalysisSolver<V> solver);

	void finishPhase1WithSeed(IFactAtStatement seed, AnalysisSolver<V> solver);

	void finishPhase2WithSeed(IFactAtStatement seed, AnalysisSolver<V> solver);

	void finishWithSeed(PathEdge<Unit, UpdatableAccessGraph> seed, boolean timeout, boolean isInErrorState,
			AnalysisSolver<V> solver);

	void afterAnalysis();

	void startAliasPhase(Set<PointOfAlias<V>> pointsOfAlias);

	void startForwardPhase(Set<PathEdge<Unit, UpdatableAccessGraph>> worklist);

	void onAliasesComputed(UpdatableAccessGraph boomerangAccessGraph, Unit curr, UpdatableAccessGraph d1, AliasResults res);

	void onAliasTimeout(UpdatableAccessGraph boomerangAccessGraph, Unit curr, UpdatableAccessGraph d1);

	void beforeAlias(AccessGraph boomerangAccessGraph, Unit curr, AccessGraph d1);

	void killAsOfStrongUpdate(UpdatableAccessGraph d1, Unit callSite, UpdatableAccessGraph callNode, Unit returnSite,
			UpdatableAccessGraph returnSideNode2);

	void detectedStrongUpdate(Unit callSite, UpdatableAccessGraph receivesUpdate);

	void onAnalysisTimeout(IFactAtStatement seed);

	void solvePOA(PointOfAlias<V> p);

	void onNormalPropagation(UpdatableAccessGraph d1, Unit curr, Unit succ, UpdatableAccessGraph source);

	void indirectFlowAtWrite(UpdatableAccessGraph source, Unit curr, UpdatableAccessGraph target);

	void indirectFlowAtCall(UpdatableAccessGraph source, Unit curr, UpdatableAccessGraph target);

}