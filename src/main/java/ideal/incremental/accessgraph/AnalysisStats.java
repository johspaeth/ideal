package ideal.incremental.accessgraph;

import java.util.concurrent.TimeUnit;

public class AnalysisStats {
	
	private long updateRunTime;
	private long changeSetComputationTime;
	private long computeRunTime;
	private long computePropagationCount;
	private long updatePropagationCount;
	
	public AnalysisStats(long updateRunTime, long changeSetComputationTime, long computeRunTime,
			long computePropagationCount, long updatePropagationCount) {
		this.updateRunTime = updateRunTime;
		this.changeSetComputationTime = changeSetComputationTime;
		this.computeRunTime = computeRunTime;
		this.computePropagationCount = computePropagationCount;
		this.updatePropagationCount = updatePropagationCount;
	}
	
	public AnalysisStats() {
		
	}

	public long getComputePropagationCount() {
		return computePropagationCount;
	}
	
	public void setComputePropagationCount(long computePropagationCount) {
		this.computePropagationCount = computePropagationCount;
	}
	
	public long getUpdatePropagationCount() {
		return updatePropagationCount;
	}
	
	public void setUpdatePropagationCount(long updatePropagationCount) {
		this.updatePropagationCount = updatePropagationCount;
	}

	public long getComputeRunTime() {
//		return TimeUnit.MILLISECONDS.convert(computeRunTime, TimeUnit.NANOSECONDS);
		return computeRunTime;
	}
	public void setComputeRunTime(long computeRunTime) {
		this.computeRunTime = computeRunTime;
	}
	public long getUpdateRunTime() {
//		return TimeUnit.MILLISECONDS.convert(updateRunTime, TimeUnit.NANOSECONDS);
		return updateRunTime;
	}
	public void setUpdateRunTime(long updateRunTime) {
		this.updateRunTime = updateRunTime;
	}
	public long getChangeSetComputationTime() {
//		return TimeUnit.MILLISECONDS.convert(changeSetComputationTime, TimeUnit.NANOSECONDS);
		return changeSetComputationTime;
	}
	public void setChangeSetComputationTime(long changeSetComputationTime) {
		this.changeSetComputationTime = changeSetComputationTime;
	}
	
	@Override
	public String toString() {
//		return TimeUnit.MILLISECONDS.convert(computeRunTime, TimeUnit.NANOSECONDS) + ", " + computePropagationCount + ", " + TimeUnit.MILLISECONDS.convert(changeSetComputationTime, TimeUnit.NANOSECONDS) + ", " + TimeUnit.MILLISECONDS.convert(updateRunTime, TimeUnit.NANOSECONDS) + ", " + updatePropagationCount + ", " + (computePropagationCount - updatePropagationCount);
		return computeRunTime + ", " + computePropagationCount + ", " + changeSetComputationTime + ", " + updateRunTime + ", " + updatePropagationCount + ", " + (computePropagationCount - updatePropagationCount);
	}

}
