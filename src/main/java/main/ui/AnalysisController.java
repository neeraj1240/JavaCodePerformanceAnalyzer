package main.ui;

import javafx.application.Platform;
import javafx.concurrent.Task;
import main.core.AnalysisResult;
import main.core.CodeAnalyzer;

import java.util.ArrayList;
import java.util.List;

public class AnalysisController {
    private final InputPane inputPane;
    private final ResultPane resultPane;
    
    private final CodeAnalyzer analyzer;
    private final List<Double> executionTimes = new ArrayList<>();
    private final List<Double> memoryUsages = new ArrayList<>();
    private final List<Integer> inputSizes = new ArrayList<>();
    private final List<Double> throughputs = new ArrayList<>();
    private final List<Double> gcPauseTimes = new ArrayList<>();
    private final List<Double> heapAllocationRates = new ArrayList<>();
    private final List<Double> p50Latencies = new ArrayList<>();
    private final List<Double> p95Latencies = new ArrayList<>();
    private final List<Double> p99Latencies = new ArrayList<>();
    private final GraphManager graphManager;
    
    private String currentInput = "";
    private String currentOutput = "";

    private DataDisplayWindow inputDataWindow;
    private DataDisplayWindow outputDataWindow;

    public AnalysisController(InputPane inputPane, ResultPane resultPane) {
        this.inputPane = inputPane;
        this.resultPane = resultPane;
        
        this.analyzer = new CodeAnalyzer();
        this.graphManager = new GraphManager(executionTimes, memoryUsages, inputSizes,
                                             throughputs, gcPauseTimes, heapAllocationRates,
                                             p50Latencies, p95Latencies, p99Latencies);

        this.inputDataWindow = new DataDisplayWindow("Input Data");
        this.outputDataWindow = new DataDisplayWindow("Output Data");

        setupCallbacks();
    }

    private void setupCallbacks() {
        inputPane.setOnAnalyze(this::handleAnalyze);
        inputPane.setOnClear(this::handleClear);

        resultPane.setOnShowInputData(this::showInputData);
        resultPane.setOnShowOutputData(this::showOutputData);
        
        resultPane.setOnShowTimeGraph(() -> {
            try {
                graphManager.showTimeGraph();
            } catch (IllegalStateException ex) {
                UIUtils.showError("No data available for time graph.");
            }
        });
        
        resultPane.setOnShowMemoryGraph(() -> {
            try {
                graphManager.showMemoryGraph();
            } catch (IllegalStateException ex) {
                UIUtils.showError("No data available for memory graph.");
            }
        });

        resultPane.setOnShowThroughputGraph(() -> {
            try {
                graphManager.showThroughputGraph();
            } catch (IllegalStateException ex) {
                UIUtils.showError("No data available for throughput graph.");
            }
        });

        resultPane.setOnShowGcPauseTimeGraph(() -> {
            try {
                graphManager.showGcPauseTimeGraph();
            } catch (IllegalStateException ex) {
                UIUtils.showError("No data available for GC pause time graph.");
            }
        });

        resultPane.setOnShowHeapAllocationRateGraph(() -> {
            try {
                graphManager.showHeapAllocationRateGraph();
            } catch (IllegalStateException ex) {
                UIUtils.showError("No data available for heap allocation rate graph.");
            }
        });

        resultPane.setOnShowLatencyGraph(() -> {
            try {
                graphManager.showLatencyGraph();
            } catch (IllegalStateException ex) {
                UIUtils.showError("No data available for latency graph.");
            }
        });
    }

    private void handleClear() {
        inputPane.clearCode();
        inputPane.clearInputSize();
        resultPane.displayDefaultResults();
        resultPane.setAnalyzing(false, null);
        
        executionTimes.clear();
        memoryUsages.clear();
        inputSizes.clear();
        throughputs.clear();
        gcPauseTimes.clear();
        heapAllocationRates.clear();
        p50Latencies.clear();
        p95Latencies.clear();
        p99Latencies.clear();
        currentInput = "";
        currentOutput = "";
    }

    // Removed showTimeGraph and showMemoryGraph

    private void showInputData() {
        inputDataWindow.showData(currentInput);
    }

    private void showOutputData() {
        outputDataWindow.showData(currentOutput);
    }

    private void handleAnalyze() {
        String code = inputPane.getCode();
        if (code.isEmpty()) {
            UIUtils.showError("Please enter code to analyze.");
            return;
        }

        if (!analyzer.hasMainMethod(code)) {
            UIUtils.showError("No main method found! Please add a main method to your code.");
            return;
        }

        if (inputPane.isRangeInput()) {
            analyzeCodeWithRange(code);
            return;
        }

        String manualInput = inputPane.getManualInputText();
        String inputSizeText = inputPane.getInputSizeText();

        try {
            final String finalInput;
            final int finalInputSize;

            if (inputPane.isManualInput()) {
                if (manualInput.isEmpty()) {
                    UIUtils.showError("Please enter manual input data.");
                    return;
                }
                finalInput = manualInput;
                finalInputSize = 0;
            } else if (inputPane.isRandomInput()) {
                int inputSize = Integer.parseInt(inputSizeText);
                if (inputSize <= 0) {
                    UIUtils.showError("Input size must be greater than 0.");
                    return;
                }
                if (inputSize > 100000) {
                    UIUtils.showError("Input size cannot exceed 100,000");
                    return;
                }
                String arrayType = "random";
                switch (inputPane.getArrayType()) {
                    case "Sorted":
                    case "Sorted Array":
                        arrayType = "sorted";
                        break;
                    case "Nearly Sorted":
                    case "Nearly Sorted Array":
                        arrayType = "nearly-sorted";
                        break;
                    default:
                        arrayType = "random";
                }
                finalInput = analyzer.generateInput(code, inputSize, arrayType);
                finalInputSize = inputSize;
            } else {
                if (!analyzer.hasHardcodedInput(code)) {
                    UIUtils.showError("No hardcoded input found in the code. Please use hardcoded values or choose a different input type.");
                    return;
                }
                finalInput = "HARDCODED";
                finalInputSize = -1;
            }

            clearPreviousData();
            resultPane.setAnalyzing(true, "Analyzing...");

            Task<AnalysisResult> analysisTask = new Task<AnalysisResult>() {
                @Override
                protected AnalysisResult call() throws Exception {
                    AnalysisResult result = analyzer.analyzeCode(code, finalInput);
                    Platform.runLater(() -> {
                        currentInput = finalInput;
                        currentOutput = analyzer.getExecutionOutput();
                    });
                    return result;
                }
            };

            analysisTask.setOnSucceeded(e -> {
                resultPane.setAnalyzing(false, null);
                AnalysisResult result = analysisTask.getValue();
                executionTimes.add(result.getExecutionTime());
                memoryUsages.add(result.getMemoryUsed());
                inputSizes.add(finalInputSize);
                throughputs.add(result.getThroughput());
                gcPauseTimes.add(result.getGcPauseTime());
                heapAllocationRates.add(result.getHeapAllocationRate());
                p50Latencies.add(result.getP50Latency());
                p95Latencies.add(result.getP95Latency());
                p99Latencies.add(result.getP99Latency());
                resultPane.displayResults(result);
            });

            analysisTask.setOnFailed(e -> {
                resultPane.setAnalyzing(false, null);
                Throwable exception = analysisTask.getException();
                UIUtils.showError(exception.getMessage());
            });

            new Thread(analysisTask).start();

        } catch (NumberFormatException e) {
            UIUtils.showError("Please enter a valid input size.");
        } catch (Exception e) {
            UIUtils.showError(e.getMessage());
        }
    }

    private void analyzeCodeWithRange(String code) {
        try {
            int minSize = Integer.parseInt(inputPane.getMinSizeText());
            int maxSize = Integer.parseInt(inputPane.getMaxSizeText());
            int stepSize = Integer.parseInt(inputPane.getStepSizeText());

            if (minSize <= 0 || maxSize <= 0 || stepSize <= 0) {
                UIUtils.showError("All size values must be greater than 0.");
                return;
            }

            if (minSize >= maxSize) {
                UIUtils.showError("Maximum size must be greater than minimum size.");
                return;
            }

            if (maxSize > 100000) {
                UIUtils.showError("Maximum size cannot exceed 100,000");
                return;
            }

            if (stepSize >= (maxSize - minSize)) {
                UIUtils.showError("Step size must be smaller than the range between min and max size.");
                return;
            }

            clearPreviousData();
            resultPane.setAnalyzing(true, "Analyzing...");

            Task<List<AnalysisResult>> analysisTask = new Task<List<AnalysisResult>>() {
                @Override
                protected List<AnalysisResult> call() throws Exception {
                    List<AnalysisResult> results = new ArrayList<>();
                    for (int currentSize = minSize; currentSize <= maxSize; currentSize += stepSize) {
                        final int finalSize = currentSize; 
                        updateMessage(String.format("Analyzing size: %d", finalSize));
                        String input = analyzer.generateInput(code, finalSize);
                        AnalysisResult result = analyzer.analyzeCode(code, input);
                        results.add(result);

                        Platform.runLater(() -> {
                            executionTimes.add(result.getExecutionTime());
                            memoryUsages.add(result.getMemoryUsed());
                            inputSizes.add(finalSize);
                            throughputs.add(result.getThroughput());
                            gcPauseTimes.add(result.getGcPauseTime());
                            heapAllocationRates.add(result.getHeapAllocationRate());
                            p50Latencies.add(result.getP50Latency());
                            p95Latencies.add(result.getP95Latency());
                            p99Latencies.add(result.getP99Latency());
                        });
                    }
                    currentInput = analyzer.getGeneratedInput();
                    return results;
                }
            };

            analysisTask.messageProperty().addListener((obs, oldMsg, newMsg) -> {
                resultPane.setAnalyzing(true, newMsg);
            });

            analysisTask.setOnSucceeded(e -> {
                resultPane.setAnalyzing(false, null);
                List<AnalysisResult> results = analysisTask.getValue();
                if (!results.isEmpty()) {
                    AnalysisResult lastResult = results.get(results.size() - 1);
                    currentOutput = analyzer.getExecutionOutput();
                    resultPane.displayResults(lastResult);
                }
            });

            analysisTask.setOnFailed(e -> {
                resultPane.setAnalyzing(false, null);
                Throwable exception = analysisTask.getException();
                UIUtils.showError(exception.getMessage());
            });

            new Thread(analysisTask).start();

        } catch (NumberFormatException e) {
            UIUtils.showError("Please enter valid numbers for min size, max size, and step size.");
        } catch (Exception e) {
            UIUtils.showError(e.getMessage());
        }
    }

    private void clearPreviousData() {
        executionTimes.clear();
        memoryUsages.clear();
        inputSizes.clear();
        throughputs.clear();
        gcPauseTimes.clear();
        heapAllocationRates.clear();
        p50Latencies.clear();
        p95Latencies.clear();
        p99Latencies.clear();
    }
}
