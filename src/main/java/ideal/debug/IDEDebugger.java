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

public class IDEDebugger<V> implements IDebugger<V>{

	@Override
	public void addSummary(SootMethod methodToSummary, PathEdge<Unit, UpdatableAccessGraph> summary) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void normalFlow(Unit start, UpdatableAccessGraph startFact, Unit target, UpdatableAccessGraph targetFact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void callFlow(Unit start, UpdatableAccessGraph startFact, Unit target, UpdatableAccessGraph targetFact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void callToReturn(Unit start, UpdatableAccessGraph startFact, Unit target, UpdatableAccessGraph targetFact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void returnFlow(Unit start, UpdatableAccessGraph startFact, Unit target, UpdatableAccessGraph targetFact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setValue(Unit start, UpdatableAccessGraph startFact, V value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeAnalysis() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startWithSeed(IFactAtStatement seed) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startPhase1WithSeed(IFactAtStatement seed, AnalysisSolver<V> solver) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startPhase2WithSeed(IFactAtStatement seed, AnalysisSolver<V> solver) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void finishPhase1WithSeed(IFactAtStatement seed, AnalysisSolver<V> solver) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void finishPhase2WithSeed(IFactAtStatement seed, AnalysisSolver<V> solver) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void finishWithSeed(PathEdge<Unit, UpdatableAccessGraph> seed, boolean timeout, boolean isInErrorState,
			AnalysisSolver<V> solver) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterAnalysis() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startAliasPhase(Set<PointOfAlias<V>> pointsOfAlias) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startForwardPhase(Set<PathEdge<Unit, UpdatableAccessGraph>> worklist) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAliasesComputed(UpdatableAccessGraph boomerangAccessGraph, Unit curr, UpdatableAccessGraph d1, AliasResults res) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAliasTimeout(UpdatableAccessGraph boomerangAccessGraph, Unit curr, UpdatableAccessGraph d1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeAlias(AccessGraph boomerangAccessGraph, Unit curr, AccessGraph d1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void killAsOfStrongUpdate(UpdatableAccessGraph d1, Unit callSite, UpdatableAccessGraph callNode, Unit returnSite,
			UpdatableAccessGraph returnSideNode2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void detectedStrongUpdate(Unit callSite, UpdatableAccessGraph receivesUpdate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnalysisTimeout(IFactAtStatement seed) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void solvePOA(PointOfAlias<V> p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNormalPropagation(UpdatableAccessGraph d1, Unit curr, Unit succ, UpdatableAccessGraph source) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void indirectFlowAtWrite(UpdatableAccessGraph source, Unit curr, UpdatableAccessGraph target) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void indirectFlowAtCall(UpdatableAccessGraph source, Unit curr, UpdatableAccessGraph target) {
		// TODO Auto-generated method stub
		
	}

}
