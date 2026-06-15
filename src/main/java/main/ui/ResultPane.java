package main.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import main.core.AnalysisResult;

public class ResultPane extends VBox {
    private VBox resultArea;
    private Label analyzingLabel;
    private ComboBox<String> timeUnitComboBox;
    private ComboBox<String> memoryUnitComboBox;

    private double lastExecutionTime = 0.0;
    private double lastMemoryUsed = 0.0;
    private double lastThroughput = 0.0;
    private double lastGcPauseTime = 0.0;
    private double lastHeapAllocationRate = 0.0;
    private double lastP50Latency = 0.0;
    private double lastP95Latency = 0.0;
    private double lastP99Latency = 0.0;

    private Runnable onShowInputData;
    private Runnable onShowOutputData;

    public ResultPane() {
        super(20);
        getStyleClass().add("right-pane");
        setPadding(new Insets(25));
        setPrefWidth(600);

        HBox titleBox = new HBox(10);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        Label resultTitle = new Label("📈 ANALYSIS RESULTS");
        resultTitle.getStyleClass().add("title-label");
        titleBox.getChildren().add(resultTitle);

        resultArea = new VBox(20);
        resultArea.getStyleClass().add("result-area");

        analyzingLabel = new Label("Analyzing...");
        analyzingLabel.getStyleClass().add("analyzing-label");
        analyzingLabel.setVisible(false);
        analyzingLabel.setManaged(false);

        setupComboBoxes();

        HBox dataButtonsBox = new HBox(15);
        dataButtonsBox.setAlignment(Pos.CENTER);

        Button showInputButton = new Button("📄 Show Input Data");
        showInputButton.getStyleClass().add("data-button-outline");
        showInputButton.setOnAction(e -> { if (onShowInputData != null) onShowInputData.run(); });

        Button showOutputButton = new Button("📄 Show Output Data");
        showOutputButton.getStyleClass().add("data-button-outline");
        showOutputButton.setOnAction(e -> { if (onShowOutputData != null) onShowOutputData.run(); });

        dataButtonsBox.getChildren().addAll(showInputButton, showOutputButton);

        displayDefaultResults();

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        getChildren().addAll(titleBox, analyzingLabel, resultArea, spacer, dataButtonsBox);
    }

    private void setupComboBoxes() {
        timeUnitComboBox = new ComboBox<>();
        timeUnitComboBox.getItems().addAll("Milliseconds", "Seconds", "Minutes");
        timeUnitComboBox.setValue("Milliseconds");
        timeUnitComboBox.getStyleClass().add("combo-box-dark");
        timeUnitComboBox.setOnAction(e -> updateDisplays());

        memoryUnitComboBox = new ComboBox<>();
        memoryUnitComboBox.getItems().addAll("Bytes", "Kilobytes", "Megabytes");
        memoryUnitComboBox.setValue("Megabytes");
        memoryUnitComboBox.getStyleClass().add("combo-box-dark");
        memoryUnitComboBox.setOnAction(e -> updateDisplays());
    }

    public void setOnShowInputData(Runnable onShowInputData) { this.onShowInputData = onShowInputData; }
    public void setOnShowOutputData(Runnable onShowOutputData) { this.onShowOutputData = onShowOutputData; }

    private Runnable onShowTimeGraph;
    private Runnable onShowMemoryGraph;

    public void setOnShowTimeGraph(Runnable onShowTimeGraph) { this.onShowTimeGraph = onShowTimeGraph; }
    public void setOnShowMemoryGraph(Runnable onShowMemoryGraph) { this.onShowMemoryGraph = onShowMemoryGraph; }

    public void displayResults(AnalysisResult result) {
        lastExecutionTime = result.getExecutionTime();
        lastMemoryUsed = result.getMemoryUsed();
        lastThroughput = result.getThroughput();
        lastGcPauseTime = result.getGcPauseTime();
        lastHeapAllocationRate = result.getHeapAllocationRate();
        lastP50Latency = result.getP50Latency();
        lastP95Latency = result.getP95Latency();
        lastP99Latency = result.getP99Latency();
        updateDisplays();
    }

    public void displayDefaultResults() {
        lastExecutionTime = 0.0;
        lastMemoryUsed = 0.0;
        lastThroughput = 0.0;
        lastGcPauseTime = 0.0;
        lastHeapAllocationRate = 0.0;
        lastP50Latency = 0.0;
        lastP95Latency = 0.0;
        lastP99Latency = 0.0;
        updateDisplays();
    }

    private void updateDisplays() {
        resultArea.getChildren().clear();

        // Time Box
        BorderPane timeBox = new BorderPane();
        timeBox.getStyleClass().add("result-box");
        
        VBox timeLeft = new VBox(10);
        Label timeHeader = new Label("⏱ EXECUTION TIME");
        timeHeader.getStyleClass().add("result-header");
        
        HBox timeValueBox = new HBox(5);
        timeValueBox.setAlignment(Pos.BASELINE_LEFT);
        Label timeValue = new Label(formatTimeNumber(lastExecutionTime));
        timeValue.getStyleClass().add("result-value");
        Label timeUnitStr = new Label(formatTimeUnit());
        timeUnitStr.getStyleClass().add("result-unit");
        timeValueBox.getChildren().addAll(timeValue, timeUnitStr);
        
        Label timeSub = new Label("Average Execution Time");
        timeSub.getStyleClass().add("result-sub");
        
        timeLeft.getChildren().addAll(timeHeader, timeValueBox, timeSub);
        
        VBox timeRight = new VBox(20);
        timeRight.setAlignment(Pos.CENTER_RIGHT);
        
        HBox timeUnitBox = new HBox(10);
        timeUnitBox.setAlignment(Pos.CENTER_RIGHT);
        Label timeUnitLabel = new Label("Time Unit");
        timeUnitLabel.getStyleClass().add("result-unit-label");
        timeUnitBox.getChildren().addAll(timeUnitLabel, timeUnitComboBox);
        
        Button showTimeGraphBtn = new Button("📈 View Time Graph");
        showTimeGraphBtn.getStyleClass().add("data-button-outline");
        showTimeGraphBtn.setOnAction(e -> { if (onShowTimeGraph != null) onShowTimeGraph.run(); });
        
        timeRight.getChildren().addAll(timeUnitBox, showTimeGraphBtn);
        
        timeBox.setLeft(timeLeft);
        timeBox.setRight(timeRight);

        // Memory Box
        BorderPane memoryBox = new BorderPane();
        memoryBox.getStyleClass().add("result-box");
        
        VBox memoryLeft = new VBox(10);
        Label memoryHeader = new Label("🧠 MEMORY USAGE");
        memoryHeader.getStyleClass().add("result-header");
        
        HBox memoryValueBox = new HBox(5);
        memoryValueBox.setAlignment(Pos.BASELINE_LEFT);
        Label memoryValue = new Label(formatMemoryNumber(lastMemoryUsed));
        memoryValue.getStyleClass().add("result-value");
        Label memoryUnitStr = new Label(formatMemoryUnit());
        memoryUnitStr.getStyleClass().add("result-unit");
        memoryValueBox.getChildren().addAll(memoryValue, memoryUnitStr);
        
        Label memorySub = new Label("Average Memory Used");
        memorySub.getStyleClass().add("result-sub");
        
        memoryLeft.getChildren().addAll(memoryHeader, memoryValueBox, memorySub);
        
        VBox memoryRight = new VBox(20);
        memoryRight.setAlignment(Pos.CENTER_RIGHT);
        
        HBox memoryUnitBox = new HBox(10);
        memoryUnitBox.setAlignment(Pos.CENTER_RIGHT);
        Label memoryUnitLabel = new Label("Memory Unit");
        memoryUnitLabel.getStyleClass().add("result-unit-label");
        memoryUnitBox.getChildren().addAll(memoryUnitLabel, memoryUnitComboBox);
        
        Button showMemoryGraphBtn = new Button("📈 View Memory Graph");
        showMemoryGraphBtn.getStyleClass().add("data-button-outline");
        showMemoryGraphBtn.setOnAction(e -> { if (onShowMemoryGraph != null) onShowMemoryGraph.run(); });
        
        memoryRight.getChildren().addAll(memoryUnitBox, showMemoryGraphBtn);
        
        memoryBox.setLeft(memoryLeft);
        memoryBox.setRight(memoryRight);

        // Throughput Box
        BorderPane throughputBox = new BorderPane();
        throughputBox.getStyleClass().add("result-box");
        VBox throughputLeft = new VBox(10);
        Label throughputHeader = new Label("🚀 THROUGHPUT");
        throughputHeader.getStyleClass().add("result-header");
        HBox throughputValueBox = new HBox(5);
        throughputValueBox.setAlignment(Pos.BASELINE_LEFT);
        Label throughputValue = new Label(String.format("%.2f", lastThroughput));
        throughputValue.getStyleClass().add("result-value");
        Label throughputUnitStr = new Label("ops/sec");
        throughputUnitStr.getStyleClass().add("result-unit");
        throughputValueBox.getChildren().addAll(throughputValue, throughputUnitStr);
        Label throughputSub = new Label("Operations Per Second");
        throughputSub.getStyleClass().add("result-sub");
        throughputLeft.getChildren().addAll(throughputHeader, throughputValueBox, throughputSub);
        throughputBox.setLeft(throughputLeft);

        // GC Pause Box
        BorderPane gcBox = new BorderPane();
        gcBox.getStyleClass().add("result-box");
        VBox gcLeft = new VBox(10);
        Label gcHeader = new Label("⏱ GC PAUSE TIME");
        gcHeader.getStyleClass().add("result-header");
        HBox gcValueBox = new HBox(5);
        gcValueBox.setAlignment(Pos.BASELINE_LEFT);
        Label gcValue = new Label(String.format("%.2f", lastGcPauseTime));
        gcValue.getStyleClass().add("result-value");
        Label gcUnitStr = new Label("ms");
        gcUnitStr.getStyleClass().add("result-unit");
        gcValueBox.getChildren().addAll(gcValue, gcUnitStr);
        Label gcSub = new Label("Total GC Overhead");
        gcSub.getStyleClass().add("result-sub");
        gcLeft.getChildren().addAll(gcHeader, gcValueBox, gcSub);
        gcBox.setLeft(gcLeft);

        // Heap Allocation Box
        BorderPane heapBox = new BorderPane();
        heapBox.getStyleClass().add("result-box");
        VBox heapLeft = new VBox(10);
        Label heapHeader = new Label("📦 HEAP ALLOCATION RATE");
        heapHeader.getStyleClass().add("result-header");
        HBox heapValueBox = new HBox(5);
        heapValueBox.setAlignment(Pos.BASELINE_LEFT);
        Label heapValue = new Label(String.format("%.2f", lastHeapAllocationRate));
        heapValue.getStyleClass().add("result-value");
        Label heapUnitStr = new Label("MB/sec");
        heapUnitStr.getStyleClass().add("result-unit");
        heapValueBox.getChildren().addAll(heapValue, heapUnitStr);
        Label heapSub = new Label("Memory Allocated Per Second");
        heapSub.getStyleClass().add("result-sub");
        heapLeft.getChildren().addAll(heapHeader, heapValueBox, heapSub);
        heapBox.setLeft(heapLeft);

        // Latency Box
        BorderPane latencyBox = new BorderPane();
        latencyBox.getStyleClass().add("result-box");
        VBox latencyLeft = new VBox(10);
        Label latencyHeader = new Label("📊 LATENCY (SAMPLE TIME)");
        latencyHeader.getStyleClass().add("result-header");

        HBox latencyValueBox = new HBox(20);
        latencyValueBox.setAlignment(Pos.BASELINE_LEFT);
        
        VBox p50Box = new VBox(2);
        Label p50Label = new Label("p50");
        p50Label.getStyleClass().add("result-sub");
        HBox p50ValBox = new HBox(2);
        p50ValBox.setAlignment(Pos.BASELINE_LEFT);
        Label p50Val = new Label(String.format("%.2f", lastP50Latency));
        p50Val.getStyleClass().add("result-value");
        p50Val.setStyle("-fx-font-size: 18px;");
        Label p50Unit = new Label("ms");
        p50Unit.getStyleClass().add("result-unit");
        p50ValBox.getChildren().addAll(p50Val, p50Unit);
        p50Box.getChildren().addAll(p50Label, p50ValBox);

        VBox p95Box = new VBox(2);
        Label p95Label = new Label("p95");
        p95Label.getStyleClass().add("result-sub");
        HBox p95ValBox = new HBox(2);
        p95ValBox.setAlignment(Pos.BASELINE_LEFT);
        Label p95Val = new Label(String.format("%.2f", lastP95Latency));
        p95Val.getStyleClass().add("result-value");
        p95Val.setStyle("-fx-font-size: 18px;");
        Label p95Unit = new Label("ms");
        p95Unit.getStyleClass().add("result-unit");
        p95ValBox.getChildren().addAll(p95Val, p95Unit);
        p95Box.getChildren().addAll(p95Label, p95ValBox);

        VBox p99Box = new VBox(2);
        Label p99Label = new Label("p99");
        p99Label.getStyleClass().add("result-sub");
        HBox p99ValBox = new HBox(2);
        p99ValBox.setAlignment(Pos.BASELINE_LEFT);
        Label p99Val = new Label(String.format("%.2f", lastP99Latency));
        p99Val.getStyleClass().add("result-value");
        p99Val.setStyle("-fx-font-size: 18px;");
        Label p99Unit = new Label("ms");
        p99Unit.getStyleClass().add("result-unit");
        p99ValBox.getChildren().addAll(p99Val, p99Unit);
        p99Box.getChildren().addAll(p99Label, p99ValBox);

        latencyValueBox.getChildren().addAll(p50Box, p95Box, p99Box);

        Label latencySub = new Label("Latency Percentiles");
        latencySub.getStyleClass().add("result-sub");
        latencyLeft.getChildren().addAll(latencyHeader, latencyValueBox, latencySub);
        latencyBox.setLeft(latencyLeft);

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);

        grid.add(timeBox, 0, 0);
        grid.add(memoryBox, 1, 0);
        grid.add(throughputBox, 0, 1);
        grid.add(gcBox, 1, 1);
        grid.add(heapBox, 0, 2);
        grid.add(latencyBox, 1, 2);

        resultArea.getChildren().add(grid);
    }



    private String formatTimeNumber(double timeInMs) {
        String unit = timeUnitComboBox.getValue();
        switch (unit) {
            case "Seconds": return String.format("%.2f", timeInMs / 1000.0);
            case "Minutes": return String.format("%.2f", timeInMs / (1000.0 * 60));
            default: return String.format("%.2f", timeInMs);
        }
    }

    private String formatTimeUnit() {
        String unit = timeUnitComboBox.getValue();
        switch (unit) {
            case "Seconds": return "s";
            case "Minutes": return "min";
            default: return "ms";
        }
    }

    private String formatMemoryNumber(double bytes) {
        String unit = memoryUnitComboBox.getValue();
        switch (unit) {
            case "Kilobytes": return String.format("%.2f", bytes / 1024.0);
            case "Megabytes": return String.format("%.2f", bytes / (1024.0 * 1024));
            default: return String.format("%.2f", bytes);
        }
    }
    
    private String formatMemoryUnit() {
        String unit = memoryUnitComboBox.getValue();
        switch (unit) {
            case "Kilobytes": return "KB";
            case "Megabytes": return "MB";
            default: return "B";
        }
    }

    public void setAnalyzing(boolean analyzing, String message) {
        analyzingLabel.setVisible(analyzing);
        analyzingLabel.setManaged(analyzing);
        if (analyzing && message != null) {
            analyzingLabel.setText(message);
        } else if (analyzing) {
            analyzingLabel.setText("Analyzing...");
        }
    }
}
