package ideal.pointsofaliasing;

import java.util.Collection;

import heros.incremental.UpdatableWrapper;
import heros.solver.PathEdge;
import ideal.PerSeedAnalysisContext;
import ideal.incremental.accessgraph.UpdatableAccessGraph;
import soot.Unit;

public interface PointOfAlias<V> {


	  /**
	   * Generates the path edges the given POA should generate.
	   */
	  public Collection<PathEdge<UpdatableWrapper<Unit>, UpdatableAccessGraph>> getPathEdges(
	      PerSeedAnalysisContext<V> tsanalysis);

	  /**
	   * Generates the path edges the given POA should generate.
	   */
	  public Collection<UpdatableAccessGraph> getIndirectFlowTargets(
	      PerSeedAnalysisContext<V> tsanalysis);

}
