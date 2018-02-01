package ideal.debug;

import java.util.Set;

import boomerang.AliasResults;
import boomerang.accessgraph.AccessGraph;
import heros.solver.PathEdge;
import ideal.AnalysisSolver;
import ideal.IFactAtStatement;
import ideal.incremental.accessgraph.UpdatableAccessGraph;
import ideal.pointsofaliasing.PointOfAlias;
import soot.SootMethod;
import soot.Unit;

public class NullDebugger<V> implements IDebugger<V> {

	@Override
	public void beforeAnalysis() {

	}

	@Override
	public void startPhase2WithSeed(IFactAtStatement seed, AnalysisSolver<V> solver) {
	}

	@Override
	public void finishWithSeed(PathEdge<Unit, UpdatableAccessGraph> seed, boolean timeout, boolean isInErrorState, AnalysisSolver<V> solver) {

	}

	@Override
	public void afterAnalysis() {

	}

	@Override
	public void startAliasPhase(Set<PointOfAlias<V>> pointsOfAlias) {

	}

	@Override
	public void startForwardPhase(Set<PathEdge<Unit, UpdatableAccessGraph>> worklist) {

	}

	@Override
	public void onAliasesComputed(UpdatableAccessGraph boomerangAccessGraph, Unit curr, UpdatableAccessGraph d1,
			AliasResults res) {

	}

	@Override
	public void onAliasTimeout(UpdatableAccessGraph boomerangAccessGraph, Unit curr, UpdatableAccessGraph d1) {

	}

	@Override
	public void beforeAlias(AccessGraph boomerangAccessGraph, Unit curr, AccessGraph d1) {

	}

	@Override
	public void killAsOfStrongUpdate(UpdatableAccessGraph d1, Unit callSite, UpdatableAccessGraph callNode,
			Unit returnSite, UpdatableAccessGraph returnSideNode2) {

	}

	@Override
	public void detectedStrongUpdate(Unit callSite, UpdatableAccessGraph receivesUpdate) {

	}

	@Override
	public void onAnalysisTimeout(IFactAtStatement seed) {

	}

	@Override
	public void solvePOA(PointOfAlias<V> p) {

	}

	@Override
	public void onNormalPropagation(UpdatableAccessGraph sourceFact, Unit curr, Unit succ,UpdatableAccessGraph d2) {

	}

	@Override
	public void addSummary(SootMethod methodToSummary, PathEdge<Unit, UpdatableAccessGraph> summary) {
		
	}

	@Override
	public void normalFlow(Unit start, UpdatableAccessGraph startFact, Unit target, UpdatableAccessGraph targetFact) {
		
	}

	@Override
	public void callFlow(Unit start, UpdatableAccessGraph startFact, Unit target, UpdatableAccessGraph targetFact) {
		
	}

	@Override
	public void callToReturn(Unit start, UpdatableAccessGraph startFact, Unit target, UpdatableAccessGraph targetFact) {
		
	}

	@Override
	public void returnFlow(Unit start, UpdatableAccessGraph startFact, Unit target, UpdatableAccessGraph targetFact) {
		
	}

	@Override
	public void setValue(Unit start, UpdatableAccessGraph startFact, V value) {
		
	}

	@Override
	public void indirectFlowAtWrite(UpdatableAccessGraph source, Unit curr, UpdatableAccessGraph target) {
		
	}

	@Override
	public void indirectFlowAtCall(UpdatableAccessGraph source, Unit curr, UpdatableAccessGraph target) {
		
	}

	@Override
	public void startWithSeed(IFactAtStatement seed) {
		
	}

	@Override
	public void startPhase1WithSeed(IFactAtStatement seed, AnalysisSolver<V> solver) {
	}

	@Override
	public void finishPhase1WithSeed(IFactAtStatement seed, AnalysisSolver<V> solver) {
		
	}

	@Override
	public void finishPhase2WithSeed(IFactAtStatement seed, AnalysisSolver<V> solver) {
		
	}

}
