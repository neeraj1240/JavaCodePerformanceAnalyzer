package main.ui;

import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.*;



public class GraphManager {
    private final List<Double> executionTimes;
    private final List<Double> memoryUsages;
    private final List<Integer> inputSizes;

    private static final String CHART_LINE_COLOR = "#2196f3";
    private static final String CHART_BACKGROUND = "#ffffff";
    private static final String AXIS_COLOR = "#757575";

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

        // Create progressive data points
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

        // Create progressive data points
        addProgressiveDataPoints(series, memoryUsages);

        showGraph(lineChart, series, "Memory Usage Graph");
    }

    private void addProgressiveDataPoints(XYChart.Series<Number, Number> series, List<Double> values) {
        // Add origin point
        series.getData().add(new XYChart.Data<>(0, 0));

        // Sort data points by input size to ensure correct line progression
        List<DataPoint> dataPoints = new ArrayList<>();
        for (int i = 0; i < inputSizes.size(); i++) {
            dataPoints.add(new DataPoint(inputSizes.get(i), values.get(i)));
        }
        dataPoints.sort(Comparator.comparingInt(dp -> dp.inputSize));

        // Add sorted data points
        for (DataPoint dp : dataPoints) {
            series.getData().add(new XYChart.Data<>(dp.inputSize, dp.value));
        }

        // Add interpolated points between existing data points if gaps are too large
        List<XYChart.Data<Number, Number>> interpolatedPoints = new ArrayList<>();
        for (int i = 0; i < series.getData().size() - 1; i++) {
            XYChart.Data<Number, Number> current = series.getData().get(i);
            XYChart.Data<Number, Number> next = series.getData().get(i + 1);

            double gap = next.getXValue().doubleValue() - current.getXValue().doubleValue();
            if (gap > 50) { // Add interpolated points if gap is larger than 50
                double steps = Math.min(10, gap / 50); // Maximum 10 interpolated points
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

        // Add interpolated points to series
        series.getData().addAll(interpolatedPoints);

        // Sort all points by x value to ensure proper line drawing
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
        lineChart.getData().clear(); // Clear any existing data
        lineChart.getData().add(series);

        series.getNode().setStyle(
                "-fx-stroke: " + CHART_LINE_COLOR + ";" +
                        "-fx-stroke-width: 2px;"
        );

        // Check if there's already a window open with this title
        Stage existingStage = null;
        for (Stage stage : getAllStages()) {
            if (title.equals(stage.getTitle())) {
                existingStage = stage;
                break;
            }
        }

        Stage graphStage;
        if (existingStage != null) {
            // If window exists, update its content
            graphStage = existingStage;
            Scene existingScene = graphStage.getScene();
            StackPane existingPane = (StackPane) existingScene.getRoot();
            existingPane.getChildren().clear();
            existingPane.getChildren().add(lineChart);
        } else {
            // Create new window if none exists
            graphStage = new Stage();
            graphStage.setTitle(title);
            StackPane graphPane = new StackPane(lineChart);
            graphPane.setStyle("-fx-background-color: " + CHART_BACKGROUND + ";");
            Scene graphScene = new Scene(graphPane, 800, 600);
            graphStage.setMinWidth(600);
            graphStage.setMinHeight(400);
            graphStage.setScene(graphScene);
        }

        graphStage.show();
        graphStage.toFront();
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