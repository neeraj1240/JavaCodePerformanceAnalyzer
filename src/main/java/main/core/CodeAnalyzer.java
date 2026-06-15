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

    public boolean hasOutputStatement(String code) {
        return Pattern.compile("System\\s*\\.\\s*(?:out|err)\\s*\\.\\s*(?:print|println|printf)\\s*\\(")
                .matcher(stripComments(code))
                .find();
    }

    public boolean hasScannerInput(String code) {
        String codeWithoutComments = stripComments(code);
        return codeWithoutComments.contains("Scanner") &&
                Pattern.compile("\\.\\s*next(?:Int|Double|Line|Long|Float|Boolean|Byte|Short)?\\s*\\(")
                        .matcher(codeWithoutComments)
                        .find();
    }

    public boolean readsStandardInput(String code) {
        String codeWithoutComments = stripComments(code);
        return hasScannerInput(codeWithoutComments) ||
                codeWithoutComments.contains("System.in") ||
                codeWithoutComments.contains("InputStreamReader");
    }

    public boolean supportsGeneratedInput(String code) {
        String codeWithoutComments = stripComments(code);
        if (!hasScannerInput(codeWithoutComments)) {
            return false;
        }

        return inputGenerator.hasSingleStringInput(codeWithoutComments) ||
                codeWithoutComments.contains("matrix") ||
                codeWithoutComments.contains("[][]") ||
                Pattern.compile("\\.\\s*next(?:Int|Double)\\s*\\(")
                        .matcher(codeWithoutComments)
                        .find();
    }

    public boolean hasHardcodedInput(String code) {
        String codeWithoutComments = stripComments(code);
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
            if (Pattern.compile(pattern).matcher(codeWithoutComments).find()) {
                return true;
            }
        }

        return codeWithoutComments.contains("int[] arr") &&
                codeWithoutComments.contains("{") &&
                codeWithoutComments.contains("}");
    }

    private String stripComments(String code) {
        return code.replaceAll("(?s)/\\*.*?\\*/", "")
                .replaceAll("(?m)//.*$", "");
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
                    List<AnalysisResult> measurements = new ArrayList<>();
                    for (int size : sizes) {
                        String generatedInput = inputGenerator.generateInputForType(inputType, size);
                        inputGenerator.setGeneratedInput(generatedInput);
                        System.gc();
                        Thread.sleep(100);
                        CodeExecutor.PerformanceMetrics metrics = codeExecutor.executeAndMeasure(tempDir, className, generatedInput);
                        measurements.add(new AnalysisResult(metrics.executionTime, metrics.memoryUsed, size,
                                metrics.throughput, metrics.gcPauseTime, metrics.heapAllocationRate,
                                metrics.p50Latency, metrics.p95Latency, metrics.p99Latency));
                    }

                    // Return result with last measurement
                    return measurements.get(measurements.size() - 1);
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

                System.gc();
                Thread.sleep(100);
                CodeExecutor.PerformanceMetrics metrics = codeExecutor.executeAndMeasure(tempDir, className, input);
                return new AnalysisResult(metrics.executionTime, metrics.memoryUsed, inputSize,
                        metrics.throughput, metrics.gcPauseTime, metrics.heapAllocationRate,
                        metrics.p50Latency, metrics.p95Latency, metrics.p99Latency);
            }
        } finally {
            codeCompiler.deleteDirectory(tempDir);
        }
    }
}
