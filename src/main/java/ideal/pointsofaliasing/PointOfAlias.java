package ideal.pointsofaliasing;

import java.util.Collection;

import boomerang.accessgraph.AccessGraph;
import heros.incremental.UpdatableWrapper;
import heros.solver.PathEdge;
import ideal.PerSeedAnalysisContext;
import soot.Unit;

public interface PointOfAlias<V> {


	  /**
	   * Generates the path edges the given POA should generate.
	   */
	  public Collection<PathEdge<UpdatableWrapper<Unit>, AccessGraph>> getPathEdges(
	      PerSeedAnalysisContext<V> tsanalysis);

	  /**
	   * Generates the path edges the given POA should generate.
	   */
	  public Collection<AccessGraph> getIndirectFlowTargets(
	      PerSeedAnalysisContext<V> tsanalysis);

}
