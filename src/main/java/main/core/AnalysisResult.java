package main.core;

public class AnalysisResult {
    private final double executionTime;
    private final double memoryUsed;
    private final int inputSize;

    public AnalysisResult(double executionTime, double memoryUsed, int inputSize) {
        this.executionTime = executionTime;
        this.memoryUsed = memoryUsed;
        this.inputSize = inputSize;
    }

    public double getExecutionTime() {
        return executionTime;
    }

    public double getMemoryUsed() {
        return memoryUsed;
    }

    public int getInputSize() {
        return inputSize;
    }
}
