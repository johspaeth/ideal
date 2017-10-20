package ideal.debug;

import java.util.Set;

import boomerang.AliasResults;
import boomerang.accessgraph.AccessGraph;
import heros.solver.PathEdge;
import ideal.AnalysisSolver;
import ideal.IFactAtStatement;
import ideal.pointsofaliasing.PointOfAlias;
import soot.SootMethod;
import soot.Unit;

public class IDEDebugger<V> implements IDebugger<V>{

	@Override
	public void addSummary(SootMethod methodToSummary, PathEdge<Unit, AccessGraph> summary) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void normalFlow(Unit start, AccessGraph startFact, Unit target, AccessGraph targetFact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void callFlow(Unit start, AccessGraph startFact, Unit target, AccessGraph targetFact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void callToReturn(Unit start, AccessGraph startFact, Unit target, AccessGraph targetFact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void returnFlow(Unit start, AccessGraph startFact, Unit target, AccessGraph targetFact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setValue(Unit start, AccessGraph startFact, V value) {
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
	public void finishWithSeed(PathEdge<Unit, AccessGraph> seed, boolean timeout, boolean isInErrorState,
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
	public void startForwardPhase(Set<PathEdge<Unit, AccessGraph>> worklist) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAliasesComputed(AccessGraph boomerangAccessGraph, Unit curr, AccessGraph d1, AliasResults res) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAliasTimeout(AccessGraph boomerangAccessGraph, Unit curr, AccessGraph d1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeAlias(AccessGraph boomerangAccessGraph, Unit curr, AccessGraph d1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void killAsOfStrongUpdate(AccessGraph d1, Unit callSite, AccessGraph callNode, Unit returnSite,
			AccessGraph returnSideNode2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void detectedStrongUpdate(Unit callSite, AccessGraph receivesUpdate) {
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
	public void onNormalPropagation(AccessGraph d1, Unit curr, Unit succ, AccessGraph source) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void indirectFlowAtWrite(AccessGraph source, Unit curr, AccessGraph target) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void indirectFlowAtCall(AccessGraph source, Unit curr, AccessGraph target) {
		// TODO Auto-generated method stub
		
	}

}
