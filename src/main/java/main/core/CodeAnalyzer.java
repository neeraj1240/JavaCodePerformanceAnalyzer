package main.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CodeAnalyzer {
    private static final int WARMUP_RUNS = 3;
    private static final int MEASUREMENT_RUNS = 5;

    private final CodeCompiler codeCompiler = new CodeCompiler();
    private final CodeExecutor codeExecutor = new CodeExecutor();
    private final InputGenerator inputGenerator = new InputGenerator();


    public String getGeneratedInput() {
        return inputGenerator.getGeneratedInput();
    }

    public String getExecutionOutput() {
        return codeExecutor.getExecutionOutput();
    }

    public String generateInput(String code, int size) {
        return inputGenerator.generateInput(code, size);
    }
    
    public String generateInput(String code, int size, String arrayType) {
        return inputGenerator.generateInput(code, size, arrayType);
    }

    public boolean hasMainMethod(String code) {
        return Pattern.compile("public\\s+static\\s+void\\s+main\\s*\\(\\s*String\\s*\\[\\s*\\]\\s*\\w+\\s*\\)")
                .matcher(code)
                .find();
    }

    public boolean hasHardcodedInput(String code) {
        String[] patterns = {
                "\\{\\s*\\d+\\s*,.*?\\}",
                "new\\s+(?:int|String|double|float|char)\\s*\\[\\s*\\]\\s*=\\s*\\{[^}]+\\}",
                "Arrays\\.asList\\([^)]+\\)",
                "List\\.of\\([^)]+\\)",
                "new\\s+ArrayList\\s*<[^>]*>\\s*\\(\\s*Arrays\\.asList\\([^)]+\\)\\)",
                "String\\s+\\w+\\s*=\\s*\"[^\"]+\"\\s*;",
                "int\\s+\\w+\\s*=\\s*\\d+\\s*;",
                "double\\s+\\w+\\s*=\\s*\\d+\\.?\\d*\\s*;"
        };

        for (String pattern : patterns) {
            if (Pattern.compile(pattern).matcher(code).find()) {
                return true;
            }
        }

        return code.contains("int[] arr") && code.contains("{") && code.contains("}");
    }

    public AnalysisResult analyzeCode(String code, String input) throws Exception {
        String className = codeCompiler.extractClassName(code);
        if (className == null) {
            throw new Exception("Could not find class name in the code.");
        }

        File tempDir = codeCompiler.createTempDirectory();
        File sourceFile = new File(tempDir, className + ".java");

        try {
            try (java.io.PrintWriter writer = new java.io.PrintWriter(sourceFile)) {
                writer.println(code);
            }

            String compilationError = codeCompiler.compileCode(sourceFile);
            if (compilationError != null) {
                throw new Exception("Compilation failed: " + compilationError);
            }

            if (input.startsWith("generate:")) {
                String[] parts = input.split(":", 3);
                if (parts.length == 3 && parts[0].equals("generate")) {
                    String inputType = parts[1];
                    String[] sizesStr = parts[2].split(",");
                    int[] sizes = new int[sizesStr.length];
                    for (int i = 0; i < sizesStr.length; i++) {
                        sizes[i] = Integer.parseInt(sizesStr[i]);
                    }
                    List<double[]> measurements = new ArrayList<>();
                    for (int size : sizes) {
                        String generatedInput = inputGenerator.generateInputForType(inputType, size);
                        inputGenerator.setGeneratedInput(generatedInput);
                        // Warm-up runs
                        for (int i = 0; i < WARMUP_RUNS; i++) {
                            codeExecutor.executeAndMeasure(tempDir, className, generatedInput);
                        }
                        double totalTime = 0;
                        double totalMemory = 0;
                        System.gc();
                        Thread.sleep(100);
                        for (int i = 0; i < MEASUREMENT_RUNS; i++) {
                            CodeExecutor.PerformanceMetrics metrics = codeExecutor.executeAndMeasure(tempDir, className, generatedInput);
                            totalTime += metrics.executionTime;
                            totalMemory += metrics.memoryUsed;
                        }
                        double avgTime = totalTime / MEASUREMENT_RUNS;
                        double avgMemory = totalMemory / MEASUREMENT_RUNS;
                        measurements.add(new double[]{size, avgTime, avgMemory});
                    }

                    double[] sizesArr = new double[measurements.size()];
                    double[] times = new double[measurements.size()];
                    double[] memories = new double[measurements.size()];
                    for (int i = 0; i < measurements.size(); i++) {
                        sizesArr[i] = measurements.get(i)[0];
                        times[i] = measurements.get(i)[1];
                        memories[i] = measurements.get(i)[2];
                    }
                    // Return result with last measurement and its size
                    double lastAvgTime = times[times.length - 1];
                    double lastAvgMemory = memories[memories.length - 1];
                    int lastSize = (int)sizesArr[sizesArr.length - 1];
                    return new AnalysisResult(lastAvgTime, lastAvgMemory, lastSize);
                } else {
                    throw new Exception("Invalid input format for detailed analysis");
                }
            } else {

                int inputSize = 0;
                if ("HARDCODED".equals(input)) {
                    inputGenerator.setGeneratedInput("Using hardcoded input from code");
                } else {
                    inputGenerator.setGeneratedInput(input);
                    // Try to determine input size from the input string
                    if (input.trim().split("\\s+").length > 1) {
                        try {
                            inputSize = Integer.parseInt(input.trim().split("\\s+")[0]);
                        } catch (NumberFormatException e) {
                            // If we can't parse the size, count the number of elements
                            inputSize = input.trim().split("\\s+").length;
                        }
                    }
                }

                for (int i = 0; i < WARMUP_RUNS; i++) {
                    codeExecutor.executeAndMeasure(tempDir, className, input);
                }
                double totalTime = 0;
                double totalMemory = 0;
                CodeExecutor.PerformanceMetrics lastMetrics = null;
                System.gc();
                Thread.sleep(100);
                for (int i = 0; i < MEASUREMENT_RUNS; i++) {
                    lastMetrics = codeExecutor.executeAndMeasure(tempDir, className, input);
                    totalTime += lastMetrics.executionTime;
                    totalMemory += lastMetrics.memoryUsed;
                }
                double avgTime = totalTime / MEASUREMENT_RUNS;
                double avgMemory = totalMemory / MEASUREMENT_RUNS;
                return new AnalysisResult(avgTime, avgMemory, inputSize);
            }
        } finally {
            codeCompiler.deleteDirectory(tempDir);
        }
    }
}
