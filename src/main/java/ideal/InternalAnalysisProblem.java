package ideal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import heros.EdgeFunction;
import heros.EdgeFunctions;
import heros.Flow;
import heros.FlowFunctions;
import heros.IDETabulationProblem;
import heros.InterproceduralCFG;
import heros.JoinLattice;
import heros.edgefunc.AllTop;
import heros.incremental.UpdatableWrapper;
import heros.solver.IPropagationController;
import heros.solver.Scheduler;
import ideal.debug.IDebugger;
import ideal.edgefunction.AnalysisEdgeFunctions;
import ideal.edgefunction.ForwardEdgeFunctions;
import ideal.flowfunctions.StandardFlowFunctions;
import ideal.incremental.accessgraph.UpdatableAccessGraph;
import ideal.pointsofaliasing.ReturnEvent;
import soot.SootMethod;
import soot.Unit;

public class InternalAnalysisProblem<V> implements
IDETabulationProblem<UpdatableWrapper<Unit>, UpdatableAccessGraph, UpdatableWrapper<SootMethod>, V, InterproceduralCFG<UpdatableWrapper<Unit>, UpdatableWrapper<SootMethod>>> {

	private InterproceduralCFG<UpdatableWrapper<Unit>, UpdatableWrapper<SootMethod>> icfg;
	private PerSeedAnalysisContext<V> context;
	private AnalysisEdgeFunctions<V> edgeFunctions;
	private IPropagationController<UpdatableWrapper<Unit>, UpdatableAccessGraph> propagationController;
	private NonIdentityEdgeFlowHandler<V> nonIdentityEdgeFlowHandler;
	private StandardFlowFunctions<V> flowFunctions;
	public final static UpdatableAccessGraph ZERO = new UpdatableAccessGraph(null){
		public String toString(){
			return "{ZERO}";
		}
	};

	InternalAnalysisProblem(IDEALAnalysisDefinition<V> analysisDefinition, PerSeedAnalysisContext<V> context) {
		this.icfg = analysisDefinition.eIcfg();
		this.edgeFunctions = analysisDefinition.edgeFunctions();
		this.propagationController = analysisDefinition.propagationController();
		this.nonIdentityEdgeFlowHandler = analysisDefinition.nonIdentityEdgeFlowHandler();
		this.flowFunctions = analysisDefinition.flowFunctions(context);
		this.context = context;
	}

	@Override
	public boolean followReturnsPastSeeds() {
		return true;
	}

	@Override
	public boolean autoAddZero() {
		return false;
	}

	@Override
	public int numThreads() {
		return 1;
	}

	@Override
	public boolean computeValues() {
		return false;
	}

	@Override
	public FlowFunctions<UpdatableWrapper<Unit>, UpdatableAccessGraph, UpdatableWrapper<SootMethod>> flowFunctions() {
		return flowFunctions;
	}

	@Override
	public InterproceduralCFG<UpdatableWrapper<Unit>, UpdatableWrapper<SootMethod>> interproceduralCFG() {
		return icfg;
	}

	@Override
	public Map<UpdatableWrapper<Unit>, Set<UpdatableAccessGraph>> initialSeeds() {
		IFactAtStatement seed = context.getSeed();
		UpdatableWrapper<Unit> stmt = seed.getStmt();
		Set<UpdatableAccessGraph> factSet = new HashSet<>();
		factSet.add(seed.getFact());
		Map<UpdatableWrapper<Unit>, Set<UpdatableAccessGraph>> initialSeed = new HashMap<>();
		initialSeed.put(stmt, factSet);
		return initialSeed;
	}

	@Override
	public UpdatableAccessGraph zeroValue() {
		return ZERO;
	}

	@Override
	public EdgeFunctions<UpdatableWrapper<Unit>, UpdatableAccessGraph, UpdatableWrapper<SootMethod>, V> edgeFunctions() {
		return new ForwardEdgeFunctions<>(context, edgeFunctions);
	}

	@Override
	public JoinLattice<V> joinLattice() {
		return new JoinLattice<V>() {

			@Override
			public V topElement() {
				return edgeFunctions.top();
			}

			@Override
			public V bottomElement() {
				return edgeFunctions.bottom();
			}

			@Override
			public V join(V left, V right) {
				if (left == topElement() && right == topElement()) {
					return topElement();
				}
				if (left == bottomElement() && right == bottomElement()) {
					return bottomElement();
				}
				return edgeFunctions.join(left, right);
			}
		};
	}

	@Override
	public EdgeFunction<V> allTopFunction() {
		return new AllTop<V>(edgeFunctions.top());
	}

	@Override
	public boolean recordEdges() {
		return false;
	}

	public IDebugger<V> getDebugger() {
		return context.debugger();
	}

	@Override
	public Flow<UpdatableWrapper<Unit>, UpdatableAccessGraph, V> flowWrapper() {
		return new Flow<UpdatableWrapper<Unit>,UpdatableAccessGraph,V>(){


			@Override
			public void nonIdentityCallToReturnFlow( UpdatableAccessGraph d2,UpdatableWrapper<Unit> callSite, UpdatableAccessGraph d3, UpdatableWrapper<Unit> returnSite,
					UpdatableAccessGraph d1, EdgeFunction<V> func) {
				//TODO search for aliases and update results.
				InternalAnalysisProblem.this.nonIdentityEdgeFlowHandler.onCallToReturnFlow(d2,callSite,d3,returnSite,d1,func);
			}

			@Override
			public void nonIdentityReturnFlow(UpdatableWrapper<Unit> exitStmt,UpdatableAccessGraph d2, UpdatableWrapper<Unit> callSite, UpdatableAccessGraph d3, UpdatableWrapper<Unit> returnSite,
					UpdatableAccessGraph d1, EdgeFunction<V> func) {
				InternalAnalysisProblem.this.nonIdentityEdgeFlowHandler.onReturnFlow(d2,callSite,d3,returnSite,d1,func);
				if(!context.isInIDEPhase())
					context.addPOA(new ReturnEvent<V>(exitStmt,d2, callSite, d3, returnSite, d1, func));
			}};
	}

	@Override
	public Scheduler getScheduler() {
		return context.scheduler;
	}

	@Override
	public IPropagationController<UpdatableWrapper<Unit>, UpdatableAccessGraph> propagationController() {
		return propagationController;
	}

	@Override
	public void updateCFG(InterproceduralCFG<UpdatableWrapper<Unit>, UpdatableWrapper<SootMethod>> cfg) {
		this.icfg = cfg;
	}

}
