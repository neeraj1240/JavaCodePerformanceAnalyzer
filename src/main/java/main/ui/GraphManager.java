package main.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
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

    private static final String CHART_LINE_COLOR = "#2196f3";
    private static final String CHART_BACKGROUND = "#ffffff";
    private static final String AXIS_COLOR = "#757575";

    private double zoomFactor = 1.0;
    private boolean isDataPointsVisible = true;
    private boolean showGridLines = true;
    private String currentTheme = "light";
    private final DecimalFormat decimalFormat = new DecimalFormat("#.##");


    private static final Map<String, ThemeColors> THEMES = new HashMap<>();
    static {
        THEMES.put("light", new ThemeColors("#ffffff", "#2196f3", "#757575"));
        THEMES.put("dark", new ThemeColors("#2b2b2b", "#64b5f6", "#bbbbbb"));
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

    public GraphManager(List<Double> executionTimes, List<Double> memoryUsages, List<Integer> inputSizes) {
        this.executionTimes = executionTimes;
        this.memoryUsages = memoryUsages;
        this.inputSizes = inputSizes;
    }

    private void clearPreviousData() {
        executionTimes.clear();
        memoryUsages.clear();
        inputSizes.clear();
    }

    public void showTimeGraph() {
        if (executionTimes.isEmpty()) {
            throw new IllegalStateException("No data available for time graph.");
        }

        NumberAxis xAxis = new NumberAxis("Input Size", 0,
                getMaxValue(inputSizes), calculateTickUnit(getMaxValue(inputSizes)));
        NumberAxis yAxis = new NumberAxis("Execution Time (ms)", 0,
                getMaxValue(executionTimes), calculateTickUnit(getMaxValue(executionTimes)));

        LineChart<Number, Number> lineChart = createLineChart(xAxis, yAxis, "Execution Time vs Input Size");
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Execution Time");

        addProgressiveDataPoints(series, executionTimes);

        showGraph(lineChart, series, "Execution Time Graph");
    }

    public void showMemoryGraph() {
        if (memoryUsages.isEmpty()) {
            throw new IllegalStateException("No data available for memory graph.");
        }

        NumberAxis xAxis = new NumberAxis("Input Size", 0,
                getMaxValue(inputSizes), calculateTickUnit(getMaxValue(inputSizes)));
        NumberAxis yAxis = new NumberAxis("Memory Usage (bytes)", 0,
                getMaxValue(memoryUsages), calculateTickUnit(getMaxValue(memoryUsages)));

        LineChart<Number, Number> lineChart = createLineChart(xAxis, yAxis, "Memory Usage vs Input Size");
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Memory Usage");

        addProgressiveDataPoints(series, memoryUsages);

        showGraph(lineChart, series, "Memory Usage Graph");
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

    private LineChart<Number, Number> createLineChart(NumberAxis xAxis, NumberAxis yAxis, String title) {
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle(title);
        lineChart.setCreateSymbols(true);
        lineChart.setAnimated(false);

        String cssStyle =
                ".chart-series-line {" +
                        "    -fx-stroke-width: 2px;" +
                        "    -fx-stroke: " + CHART_LINE_COLOR + ";" +
                        "}" +
                        ".chart-line-symbol {" +
                        "    -fx-background-color: " + CHART_LINE_COLOR + ", white;" +
                        "    -fx-background-insets: 0, 2;" +
                        "    -fx-background-radius: 5px;" +
                        "    -fx-padding: 5px;" +
                        "}";

        lineChart.getStylesheets().add("data:text/css," + cssStyle.replace(" ", "%20"));
        lineChart.setStyle(
                "-fx-background-color: " + CHART_BACKGROUND + ";" +
                        "-fx-padding: 10px;" +
                        "-fx-text-fill: " + AXIS_COLOR + ";"
        );

        lineChart.lookup(".chart-plot-background").setStyle(
                "-fx-background-color: white;"
        );

        lineChart.setTitleSide(javafx.geometry.Side.TOP);
        lineChart.setStyle(lineChart.getStyle() +
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;"
        );

        return lineChart;
    }

    private void showGraph(LineChart<Number, Number> lineChart, XYChart.Series<Number, Number> series, String title) {
        lineChart.getData().clear();
        lineChart.getData().add(series);

        // Add interactive features
        addDataPointInteraction(lineChart);
        VBox controlPanel = createControlPanel(lineChart);

        // Add zoom functionality
        addZoomCapability(lineChart);

        Stage graphStage = setupGraphStage(lineChart, controlPanel, title);
        graphStage.show();
        graphStage.toFront();
    }

    private void addDataPointInteraction(LineChart<Number, Number> chart) {
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

    private void addZoomCapability(LineChart<Number, Number> chart) {
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

    private VBox createControlPanel(LineChart<Number, Number> chart) {
        VBox controlPanel = new VBox(10);
        controlPanel.setPadding(new Insets(10));
        controlPanel.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10;");


        ComboBox<String> themeSelector = new ComboBox<>();
        themeSelector.getItems().addAll("Light", "Dark", "Contrast");
        themeSelector.setValue("Light");
        themeSelector.setOnAction(e -> applyTheme(chart, themeSelector.getValue().toLowerCase()));


        CheckBox showDataPoints = new CheckBox("Show Data Points");
        showDataPoints.setSelected(true);
        showDataPoints.setOnAction(e -> toggleDataPoints(chart, showDataPoints.isSelected()));

        // Toggle grid lines
        CheckBox showGrid = new CheckBox("Show Grid Lines");
        showGrid.setSelected(true);
        showGrid.setOnAction(e -> toggleGridLines(chart, showGrid.isSelected()));


        Button exportButton = new Button("Export Data");
        exportButton.setOnAction(e -> exportChartData(chart));


        Button resetZoomButton = new Button("Reset Zoom");
        resetZoomButton.setOnAction(e -> resetZoom(chart));

        controlPanel.getChildren().addAll(
                new Label("Controls:"),
                themeSelector,
                showDataPoints,
                showGrid,
                exportButton,
                resetZoomButton
        );

        return controlPanel;
    }

    private Stage setupGraphStage(LineChart<Number, Number> lineChart, VBox controlPanel, String title) {
        Stage graphStage = new Stage();
        graphStage.setTitle(title);

        HBox root = new HBox(10);
        root.setPadding(new Insets(10));
        root.getChildren().addAll(lineChart, controlPanel);
        HBox.setHgrow(lineChart, Priority.ALWAYS);

        Scene scene = new Scene(root);
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

    private void applyTheme(LineChart<Number, Number> chart, String themeName) {
        ThemeColors colors = THEMES.get(themeName);
        if (colors != null) {
            chart.setStyle(String.format(
                    "-fx-background-color: %s; -fx-text-fill: %s;",
                    colors.background, colors.textColor
            ));

            for (XYChart.Series<Number, Number> series : chart.getData()) {
                series.getNode().setStyle(String.format(
                        "-fx-stroke: %s; -fx-stroke-width: 2px;",
                        colors.lineColor
                ));
            }
        }
    }

    private void toggleDataPoints(LineChart<Number, Number> chart, boolean show) {
        chart.setCreateSymbols(show);
    }

    private void toggleGridLines(LineChart<Number, Number> chart, boolean show) {
        chart.setHorizontalGridLinesVisible(show);
        chart.setVerticalGridLinesVisible(show);
    }

    private void resetZoom(LineChart<Number, Number> chart) {
        NumberAxis xAxis = (NumberAxis) chart.getXAxis();
        NumberAxis yAxis = (NumberAxis) chart.getYAxis();

        xAxis.setAutoRanging(true);
        yAxis.setAutoRanging(true);
    }

    private void exportChartData(LineChart<Number, Number> chart) {
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
