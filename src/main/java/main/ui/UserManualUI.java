package main.ui;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class UserManualUI {
    private final Stage stage;

    public UserManualUI() {
        stage = new Stage();
        stage.setTitle("User Manual");
        stage.initModality(Modality.NONE);

        try {
            Image icon = new Image(getClass().getResourceAsStream("/JAVA CODE PERFORMACE ANALYZER.png"));
            stage.getIcons().add(icon);
        } catch (Exception e) {
            System.err.println("Could not load application icon: " + e.getMessage());
        }

        FlowPane manualLayout = new FlowPane(20, 20);
        manualLayout.setPadding(new Insets(20));
        manualLayout.setPrefWrapLength(800);



        // Create clickable sections
        VBox inputStructureBox = createManualSection("Input Structure",
                "Click to learn about expected input format from user's program");
        inputStructureBox.setOnMouseClicked(e -> showInputStructureGuide());

        VBox mainMethodBox = createManualSection("Main Method",
                "Click to learn about the importance of main method and proper structure");
        mainMethodBox.setOnMouseClicked(e -> showMainMethodGuide());

        VBox inputOptionsBox = createManualSection("Input Options",
                "Click to learn about different input options and when to use them");
        inputOptionsBox.setOnMouseClicked(e -> showInputOptionsGuide());

        VBox performanceAnalysisBox = createManualSection("Performance Analysis",
                "Click to learn how your code's performance is analyzed");
        performanceAnalysisBox.setOnMouseClicked(e -> showPerformanceAnalysisGuide());

        VBox outputFormatBox = createManualSection("Output Format",
                "Click to learn about output formatting and display");
        outputFormatBox.setOnMouseClicked(e -> showOutputFormatGuide());

        VBox accuracyBox = createManualSection("Accuracy Information",
                "Click to understand the accuracy and limitations of performance analysis");
        accuracyBox.setOnMouseClicked(e -> showAccuracyInformationGuide());


        manualLayout.getChildren().addAll(
                inputStructureBox,
                mainMethodBox,
                inputOptionsBox,
                performanceAnalysisBox,
                outputFormatBox,
                accuracyBox
        );

        Scene scene = new Scene(manualLayout, 850, 600);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        stage.setScene(scene);
    }

    private void showAccuracyInformationGuide() {
        Stage guideStage = new Stage();
        guideStage.setTitle("Accuracy and Limitations Guide");
        guideStage.initModality(Modality.NONE);
        try {
            Image icon = new Image(getClass().getResourceAsStream("/JAVA CODE PERFORMACE ANALYZER.png"));
            guideStage.getIcons().add(icon);
        } catch (Exception e) {
            System.err.println("Could not load application icon: " + e.getMessage());
        }

        ScrollPane scrollPane = new ScrollPane();
        VBox content = createAccuracyInformationContent();
        scrollPane.setContent(content);
        scrollPane.setFitToWidth(true);

        Scene scene = new Scene(scrollPane, 900, 700);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        guideStage.setScene(scene);
        guideStage.show();
    }

    private VBox createAccuracyInformationContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.getStyleClass().add("guide-content");



        // Title
        Label titleLabel = new Label("Accuracy and Limitations Guide");
        titleLabel.getStyleClass().add("guide-title");

        // 1. Overview of Accuracy
        VBox overview = new VBox(10);
        Label overviewTitle = new Label("1. Understanding Result Accuracy");
        overviewTitle.getStyleClass().add("section-title");
        TextArea overviewContent = createInfoArea(
                "ACCURACY DISCLAIMER:\n\n" +
                        "• Results are approximations, not absolute measurements\n" +
                        "• Multiple factors can influence performance analysis\n" +
                        "• Different environments may yield slightly different results\n\n" +
                        "KEY PRINCIPLES:\n" +
                        "• Provides relative performance insights\n" +
                        "• Helps compare different implementation approaches\n" +
                        "• Not a definitive performance guarantee"
        );

        // 2. Performance Measurement Limitations
        VBox measurementLimitations = new VBox(10);
        Label measurementTitle = new Label("2. Performance Measurement Limitations");
        measurementTitle.getStyleClass().add("section-title");
        TextArea measurementContent = createInfoArea(
                "EXECUTION TIME LIMITATIONS:\n" +
                        "• Influenced by system resources\n" +
                        "• JVM warm-up can affect initial measurements\n" +
                        "• Background processes may impact results\n" +
                        "• Small input sizes may not show true complexity\n\n" +
                        "MEMORY MEASUREMENT LIMITATIONS:\n" +
                        "• Includes JVM overhead\n" +
                        "• Garbage collection can introduce variations\n" +
                        "• Doesn't capture all memory nuances\n" +
                        "• Platform-specific memory management"
        );

        // 3. Complexity Analysis Challenges
        VBox complexityLimitations = new VBox(10);
        Label complexityTitle = new Label("3. Complexity Analysis Challenges");
        complexityTitle.getStyleClass().add("section-title");
        TextArea complexityContent = createInfoArea(
                "TIME COMPLEXITY CHALLENGES:\n" +
                        "• Automated detection is an approximation\n" +
                        "• May not capture nuanced algorithm behaviors\n" +
                        "• Limited by input size and test cases\n\n" +
                        "POTENTIAL MISCLASSIFICATIONS:\n" +
                        "• O(n) might be misidentified as O(log n)\n" +
                        "• Constant-time operations can look linear\n" +
                        "• Hybrid algorithms may be hard to classify\n\n" +
                        "FACTORS AFFECTING ACCURACY:\n" +
                        "• Input data distribution\n" +
                        "• Hardware specifications\n" +
                        "• JVM optimizations\n" +
                        "• Specific implementation details"
        );

        // 4. Scenarios of Inaccurate Measurements
        VBox scenarios = new VBox(10);
        Label scenariosTitle = new Label("4. Scenarios of Potential Inaccuracies");
        scenariosTitle.getStyleClass().add("section-title");
        TextArea scenariosContent = createInfoArea(
                "POTENTIAL INACCURACY SCENARIOS:\n\n" +
                        "1. Very Small Input Sizes\n" +
                        "   • Complexity might not be clearly visible\n" +
                        "   • JVM optimizations can mask true performance\n\n" +
                        "2. Recursive Algorithms\n" +
                        "   • Overhead of function calls can skew measurements\n" +
                        "   • Tail recursion optimizations vary\n\n" +
                        "3. Algorithms with Conditional Complexity\n" +
                        "   • Different input scenarios yield different complexities\n" +
                        "   • Single test might not represent all cases\n\n" +
                        "4. Memory-Intensive Algorithms\n" +
                        "   • Garbage collection can introduce variations\n" +
                        "   • Platform-specific memory management"
        );

        // 5. Best Practices for Accurate Analysis
        VBox bestPractices = new VBox(10);
        Label bestPracticesTitle = new Label("5. Best Practices for Accurate Analysis");
        bestPracticesTitle.getStyleClass().add("section-title");
        TextArea bestPracticesContent = createInfoArea(
                "IMPROVING ACCURACY:\n" +
                        "• Run multiple analyses\n" +
                        "• Test with various input sizes\n" +
                        "• Use consistent hardware\n" +
                        "• Close background applications\n" +
                        "• Warm up the JVM before measuring\n\n" +
                        "RECOMMENDED APPROACH:\n" +
                        "• Compare relative performance\n" +
                        "• Look for consistent patterns\n" +
                        "• Don't rely on single measurement\n" +
                        "• Consider multiple test scenarios"
        );

        // 6. Understanding Complexity Notations
        VBox complexityNotation = new VBox(10);
        Label notationTitle = new Label("6. Understanding Complexity Notations");
        notationTitle.getStyleClass().add("section-title");
        TextArea notationContent = createInfoArea(
                "COMPLEXITY NOTATION GUIDE:\n\n" +
                        "O(1): Constant Time\n" +
                        "   • Executes in same time regardless of input\n" +
                        "   • Most predictable performance\n\n" +
                        "O(log n): Logarithmic Time\n" +
                        "   • Performance increases slowly\n" +
                        "   • Efficient for large datasets\n\n" +
                        "O(n): Linear Time\n" +
                        "   • Performance grows linearly with input\n" +
                        "   • Straightforward, predictable growth\n\n" +
                        "O(n log n): Linearithmic Time\n" +
                        "   • Common in efficient sorting algorithms\n" +
                        "   • Balance between linear and quadratic\n\n" +
                        "O(n²): Quadratic Time\n" +
                        "   • Performance degrades quickly\n" +
                        "   • Nested loops are typical causes"
        );

        // 7. Improvement and Feedback
        VBox improvement = new VBox(10);
        Label improvementTitle = new Label("7. Continuous Improvement");
        improvementTitle.getStyleClass().add("section-title");
        TextArea improvementContent = createInfoArea(
                "OUR COMMITMENT:\n" +
                        "• Continuously refining analysis algorithms\n" +
                        "• Incorporating user feedback\n" +
                        "• Expanding complexity detection capabilities\n\n" +
                        "HOW YOU CAN HELP:\n" +
                        "• Report unusual or unexpected results\n" +
                        "• Provide diverse test cases\n" +
                        "• Share your performance analysis experiences"
        );

        content.getChildren().addAll(
                titleLabel,
                overview, overviewTitle, overviewContent,
                measurementLimitations, measurementTitle, measurementContent,
                complexityLimitations, complexityTitle, complexityContent,
                scenarios, scenariosTitle, scenariosContent,
                bestPractices, bestPracticesTitle, bestPracticesContent,
                complexityNotation, notationTitle, notationContent,
                improvement, improvementTitle, improvementContent
        );

        return content;
    }

    private void showOutputFormatGuide() {
        Stage guideStage = new Stage();
        guideStage.setTitle("Output Format Guide");
        guideStage.initModality(Modality.NONE);

        try {
            Image icon = new Image(getClass().getResourceAsStream("/JAVA CODE PERFORMACE ANALYZER.png"));
            guideStage.getIcons().add(icon);
        } catch (Exception e) {
            System.err.println("Could not load application icon: " + e.getMessage());
        }

        ScrollPane scrollPane = new ScrollPane();
        VBox content = createOutputFormatContent();
        scrollPane.setContent(content);
        scrollPane.setFitToWidth(true);

        Scene scene = new Scene(scrollPane, 900, 700);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        guideStage.setScene(scene);
        guideStage.show();
    }

    private VBox createOutputFormatContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.getStyleClass().add("guide-content");

        // Title
        Label titleLabel = new Label("Output Format Guide");
        titleLabel.getStyleClass().add("guide-title");

        // 1. Overview
        VBox overview = new VBox(10);
        Label overviewTitle = new Label("1. Overview");
        overviewTitle.getStyleClass().add("section-title");
        TextArea overviewContent = createInfoArea(
                "The Output Format section explains:\n\n" +
                        "• How to structure your program's output\n" +
                        "• How to view and interpret output data\n" +
                        "• Different ways to format your output\n" +
                        "• Best practices for output generation\n\n" +
                        "The analyzer captures all output generated by your program through System.out"
        );

        // 2. Program Output Requirements
        VBox requirements = new VBox(10);
        Label reqTitle = new Label("2. Program Output Requirements");
        reqTitle.getStyleClass().add("section-title");
        TextArea reqContent = createInfoArea(
                "To properly display output, your program should:\n\n" +
                        "• Use System.out.println() or System.out.print() for output\n" +
                        "• Generate output in a consistent format\n" +
                        "• Avoid unnecessary debug messages\n" +
                        "• Clear and organized output structure"
        );

        // 3. Example Output Formats
        VBox examples = new VBox(10);
        Label examplesTitle = new Label("3. Example Output Formats");
        examplesTitle.getStyleClass().add("section-title");

        // Array Output Example
        TextArea arrayExample = createCodeArea(
                "// Example 1: Array Output\n" +
                        "public static void main(String[] args) {\n" +
                        "    int[] arr = {1, 2, 3, 4, 5};\n" +
                        "    \n" +
                        "    // Good Output Format\n" +
                        "    System.out.println(\"Sorted Array:\");\n" +
                        "    for(int num : arr) {\n" +
                        "        System.out.print(num + \" \");\n" +
                        "    }\n" +
                        "    System.out.println(); // New line after array\n" +
                        "    \n" +
                        "    // Additional Information\n" +
                        "    System.out.println(\"Array Size: \" + arr.length);\n" +
                        "}"
        );

        // Matrix Output Example
        TextArea matrixExample = createCodeArea(
                "// Example 2: Matrix Output\n" +
                        "public static void main(String[] args) {\n" +
                        "    int[][] matrix = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};\n" +
                        "    \n" +
                        "    System.out.println(\"Matrix:\");\n" +
                        "    for(int[] row : matrix) {\n" +
                        "        for(int num : row) {\n" +
                        "            System.out.print(num + \"\\t\");\n" +
                        "        }\n" +
                        "        System.out.println();\n" +
                        "    }\n" +
                        "}"
        );

        // Results Output Example
        TextArea resultsExample = createCodeArea(
                "// Example 3: Search Results Output\n" +
                        "public static void main(String[] args) {\n" +
                        "    int target = 5;\n" +
                        "    int result = searchArray(arr, target);\n" +
                        "    \n" +
                        "    System.out.println(\"Search Results:\");\n" +
                        "    System.out.println(\"Target: \" + target);\n" +
                        "    System.out.println(\"Found at index: \" + result);\n" +
                        "    System.out.println(\"Search Status: \" + \n" +
                        "        (result != -1 ? \"Found\" : \"Not Found\"));\n" +
                        "}"
        );

        // 4. Viewing Output Data
        VBox viewing = new VBox(10);
        Label viewingTitle = new Label("4. Viewing Output Data");
        viewingTitle.getStyleClass().add("section-title");
        TextArea viewingContent = createInfoArea(
                "HOW TO VIEW OUTPUT:\n\n" +
                        "1. Run your code analysis\n" +
                        "2. Click the 'Show Output Data' button\n" +
                        "3. A new window will display your program's output\n\n" +
                        "OUTPUT WINDOW FEATURES:\n" +
                        "• Scrollable text area for large outputs\n" +
                        "• Copy functionality for output text\n" +
                        "• Clear formatting for easy reading\n\n" +
                        "The output window shows exactly what your program printed during execution."
        );

        // 5. Best Practices
        VBox practices = new VBox(10);
        Label practicesTitle = new Label("5. Best Practices");
        practicesTitle.getStyleClass().add("section-title");
        TextArea practicesContent = createInfoArea(
                "FORMATTING TIPS:\n" +
                        "• Use clear labels for different output sections\n" +
                        "• Add appropriate spacing and newlines\n" +
                        "• Format numbers and data consistently\n" +
                        "• Include relevant headers and descriptions\n\n" +
                        "AVOID:\n" +
                        "• Excessive debug messages\n" +
                        "• Unformatted data dumps\n" +
                        "• Inconsistent spacing\n" +
                        "• Missing labels or context\n\n" +
                        "RECOMMENDED:\n" +
                        "• Structure output in a logical order\n" +
                        "• Include input size or parameters used\n" +
                        "• Add summary information when applicable\n" +
                        "• Use appropriate precision for decimal numbers"
        );

        // 6. Common Issues and Solutions
        VBox issues = new VBox(10);
        Label issuesTitle = new Label("6. Common Issues and Solutions");
        issuesTitle.getStyleClass().add("section-title");
        TextArea issuesContent = createInfoArea(
                "ISSUE: Output not showing\n" +
                        "SOLUTION: Ensure you're using System.out.println() or System.out.print()\n\n" +
                        "ISSUE: Garbled output\n" +
                        "SOLUTION: Add proper spacing and newlines between outputs\n\n" +
                        "ISSUE: Incomplete output\n" +
                        "SOLUTION: Flush System.out after large outputs\n\n" +
                        "ISSUE: Unreadable formatting\n" +
                        "SOLUTION: Use tabs or consistent spacing for alignment"
        );

        content.getChildren().addAll(
                titleLabel,
                overview, overviewTitle, overviewContent,
                requirements, reqTitle, reqContent,
                examples, examplesTitle, arrayExample, matrixExample, resultsExample,
                viewing, viewingTitle, viewingContent,
                practices, practicesTitle, practicesContent,
                issues, issuesTitle, issuesContent
        );

        return content;
    }


    private void showPerformanceAnalysisGuide() {
        Stage guideStage = new Stage();
        guideStage.setTitle("Performance Analysis Guide");
        guideStage.initModality(Modality.NONE);

        try {
            Image icon = new Image(getClass().getResourceAsStream("/JAVA CODE PERFORMACE ANALYZER.png"));
            guideStage.getIcons().add(icon);
        } catch (Exception e) {
            System.err.println("Could not load application icon: " + e.getMessage());
        }

        ScrollPane scrollPane = new ScrollPane();
        VBox content = createPerformanceAnalysisContent();
        scrollPane.setContent(content);
        scrollPane.setFitToWidth(true);

        Scene scene = new Scene(scrollPane, 900, 700);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        guideStage.setScene(scene);
        guideStage.show();
    }

    private VBox createPerformanceAnalysisContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.getStyleClass().add("guide-content");

        // Title
        Label titleLabel = new Label("Performance Analysis Guide");
        titleLabel.getStyleClass().add("guide-title");

        // 1. Overview
        VBox overview = new VBox(10);
        Label overviewTitle = new Label("1. Overview");
        overviewTitle.getStyleClass().add("section-title");
        TextArea overviewContent = createInfoArea(
                "The analyzer evaluates your code's performance across four key metrics:\n\n" +
                        "1. Execution Time: How long your code takes to run\n" +
                        "2. Memory Usage: How much memory your code consumes\n" +
                        "3. Time Complexity: The growth rate of execution time with input size\n" +
                        "4. Space Complexity: The growth rate of memory usage with input size\n\n" +
                        "These metrics help you understand your code's efficiency and scalability."
        );

        // 2. Execution Time Analysis
        VBox timeAnalysis = new VBox(10);
        Label timeTitle = new Label("2. Execution Time Analysis");
        timeTitle.getStyleClass().add("section-title");
        TextArea timeContent = createInfoArea(
                "HOW IT'S MEASURED:\n" +
                        "• Start time is captured when main method begins\n" +
                        "• End time is captured when main method completes\n" +
                        "• Multiple runs are performed to ensure accuracy\n" +
                        "• System operations and garbage collection time are excluded\n\n" +
                        "UNITS AND CONVERSION:\n" +
                        "• Default measurement is in milliseconds\n" +
                        "• Can be converted to seconds or minutes in the UI\n" +
                        "• High-precision timer is used for accuracy\n\n" +
                        "INTERPRETATION:\n" +
                        "• Lower values indicate better performance\n" +
                        "• Consider input size when comparing times\n" +
                        "• Look for patterns in time growth with increasing input"
        );

        // 3. Memory Usage Analysis
        VBox memoryAnalysis = new VBox(10);
        Label memoryTitle = new Label("3. Memory Usage Analysis");
        memoryTitle.getStyleClass().add("section-title");
        TextArea memoryContent = createInfoArea(
                "HOW IT'S MEASURED:\n" +
                        "• Heap memory is monitored during execution\n" +
                        "• Peak memory usage is recorded\n" +
                        "• Both allocated and used memory are tracked\n" +
                        "• Stack memory for method calls is included\n\n" +
                        "UNITS AND CONVERSION:\n" +
                        "• Default measurement is in bytes\n" +
                        "• Can be converted to KB or MB in the UI\n" +
                        "• Both heap and non-heap memory are considered\n\n" +
                        "INTERPRETATION:\n" +
                        "• Lower values indicate more efficient memory usage\n" +
                        "• Consider memory leaks if usage grows unexpectedly\n" +
                        "• Check for unnecessary object creation"
        );

        // 4. Complexity Analysis
        VBox complexityAnalysis = new VBox(10);
        Label complexityTitle = new Label("4. Complexity Analysis");
        complexityTitle.getStyleClass().add("section-title");
        TextArea complexityContent = createInfoArea(
                "TIME COMPLEXITY:\n" +
                        "• Determined by analyzing execution time patterns\n" +
                        "• Common complexities: O(1), O(log n), O(n), O(n log n), O(n²)\n" +
                        "• Based on how execution time grows with input size\n\n" +
                        "SPACE COMPLEXITY:\n" +
                        "• Analyzed through memory usage patterns\n" +
                        "• Considers both auxiliary and input space\n" +
                        "• Helps identify memory-intensive operations\n\n" +
                        "INTERPRETATION:\n" +
                        "• Lower complexity orders are better\n" +
                        "• Consider trade-offs between time and space\n" +
                        "• Look for opportunities to optimize"
        );

        // 5. Performance Visualization
        VBox visualization = new VBox(10);
        Label vizTitle = new Label("5. Performance Visualization");
        vizTitle.getStyleClass().add("section-title");
        TextArea vizContent = createInfoArea(
                "GRAPHS AND CHARTS:\n" +
                        "• Time Graph: Shows execution time trends\n" +
                        "• Memory Graph: Displays memory usage patterns\n" +
                        "• Complexity curves for different input sizes\n\n" +
                        "HOW TO USE:\n" +
                        "• Click 'Show Time Graph' or 'Show Memory Graph' buttons\n" +
                        "• Compare multiple runs with different input sizes\n" +
                        "• Use graphs to identify performance bottlenecks"
        );

        // 6. Tips for Accurate Analysis
        VBox tips = new VBox(10);
        Label tipsTitle = new Label("6. Tips for Accurate Analysis");
        tipsTitle.getStyleClass().add("section-title");
        TextArea tipsContent = createInfoArea(
                "FOR EXECUTION TIME:\n" +
                        "• Avoid System.out.println in performance-critical sections\n" +
                        "• Close all resources properly\n" +
                        "• Minimize external factors (other running programs)\n\n" +
                        "FOR MEMORY USAGE:\n" +
                        "• Clear unused references\n" +
                        "• Use appropriate data structures\n" +
                        "• Consider using primitive types over objects when possible\n\n" +
                        "FOR BEST RESULTS:\n" +
                        "• Test with various input sizes\n" +
                        "• Run multiple analyses for consistency\n" +
                        "• Consider both average and worst-case scenarios"
        );

        // 7. Understanding Results
        VBox results = new VBox(10);
        Label resultsTitle = new Label("7. Understanding Results");
        resultsTitle.getStyleClass().add("section-title");
        TextArea resultsContent = createInfoArea(
                "EXECUTION TIME INDICATORS:\n" +
                        "✓ Excellent: < 100ms\n" +
                        "✓ Good: 100ms - 500ms\n" +
                        "⚠ Fair: 500ms - 2000ms\n" +
                        "⚠ Poor: > 2000ms\n\n" +
                        "MEMORY USAGE INDICATORS:\n" +
                        "✓ Efficient: < 1MB\n" +
                        "✓ Good: 1MB - 10MB\n" +
                        "⚠ Moderate: 10MB - 50MB\n" +
                        "⚠ High: > 50MB\n\n" +
                        "Note: These are general guidelines and may vary based on your specific use case and input size."
        );

        content.getChildren().addAll(
                titleLabel,
                overview, overviewTitle, overviewContent,
                timeAnalysis, timeTitle, timeContent,
                memoryAnalysis, memoryTitle, memoryContent,
                complexityAnalysis, complexityTitle, complexityContent,
                visualization, vizTitle, vizContent,
                tips, tipsTitle, tipsContent,
                results, resultsTitle, resultsContent
        );

        return content;
    }


    private void showInputOptionsGuide() {
        Stage guideStage = new Stage();
        guideStage.setTitle("Input Options Guide");
        guideStage.initModality(Modality.NONE);

        try {
            Image icon = new Image(getClass().getResourceAsStream("/JAVA CODE PERFORMACE ANALYZER.png"));
            guideStage.getIcons().add(icon);
        } catch (Exception e) {
            System.err.println("Could not load application icon: " + e.getMessage());
        }

        ScrollPane scrollPane = new ScrollPane();
        VBox content = createInputOptionsContent();
        scrollPane.setContent(content);
        scrollPane.setFitToWidth(true);

        Scene scene = new Scene(scrollPane, 900, 700);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        guideStage.setScene(scene);
        guideStage.show();
    }

    private VBox createInputOptionsContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.getStyleClass().add("guide-content");

        // Title
        Label titleLabel = new Label("Input Options Guide");
        titleLabel.getStyleClass().add("guide-title");

        // 1. Overview
        VBox overview = new VBox(10);
        Label overviewTitle = new Label("1. Overview");
        overviewTitle.getStyleClass().add("section-title");
        TextArea overviewContent = createInfoArea(
                "The analyzer provides three input options:\n\n" +
                        "1. Random Input: Automatically generates test data based on specified size\n" +
                        "2. Manual Input: Allows you to provide specific test cases\n" +
                        "3. Hardcoded Input: Uses predefined test data within your code\n\n" +
                        "Each option serves different testing purposes and scenarios."
        );

        // 2. Random Input
        VBox randomInput = new VBox(10);
        Label randomTitle = new Label("2. Random Input");
        randomTitle.getStyleClass().add("section-title");
        TextArea randomContent = createInfoArea(
                "WHEN TO USE:\n" +
                        "• Testing algorithm performance with large datasets\n" +
                        "• Analyzing time complexity with varying input sizes\n" +
                        "• Stress testing your program\n" +
                        "• When specific test cases aren't critical\n\n" +
                        "HOW IT WORKS:\n" +
                        "• You specify the input size in the UI\n" +
                        "• The analyzer generates random test data\n" +
                        "• Your program should be ready to process the size and elements"
        );

        TextArea randomCode = createCodeArea(
                "// Example program for Random Input\n" +
                        "public class SortingExample {\n" +
                        "    public static void main(String[] args) {\n" +
                        "        Scanner sc = new Scanner(System.in);\n" +
                        "        \n" +
                        "        // Read size (will be provided by analyzer)\n" +
                        "        int n = sc.nextInt();\n" +
                        "        int[] arr = new int[n];\n" +
                        "        \n" +
                        "        // Read elements (will be randomly generated)\n" +
                        "        for(int i = 0; i < n; i++) {\n" +
                        "            arr[i] = sc.nextInt();\n" +
                        "        }\n" +
                        "        \n" +
                        "        // Your sorting logic here\n" +
                        "        sort(arr);\n" +
                        "        sc.close();\n" +
                        "    }\n" +
                        "}"
        );

        // 3. Manual Input
        VBox manualInput = new VBox(10);
        Label manualTitle = new Label("3. Manual Input");
        manualTitle.getStyleClass().add("section-title");
        TextArea manualContent = createInfoArea(
                "WHEN TO USE:\n" +
                        "• Testing specific edge cases\n" +
                        "• Debugging particular scenarios\n" +
                        "• Verifying correctness with known inputs\n" +
                        "• When you need controlled test cases\n\n" +
                        "HOW IT WORKS:\n" +
                        "• You provide the test data in the Manual Input text area\n" +
                        "• Data should match your program's input format\n" +
                        "• Useful for specific test cases and edge cases"
        );

        TextArea manualCode = createCodeArea(
                "// Example program for Manual Input\n" +
                        "public class BinarySearchExample {\n" +
                        "    public static void main(String[] args) {\n" +
                        "        Scanner sc = new Scanner(System.in);\n" +
                        "        \n" +
                        "        // Read array size and target\n" +
                        "        int n = sc.nextInt();\n" +
                        "        int target = sc.nextInt();\n" +
                        "        int[] arr = new int[n];\n" +
                        "        \n" +
                        "        // Read sorted array\n" +
                        "        for(int i = 0; i < n; i++) {\n" +
                        "            arr[i] = sc.nextInt();\n" +
                        "        }\n" +
                        "        \n" +
                        "        // Binary search implementation\n" +
                        "        int result = binarySearch(arr, target);\n" +
                        "        sc.close();\n" +
                        "    }\n" +
                        "}"
        );

        // Example of manual input format
        TextArea manualInputExample = createInfoArea(
                "Example Manual Input Format:\n\n" +
                        "5 7        // size=5, target=7\n" +
                        "1 3 5 7 9  // sorted array\n\n" +
                        "This input would test searching for 7 in a sorted array of 5 elements."
        );

        // 4. Hardcoded Input
        VBox hardcodedInput = new VBox(10);
        Label hardcodedTitle = new Label("4. Hardcoded Input");
        hardcodedTitle.getStyleClass().add("section-title");
        TextArea hardcodedContent = createInfoArea(
                "WHEN TO USE:\n" +
                        "• Testing with fixed test cases\n" +
                        "• When input processing isn't part of performance analysis\n" +
                        "• Benchmarking with consistent data\n" +
                        "• Unit testing specific scenarios\n\n" +
                        "HOW IT WORKS:\n" +
                        "• Test data is defined within your code\n" +
                        "• No external input is needed\n" +
                        "• Useful for consistent benchmarking"
        );

        TextArea hardcodedCode = createCodeArea(
                "// Example program for Hardcoded Input\n" +
                        "public class GraphAnalysisExample {\n" +
                        "    public static void main(String[] args) {\n" +
                        "        // Hardcoded test data\n" +
                        "        int[][] graph = {\n" +
                        "            {0, 1, 1, 0},\n" +
                        "            {1, 0, 1, 1},\n" +
                        "            {1, 1, 0, 1},\n" +
                        "            {0, 1, 1, 0}\n" +
                        "        };\n" +
                        "        \n" +
                        "        // Your graph analysis logic here\n" +
                        "        analyzeGraph(graph);\n" +
                        "    }\n" +
                        "}"
        );

        // 5. Important Considerations
        VBox considerations = new VBox(10);
        Label considerationsTitle = new Label("5. Important Considerations");
        considerationsTitle.getStyleClass().add("section-title");
        TextArea considerationsContent = createInfoArea(
                "RANDOM INPUT:\n" +
                        "• Ensure your program can handle various input sizes\n" +
                        "• Consider edge cases with random data\n" +
                        "• Maximum input size is limited to 100,000\n\n" +
                        "MANUAL INPUT:\n" +
                        "• Follow the exact input format expected by your program\n" +
                        "• Use for testing specific scenarios and edge cases\n" +
                        "• Useful for debugging and verification\n\n" +
                        "HARDCODED INPUT:\n" +
                        "• Make sure test data is representative\n" +
                        "• Consider memory implications of large hardcoded datasets\n" +
                        "• Useful for consistent benchmarking"
        );

        content.getChildren().addAll(
                titleLabel,
                overview, overviewTitle, overviewContent,
                randomInput, randomTitle, randomContent, randomCode,
                manualInput, manualTitle, manualContent, manualCode, manualInputExample,
                hardcodedInput, hardcodedTitle, hardcodedContent, hardcodedCode,
                considerations, considerationsTitle, considerationsContent
        );

        return content;
    }


    private void showInputStructureGuide() {
        Stage guideStage = new Stage();
        guideStage.setTitle("Input Structure Guidelines");
        guideStage.initModality(Modality.NONE);

        try {
            Image icon = new Image(getClass().getResourceAsStream("/JAVA CODE PERFORMACE ANALYZER.png"));
            guideStage.getIcons().add(icon);
        } catch (Exception e) {
            System.err.println("Could not load application icon: " + e.getMessage());
        }

        ScrollPane scrollPane = new ScrollPane();
        VBox content = createInputStructureContent();
        scrollPane.setContent(content);
        scrollPane.setFitToWidth(true);

        Scene scene = new Scene(scrollPane, 900, 700);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        guideStage.setScene(scene);
        guideStage.show();
    }

    private void showMainMethodGuide() {
        Stage guideStage = new Stage();
        guideStage.setTitle("Main Method Guidelines");
        guideStage.initModality(Modality.NONE);

        try {
            Image icon = new Image(getClass().getResourceAsStream("/JAVA CODE PERFORMACE ANALYZER.png"));
            guideStage.getIcons().add(icon);
        } catch (Exception e) {
            System.err.println("Could not load application icon: " + e.getMessage());
        }

        ScrollPane scrollPane = new ScrollPane();
        VBox content = createMainMethodContent();
        scrollPane.setContent(content);
        scrollPane.setFitToWidth(true);

        Scene scene = new Scene(scrollPane, 900, 700);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        guideStage.setScene(scene);
        guideStage.show();
    }

    private VBox createMainMethodContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.getStyleClass().add("guide-content");

        // Title
        Label titleLabel = new Label("Main Method Guidelines");
        titleLabel.getStyleClass().add("guide-title");

        // 1. Basic Requirement
        VBox basicReq = new VBox(10);
        Label basicTitle = new Label("1. Basic Requirement");
        basicTitle.getStyleClass().add("section-title");
        TextArea basicContent = createInfoArea(
                "The main method is MANDATORY for code analysis. Your program must include:\n\n" +
                        "public static void main(String[] args)\n\n" +
                        "This method serves as the entry point for the analyzer to execute and measure your code's performance."
        );

        // 2. Why It's Important
        VBox importance = new VBox(10);
        Label importanceTitle = new Label("2. Why It's Important");
        importanceTitle.getStyleClass().add("section-title");
        TextArea importanceContent = createInfoArea(
                "• Entry Point: The analyzer needs a consistent entry point to start execution\n\n" +
                        "• Performance Measurement: Execution time is measured from main method entry to exit\n\n" +
                        "• Memory Analysis: Heap memory allocation is tracked from the start of main method\n\n" +
                        "• Input Processing: Command line arguments and input processing must be handled in main\n\n" +
                        "• Standardization: Ensures consistent analysis across different programs"
        );

        // 3. Common Mistakes to Avoid
        VBox mistakes = new VBox(10);
        Label mistakesTitle = new Label("3. Common Mistakes to Avoid");
        mistakesTitle.getStyleClass().add("section-title");
        TextArea mistakesContent = createInfoArea(
                "• Not declaring main as public static void\n\n" +
                        "• Missing String[] args parameter\n\n" +
                        "• Placing core logic outside main method\n\n" +
                        "• Not handling input/output within main\n\n" +
                        "• Using different method names or signatures"
        );

        // 4. Best Practices
        VBox practices = new VBox(10);
        Label practicesTitle = new Label("4. Best Practices");
        practicesTitle.getStyleClass().add("section-title");
        TextArea practicesContent = createInfoArea(
                "• Keep input processing at the start of main\n\n" +
                        "• Initialize all necessary variables within main\n\n" +
                        "• Ensure proper exception handling\n\n" +
                        "• Close all resources (Scanner, files, etc.) before main exits\n\n" +
                        "• Avoid System.exit() calls that might interfere with analysis"
        );

        // 5. Example Structure (optional but helpful)
        VBox example = new VBox(10);
        Label exampleTitle = new Label("5. Recommended Structure");
        exampleTitle.getStyleClass().add("section-title");
        TextArea exampleCode = createCodeArea(
                "public class YourProgram {\n" +
                        "    public static void main(String[] args) {\n" +
                        "        // 1. Input processing\n" +
                        "        // Handle your input here\n\n" +
                        "        // 2. Algorithm initialization\n" +
                        "        // Set up your data structures\n\n" +
                        "        // 3. Core logic\n" +
                        "        // Main algorithm implementation\n\n" +
                        "        // 4. Output processing\n" +
                        "        // Handle your output here\n\n" +
                        "        // 5. Resource cleanup\n" +
                        "        // Close any open resources\n" +
                        "    }\n" +
                        "}"
        );

        // 6. Important Notes
        VBox notes = new VBox(10);
        Label notesTitle = new Label("6. Important Notes");
        notesTitle.getStyleClass().add("section-title");
        TextArea notesContent = createInfoArea(
                "• The analyzer will not recognize your program without a proper main method\n\n" +
                        "• Performance metrics start recording when main begins execution\n\n" +
                        "• All necessary initialization should be done within main\n\n" +
                        "• The analyzer tracks memory usage of objects created during main's execution\n\n" +
                        "• Ensure your main method handles program termination properly"
        );

        content.getChildren().addAll(
                titleLabel,
                basicReq, basicTitle, basicContent,
                importance, importanceTitle, importanceContent,
                mistakes, mistakesTitle, mistakesContent,
                practices, practicesTitle, practicesContent,
                example, exampleTitle, exampleCode,
                notes, notesTitle, notesContent
        );

        return content;
    }

    private TextArea createInfoArea(String text) {
        TextArea infoArea = new TextArea(text);
        infoArea.setEditable(false);
        infoArea.setWrapText(true);
        infoArea.getStyleClass().add("info-area");
        return infoArea;
    }

    private VBox createInputStructureContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.getStyleClass().add("guide-content");

        Label titleLabel = new Label("Input Structure Guidelines");
        titleLabel.getStyleClass().add("guide-title");

        // Basic Requirements
        VBox basicReq = new VBox(10);
        Label basicTitle = new Label("1. Basic Requirements");
        basicTitle.getStyleClass().add("section-title");
        TextArea basicCode = createCodeArea(
                "import java.util.Scanner;\n\n" +
                        "public class Main {\n" +
                        "    public static void main(String[] args) {\n" +
                        "        Scanner sc = new Scanner(System.in);\n" +
                        "        // Your code here\n" +
                        "        sc.close();\n" +
                        "    }\n" +
                        "}"
        );

        // Single Array Input
        VBox singleArray = new VBox(10);
        Label arrayTitle = new Label("2. Single Array Input");
        arrayTitle.getStyleClass().add("section-title");
        TextArea arrayCode = createCodeArea(
                "Scanner sc = new Scanner(System.in);\n\n" +
                        "// Read array size\n" +
                        "int n = sc.nextInt();\n" +
                        "int[] arr = new int[n];\n\n" +
                        "// Read array elements\n" +
                        "for(int i = 0; i < n; i++) {\n" +
                        "    arr[i] = sc.nextInt();\n" +
                        "}"
        );

        // Two Arrays Input
        VBox twoArrays = new VBox(10);
        Label twoArrayTitle = new Label("3. Two Arrays Input");
        twoArrayTitle.getStyleClass().add("section-title");
        TextArea twoArrayCode = createCodeArea(
                "Scanner sc = new Scanner(System.in);\n\n" +
                        "// Read first array\n" +
                        "int n1 = sc.nextInt();\n" +
                        "int[] arr1 = new int[n1];\n" +
                        "for(int i = 0; i < n1; i++) {\n" +
                        "    arr1[i] = sc.nextInt();\n" +
                        "}\n\n" +
                        "// Read second array\n" +
                        "int n2 = sc.nextInt();\n" +
                        "int[] arr2 = new int[n2];\n" +
                        "for(int i = 0; i < n2; i++) {\n" +
                        "    arr2[i] = sc.nextInt();\n" +
                        "}"
        );

        // String Input
        VBox stringInput = new VBox(10);
        Label stringTitle = new Label("4. String Input");
        stringTitle.getStyleClass().add("section-title");
        TextArea stringCode = createCodeArea(
                "Scanner sc = new Scanner(System.in);\n\n" +
                        "// Read number of strings\n" +
                        "int n = sc.nextInt();\n" +
                        "sc.nextLine(); // consume newline\n" +
                        "String[] strings = new String[n];\n\n" +
                        "// Read strings\n" +
                        "for(int i = 0; i < n; i++) {\n" +
                        "    strings[i] = sc.nextLine();\n" +
                        "}"
        );

        // Matrix Input
        VBox matrixInput = new VBox(10);
        Label matrixTitle = new Label("5. Matrix Input");
        matrixTitle.getStyleClass().add("section-title");
        TextArea matrixCode = createCodeArea(
                "Scanner sc = new Scanner(System.in);\n\n" +
                        "// Read matrix dimensions\n" +
                        "int rows = sc.nextInt();\n" +
                        "int cols = sc.nextInt();\n" +
                        "int[][] matrix = new int[rows][cols];\n\n" +
                        "// Read matrix elements\n" +
                        "for(int i = 0; i < rows; i++) {\n" +
                        "    for(int j = 0; j < cols; j++) {\n" +
                        "        matrix[i][j] = sc.nextInt();\n" +
                        "    }\n" +
                        "}"
        );

        // Important Notes
        VBox notes = new VBox(10);
        Label notesTitle = new Label("6. Important Notes");
        notesTitle.getStyleClass().add("section-title");
        TextArea notesText = new TextArea(
                "• Maximum input size is limited to 100,000\n" +
                        "• Always close Scanner when done\n" +
                        "• Use nextLine() for strings with spaces\n" +
                        "• Use nextDouble() for precise decimal values\n" +
                        "• Remember to handle exceptions appropriately"
        );
        notesText.setEditable(false);
        notesText.setPrefRowCount(5);
        notesText.setWrapText(true);
        notesText.getStyleClass().add("notes-area");

        content.getChildren().addAll(
                titleLabel,
                basicReq, basicTitle, basicCode,
                singleArray, arrayTitle, arrayCode,
                twoArrays, twoArrayTitle, twoArrayCode,
                stringInput, stringTitle, stringCode,
                matrixInput, matrixTitle, matrixCode,
                notes, notesTitle, notesText
        );

        return content;
    }

    private TextArea createCodeArea(String code) {
        TextArea codeArea = new TextArea(code);
        codeArea.setEditable(false);
        codeArea.setPrefRowCount(code.split("\n").length);
        codeArea.setWrapText(true);
        codeArea.getStyleClass().add("code-area");
        return codeArea;
    }

    private VBox createManualSection(String title, String content) {
        VBox section = new VBox(10);
        section.getStyleClass().add("manual-section");
        section.setPrefSize(250, 200);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("manual-section-title");

        Label contentLabel = new Label(content);
        contentLabel.getStyleClass().add("manual-section-content");

        section.getChildren().addAll(titleLabel, contentLabel);
        return section;
    }

    public void show() {
        stage.show();
    }
}
