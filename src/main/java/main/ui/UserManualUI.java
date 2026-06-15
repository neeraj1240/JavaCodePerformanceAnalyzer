package main.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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
        UIUtils.setStageIcon(stage);

        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(40, 50, 40, 50));
        mainLayout.setStyle("-fx-background-color: #161b22;");
        mainLayout.setAlignment(Pos.TOP_CENTER);

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

        faqContainer.getChildren().addAll(
            createFAQItem("What does Single Input do?",
                "- Single Input runs your program for one input case and shows the latest benchmark metrics.\n" +
                "- Use Manual Input when you need exact test data or edge cases.\n" +
                "- Use Random Input when your program reads a simple generated input format.\n" +
                "- Use Hardcoded Input when your data is already inside the Java program.",
                ""),
            createFAQItem("What does Input Range do?",
                "Input Range repeats the analysis from Min Size to Max Size using the Step Size. The result cards show the last completed size, while the graph buttons plot every measured size.\n\n" +
                "The app does not automatically prove Big-O complexity. Use the graphs to visually compare how execution time, memory, throughput, GC, and latency change as input grows.",
                ""),
            createFAQItem("Which data source should I choose?",
                "- Manual Input: You provide the exact stdin text. This is best for debugging, edge cases, and custom input formats.\n" +
                "- Random Input: The app generates stdin from the input size. This is best for simple arrays, matrices, numeric loops, and plain strings.\n" +
                "- Hardcoded Input: The app sends no real external input. Choose this only when the program already contains its own test data and does not need Scanner input.",
                "// Example of Hardcoded Input\n" +
                "public static void main(String[] args) {\n" +
                "    int[] arr = { 5, 2, 9, 1, 5, 6 };\n" +
                "    // Your logic here without Scanner\n" +
                "}"),
            createFAQItem("What Java structure is required?",
                "Your code must contain one public class and a standard public static void main(String[] args) method. The public class name is detected automatically and used as the file name before compilation.\n\n" +
                "If there is no public class or no matching main method, the analyzer cannot compile and launch your program.",
                "public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        // Your code here\n" +
                "    }\n" +
                "}"),
            createFAQItem("How does Random Input generation work?",
                "Random Input is based on simple code detection. If your code uses Scanner and nextInt(), nextDouble(), nextLine(), arrays, or matrix-like declarations, the app creates matching stdin for common cases.\n\n" +
                "It does not understand every custom protocol. If your program needs prompts, menus, multiple unrelated values, special separators, or a very specific format, use Manual Input.",
                ""),
            createFAQItem("Why did Random Input not match my program?",
                "The generator only sees patterns in your source code. It may guess wrong if your input format is unusual, if variable names do not make the structure obvious, or if the program mixes strings and numbers in a custom order.\n\n" +
                "For reliable random analysis, write Scanner reads in a simple order: size first, then the generated values. For anything more specific, paste the exact input in Manual Input.",
                ""),
            createFAQItem("Array input format",
                "For a single generated array, Random Input writes the array size first, followed by the array values. The values may be Random, Sorted, or Nearly Sorted depending on the Array Type selector.",
                "public static void main(String[] args) {\n" +
                "    Scanner sc = new Scanner(System.in);\n" +
                "    int size = sc.nextInt();\n" +
                "    int[] arr = new int[size];\n" +
                "    for (int i = 0; i < size; i++) {\n" +
                "        arr[i] = sc.nextInt();\n" +
                "    }\n" +
                "    // Your logic here\n" +
                "}"),
            createFAQItem("Example: Bubble Sort program",
                "This is a complete example that works well with Random Input, Manual Input, and Input Range. It reads the array size first, then reads exactly that many integers, sorts them, and prints the sorted values.",
                "import java.util.Scanner;\n" +
                "\n" +
                "public class Main {\n" +
                "    public static void bubbleSort(int[] arr) {\n" +
                "        int n = arr.length;\n" +
                "\n" +
                "        for (int i = 0; i < n - 1; i++) {\n" +
                "            for (int j = 0; j < n - i - 1; j++) {\n" +
                "                if (arr[j] > arr[j + 1]) {\n" +
                "                    int temp = arr[j];\n" +
                "                    arr[j] = arr[j + 1];\n" +
                "                    arr[j + 1] = temp;\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    public static void main(String[] args) {\n" +
                "        Scanner sc = new Scanner(System.in);\n" +
                "\n" +
                "        int n = sc.nextInt();\n" +
                "        int[] arr = new int[n];\n" +
                "\n" +
                "        for (int i = 0; i < n; i++) {\n" +
                "            arr[i] = sc.nextInt();\n" +
                "        }\n" +
                "\n" +
                "        bubbleSort(arr);\n" +
                "\n" +
                "        for (int i = 0; i < n; i++) {\n" +
                "            System.out.print(arr[i] + \" \");\n" +
                "        }\n" +
                "\n" +
                "        sc.close();\n" +
                "    }\n" +
                "}"),
            createFAQItem("Multiple-array input format",
                "If the generator detects more than one array allocation, it writes each array as its own size line followed by that array's values. This works best for programs that read two arrays in the same size-then-values pattern.",
                "public static void main(String[] args) {\n" +
                "    Scanner sc = new Scanner(System.in);\n" +
                "    int n = sc.nextInt();\n" +
                "    int[] first = new int[n];\n" +
                "    for (int i = 0; i < n; i++) first[i] = sc.nextInt();\n" +
                "\n" +
                "    int m = sc.nextInt();\n" +
                "    int[] second = new int[m];\n" +
                "    for (int i = 0; i < m; i++) second[i] = sc.nextInt();\n" +
                "}"),
            createFAQItem("String input format",
                "For code that only reads a line with nextLine(), Random Input generates a space-separated sentence of common words. For the explicit string generator path, it may generate one continuous lowercase string.\n\n" +
                "If your code needs spaces, use nextLine(). If your code reads one token with next(), Manual Input is usually safer.",
                "public static void main(String[] args) {\n" +
                "    Scanner sc = new Scanner(System.in);\n" +
                "    String input = sc.nextLine();\n" +
                "    // Your logic here\n" +
                "}"),
            createFAQItem("Character input format",
                "Random Input does not have a dedicated character-array generator in the main UI flow. If your code reads chars with sc.next().charAt(0), use Manual Input for the most predictable results.\n\n" +
                "You can still benchmark character logic reliably by pasting the exact size and characters yourself.",
                "public static void main(String[] args) {\n" +
                "    Scanner sc = new Scanner(System.in);\n" +
                "    int size = sc.nextInt();\n" +
                "    char[] arr = new char[size];\n" +
                "    for (int i = 0; i < size; i++) {\n" +
                "        arr[i] = sc.next().charAt(0);\n" +
                "    }\n" +
                "}"),
            createFAQItem("Matrix input format",
                "For matrix-style code, the generator may provide two dimensions before the matrix values. In the main random-input path, matrix size is capped at 100 so very large matrix runs do not explode in generated data.\n\n" +
                "If your program reads one matrix, consume rows and columns first. If your program expects a different matrix format, use Manual Input.",
                "public static void main(String[] args) {\n" +
                "    Scanner sc = new Scanner(System.in);\n" +
                "    int rows = sc.nextInt();\n" +
                "    int cols = sc.nextInt();\n" +
                "    int[][] matrix = new int[rows][cols];\n" +
                "    for (int i = 0; i < rows; i++) {\n" +
                "        for (int j = 0; j < cols; j++) {\n" +
                "            matrix[i][j] = sc.nextInt();\n" +
                "        }\n" +
                "    }\n" +
                "}"),
            createFAQItem("How do I see my program output?",
                "The analyzer first runs your program normally to capture stdout and stderr. After analysis finishes, click 'Show Output Data' to view that captured text.\n\n" +
                "In Input Range mode, the displayed output comes from the latest completed generated input size.",
                "public static void main(String[] args) {\n" +
                "    System.out.println(\"Result: \" + result);\n" +
                "}"),
            createFAQItem("Why does printing not affect benchmark results?",
                "The app uses two phases. First, a normal run captures output and verifies that the program exits successfully. Second, JMH benchmarks your main method while System.out and System.err are discarded, so console I/O does not dominate the performance numbers.",
                ""),
            createFAQItem("What does each performance metric mean?",
                "- Execution Time: Average JMH time for one invocation of your main method. Lower is better.\n" +
                "- Memory Used: Bytes allocated per operation, reported by the JMH GC profiler.\n" +
                "- Throughput: Operations completed per second. Higher is better.\n" +
                "- GC Pause Time: Time spent in garbage collection during measurement.\n" +
                "- Heap Allocation Rate: How quickly the program allocates memory, in MB/sec.\n" +
                "- p50, p95, p99 Latency: Sample-time percentiles. p50 is the median, p95 is slower than 95% of samples, and p99 highlights rare slow runs.",
                ""),
            createFAQItem("How is performance measured?",
                "The analyzer uses JMH (Java Microbenchmark Harness). It runs 2 warmup iterations so the JVM can optimize the code, then 3 measurement iterations for Average Time, Throughput, and Sample Time.\n\n" +
                "Each benchmark invocation reloads the same generated or manual input into System.in before calling your main method.",
                ""),
            createFAQItem("Are there size, timeout, and memory limits?",
                "Single Random Input and Input Range both reject sizes above 100,000. Matrix generation in the main random path is capped internally at 100 by 100.\n\n" +
                "The initial verification run has a 10-second timeout and uses a JVM heap limit of 512MB (-Xmx512m). JMH benchmarking can still take longer because it runs warmup and measurement iterations.",
                ""),
            createFAQItem("Why are graphs only available for Input Range?",
                "Graphs need multiple input sizes. Single Input creates only one data point, so there is no trend to plot. Use Input Range, then open the graph buttons for execution time, memory, throughput, GC pause time, heap allocation rate, or latency.",
                ""),
            createFAQItem("How should I write Scanner input for best results?",
                "Keep benchmark programs non-interactive. Do not wait for menu choices or rely on prompts like 'Enter size:' being answered by a human. The analyzer only sends stdin data.\n\n" +
                "A predictable pattern such as size first, then values, works best with Random Input and Input Range.",
                "public static void main(String[] args) {\n" +
                "    Scanner sc = new Scanner(System.in);\n" +
                "    int size = sc.nextInt();\n" +
                "    int[] arr = new int[size];\n" +
                "    for (int i = 0; i < size; i++) {\n" +
                "        arr[i] = sc.nextInt();\n" +
                "    }\n" +
                "    // Your logic here\n" +
                "}")
        );

        mainLayout.getChildren().addAll(titleBox, faqContainer);

        ScrollPane scrollPane = new ScrollPane(mainLayout);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #161b22; -fx-background: #161b22;");
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
