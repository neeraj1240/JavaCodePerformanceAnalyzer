package main.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import main.core.AnalysisResult;

public class ResultPane extends VBox {
    private VBox resultArea;
    private Label analyzingLabel;
    private ComboBox<String> timeUnitComboBox;
    private ComboBox<String> memoryUnitComboBox;

    private double lastExecutionTime = 0.0;
    private double lastMemoryUsed = 0.0;

    private Runnable onShowTimeGraph;
    private Runnable onShowMemoryGraph;
    private Runnable onShowInputData;
    private Runnable onShowOutputData;

    public ResultPane() {
        super(10);
        getStyleClass().add("right-pane");
        setPadding(new Insets(20));
        setPrefWidth(600);

        Label resultTitle = new Label("Analysis Results");
        resultTitle.getStyleClass().add("title-label");

        resultArea = new VBox(10);
        resultArea.getStyleClass().add("result-area");

        analyzingLabel = new Label("Analyzing...");
        analyzingLabel.getStyleClass().add("analyzing-label");
        analyzingLabel.setVisible(false);

        setupComboBoxes();

        HBox dataButtonsBox = new HBox(10);
        dataButtonsBox.setAlignment(Pos.CENTER);

        Button showInputButton = new Button("Show Input Data");
        showInputButton.getStyleClass().add("data-button");
        showInputButton.setOnAction(e -> { if (onShowInputData != null) onShowInputData.run(); });

        Button showOutputButton = new Button("Show Output Data");
        showOutputButton.getStyleClass().add("data-button");
        showOutputButton.setOnAction(e -> { if (onShowOutputData != null) onShowOutputData.run(); });

        dataButtonsBox.getChildren().addAll(showInputButton, showOutputButton);

        displayDefaultResults();

        getChildren().addAll(resultTitle, resultArea, analyzingLabel, dataButtonsBox);
    }

    private void setupComboBoxes() {
        timeUnitComboBox = new ComboBox<>();
        timeUnitComboBox.getItems().addAll("Milliseconds", "Seconds", "Minutes");
        timeUnitComboBox.setValue("Milliseconds");
        styleComboBox(timeUnitComboBox);
        timeUnitComboBox.setOnAction(e -> updateTimeDisplay());

        memoryUnitComboBox = new ComboBox<>();
        memoryUnitComboBox.getItems().addAll("Bytes", "Kilobytes", "Megabytes");
        memoryUnitComboBox.setValue("Bytes");
        styleComboBox(memoryUnitComboBox);
        memoryUnitComboBox.setOnAction(e -> updateMemoryDisplay());
    }

    private void styleComboBox(ComboBox<String> comboBox) {
        comboBox.setStyle("-fx-background-color: #3c3c3c; -fx-text-fill: white;");
        comboBox.setOnMouseEntered(e ->
                comboBox.setStyle("-fx-background-color: #4c4c4c; -fx-text-fill: white; -fx-cursor: hand;"));
        comboBox.setOnMouseExited(e ->
                comboBox.setStyle("-fx-background-color: #3c3c3c; -fx-text-fill: white;"));

        comboBox.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item);
                    setStyle("-fx-text-fill: white; -fx-font-family: 'Segoe UI Light'; -fx-font-size: 13px;");
                }
            }
        });

        comboBox.setCellFactory(listView -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item);
                    setStyle("-fx-text-fill: white; -fx-font-family: 'Segoe UI Light'; -fx-font-size: 13px; -fx-background-color: #2b2b2b;");
                    setOnMouseEntered(e ->
                            setStyle("-fx-text-fill: white; -fx-font-family: 'Segoe UI Light'; -fx-font-size: 13px; -fx-background-color: #4c4c4c; -fx-cursor: hand;"));
                    setOnMouseExited(e ->
                            setStyle("-fx-text-fill: white; -fx-font-family: 'Segoe UI Light'; -fx-font-size: 13px; -fx-background-color: #2b2b2b;"));
                }
            }
        });
    }

    public void setOnShowTimeGraph(Runnable onShowTimeGraph) { this.onShowTimeGraph = onShowTimeGraph; }
    public void setOnShowMemoryGraph(Runnable onShowMemoryGraph) { this.onShowMemoryGraph = onShowMemoryGraph; }
    public void setOnShowInputData(Runnable onShowInputData) { this.onShowInputData = onShowInputData; }
    public void setOnShowOutputData(Runnable onShowOutputData) { this.onShowOutputData = onShowOutputData; }

    public void displayResults(AnalysisResult result) {
        lastExecutionTime = result.getExecutionTime();
        lastMemoryUsed = result.getMemoryUsed();
        updateDisplays(true);
    }

    public void displayDefaultResults() {
        lastExecutionTime = 0.0;
        lastMemoryUsed = 0.0;
        updateDisplays(false);
    }

    private void updateDisplays(boolean hasGraphAction) {
        resultArea.getChildren().clear();

        // Time Box
        VBox timeBox = new VBox(5);
        timeBox.getStyleClass().add("result-box");
        Label timeLabel = new Label(formatTimeValue(lastExecutionTime));
        timeLabel.setStyle("-fx-text-fill: #b2ebf2;");

        HBox timeUnitBox = new HBox(10);
        timeUnitBox.setAlignment(Pos.CENTER_LEFT);
        Label timeUnitLabel = new Label("Time Unit:");
        timeUnitLabel.setStyle("-fx-text-fill: #f0f0f0;");
        timeUnitBox.getChildren().addAll(timeUnitLabel, timeUnitComboBox);

        Button timeGraphBtn = new Button("Show Time Graph");
        timeGraphBtn.setStyle("-fx-text-fill: #f0f0f0;");
        if (hasGraphAction) {
            timeGraphBtn.setOnAction(e -> { if (onShowTimeGraph != null) onShowTimeGraph.run(); });
        }
        timeBox.getChildren().addAll(timeLabel, timeUnitBox, timeGraphBtn);

        // Memory Box
        VBox memoryBox = new VBox(5);
        memoryBox.getStyleClass().add("result-box");
        Label memoryLabel = new Label(formatMemoryValue(lastMemoryUsed));
        memoryLabel.setStyle("-fx-text-fill: #b2ebf2;");

        HBox memoryUnitBox = new HBox(10);
        memoryUnitBox.setAlignment(Pos.CENTER_LEFT);
        Label memoryUnitLabel = new Label("Memory Unit:");
        memoryUnitLabel.getStyleClass().add("memory-unit-label");
        memoryUnitLabel.setStyle("-fx-text-fill: #f0f0f0;");
        memoryUnitBox.getChildren().addAll(memoryUnitLabel, memoryUnitComboBox);

        Button memoryGraphBtn = new Button("Show Memory Graph");
        memoryGraphBtn.setStyle("-fx-text-fill: #f0f0f0;");
        if (hasGraphAction) {
            memoryGraphBtn.setOnAction(e -> { if (onShowMemoryGraph != null) onShowMemoryGraph.run(); });
        }
        memoryBox.getChildren().addAll(memoryLabel, memoryUnitBox, memoryGraphBtn);

        resultArea.getChildren().addAll(timeBox, memoryBox);
    }

    private void updateTimeDisplay() {
        if (!resultArea.getChildren().isEmpty()) {
            VBox timeBox = (VBox) resultArea.getChildren().get(0);
            Label timeLabel = (Label) timeBox.getChildren().get(0);
            timeLabel.setText(formatTimeValue(lastExecutionTime));
        }
    }

    private void updateMemoryDisplay() {
        if (!resultArea.getChildren().isEmpty() && resultArea.getChildren().size() > 1) {
            VBox memoryBox = (VBox) resultArea.getChildren().get(1);
            Label memoryLabel = (Label) memoryBox.getChildren().get(0);
            memoryLabel.setText(formatMemoryValue(lastMemoryUsed));
        }
    }

    private String formatTimeValue(double timeInMs) {
        String unit = timeUnitComboBox.getValue();
        String baseFormat = "Average Execution Time: %.2f %s";
        switch (unit) {
            case "Seconds": return String.format(baseFormat, timeInMs / 1000.0, "s");
            case "Minutes": return String.format(baseFormat, timeInMs / (1000.0 * 60), "min");
            default: return String.format(baseFormat, timeInMs, "ms");
        }
    }

    private String formatMemoryValue(double bytes) {
        String unit = memoryUnitComboBox.getValue();
        String baseFormat = "Average Memory Used: %.2f %s";
        switch (unit) {
            case "Kilobytes": return String.format(baseFormat, bytes / 1024.0, "KB");
            case "Megabytes": return String.format(baseFormat, bytes / (1024.0 * 1024), "MB");
            default: return String.format(baseFormat, bytes, "bytes");
        }
    }

    public void setAnalyzing(boolean analyzing, String message) {
        analyzingLabel.setVisible(analyzing);
        if (analyzing && message != null) {
            analyzingLabel.setText(message);
        } else if (analyzing) {
            analyzingLabel.setText("Analyzing...");
        }
    }
}
