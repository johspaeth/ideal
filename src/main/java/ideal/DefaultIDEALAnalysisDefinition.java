package ideal;

import boomerang.BoomerangOptions;
import heros.BiDiInterproceduralCFG;
import heros.EdgeFunction;
import heros.incremental.UpdatableWrapper;
import heros.solver.IPropagationController;
import ideal.flowfunctions.StandardFlowFunctions;
import ideal.incremental.accessgraph.UpdatableAccessGraph;
import soot.SootMethod;
import soot.Unit;

public abstract class DefaultIDEALAnalysisDefinition<V> extends IDEALAnalysisDefinition<V> {
	@Override
	public BoomerangOptions boomerangOptions() {
		return new BoomerangOptions(){
			@Override
			public long getTimeBudget() {
				return 500;
			}
			@Override
			public boolean getTrackStaticFields() {
				return Analysis.ALIASING_FOR_STATIC_FIELDS;
			}
			@Override
			public BiDiInterproceduralCFG<Unit, SootMethod> icfg() {
				return DefaultIDEALAnalysisDefinition.this.icfg();
			}
		};
	}
	
	@Override
	public boolean enableAliasing() {
		return true;
	} 
	
	@Override
	public boolean enableNullPointOfAlias() {
		return false;
	}
	
	@Override
	public boolean enableStrongUpdates() {
		return true;
	}
	
	@Override
	public long analysisBudgetInSeconds() {
		return 30000;
	}
	
	@Override
	public IDEALScheduler<V> getScheduler() {
		return new IDEALScheduler<>();
	}
	
	@Override
	public IPropagationController<UpdatableWrapper<Unit>, UpdatableAccessGraph> propagationController() {
		return new IPropagationController<UpdatableWrapper<Unit>,UpdatableAccessGraph>(){

			@Override
			public boolean continuePropagate(UpdatableAccessGraph d1, UpdatableWrapper<Unit> n, UpdatableAccessGraph d2) {
				return true;
			}};
	}
	
	@Override
	public void onFinishWithSeed(IFactAtStatement seed, AnalysisSolver<V> solver) {
	}

	@Override
	public void onStartWithSeed(IFactAtStatement seed, AnalysisSolver<V> solver) {
	}
	@Override
	public NonIdentityEdgeFlowHandler<V> nonIdentityEdgeFlowHandler(){
		return new NonIdentityEdgeFlowHandler<V>() {
			@Override
			public void onCallToReturnFlow(UpdatableAccessGraph d2, UpdatableWrapper<Unit> callSite, UpdatableAccessGraph d3, UpdatableWrapper<Unit> returnSite,
					UpdatableAccessGraph d1, EdgeFunction<V> func) {
			}

			@Override
			public void onReturnFlow(UpdatableAccessGraph d2, UpdatableWrapper<Unit> callSite, UpdatableAccessGraph d3, UpdatableWrapper<Unit> returnSite, UpdatableAccessGraph d1,
					EdgeFunction<V> func) {
			}
		};
	}
	
	@Override
	public StandardFlowFunctions<V> flowFunctions(PerSeedAnalysisContext<V> context) {
		return new StandardFlowFunctions<>(context);
	}
}
