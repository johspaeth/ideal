package ideal.pointsofaliasing;

import java.util.Collection;

import boomerang.accessgraph.AccessGraph;
import heros.solver.PathEdge;
import ideal.ap.PerSeedAnalysisContext;
import soot.Unit;

public interface PointOfAlias<V> {


	  /**
	   * Generates the path edges the given POA should generate.
	   */
	  public Collection<PathEdge<Unit, AccessGraph>> getPathEdges(
	      PerSeedAnalysisContext<V> tsanalysis);

	  /**
	   * Generates the path edges the given POA should generate.
	   */
	  public Collection<AccessGraph> getIndirectFlowTargets(
	      PerSeedAnalysisContext<V> tsanalysis);

}
