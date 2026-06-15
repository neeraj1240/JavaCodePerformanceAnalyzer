package main.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import main.core.AnalysisResult;
import javafx.animation.Animation;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;

public class ResultPane extends VBox {
    private VBox resultArea;
    private HBox analyzingContainer;
    private Label analyzingLabel;
    private ScaleTransition[] dotTransitions = new ScaleTransition[3];
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
        Label resultTitle = new Label("ANALYSIS RESULTS");
        resultTitle.getStyleClass().add("title-label");
        UIUtils.setIcon(resultTitle, "/icons/analysis_result.png", 20);
        titleBox.getChildren().add(resultTitle);

        resultArea = new VBox(20);
        resultArea.getStyleClass().add("result-area");

        analyzingLabel = new Label("Analyzing...");
        analyzingLabel.getStyleClass().add("analyzing-label");

        HBox dotsBox = new HBox(6);
        dotsBox.setAlignment(Pos.CENTER);
        for (int i = 0; i < 3; i++) {
            Circle dot = new Circle(4, Color.web("#58a6ff"));
            dotTransitions[i] = new ScaleTransition(Duration.millis(500), dot);
            dotTransitions[i].setByX(0.5);
            dotTransitions[i].setByY(0.5);
            dotTransitions[i].setAutoReverse(true);
            dotTransitions[i].setCycleCount(Animation.INDEFINITE);
            dotTransitions[i].setDelay(Duration.millis(i * 150));
            dotsBox.getChildren().add(dot);
        }

        analyzingContainer = new HBox(15, dotsBox, analyzingLabel);
        analyzingContainer.setAlignment(Pos.CENTER);
        analyzingContainer.setVisible(false);
        analyzingContainer.setManaged(false);

        setupComboBoxes();

        HBox dataButtonsBox = new HBox(15);
        dataButtonsBox.setAlignment(Pos.CENTER);

        Button showInputButton = new Button("📄 Show Input Data");
        showInputButton.setTooltip(new Tooltip("View the actual input data that was used or generated during analysis"));
        showInputButton.getStyleClass().add("data-button-outline");
        showInputButton.setOnAction(e -> { if (onShowInputData != null) onShowInputData.run(); });

        Button showOutputButton = new Button("📄 Show Output Data");
        showOutputButton.setTooltip(new Tooltip("View the standard output (stdout) and standard error (stderr) produced by your code"));
        showOutputButton.getStyleClass().add("data-button-outline");
        showOutputButton.setOnAction(e -> { if (onShowOutputData != null) onShowOutputData.run(); });

        dataButtonsBox.getChildren().addAll(showInputButton, showOutputButton);

        displayDefaultResults();

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        getChildren().addAll(titleBox, resultArea, analyzingContainer, spacer, dataButtonsBox);
    }

    private void setupComboBoxes() {
        timeUnitComboBox = new ComboBox<>();
        timeUnitComboBox.setTooltip(new Tooltip("Select the unit of measurement for execution time"));
        timeUnitComboBox.getItems().addAll("Milliseconds", "Seconds", "Minutes");
        timeUnitComboBox.setValue("Milliseconds");
        timeUnitComboBox.getStyleClass().add("combo-box-small");
        timeUnitComboBox.setPrefHeight(30);
        timeUnitComboBox.setOnAction(e -> updateDisplays());

        memoryUnitComboBox = new ComboBox<>();
        memoryUnitComboBox.setTooltip(new Tooltip("Select the unit of measurement for memory usage"));
        memoryUnitComboBox.getItems().addAll("Bytes", "Kilobytes", "Megabytes");
        memoryUnitComboBox.setValue("Megabytes");
        memoryUnitComboBox.getStyleClass().add("combo-box-small");
        memoryUnitComboBox.setPrefHeight(30);
        memoryUnitComboBox.setOnAction(e -> updateDisplays());
    }

    public void setOnShowInputData(Runnable onShowInputData) { this.onShowInputData = onShowInputData; }
    public void setOnShowOutputData(Runnable onShowOutputData) { this.onShowOutputData = onShowOutputData; }

    private Runnable onShowTimeGraph;
    private Runnable onShowMemoryGraph;
    private Runnable onShowThroughputGraph;
    private Runnable onShowGcPauseTimeGraph;
    private Runnable onShowHeapAllocationRateGraph;
    private Runnable onShowLatencyGraph;

    public void setOnShowTimeGraph(Runnable onShowTimeGraph) { this.onShowTimeGraph = onShowTimeGraph; }
    public void setOnShowMemoryGraph(Runnable onShowMemoryGraph) { this.onShowMemoryGraph = onShowMemoryGraph; }
    public void setOnShowThroughputGraph(Runnable onShowThroughputGraph) { this.onShowThroughputGraph = onShowThroughputGraph; }
    public void setOnShowGcPauseTimeGraph(Runnable onShowGcPauseTimeGraph) { this.onShowGcPauseTimeGraph = onShowGcPauseTimeGraph; }
    public void setOnShowHeapAllocationRateGraph(Runnable onShowHeapAllocationRateGraph) { this.onShowHeapAllocationRateGraph = onShowHeapAllocationRateGraph; }
    public void setOnShowLatencyGraph(Runnable onShowLatencyGraph) { this.onShowLatencyGraph = onShowLatencyGraph; }

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
        VBox timeBox = new VBox(10);
        timeBox.getStyleClass().add("result-box");
        
        Label timeHeader = new Label("EXECUTION TIME");
        timeHeader.getStyleClass().add("result-header");
        UIUtils.setIcon(timeHeader, "/icons/execution_time.png", 18);
        
        BorderPane timeValueRow = new BorderPane();
        
        HBox timeValueBox = new HBox(5);
        timeValueBox.setAlignment(Pos.BASELINE_LEFT);
        Label timeValue = new Label(formatTimeNumber(lastExecutionTime));
        timeValue.getStyleClass().add("result-value");
        Label timeUnitStr = new Label(formatTimeUnit());
        timeUnitStr.getStyleClass().add("result-unit");
        timeValueBox.getChildren().addAll(timeValue, timeUnitStr);
        
        timeValueRow.setLeft(timeValueBox);
        timeValueRow.setRight(timeUnitComboBox);
        BorderPane.setAlignment(timeUnitComboBox, Pos.CENTER_RIGHT);
        
        HBox timeBottomRow = new HBox();
        timeBottomRow.setAlignment(Pos.CENTER_LEFT);
        Label timeSub = new Label("Average Execution Time");
        timeSub.getStyleClass().add("result-sub");
        Region timeSpacer = new Region();
        HBox.setHgrow(timeSpacer, Priority.ALWAYS);
        Button showTimeGraphBtn = new Button("📈 View Graph");
        showTimeGraphBtn.setTooltip(new Tooltip("Show execution time vs input size graph"));
        showTimeGraphBtn.getStyleClass().add("view-graph-btn");
        showTimeGraphBtn.setOnAction(e -> { if (onShowTimeGraph != null) onShowTimeGraph.run(); });
        timeBottomRow.getChildren().addAll(timeSub, timeSpacer, showTimeGraphBtn);
        
        timeBox.getChildren().addAll(timeHeader, timeValueRow, timeBottomRow);

        // Memory Box
        VBox memoryBox = new VBox(10);
        memoryBox.getStyleClass().add("result-box");
        
        Label memoryHeader = new Label("MEMORY USAGE");
        memoryHeader.getStyleClass().add("result-header");
        UIUtils.setIcon(memoryHeader, "/icons/memory_usage.png", 18);
        
        BorderPane memoryValueRow = new BorderPane();
        
        HBox memoryValueBox = new HBox(5);
        memoryValueBox.setAlignment(Pos.BASELINE_LEFT);
        Label memoryValue = new Label(formatMemoryNumber(lastMemoryUsed));
        memoryValue.getStyleClass().add("result-value");
        Label memoryUnitStr = new Label(formatMemoryUnit());
        memoryUnitStr.getStyleClass().add("result-unit");
        memoryValueBox.getChildren().addAll(memoryValue, memoryUnitStr);
        
        memoryValueRow.setLeft(memoryValueBox);
        memoryValueRow.setRight(memoryUnitComboBox);
        BorderPane.setAlignment(memoryUnitComboBox, Pos.CENTER_RIGHT);
        
        HBox memoryBottomRow = new HBox();
        memoryBottomRow.setAlignment(Pos.CENTER_LEFT);
        Label memorySub = new Label("Average Memory Used");
        memorySub.getStyleClass().add("result-sub");
        Region memorySpacer = new Region();
        HBox.setHgrow(memorySpacer, Priority.ALWAYS);
        Button showMemoryGraphBtn = new Button("📈 View Graph");
        showMemoryGraphBtn.setTooltip(new Tooltip("Show memory usage vs input size graph"));
        showMemoryGraphBtn.getStyleClass().add("view-graph-btn");
        showMemoryGraphBtn.setOnAction(e -> { if (onShowMemoryGraph != null) onShowMemoryGraph.run(); });
        memoryBottomRow.getChildren().addAll(memorySub, memorySpacer, showMemoryGraphBtn);
        
        memoryBox.getChildren().addAll(memoryHeader, memoryValueRow, memoryBottomRow);

        // Throughput Box
        VBox throughputBox = new VBox(10);
        throughputBox.getStyleClass().add("result-box");
        
        Label throughputHeader = new Label("THROUGHPUT");
        throughputHeader.getStyleClass().add("result-header");
        UIUtils.setIcon(throughputHeader, "/icons/throughput.png", 18);
        
        HBox throughputValueBox = new HBox(5);
        throughputValueBox.setAlignment(Pos.BASELINE_LEFT);
        Label throughputValue = new Label(String.format("%.2f", lastThroughput));
        throughputValue.getStyleClass().add("result-value");
        Label throughputUnitStr = new Label("ops/sec");
        throughputUnitStr.getStyleClass().add("result-unit");
        throughputValueBox.getChildren().addAll(throughputValue, throughputUnitStr);
        
        HBox throughputBottomRow = new HBox();
        throughputBottomRow.setAlignment(Pos.CENTER_LEFT);
        Label throughputSub = new Label("Operations Per Second");
        throughputSub.getStyleClass().add("result-sub");
        Region throughputSpacer = new Region();
        HBox.setHgrow(throughputSpacer, Priority.ALWAYS);
        Button showThroughputGraphBtn = new Button("📈 View Graph");
        showThroughputGraphBtn.setTooltip(new Tooltip("Show throughput (ops/sec) vs input size graph"));
        showThroughputGraphBtn.getStyleClass().add("view-graph-btn");
        showThroughputGraphBtn.setOnAction(e -> { if (onShowThroughputGraph != null) onShowThroughputGraph.run(); });
        throughputBottomRow.getChildren().addAll(throughputSub, throughputSpacer, showThroughputGraphBtn);
        
        throughputBox.getChildren().addAll(throughputHeader, throughputValueBox, throughputBottomRow);

        // GC Pause Box
        VBox gcBox = new VBox(10);
        gcBox.getStyleClass().add("result-box");
        
        Label gcHeader = new Label("GC PAUSE TIME");
        gcHeader.getStyleClass().add("result-header");
        UIUtils.setIcon(gcHeader, "/icons/gc_pause_time.png", 18);
        
        HBox gcValueBox = new HBox(5);
        gcValueBox.setAlignment(Pos.BASELINE_LEFT);
        Label gcValue = new Label(String.format("%.2f", lastGcPauseTime));
        gcValue.getStyleClass().add("result-value");
        Label gcUnitStr = new Label("ms");
        gcUnitStr.getStyleClass().add("result-unit");
        gcValueBox.getChildren().addAll(gcValue, gcUnitStr);
        
        HBox gcBottomRow = new HBox();
        gcBottomRow.setAlignment(Pos.CENTER_LEFT);
        Label gcSub = new Label("Total GC Overhead");
        gcSub.getStyleClass().add("result-sub");
        Region gcSpacer = new Region();
        HBox.setHgrow(gcSpacer, Priority.ALWAYS);
        Button showGcGraphBtn = new Button("📈 View Graph");
        showGcGraphBtn.setTooltip(new Tooltip("Show GC pause time vs input size graph"));
        showGcGraphBtn.getStyleClass().add("view-graph-btn");
        showGcGraphBtn.setOnAction(e -> { if (onShowGcPauseTimeGraph != null) onShowGcPauseTimeGraph.run(); });
        gcBottomRow.getChildren().addAll(gcSub, gcSpacer, showGcGraphBtn);
        
        gcBox.getChildren().addAll(gcHeader, gcValueBox, gcBottomRow);

        // Heap Allocation Box
        VBox heapBox = new VBox(10);
        heapBox.getStyleClass().add("result-box");
        
        Label heapHeader = new Label("HEAP ALLOCATION RATE");
        heapHeader.getStyleClass().add("result-header");
        UIUtils.setIcon(heapHeader, "/icons/heap.png", 18);
        
        HBox heapValueBox = new HBox(5);
        heapValueBox.setAlignment(Pos.BASELINE_LEFT);
        Label heapValue = new Label(String.format("%.2f", lastHeapAllocationRate));
        heapValue.getStyleClass().add("result-value");
        Label heapUnitStr = new Label("MB/sec");
        heapUnitStr.getStyleClass().add("result-unit");
        heapValueBox.getChildren().addAll(heapValue, heapUnitStr);
        
        HBox heapBottomRow = new HBox();
        heapBottomRow.setAlignment(Pos.CENTER_LEFT);
        Label heapSub = new Label("Memory Allocated Per Second");
        heapSub.getStyleClass().add("result-sub");
        Region heapSpacer = new Region();
        HBox.setHgrow(heapSpacer, Priority.ALWAYS);
        Button showHeapGraphBtn = new Button("📈 View Graph");
        showHeapGraphBtn.setTooltip(new Tooltip("Show heap allocation rate vs input size graph"));
        showHeapGraphBtn.getStyleClass().add("view-graph-btn");
        showHeapGraphBtn.setOnAction(e -> { if (onShowHeapAllocationRateGraph != null) onShowHeapAllocationRateGraph.run(); });
        heapBottomRow.getChildren().addAll(heapSub, heapSpacer, showHeapGraphBtn);
        
        heapBox.getChildren().addAll(heapHeader, heapValueBox, heapBottomRow);

        // Latency Box
        VBox latencyBox = new VBox(10);
        latencyBox.getStyleClass().add("result-box");
        
        Label latencyHeader = new Label("LATENCY (SAMPLE TIME)");
        latencyHeader.getStyleClass().add("result-header");
        UIUtils.setIcon(latencyHeader, "/icons/latency.png", 18);

        HBox latencyValueBox = new HBox(12);
        latencyValueBox.setAlignment(Pos.BASELINE_LEFT);
        
        VBox p50Box = new VBox(2);
        Label p50Label = new Label("p50");
        p50Label.getStyleClass().add("result-sub");
        HBox p50ValBox = new HBox(2);
        p50ValBox.setAlignment(Pos.BASELINE_LEFT);
        Label p50Val = new Label();
        p50Val.getStyleClass().add("result-value");
        Label p50Unit = new Label("ms");
        p50Unit.getStyleClass().add("result-unit");
        adjustLatencyFontSize(p50Val, p50Unit, lastP50Latency);
        p50ValBox.getChildren().addAll(p50Val, p50Unit);
        p50Box.getChildren().addAll(p50Label, p50ValBox);

        VBox p95Box = new VBox(2);
        Label p95Label = new Label("p95");
        p95Label.getStyleClass().add("result-sub");
        HBox p95ValBox = new HBox(2);
        p95ValBox.setAlignment(Pos.BASELINE_LEFT);
        Label p95Val = new Label();
        p95Val.getStyleClass().add("result-value");
        Label p95Unit = new Label("ms");
        p95Unit.getStyleClass().add("result-unit");
        adjustLatencyFontSize(p95Val, p95Unit, lastP95Latency);
        p95ValBox.getChildren().addAll(p95Val, p95Unit);
        p95Box.getChildren().addAll(p95Label, p95ValBox);

        VBox p99Box = new VBox(2);
        Label p99Label = new Label("p99");
        p99Label.getStyleClass().add("result-sub");
        HBox p99ValBox = new HBox(2);
        p99ValBox.setAlignment(Pos.BASELINE_LEFT);
        Label p99Val = new Label();
        p99Val.getStyleClass().add("result-value");
        Label p99Unit = new Label("ms");
        p99Unit.getStyleClass().add("result-unit");
        adjustLatencyFontSize(p99Val, p99Unit, lastP99Latency);
        p99ValBox.getChildren().addAll(p99Val, p99Unit);
        p99Box.getChildren().addAll(p99Label, p99ValBox);

        latencyValueBox.getChildren().addAll(p50Box, p95Box, p99Box);

        HBox latencyBottomRow = new HBox();
        latencyBottomRow.setAlignment(Pos.CENTER_LEFT);
        Label latencySub = new Label("Latency Percentiles");
        latencySub.getStyleClass().add("result-sub");
        Region latencySpacer = new Region();
        HBox.setHgrow(latencySpacer, Priority.ALWAYS);
        Button showLatencyGraphBtn = new Button("📈 View Graph");
        showLatencyGraphBtn.setTooltip(new Tooltip("Show latency percentiles vs input size graph"));
        showLatencyGraphBtn.getStyleClass().add("view-graph-btn");
        showLatencyGraphBtn.setOnAction(e -> { if (onShowLatencyGraph != null) onShowLatencyGraph.run(); });
        latencyBottomRow.getChildren().addAll(latencySub, latencySpacer, showLatencyGraphBtn);
        
        latencyBox.getChildren().addAll(latencyHeader, latencyValueBox, latencyBottomRow);

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
        analyzingContainer.setVisible(analyzing);
        analyzingContainer.setManaged(analyzing);
        if (analyzing && message != null) {
            analyzingLabel.setText(message);
        } else if (analyzing) {
            analyzingLabel.setText("Analyzing...");
        }
        
        for (ScaleTransition st : dotTransitions) {
            if (st != null) {
                if (analyzing) {
                    st.play();
                } else {
                    st.pause();
                }
            }
        }
    }

    private void adjustLatencyFontSize(Label valLabel, Label unitLabel, double value) {
        String formatted = String.format("%.2f", value);
        valLabel.setText(formatted);
        int len = formatted.length();
        if (len <= 5) {
            valLabel.setStyle("-fx-font-size: 18px;");
            unitLabel.setStyle("-fx-font-size: 12px;");
        } else if (len == 6) {
            valLabel.setStyle("-fx-font-size: 16px;");
            unitLabel.setStyle("-fx-font-size: 11px;");
        } else if (len == 7) {
            valLabel.setStyle("-fx-font-size: 14px;");
            unitLabel.setStyle("-fx-font-size: 10px;");
        } else if (len == 8) {
            valLabel.setStyle("-fx-font-size: 12px;");
            unitLabel.setStyle("-fx-font-size: 9px;");
        } else if (len == 9) {
            valLabel.setStyle("-fx-font-size: 11px;");
            unitLabel.setStyle("-fx-font-size: 8px;");
        } else {
            valLabel.setStyle("-fx-font-size: 9px;");
            unitLabel.setStyle("-fx-font-size: 8px;");
        }
    }
}
