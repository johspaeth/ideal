package ideal;

import java.util.Map;
import java.util.Set;

import boomerang.accessgraph.AccessGraph;
import boomerang.incremental.UpdatableWrapper;
import heros.EdgeFunction;
import heros.EdgeFunctions;
import heros.Flow;
import heros.FlowFunctions;
import heros.IDETabulationProblem;
import heros.InterproceduralCFG;
import heros.JoinLattice;
import heros.edgefunc.AllTop;
import heros.solver.IPropagationController;
import heros.solver.Scheduler;
import ideal.debug.IDebugger;
import ideal.edgefunction.AnalysisEdgeFunctions;
import ideal.edgefunction.ForwardEdgeFunctions;
import ideal.flowfunctions.StandardFlowFunctions;
import ideal.pointsofaliasing.ReturnEvent;
import soot.SootMethod;
import soot.Unit;

public class InternalAnalysisProblem<V> implements
    IDETabulationProblem<UpdatableWrapper<Unit>, AccessGraph, UpdatableWrapper<SootMethod>, V, InterproceduralCFG<UpdatableWrapper<Unit>, UpdatableWrapper<SootMethod>>> {

  private InterproceduralCFG<UpdatableWrapper<Unit>, UpdatableWrapper<SootMethod>> icfg;
  private PerSeedAnalysisContext<V> context;
  private AnalysisEdgeFunctions<V> edgeFunctions;
  private IPropagationController<UpdatableWrapper<Unit>, AccessGraph> propagationController;
  private NonIdentityEdgeFlowHandler<V> nonIdentityEdgeFlowHandler;
  private StandardFlowFunctions<V> flowFunctions;
  public final static AccessGraph ZERO = new AccessGraph(null){
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
  public FlowFunctions<UpdatableWrapper<Unit>, AccessGraph, UpdatableWrapper<SootMethod>> flowFunctions() {
    return flowFunctions;
  }

  @Override
  public InterproceduralCFG<UpdatableWrapper<Unit>, UpdatableWrapper<SootMethod>> interproceduralCFG() {
    return icfg;
  }

  @Override
  public Map<UpdatableWrapper<Unit>, Set<AccessGraph>> initialSeeds() {
    return null;
  }

  @Override
  public AccessGraph zeroValue() {
    return ZERO;
  }

  @Override
  public EdgeFunctions<UpdatableWrapper<Unit>, AccessGraph, UpdatableWrapper<SootMethod>, V> edgeFunctions() {
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
	public Flow<UpdatableWrapper<Unit>, AccessGraph, V> flowWrapper() {
		return new Flow<UpdatableWrapper<Unit>,AccessGraph,V>(){


			@Override
			public void nonIdentityCallToReturnFlow( AccessGraph d2,UpdatableWrapper<Unit> callSite, AccessGraph d3, UpdatableWrapper<Unit> returnSite,
					AccessGraph d1, EdgeFunction<V> func) {
				//TODO search for aliases and update results.
				InternalAnalysisProblem.this.nonIdentityEdgeFlowHandler.onCallToReturnFlow(d2,callSite,d3,returnSite,d1,func);
			}

			@Override
			public void nonIdentityReturnFlow(UpdatableWrapper<Unit> exitStmt,AccessGraph d2, UpdatableWrapper<Unit> callSite, AccessGraph d3, UpdatableWrapper<Unit> returnSite,
					AccessGraph d1, EdgeFunction<V> func) {
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
	public IPropagationController<UpdatableWrapper<Unit>, AccessGraph> propagationController() {
		return propagationController;
	}

	@Override
	public void updateCFG(InterproceduralCFG<UpdatableWrapper<Unit>, UpdatableWrapper<SootMethod>> cfg) {
		this.icfg = cfg;
	}

}
