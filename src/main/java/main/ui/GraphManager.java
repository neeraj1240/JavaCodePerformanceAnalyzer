package main.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import javafx.geometry.Point2D;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.io.File;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.*;

public class GraphManager {
    private final List<Double> executionTimes;
    private final List<Double> memoryUsages;
    private final List<Integer> inputSizes;
    private final List<Double> throughputs;
    private final List<Double> gcPauseTimes;
    private final List<Double> heapAllocationRates;
    private final List<Double> p50Latencies;
    private final List<Double> p95Latencies;
    private final List<Double> p99Latencies;

    private static final String CHART_LINE_COLOR = "#2196f3";
    private static final String CHART_BACKGROUND = "#ffffff";
    private static final String AXIS_COLOR = "#757575";

    private double zoomFactor = 1.0;
    private boolean isDataPointsVisible = true;
    private boolean showGridLines = true;
    private String currentTheme = "light";
    private final DecimalFormat decimalFormat = new DecimalFormat("#.##");

    // Theme colors
    private static final Map<String, ThemeColors> THEMES = new HashMap<>();
    static {
        THEMES.put("light", new ThemeColors("#ffffff", "#2196f3", "#757575"));
        THEMES.put("dark", new ThemeColors("#0b0f19", "#1f6feb", "#c9d1d9"));
        THEMES.put("contrast", new ThemeColors("#000000", "#00ff00", "#ffffff"));
    }

    private static class ThemeColors {
        final String background;
        final String lineColor;
        final String textColor;

        ThemeColors(String background, String lineColor, String textColor) {
            this.background = background;
            this.lineColor = lineColor;
            this.textColor = textColor;
        }
    }

    public GraphManager(List<Double> executionTimes, List<Double> memoryUsages, List<Integer> inputSizes,
                        List<Double> throughputs, List<Double> gcPauseTimes, List<Double> heapAllocationRates,
                        List<Double> p50Latencies, List<Double> p95Latencies, List<Double> p99Latencies) {
        this.executionTimes = executionTimes;
        this.memoryUsages = memoryUsages;
        this.inputSizes = inputSizes;
        this.throughputs = throughputs;
        this.gcPauseTimes = gcPauseTimes;
        this.heapAllocationRates = heapAllocationRates;
        this.p50Latencies = p50Latencies;
        this.p95Latencies = p95Latencies;
        this.p99Latencies = p99Latencies;
    }

    private void clearPreviousData() {
        executionTimes.clear();
        memoryUsages.clear();
        inputSizes.clear();
        throughputs.clear();
        gcPauseTimes.clear();
        heapAllocationRates.clear();
        p50Latencies.clear();
        p95Latencies.clear();
        p99Latencies.clear();
    }

    public void showTimeGraph() {
        if (executionTimes.isEmpty()) {
            throw new IllegalStateException("No data available for time graph.");
        }

        NumberAxis xAxis = new NumberAxis("Input Size", 0,
                getMaxValue(inputSizes), calculateTickUnit(getMaxValue(inputSizes)));
        NumberAxis yAxis = new NumberAxis("Execution Time (ms)", 0,
                getMaxValue(executionTimes), calculateTickUnit(getMaxValue(executionTimes)));

        AreaChart<Number, Number> chart = createAreaChart(xAxis, yAxis, "Execution Time vs Input Size");
        chart.getStyleClass().add("embedded-time-chart");
        if (getClass().getResource("/styles.css") != null) {
            chart.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        }
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Execution Time");

        addProgressiveDataPoints(series, executionTimes);

        showGraph(chart, series, "Execution Time Graph");
    }

    public void showMemoryGraph() {
        if (memoryUsages.isEmpty()) {
            throw new IllegalStateException("No data available for memory graph.");
        }

        NumberAxis xAxis = new NumberAxis("Input Size", 0,
                getMaxValue(inputSizes), calculateTickUnit(getMaxValue(inputSizes)));
        NumberAxis yAxis = new NumberAxis("Memory Usage (bytes)", 0,
                getMaxValue(memoryUsages), calculateTickUnit(getMaxValue(memoryUsages)));

        AreaChart<Number, Number> chart = createAreaChart(xAxis, yAxis, "Memory Usage vs Input Size");
        chart.getStyleClass().add("embedded-memory-chart");
        if (getClass().getResource("/styles.css") != null) {
            chart.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        }
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Memory Usage");

        addProgressiveDataPoints(series, memoryUsages);

        showGraph(chart, series, "Memory Usage Graph");
    }

    public void showThroughputGraph() {
        if (throughputs.isEmpty()) throw new IllegalStateException("No data available for throughput graph.");
        NumberAxis xAxis = new NumberAxis("Input Size", 0, getMaxValue(inputSizes), calculateTickUnit(getMaxValue(inputSizes)));
        NumberAxis yAxis = new NumberAxis("Throughput (ops/sec)", 0, getMaxValue(throughputs), calculateTickUnit(getMaxValue(throughputs)));
        AreaChart<Number, Number> chart = createAreaChart(xAxis, yAxis, "Throughput vs Input Size");
        chart.getStyleClass().add("embedded-time-chart");
        if (getClass().getResource("/styles.css") != null) chart.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Throughput");
        addProgressiveDataPoints(series, throughputs);
        showGraph(chart, series, "Throughput Graph");
    }

    public void showGcPauseTimeGraph() {
        if (gcPauseTimes.isEmpty()) throw new IllegalStateException("No data available for GC pause time graph.");
        NumberAxis xAxis = new NumberAxis("Input Size", 0, getMaxValue(inputSizes), calculateTickUnit(getMaxValue(inputSizes)));
        NumberAxis yAxis = new NumberAxis("GC Pause Time (ms)", 0, getMaxValue(gcPauseTimes), calculateTickUnit(getMaxValue(gcPauseTimes)));
        AreaChart<Number, Number> chart = createAreaChart(xAxis, yAxis, "GC Pause Time vs Input Size");
        chart.getStyleClass().add("embedded-time-chart");
        if (getClass().getResource("/styles.css") != null) chart.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("GC Pause Time");
        addProgressiveDataPoints(series, gcPauseTimes);
        showGraph(chart, series, "GC Pause Time Graph");
    }

    public void showHeapAllocationRateGraph() {
        if (heapAllocationRates.isEmpty()) throw new IllegalStateException("No data available for heap allocation rate graph.");
        NumberAxis xAxis = new NumberAxis("Input Size", 0, getMaxValue(inputSizes), calculateTickUnit(getMaxValue(inputSizes)));
        NumberAxis yAxis = new NumberAxis("Heap Allocation Rate (MB/sec)", 0, getMaxValue(heapAllocationRates), calculateTickUnit(getMaxValue(heapAllocationRates)));
        AreaChart<Number, Number> chart = createAreaChart(xAxis, yAxis, "Heap Allocation Rate vs Input Size");
        chart.getStyleClass().add("embedded-memory-chart");
        if (getClass().getResource("/styles.css") != null) chart.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Heap Allocation Rate");
        addProgressiveDataPoints(series, heapAllocationRates);
        showGraph(chart, series, "Heap Allocation Rate Graph");
    }

    public void showLatencyGraph() {
        if (p50Latencies.isEmpty()) throw new IllegalStateException("No data available for latency graph.");
        double maxP99 = getMaxValue(p99Latencies);
        NumberAxis xAxis = new NumberAxis("Input Size", 0, getMaxValue(inputSizes), calculateTickUnit(getMaxValue(inputSizes)));
        NumberAxis yAxis = new NumberAxis("Latency (ms)", 0, maxP99, calculateTickUnit(maxP99));
        AreaChart<Number, Number> chart = createAreaChart(xAxis, yAxis, "Latency Percentiles vs Input Size");
        chart.getStyleClass().add("embedded-time-chart");
        if (getClass().getResource("/styles.css") != null) chart.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        
        XYChart.Series<Number, Number> p50Series = new XYChart.Series<>();
        p50Series.setName("p50");
        addProgressiveDataPoints(p50Series, p50Latencies);
        
        XYChart.Series<Number, Number> p95Series = new XYChart.Series<>();
        p95Series.setName("p95");
        addProgressiveDataPoints(p95Series, p95Latencies);
        
        XYChart.Series<Number, Number> p99Series = new XYChart.Series<>();
        p99Series.setName("p99");
        addProgressiveDataPoints(p99Series, p99Latencies);
        
        chart.setLegendVisible(true);
        showGraph(chart, Arrays.asList(p50Series, p95Series, p99Series), "Latency Graph");
    }

    public Node createEmbeddedTimeGraph() {
        if (executionTimes.isEmpty()) {
            Label noData = new Label("No data available");
            noData.getStyleClass().add("result-sub");
            return noData;
        }

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Execution");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Time (ms)");

        AreaChart<Number, Number> chart = new AreaChart<>(xAxis, yAxis);
        chart.setTitle("TIME GRAPH");
        chart.setCreateSymbols(true);
        chart.setAnimated(false);
        chart.setLegendVisible(false);
        
        chart.getStyleClass().add("embedded-time-chart");
        if (getClass().getResource("/styles.css") != null) {
            chart.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        }

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        addEmbeddedDataPoints(series, executionTimes);
        chart.getData().add(series);

        return wrapEmbeddedGraph(chart, this::showTimeGraph);
    }

    public Node createEmbeddedMemoryGraph() {
        if (memoryUsages.isEmpty()) {
            Label noData = new Label("No data available");
            noData.getStyleClass().add("result-sub");
            return noData;
        }

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Execution");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Memory (MB)");

        AreaChart<Number, Number> chart = new AreaChart<>(xAxis, yAxis);
        chart.setTitle("MEMORY GRAPH");
        chart.setCreateSymbols(true);
        chart.setAnimated(false);
        chart.setLegendVisible(false);
        
        chart.getStyleClass().add("embedded-memory-chart");
        if (getClass().getResource("/styles.css") != null) {
            chart.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        }

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        addEmbeddedDataPoints(series, memoryUsages);
        chart.getData().add(series);

        return wrapEmbeddedGraph(chart, this::showMemoryGraph);
    }

    private void addEmbeddedDataPoints(XYChart.Series<Number, Number> series, List<Double> values) {
        for (int i = 0; i < values.size(); i++) {
            series.getData().add(new XYChart.Data<>(i + 1, values.get(i)));
        }
    }

    private Node wrapEmbeddedGraph(Node chart, Runnable onExpand) {
        StackPane pane = new StackPane();
        pane.getStyleClass().add("embedded-graph-container");
        pane.getChildren().add(chart);
        
        Button expandBtn = new Button("↗");
        expandBtn.getStyleClass().add("expand-graph-btn");
        expandBtn.setOnAction(e -> onExpand.run());
        
        StackPane.setAlignment(expandBtn, Pos.TOP_RIGHT);
        StackPane.setMargin(expandBtn, new Insets(10));
        
        pane.getChildren().add(expandBtn);
        return pane;
    }

    private void addProgressiveDataPoints(XYChart.Series<Number, Number> series, List<Double> values) {
        series.getData().add(new XYChart.Data<>(0, 0));

        List<DataPoint> dataPoints = new ArrayList<>();
        for (int i = 0; i < inputSizes.size(); i++) {
            dataPoints.add(new DataPoint(inputSizes.get(i), values.get(i)));
        }
        dataPoints.sort(Comparator.comparingInt(dp -> dp.inputSize));

        for (DataPoint dp : dataPoints) {
            series.getData().add(new XYChart.Data<>(dp.inputSize, dp.value));
        }

        List<XYChart.Data<Number, Number>> interpolatedPoints = new ArrayList<>();
        for (int i = 0; i < series.getData().size() - 1; i++) {
            XYChart.Data<Number, Number> current = series.getData().get(i);
            XYChart.Data<Number, Number> next = series.getData().get(i + 1);

            double gap = next.getXValue().doubleValue() - current.getXValue().doubleValue();
            if (gap > 50) {
                double steps = Math.min(10, gap / 50);
                for (int j = 1; j < steps; j++) {
                    double x = current.getXValue().doubleValue() + (gap * j / steps);
                    double y = interpolateValue(
                            current.getXValue().doubleValue(), current.getYValue().doubleValue(),
                            next.getXValue().doubleValue(), next.getYValue().doubleValue(),
                            x
                    );
                    interpolatedPoints.add(new XYChart.Data<>(x, y));
                }
            }
        }

        series.getData().addAll(interpolatedPoints);

        series.getData().sort((a, b) ->
                Double.compare(a.getXValue().doubleValue(), b.getXValue().doubleValue()));
    }

    private double interpolateValue(double x1, double y1, double x2, double y2, double x) {
        return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
    }

    private static class DataPoint {
        int inputSize;
        double value;

        DataPoint(int inputSize, double value) {
            this.inputSize = inputSize;
            this.value = value;
        }
    }

    private AreaChart<Number, Number> createAreaChart(NumberAxis xAxis, NumberAxis yAxis, String title) {
        AreaChart<Number, Number> chart = new AreaChart<>(xAxis, yAxis);
        chart.setTitle(title);
        chart.setCreateSymbols(true);
        chart.setAnimated(false);

        // Allow CSS to style it
        chart.setTitleSide(javafx.geometry.Side.TOP);
        return chart;
    }

    private void showGraph(AreaChart<Number, Number> AreaChart, XYChart.Series<Number, Number> series, String title) {
        showGraph(AreaChart, Collections.singletonList(series), title);
    }

    private void showGraph(AreaChart<Number, Number> AreaChart, List<XYChart.Series<Number, Number>> seriesList, String title) {
        AreaChart.getData().clear();
        AreaChart.getData().addAll(seriesList);

        // Add interactive features
        addDataPointInteraction(AreaChart);
        VBox controlPanel = createControlPanel(AreaChart);

        // Add zoom functionality
        addZoomCapability(AreaChart);

        Stage graphStage = setupGraphStage(AreaChart, controlPanel, title);
        applyTheme(AreaChart, "contrast");
        graphStage.show();
        graphStage.toFront();
    }

    private void addDataPointInteraction(AreaChart<Number, Number> chart) {
        for (XYChart.Series<Number, Number> series : chart.getData()) {
            for (XYChart.Data<Number, Number> data : series.getData()) {
                Node node = data.getNode();
                if (node != null) {
                    Tooltip tooltip = new Tooltip();
                    tooltip.setShowDelay(Duration.millis(100));

                    node.setOnMouseEntered(event -> {
                        String tooltipText = String.format(
                                "Input Size: %d%nValue: %s",
                                data.getXValue().intValue(),
                                decimalFormat.format(data.getYValue())
                        );
                        tooltip.setText(tooltipText);
                        Tooltip.install(node, tooltip);
                        node.setScaleX(1.5);
                        node.setScaleY(1.5);
                    });

                    node.setOnMouseExited(event -> {
                        Tooltip.uninstall(node, tooltip);
                        node.setScaleX(1);
                        node.setScaleY(1);
                    });

                    // Context menu for data point
                    ContextMenu contextMenu = new ContextMenu();
                    MenuItem detailsItem = new MenuItem("Show Details");
                    detailsItem.setOnAction(e -> showDataPointDetails(data));
                    contextMenu.getItems().add(detailsItem);

                    node.setOnMouseClicked(event -> {
                        if (event.getButton() == MouseButton.SECONDARY) {
                            contextMenu.show(node, event.getScreenX(), event.getScreenY());
                        }
                    });
                }
            }
        }
    }

    private void addZoomCapability(AreaChart<Number, Number> chart) {
        chart.setOnScroll(event -> {
            event.consume();
            if (event.isControlDown()) {
                double zoomFactor = 1.1;
                if (event.getDeltaY() < 0) {
                    zoomFactor = 1/zoomFactor;
                }

                NumberAxis xAxis = (NumberAxis) chart.getXAxis();
                NumberAxis yAxis = (NumberAxis) chart.getYAxis();

                double centerX = xAxis.getValueForDisplay(event.getX()).doubleValue();
                double centerY = yAxis.getValueForDisplay(event.getY()).doubleValue();

                xAxis.setAutoRanging(false);
                yAxis.setAutoRanging(false);

                double newXLowerBound = centerX - (centerX - xAxis.getLowerBound()) * zoomFactor;
                double newXUpperBound = centerX + (xAxis.getUpperBound() - centerX) * zoomFactor;
                double newYLowerBound = centerY - (centerY - yAxis.getLowerBound()) * zoomFactor;
                double newYUpperBound = centerY + (yAxis.getUpperBound() - centerY) * zoomFactor;

                xAxis.setLowerBound(newXLowerBound);
                xAxis.setUpperBound(newXUpperBound);
                yAxis.setLowerBound(newYLowerBound);
                yAxis.setUpperBound(newYUpperBound);
            }
        });

        // Pan functionality
        final ObjectProperty<Point2D> mouseAnchor = new SimpleObjectProperty<>();
        chart.setOnMousePressed(event -> {
            mouseAnchor.set(new Point2D(event.getX(), event.getY()));
        });

        chart.setOnMouseDragged(event -> {
            if (mouseAnchor.get() != null && event.isPrimaryButtonDown()) {
                double deltaX = event.getX() - mouseAnchor.get().getX();
                double deltaY = event.getY() - mouseAnchor.get().getY();

                NumberAxis xAxis = (NumberAxis) chart.getXAxis();
                NumberAxis yAxis = (NumberAxis) chart.getYAxis();

                double xAxisScale = (xAxis.getUpperBound() - xAxis.getLowerBound()) / chart.getWidth();
                double yAxisScale = (yAxis.getUpperBound() - yAxis.getLowerBound()) / chart.getHeight();

                xAxis.setLowerBound(xAxis.getLowerBound() - deltaX * xAxisScale);
                xAxis.setUpperBound(xAxis.getUpperBound() - deltaX * xAxisScale);
                yAxis.setLowerBound(yAxis.getLowerBound() + deltaY * yAxisScale);
                yAxis.setUpperBound(yAxis.getUpperBound() + deltaY * yAxisScale);

                mouseAnchor.set(new Point2D(event.getX(), event.getY()));
            }
        });
    }

    private VBox createControlPanel(AreaChart<Number, Number> chart) {
        VBox controlPanel = new VBox(10);
        controlPanel.setPadding(new Insets(10));
        controlPanel.setStyle("-fx-background-color: #1a1e29; -fx-padding: 10;");

        Label controlsLabel = new Label("Controls:");
        controlsLabel.setStyle("-fx-text-fill: white;");

        // Toggle data points
        CheckBox showDataPoints = new CheckBox("Show Data Points");
        showDataPoints.setSelected(true);
        showDataPoints.setStyle("-fx-text-fill: white;");
        showDataPoints.setOnAction(e -> toggleDataPoints(chart, showDataPoints.isSelected()));

        // Toggle grid lines
        CheckBox showGrid = new CheckBox("Show Grid Lines");
        showGrid.setSelected(true);
        showGrid.setStyle("-fx-text-fill: white;");
        showGrid.setOnAction(e -> toggleGridLines(chart, showGrid.isSelected()));

        // Export button
        Button exportButton = new Button("Export Data");
        exportButton.setOnAction(e -> exportChartData(chart));

        // Reset zoom button
        Button resetZoomButton = new Button("Reset Zoom");
        resetZoomButton.setOnAction(e -> resetZoom(chart));

        controlPanel.getChildren().addAll(
                controlsLabel,
                showDataPoints,
                showGrid,
                exportButton,
                resetZoomButton
        );

        return controlPanel;
    }

    private Stage setupGraphStage(AreaChart<Number, Number> AreaChart, VBox controlPanel, String title) {
        Stage graphStage = new Stage();
        graphStage.setTitle(title);

        HBox root = new HBox(10);
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #0b0f19;");
        root.getChildren().addAll(AreaChart, controlPanel);
        HBox.setHgrow(AreaChart, Priority.ALWAYS);

        Scene scene = new Scene(root);
        if (getClass().getResource("/styles.css") != null) {
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        }
        graphStage.setScene(scene);
        graphStage.setMinWidth(1000);
        graphStage.setMinHeight(600);

        return graphStage;
    }

    private void showDataPointDetails(XYChart.Data<Number, Number> data) {
        Stage detailsStage = new Stage();
        detailsStage.setTitle("Data Point Details");

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.getChildren().addAll(
                new Label(String.format("Input Size: %d", data.getXValue().intValue())),
                new Label(String.format("Value: %s", decimalFormat.format(data.getYValue())))
        );

        Scene scene = new Scene(content);
        detailsStage.setScene(scene);
        detailsStage.show();
    }

    private void applyTheme(AreaChart<Number, Number> chart, String themeName) {
        ThemeColors colors = THEMES.get(themeName);
        if (colors != null) {
            chart.setStyle(String.format(
                    "-fx-background-color: %s; -fx-text-fill: %s;",
                    colors.background, colors.textColor
            ));
        }
    }

    private void toggleDataPoints(AreaChart<Number, Number> chart, boolean show) {
        chart.setCreateSymbols(show);
    }

    private void toggleGridLines(AreaChart<Number, Number> chart, boolean show) {
        chart.setHorizontalGridLinesVisible(show);
        chart.setVerticalGridLinesVisible(show);
    }

    private void resetZoom(AreaChart<Number, Number> chart) {
        NumberAxis xAxis = (NumberAxis) chart.getXAxis();
        NumberAxis yAxis = (NumberAxis) chart.getYAxis();

        xAxis.setAutoRanging(true);
        yAxis.setAutoRanging(true);
    }

    private void exportChartData(AreaChart<Number, Number> chart) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Data");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );

        File file = fileChooser.showSaveDialog(chart.getScene().getWindow());
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.println("Input Size,Value");

                for (XYChart.Series<Number, Number> series : chart.getData()) {
                    for (XYChart.Data<Number, Number> data : series.getData()) {
                        writer.printf("%d,%s%n",
                                data.getXValue().intValue(),
                                decimalFormat.format(data.getYValue())
                        );
                    }
                }
            } catch (Exception e) {
                showError("Export Error", "Failed to export data: " + e.getMessage());
            }
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private List<Stage> getAllStages() {
        List<Stage> stages = new ArrayList<>();
        for (Window window : Stage.getWindows()) {
            if (window instanceof Stage) {
                stages.add((Stage) window);
            }
        }
        return stages;
    }

    private double getMaxValue(List<? extends Number> list) {
        return list.stream()
                .mapToDouble(Number::doubleValue)
                .max()
                .orElse(100.0) * 1.1;
    }

    private double calculateTickUnit(double maxValue) {
        int magnitude = (int) Math.floor(Math.log10(maxValue));
        return Math.pow(10, magnitude - 1);
    }
}
