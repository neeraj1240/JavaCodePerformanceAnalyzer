package main.core;

public class AnalysisResult {
    private final double executionTime;
    private final double memoryUsed;
    private final String timeComplexity;
    private final String spaceComplexity;



    public AnalysisResult(double executionTime, double memoryUsed, String timeComplexity, String spaceComplexity) {
        this.executionTime = executionTime;
        this.memoryUsed = memoryUsed;
        this.timeComplexity = timeComplexity;
        this.spaceComplexity = spaceComplexity;
    }

    public double getExecutionTime() {
        return executionTime;
    }

    public double getMemoryUsed() {
        return memoryUsed;
    }

    public String getTimeComplexity() {
        return timeComplexity;
    }


    public String getSpaceComplexity() {
        return spaceComplexity;
    }


}