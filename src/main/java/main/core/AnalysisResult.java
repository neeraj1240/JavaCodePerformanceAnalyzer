package main.core;

public class AnalysisResult {
    private final double executionTime;
    private final double memoryUsed;
    private final int inputSize;
    private final double throughput;
    private final double gcPauseTime;
    private final double heapAllocationRate;
    private final double p50Latency;
    private final double p95Latency;
    private final double p99Latency;

    public AnalysisResult(double executionTime, double memoryUsed, int inputSize,
                          double throughput, double gcPauseTime, double heapAllocationRate,
                          double p50Latency, double p95Latency, double p99Latency) {
        this.executionTime = executionTime;
        this.memoryUsed = memoryUsed;
        this.inputSize = inputSize;
        this.throughput = throughput;
        this.gcPauseTime = gcPauseTime;
        this.heapAllocationRate = heapAllocationRate;
        this.p50Latency = p50Latency;
        this.p95Latency = p95Latency;
        this.p99Latency = p99Latency;
    }

    public double getExecutionTime() { return executionTime; }
    public double getMemoryUsed() { return memoryUsed; }
    public int getInputSize() { return inputSize; }
    public double getThroughput() { return throughput; }
    public double getGcPauseTime() { return gcPauseTime; }
    public double getHeapAllocationRate() { return heapAllocationRate; }
    public double getP50Latency() { return p50Latency; }
    public double getP95Latency() { return p95Latency; }
    public double getP99Latency() { return p99Latency; }
}
