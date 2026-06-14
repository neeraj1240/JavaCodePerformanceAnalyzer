package main.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private static final String GRAPH_PATTERN = "(?s).*graph.*|.*adj.*list.*|.*edge.*|.*vertex.*";

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
        ITERATIVE,
        GRAPH
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

        if (Pattern.compile(GRAPH_PATTERN, Pattern.CASE_INSENSITIVE).matcher(code).find()) {
            return analyzeGraphComplexity(code);
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
            case GRAPH:
                return analyzeGraphComplexity(code);
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
        if (Pattern.compile(GRAPH_PATTERN).matcher(code).find()) {
            return AlgorithmPattern.GRAPH;
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

    private String analyzeGraphComplexity(String code) {
        if (code.contains("bfs") || code.contains("BFS")) {
            return "O(V + E)";
        }
        if (code.contains("dfs") || code.contains("DFS")) {
            return "O(V + E)";
        }
        if (code.contains("dijkstra") || code.contains("Dijkstra")) {
            return "O((V + E) log V)";
        }
        return "O(V^2)";
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

        if (Pattern.compile(GRAPH_PATTERN).matcher(code).find()) {
            return "O(V + E)";
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
