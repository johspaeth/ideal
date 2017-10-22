package ideal;

import boomerang.BoomerangOptions;
import boomerang.accessgraph.AccessGraph;
import boomerang.incremental.UpdatableWrapper;
import heros.EdgeFunction;
import heros.solver.IPropagationController;
import ideal.flowfunctions.StandardFlowFunctions;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.toolkits.ide.icfg.BiDiInterproceduralCFG;

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
		return 30;
	}
	
	@Override
	public IDEALScheduler<V> getScheduler() {
		return new IDEALScheduler<>();
	}
	
	@Override
	public IPropagationController<UpdatableWrapper<Unit>, AccessGraph> propagationController() {
		return new IPropagationController<UpdatableWrapper<Unit>,AccessGraph>(){

			@Override
			public boolean continuePropagate(AccessGraph d1, UpdatableWrapper<Unit> n, AccessGraph d2) {
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
			public void onCallToReturnFlow(AccessGraph d2, UpdatableWrapper<Unit> callSite, AccessGraph d3, UpdatableWrapper<Unit> returnSite,
					AccessGraph d1, EdgeFunction<V> func) {
			}

			@Override
			public void onReturnFlow(AccessGraph d2, UpdatableWrapper<Unit> callSite, AccessGraph d3, UpdatableWrapper<Unit> returnSite, AccessGraph d1,
					EdgeFunction<V> func) {
			}
		};
	}
	
	@Override
	public StandardFlowFunctions<V> flowFunctions(PerSeedAnalysisContext<V> context) {
		return new StandardFlowFunctions<>(context);
	}
}
