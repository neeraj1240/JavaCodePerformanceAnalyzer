package org.example;

import junit.framework.TestCase;
import main.core.CodeAnalyzer;
import main.core.AnalysisResult;

public class PerformanceTest extends TestCase {
    
    public void testBubbleSortAnalysis() throws Exception {
        String code = 
            "public class BubbleSort {\n" +
            "    public static void main(String[] args) {\n" +
            "        java.util.Scanner sc = new java.util.Scanner(System.in);\n" +
            "        if (!sc.hasNextInt()) return;\n" +
            "        int n = sc.nextInt();\n" +
            "        int[] arr = new int[n];\n" +
            "        for (int i = 0; i < n; i++) {\n" +
            "            if (sc.hasNextInt()) arr[i] = sc.nextInt();\n" +
            "        }\n" +
            "        for (int i = 0; i < n; i++) {\n" +
            "            for (int j = 0; j < n - 1; j++) {\n" +
            "                if (arr[j] > arr[j+1]) {\n" +
            "                    int temp = arr[j];\n" +
            "                    arr[j] = arr[j+1];\n" +
            "                    arr[j+1] = temp;\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "}\n";

        CodeAnalyzer analyzer = new CodeAnalyzer();
        long startTime = System.currentTimeMillis();
        
        // Let's run a single analysis with size 100
        System.out.println("Starting analysis of BubbleSort...");
        AnalysisResult result = analyzer.analyzeCode(code, "generate:array:100");
        long duration = System.currentTimeMillis() - startTime;
        
        System.out.println("Analysis finished in " + duration + " ms");
        System.out.println("Average execution time: " + result.getExecutionTime() + " ms");
        System.out.println("Average memory used: " + result.getMemoryUsed() + " bytes");
        
        // Assert that the analysis succeeded and took less than 20 seconds
        assertTrue("Analysis result should be valid", result.getExecutionTime() >= 0);
        assertTrue("Analysis should finish under 20 seconds (optimized)", duration < 20000);
    }
}
