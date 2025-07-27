package main.core;

import java.io.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.regex.Matcher;



public class CodeAnalyzer {
    private static final int WARMUP_RUNS = 3;
    private static final int MEASUREMENT_RUNS = 5;
    private final Random random = new Random(42);

    private String generatedInput = "";
    private String executionOutput = "";

    public String getGeneratedInput() {
        return generatedInput;
    }

    public String getExecutionOutput() {
        return executionOutput;
    }

    String[] COMMON_WORDS = {"the","of","and","a","to","in","is","you","that",
            "it","he","was","for","on","are","as","with","his","they","I","at",
            "be","this","have","from","or","one","had","by","word","but","not",
            "what","all","were","we","when","your","can","said","there","use",
            "an","each","which","she","do","how","their","if","will","up","other",
            "about","out","many","then","them","these","so","some","her","would",
            "make","like","him","into","time","has","look","two","more","write","go",
            "see","number","no","way","could","people","my","than","first","water",
            "been","call","who","oil","its","now","find","long","down","day","did",
            "get","come","made","may","part"};


    public boolean hasMainMethod(String code) {
        return Pattern.compile("public\\s+static\\s+void\\s+main\\s*\\(\\s*String\\s*\\[\\s*\\]\\s*\\w+\\s*\\)")
                .matcher(code)
                .find();
    }


    public AnalysisResult analyzeCode(String code, String input) throws Exception {

        if ("HARDCODED".equals(input)) {
            this.generatedInput = "Using hardcoded input from code";
        }


        String className = extractClassName(code);
        if (className == null) {
            throw new Exception("Could not find class name in the code.");
        }

        File tempDir = createTempDirectory();
        File sourceFile = new File(tempDir, className + ".java");

        try {

            try (PrintWriter writer = new PrintWriter(sourceFile)) {
                writer.println(code);
            }


            String compilationError = compileCode(sourceFile);
            if (compilationError != null) {
                throw new Exception("Compilation failed: " + compilationError);
            }


            for (int i = 0; i < WARMUP_RUNS; i++) {
                executeAndMeasure(tempDir, className, input);
            }


            double totalTime = 0;
            double totalMemory = 0;
            PerformanceMetrics lastMetrics = null;

            System.gc();
            Thread.sleep(100);

            for (int i = 0; i < MEASUREMENT_RUNS; i++) {
                lastMetrics = executeAndMeasure(tempDir, className, input);
                totalTime += lastMetrics.executionTime;
                totalMemory += lastMetrics.memoryUsed;
            }


            double avgTime = totalTime / MEASUREMENT_RUNS;
            double avgMemory = totalMemory / MEASUREMENT_RUNS;

            ComplexityAnalyzer a = new ComplexityAnalyzer();
            String timeComplexity = a.analyzeTimeComplexity(code);
            String spaceComplexity = a.analyzeSpaceComplexity(code);

            return new AnalysisResult(avgTime, avgMemory, timeComplexity, spaceComplexity);

        } finally {

            deleteDirectory(tempDir);
        }
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








    public class ComplexityAnalyzer {


        private static final String LOOP_PATTERN = "for\\s*\\(|while\\s*\\(";
        private static final String BINARY_SEARCH_PATTERN = "mid\\s*=|middle\\s*=|/\\s*2";
        private static final String DIVIDE_PATTERN = "divide|split|partition|mid";
        private static final String RECURSIVE_PATTERN = "\\w+\\s*\\(.*\\)\\s*\\{";
        private static final String RECURSIVE_CALL_PATTERN = "\\w+\\s*\\(.*,?\\s*[\\w+\\s*[-+]?\\s*1],?.*\\)";
        private static final String BACKTRACK_PATTERN = "(?s).*\\bfor\\b.*\\{.*\\breturn\\b.*\\|\\|.*\\}.*";
        private static final String MATRIX_PATTERN = "\\[\\]\\[\\]";
        private static final String DYNAMIC_PROGRAMMING_PATTERN = "dp\\[.*\\]\\[.*\\]|memo\\[.*\\]\\[.*\\]";
        private static final String MERGE_SORT_PATTERN = "(?s).*merge.*sort.*|.*sort.*merge.*";
        private static final String HEAP_PATTERN = "heap\\s*(sort|ify)|priority.*queue";
        private static final String QUICK_SORT_PATTERN = "(?s).*quick.*sort.*|.*partition.*pivot.*";
        private static final String BINARY_TREE_PATTERN = "(?s).*tree\\.*(insert|search|delete).*|.*BST.*|.*binary.*search.*tree.*";


        private static final String QUEUE_PATTERN = "Queue|PriorityQueue|Deque|ArrayDeque";
        private static final String STACK_PATTERN = "Stack|push\\s*\\(|pop\\s*\\(";
        private static final String TREE_PATTERN = "TreeNode|TreeMap|TreeSet";
        private static final String BST_PATTERN = "BinarySearchTree|BST";


        private static final String ARRAYS_SORT_PATTERN = "Arrays\\.sort\\s*\\(";
        private static final String COLLECTIONS_SORT_PATTERN = "Collections\\.sort\\s*\\(";
        private static final String BINARY_SEARCH_METHOD_PATTERN = "Arrays\\.binarySearch\\s*\\(";
        private static final String LIST_ADD_PATTERN = "\\.add\\s*\\(";
        private static final String LIST_REMOVE_PATTERN = "\\.remove\\s*\\(";
        private static final String MAP_PUT_PATTERN = "\\.put\\s*\\(";
        private static final String MAP_GET_PATTERN = "\\.get\\s*\\(";
        private static final String QUEUE_OPERATIONS = "offer|poll|peek";
        private static final String STACK_OPERATIONS = "push|pop|peek";

        private enum AlgorithmPattern {
            BACKTRACKING,
            DYNAMIC_PROGRAMMING,
            DIVIDE_AND_CONQUER,
            RECURSIVE,
            ITERATIVE
        }

        public String analyzeTimeComplexity(String code) {

            if (Pattern.compile(MERGE_SORT_PATTERN, Pattern.CASE_INSENSITIVE).matcher(code).find() ||
                    Pattern.compile(HEAP_PATTERN, Pattern.CASE_INSENSITIVE).matcher(code).find() ||
                    Pattern.compile(QUICK_SORT_PATTERN, Pattern.CASE_INSENSITIVE).matcher(code).find()) {
                return "O(n log n)";
            }


            if (Pattern.compile(BINARY_TREE_PATTERN, Pattern.CASE_INSENSITIVE).matcher(code).find() ||
                    (Pattern.compile(BINARY_SEARCH_PATTERN).matcher(code).find() &&
                            !Pattern.compile(LOOP_PATTERN).matcher(code).find())) {
                return "O(log n)";
            }


            AlgorithmPattern pattern = identifyAlgorithmPattern(code);

            switch (pattern) {
                case BACKTRACKING:
                    return analyzeBacktrackingComplexity(code);
                case DYNAMIC_PROGRAMMING:
                    return analyzeDPComplexity(code);
                case DIVIDE_AND_CONQUER:
                    return analyzeDivideAndConquerComplexity(code);
                case RECURSIVE:
                    return analyzeRecursiveComplexity(code);
                default:
                    return analyzeIterativeComplexity(code);
            }
        }

        private AlgorithmPattern identifyAlgorithmPattern(String code) {
            if (Pattern.compile(BACKTRACK_PATTERN).matcher(code).find()) {
                return AlgorithmPattern.BACKTRACKING;
            }
            if (Pattern.compile(DYNAMIC_PROGRAMMING_PATTERN).matcher(code).find()) {
                return AlgorithmPattern.DYNAMIC_PROGRAMMING;
            }
            if (Pattern.compile(DIVIDE_PATTERN).matcher(code).find() &&
                    Pattern.compile(RECURSIVE_PATTERN).matcher(code).find()) {
                return AlgorithmPattern.DIVIDE_AND_CONQUER;
            }
            if (Pattern.compile(RECURSIVE_PATTERN).matcher(code).find()) {
                return AlgorithmPattern.RECURSIVE;
            }
            return AlgorithmPattern.ITERATIVE;
        }

        private String analyzeBacktrackingComplexity(String code) {
            int decisionPoints = countDecisionPoints(code);


            if (code.contains("board[") && decisionPoints > 1) {
                return "O(N!)";
            }

            return "O(" + decisionPoints + "^N)";
        }

        private String analyzeDPComplexity(String code) {
            int dimensions = countDPDimensions(code);
            return "O(N^" + dimensions + ")";
        }

        private String analyzeDivideAndConquerComplexity(String code) {
            if (code.contains("merge") || code.contains("partition")) {
                return "O(n log n)";
            }
            return "O(log n)";
        }

        private String analyzeRecursiveComplexity(String code) {
            if (Pattern.compile(RECURSIVE_CALL_PATTERN).matcher(code).find()) {
                return "O(2^n)";
            }
            return "O(n)";
        }

        private String analyzeIterativeComplexity(String code) {
            int maxLoopDepth = countNestedLoops(code);
            return maxLoopDepth > 1 ? "O(n^" + maxLoopDepth + ")" : "O(n)";
        }

        public String analyzeSpaceComplexity(String code) {

            if (Pattern.compile(TREE_PATTERN).matcher(code).find() ||
                    Pattern.compile(BST_PATTERN).matcher(code).find()) {
                return "O(n)";
            }


            if (Pattern.compile(HEAP_PATTERN).matcher(code).find()) {
                return "O(n)";
            }


            if (Pattern.compile(MATRIX_PATTERN).matcher(code).find()) {
                return "O(n^2)";
            }


            if (Pattern.compile(ARRAYS_SORT_PATTERN).matcher(code).find()) {
                if (code.contains("mergeSort") || code.contains("MergeSort")) {
                    return "O(n)";
                }
                if (code.contains("quickSort") || code.contains("QuickSort")) {
                    return "O(log n)";
                }
                if (code.contains("heapSort") || code.contains("HeapSort")) {
                    return "O(1)";
                }
                return "O(log n)";
            }


            if (Pattern.compile(QUEUE_PATTERN).matcher(code).find() ||
                    Pattern.compile(STACK_PATTERN).matcher(code).find()) {
                return "O(n)";
            }


            int dataStructureCount = countDataStructures(code);
            if (dataStructureCount > 1) {
                return "O(n)";
            }

            return "O(1)";
        }

        private int countDecisionPoints(String code) {
            int count = 0;
            Matcher loopMatcher = Pattern.compile(LOOP_PATTERN).matcher(code);
            while (loopMatcher.find()) {
                if (!isUtilityLoop(code.substring(loopMatcher.start()))) {
                    count++;
                }
            }
            return count;
        }

        private boolean isUtilityLoop(String loopCode) {
            return loopCode.contains("System.out.print") ||
                    loopCode.contains("Arrays.fill") ||
                    Pattern.compile("\\[\\]\\s*=\\s*new").matcher(loopCode).find();
        }

        private int countNestedLoops(String code) {
            int maxDepth = 0;
            int currentDepth = 0;

            for (int i = 0; i < code.length(); i++) {
                if (code.startsWith("for", i) || code.startsWith("while", i)) {
                    currentDepth++;
                    maxDepth = Math.max(maxDepth, currentDepth);
                } else if (code.charAt(i) == '}') {
                    currentDepth = Math.max(0, currentDepth - 1);
                }
            }
            return maxDepth;
        }

        private int countDPDimensions(String code) {
            Matcher matcher = Pattern.compile("\\[.*?\\]").matcher(code);
            int maxDimensions = 0;
            while (matcher.find()) {
                maxDimensions++;
            }
            return Math.max(1, maxDimensions / 2);
        }

        private int countDataStructures(String code) {
            Pattern pattern = Pattern.compile(
                    "(new\\s+[A-Za-z]+\\s*\\[|ArrayList|LinkedList|HashMap|HashSet|Queue|Stack|TreeMap|TreeSet|PriorityQueue)"
            );
            Matcher matcher = pattern.matcher(code);
            int count = 0;
            while (matcher.find()) {
                count++;
            }
            return count;
        }

        private boolean containsIndexOperation(String code) {
            return Pattern.compile("\\.(add|remove)\\s*\\(\\s*\\d+\\s*,").matcher(code).find();
        }
    }









    private String extractClassName(String code) {

        Pattern pattern = Pattern.compile("public\\s+class\\s+(\\w+)");
        Matcher matcher = pattern.matcher(code);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private File createTempDirectory() throws IOException {
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "codeanalysis_" + System.currentTimeMillis());
        if (!tempDir.mkdir()) {
            throw new IOException("Failed to create temporary directory");
        }
        return tempDir;
    }

    private String compileCode(File sourceFile) throws IOException {
        ProcessBuilder compileBuilder = new ProcessBuilder(
                "javac",
                sourceFile.getAbsolutePath()
        );
        compileBuilder.redirectErrorStream(true);
        Process compileProcess = compileBuilder.start();


        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(compileProcess.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        try {
            int exitCode = compileProcess.waitFor();
            if (exitCode != 0) {
                return output.toString();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Compilation interrupted", e);
        }

        return null;
    }

    public String generateInput(String code, int size) {
        StringBuilder input = new StringBuilder();

        if (hasSingleStringInput(code)) {

            StringBuilder sentence = new StringBuilder();
            for (int i = 0; i < size; i++) {
                sentence.append(COMMON_WORDS[random.nextInt(COMMON_WORDS.length)]);
                if (i < size - 1) {
                    sentence.append(" ");
                }
            }
            input.append(sentence);
        } else if (code.contains("Scanner")) {

            if (code.contains("matrix") || code.contains("[][]")) {
                int matrixDim = Math.min(size, 100);


                input.append(matrixDim).append("\n");
                input.append(matrixDim).append("\n");


                for (int i = 0; i < matrixDim; i++) {
                    for (int j = 0; j < matrixDim; j++) {
                        input.append(random.nextInt(10)).append(" ");
                    }
                    input.append("\n");
                }
                for (int i = 0; i < matrixDim; i++) {
                    for (int j = 0; j < matrixDim; j++) {
                        input.append(random.nextInt(10)).append(" ");
                    }
                    input.append("\n");
                }
            } else {

                Pattern arrayPattern = Pattern.compile("(\\w+)\\s*=\\s*new\\s+(\\w+)\\s*\\[");
                Matcher arrayMatcher = arrayPattern.matcher(code);
                int arrayCount = 0;
                while (arrayMatcher.find()) {
                    arrayCount++;
                }


                if (arrayCount > 1) {

                    input.append(size).append("\n");

                    for (int i = 0; i < size; i++) {
                        input.append(random.nextInt(100)).append(" ");
                    }
                    input.append("\n");


                    input.append(size).append("\n");

                    for (int i = 0; i < size; i++) {
                        input.append(random.nextInt(100)).append(" ");
                    }
                    input.append("\n");
                } else {

                    Pattern nextIntPattern = Pattern.compile("nextInt\\(\\)");
                    Matcher nextIntMatcher = nextIntPattern.matcher(code);
                    int nextIntCount = 0;
                    while (nextIntMatcher.find()) {
                        nextIntCount++;
                    }

                    Pattern nextDoublePattern = Pattern.compile("nextDouble\\(\\)");
                    Matcher nextDoubleMatcher = nextDoublePattern.matcher(code);
                    int nextDoubleCount = 0;
                    while (nextDoubleMatcher.find()) {
                        nextDoubleCount++;
                    }

                    if (nextIntCount > 0 || nextDoubleCount > 0) {
                        input.append(size).append("\n");
                        for (int i = 0; i < size; i++) {
                            for (int j = 0; j < nextIntCount - 1; j++) {
                                input.append(random.nextInt(100)).append(" ");
                            }
                            for (int j = 0; j < nextDoubleCount; j++) {
                                input.append(random.nextDouble() * 100).append(" ");
                            }
                            input.append("\n");
                        }
                    }
                }
            }
        }

        this.generatedInput = input.toString();
        return input.toString();
    }




    private boolean hasSingleStringInput(String code) {
        return (code.contains("scanner.nextLine()") ||
                code.contains("Scanner") && code.contains("nextLine()")) &&
                !code.contains("nextInt()") &&
                !code.contains("nextDouble()") &&
                !code.contains("next()");
    }





    private boolean isNumericInputRequired(String code) {

        boolean hasStringIndicators = code.contains("String ") ||
                code.contains("nextLine()") ||
                code.contains("charAt(") ||
                code.contains("length()");


        if (hasStringIndicators) {
            return false;
        }


        return code.contains("parseInt") ||
                code.contains("Integer") ||
                code.contains("int ") ||
                code.contains("nextInt()");
    }


    private PerformanceMetrics executeAndMeasure(File directory, String className, String input) throws Exception {
        ProcessBuilder runBuilder = new ProcessBuilder(
                "java",
                "-XX:+UseSerialGC",
                "-Xms64m",
                "-Xmx512m",
                "-cp",
                directory.getAbsolutePath(),
                className
        );
        runBuilder.redirectErrorStream(true);


        for (int i = 0; i < 3; i++) {
            System.gc();
            Thread.sleep(50);
        }

        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        Thread.sleep(100);

        long startMemory = Math.max(0, runtime.totalMemory() - runtime.freeMemory());
        long startTime = System.nanoTime();

        Process runProcess = runBuilder.start();
        StringBuilder output = new StringBuilder();


        Thread inputThread = new Thread(() -> {
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(runProcess.getOutputStream()))) {
                String[] inputLines = input.split("\n");
                for (String line : inputLines) {
                    writer.write(line);
                    writer.newLine();
                    writer.flush();
                    Thread.sleep(10);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Thread outputThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(runProcess.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        inputThread.start();
        outputThread.start();


        inputThread.join();
        outputThread.join();


        if (!runProcess.waitFor(10, TimeUnit.SECONDS)) {
            runProcess.destroyForcibly();
            throw new Exception("Process timed out");
        }

        int exitCode = runProcess.exitValue();
        if (exitCode != 0) {
            throw new Exception("Program execution failed with exit code " + exitCode + ": " + output);
        }

        this.executionOutput = output.toString();

        long endTime = System.nanoTime();
        runtime.gc();
        Thread.sleep(100);
        long endMemory = Math.max(0, runtime.totalMemory() - runtime.freeMemory());
        long memoryUsed = Math.max(0, endMemory - startMemory);

        return new PerformanceMetrics(
                (endTime - startTime) / 1_000_000.0,
                memoryUsed
        );
    }



    private void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
        directory.delete();
    }

    private static class PerformanceMetrics {
        final double executionTime;
        final double memoryUsed;

        PerformanceMetrics(double executionTime, double memoryUsed) {
            this.executionTime = executionTime;
            this.memoryUsed = memoryUsed;
        }
    }
}




