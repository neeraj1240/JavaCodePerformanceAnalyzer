package main.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class UserManualUI {
    private final Stage stage;

    public UserManualUI() {
        stage = new Stage();
        stage.setTitle("FAQ");
        stage.initModality(Modality.NONE);

        try {
            Image icon = new Image(getClass().getResourceAsStream("/logo.ico"));
            stage.getIcons().add(icon);
        } catch (Exception e) {
            System.err.println("Could not load application icon: " + e.getMessage());
        }

        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(40, 50, 40, 50));
        mainLayout.setStyle("-fx-background-color: #161b22;");
        mainLayout.setAlignment(Pos.TOP_CENTER);

        // Title
        VBox titleBox = new VBox(0);
        titleBox.setAlignment(Pos.CENTER);
        Label title1 = new Label("General");
        title1.setStyle("-fx-text-fill: white; -fx-font-family: 'Inter', 'Segoe UI', sans-serif; -fx-font-size: 36px; -fx-font-weight: normal;");
        Label title2 = new Label("FAQs");
        title2.setStyle("-fx-text-fill: white; -fx-font-family: 'Inter', 'Segoe UI', sans-serif; -fx-font-size: 36px; -fx-font-weight: bold;");
        titleBox.getChildren().addAll(title1, title2);
        VBox.setMargin(titleBox, new Insets(0, 0, 40, 0));

        VBox faqContainer = new VBox(0);
        faqContainer.setMaxWidth(800);
        faqContainer.setAlignment(Pos.TOP_CENTER);

        // Add questions
        faqContainer.getChildren().addAll(
            createFAQItem("Single Input vs. Input Range", 
                "• Single Input: Analyzes your code execution for one specific input size. Best for testing correctness and basic benchmarking.\n" +
                "• Input Range: Analyzes your code iteratively over a range of sizes (from Min Size to Max Size with a Step Size) to generate complexity graphs. Best for understanding Time and Space Complexity (e.g., O(N), O(N^2)).",
                ""),
            createFAQItem("Data Sources: Manual, Random, and Hardcoded", 
                "• Manual Input: You provide the exact test cases. Ideal for testing specific edge cases or debugging.\n" +
                "• Random Input: The analyzer automatically generates input data of the specified size. Perfect for stress testing and complexity analysis.\n" +
                "• Hardcoded Input: No external input is fed to the scanner. Your code inherently defines the data within the program itself.",
                "// Example of Hardcoded Input\n" +
                "public static void main(String[] args) {\n" +
                "    int[] arr = { 5, 2, 9, 1, 5, 6 };\n" +
                "    // Your logic here without Scanner\n" +
                "}"),
            createFAQItem("How should I write my code to see the output?", 
                "The analyzer captures standard console output (System.out.println) from your main method during the initial verification run. To view it, click the 'Show Output Data' button after analysis completes. For the best display, avoid excessive debug printing and clearly label your results.",
                "public static void main(String[] args) {\n" +
                "    int target = 5;\n" +
                "    int result = searchArray(arr, target);\n" +
                "    \n" +
                "    // Clean, clear output\n" +
                "    System.out.println(\"Search Results:\");\n" +
                "    System.out.println(\"Target: \" + target + \" found at index: \" + result);\n" +
                "}"),
            createFAQItem("What does each performance metric mean?", 
                "• Execution Time: The average time taken to run your method. Lower is better.\n" +
                "• Memory Used: The total bytes allocated per operation.\n" +
                "• Throughput: Operations completed per millisecond. Higher is better.\n" +
                "• GC Pause Time: Total time (in ms) your program stalled due to Garbage Collection cleaning up memory. High GC pauses drastically affect responsiveness.\n" +
                "• Heap Allocation Rate: Speed at which new objects are created (e.g., MB/sec). High rates trigger more GC pauses.\n" +
                "• p50, p95, p99 Latency: Percentiles showing execution time distribution. p50 is the median run, while p99 shows the worst 1% of runs (highlighting rare performance spikes).",
                ""),
            createFAQItem("How is performance measured so accurately?", 
                "The analyzer uses JMH (Java Microbenchmark Harness) under the hood. It runs 2 warmup iterations to allow the JVM to optimize your code, followed by 3 measurement iterations to get the final score. During these benchmarks, standard output (like System.out.println) is suppressed to prevent console I/O from skewing your execution times.",
                ""),
            createFAQItem("Are there any execution timeouts or memory limits?", 
                "Yes. To prevent infinite loops and system crashes, your program is strictly limited to a 10-second timeout. Additionally, the JVM is capped at 512MB of maximum heap memory (-Xmx512m). If your code exceeds these limits, the analysis will throw an error.",
                ""),
            createFAQItem("What are the structural requirements for my Java code?", 
                "Your code must contain a standard 'public class ClassName' declaration and a 'public static void main(String[] args)' method. The compiler automatically detects your class name, so the class must be declared as public. Without the main method, the executor cannot launch your program.",
                ""),
            createFAQItem("Why does my System.out output sometimes not affect performance?", 
                "The analyzer runs your code in two separate phases. First, it runs a standard execution to capture and verify your System.out.print output. Second, it runs the JMH benchmarks where output streams are silently discarded. This ensures your performance metrics measure your actual algorithmic logic, not your console printing speed.",
                ""),
            createFAQItem("Array input format", 
                "The array input format requires the size of the array on the first line.\n" +
                "This must be followed by the array elements separated by spaces on the next line.",
                "public static void main(String[] args) {\n" +
                "    Scanner sc = new Scanner(System.in);\n" +
                "    int size = sc.nextInt();\n" +
                "    int[] arr = new int[size];\n" +
                "    for (int i = 0; i < size; i++) {\n" +
                "        arr[i] = sc.nextInt();\n" +
                "    }\n" +
                "    // Your logic here\n" +
                "}"),
            createFAQItem("String input format", 
                "The string input format expects a single string of alphabetical characters without spaces.\n" +
                "Depending on your code's Scanner usage, it may also accept space-separated words.",
                "public static void main(String[] args) {\n" +
                "    Scanner sc = new Scanner(System.in);\n" +
                "    String input = sc.nextLine();\n" +
                "    // Your logic here\n" +
                "}"),
            createFAQItem("Character array input format", 
                "The character array input format is treated similarly to a standard string or integer array.\n" +
                "Provide the characters sequentially as expected by your program's input logic.",
                "public static void main(String[] args) {\n" +
                "    Scanner sc = new Scanner(System.in);\n" +
                "    int size = sc.nextInt();\n" +
                "    char[] arr = new char[size];\n" +
                "    for (int i = 0; i < size; i++) {\n" +
                "        arr[i] = sc.next().charAt(0);\n" +
                "    }\n" +
                "    // Your logic here\n" +
                "}"),
            createFAQItem("Matrix input format", 
                "The matrix input format requires the dimension (size) on the first line.\n" +
                "This is followed by 'size' number of rows, each containing 'size' elements separated by spaces.",
                "public static void main(String[] args) {\n" +
                "    Scanner sc = new Scanner(System.in);\n" +
                "    int size = sc.nextInt();\n" +
                "    sc.nextInt(); // Consume the second dimension provided by the generator\n" +
                "    int[][] matrix = new int[size][size];\n" +
                "    for (int i = 0; i < size; i++) {\n" +
                "        for (int j = 0; j < size; j++) {\n" +
                "            matrix[i][j] = sc.nextInt();\n" +
                "        }\n" +
                "    }\n" +
                "    // Your logic here\n" +
                "}")
        );

        mainLayout.getChildren().addAll(titleBox, faqContainer);

        ScrollPane scrollPane = new ScrollPane(mainLayout);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #161b22; -fx-background: #161b22;");
        
        // Remove scrollpane borders
        scrollPane.getStylesheets().add("data:text/css,.scroll-pane > .viewport { -fx-background-color: transparent; } .scroll-pane { -fx-background-color: transparent; -fx-padding: 0; }");

        Scene scene = new Scene(scrollPane, 900, 700);
        stage.setScene(scene);
    }

    private VBox createFAQItem(String question, String answer, String codeSnippet) {
        VBox itemBox = new VBox();
        itemBox.setStyle("-fx-border-color: #30363d; -fx-border-width: 0 0 1 0; -fx-padding: 25 0 25 0;");

        Label questionLabel = new Label(question);
        questionLabel.setStyle("-fx-text-fill: white; -fx-font-family: 'Inter', 'Segoe UI', sans-serif; -fx-font-size: 18px; -fx-font-weight: bold; -fx-cursor: hand;");
        questionLabel.setWrapText(true);

        Text answerText = new Text(answer);
        answerText.setStyle("-fx-fill: #c9d1d9; -fx-font-family: 'Inter', 'Segoe UI', sans-serif; -fx-font-size: 15px; -fx-line-spacing: 5px;");
        answerText.setWrappingWidth(780);
        
        VBox answerBox = new VBox(15);
        answerBox.getChildren().add(answerText);
        
        if (codeSnippet != null && !codeSnippet.isEmpty()) {
            Label codeLabel = new Label(codeSnippet);
            codeLabel.setStyle("-fx-text-fill: #c9d1d9; -fx-font-family: 'Consolas', 'Courier New', monospace; -fx-font-size: 14px; -fx-background-color: #0d1117; -fx-border-color: #30363d; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 15;");
            codeLabel.setWrapText(true);
            codeLabel.setMaxWidth(780);
            answerBox.getChildren().add(codeLabel);
        }
        
        answerBox.setPadding(new Insets(20, 0, 0, 0));
        answerBox.setVisible(false);
        answerBox.setManaged(false);

        questionLabel.setOnMouseClicked(e -> {
            boolean isVisible = answerBox.isVisible();
            answerBox.setVisible(!isVisible);
            answerBox.setManaged(!isVisible);
        });

        itemBox.getChildren().addAll(questionLabel, answerBox);
        return itemBox;
    }

    public void show() {
        if (!stage.isShowing()) {
            stage.show();
        }
        stage.toFront();
    }
}
