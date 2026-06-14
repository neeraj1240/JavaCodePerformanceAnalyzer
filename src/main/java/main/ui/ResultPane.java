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
        timeUnitComboBox.setOnAction(e -> updateTimeDisplay());

        memoryUnitComboBox = new ComboBox<>();
        memoryUnitComboBox.getItems().addAll("Bytes", "Kilobytes", "Megabytes");
        memoryUnitComboBox.setValue("Megabytes");
        memoryUnitComboBox.getStyleClass().add("combo-box-dark");
        memoryUnitComboBox.setOnAction(e -> updateMemoryDisplay());
    }

    public void setOnShowInputData(Runnable onShowInputData) { this.onShowInputData = onShowInputData; }
    public void setOnShowOutputData(Runnable onShowOutputData) { this.onShowOutputData = onShowOutputData; }

    public void displayResults(AnalysisResult result) {
        lastExecutionTime = result.getExecutionTime();
        lastMemoryUsed = result.getMemoryUsed();
        updateDisplays();
    }

    public void displayDefaultResults() {
        lastExecutionTime = 0.0;
        lastMemoryUsed = 0.0;
        updateDisplays();
    }

    private void updateDisplays() {
        // Keep the graphs if they exist
        Node graphsBox = null;
        if (resultArea.getChildren().size() > 2) {
            graphsBox = resultArea.getChildren().get(2);
        }

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
        
        timeRight.getChildren().addAll(timeUnitBox);
        
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
        
        memoryRight.getChildren().addAll(memoryUnitBox);
        
        memoryBox.setLeft(memoryLeft);
        memoryBox.setRight(memoryRight);

        resultArea.getChildren().addAll(timeBox, memoryBox);
        
        if (graphsBox != null) {
            resultArea.getChildren().add(graphsBox);
        }
    }

    public void displayGraphs(Node timeGraph, Node memoryGraph) {
        HBox graphsBox = new HBox(20);
        graphsBox.setAlignment(Pos.CENTER);
        
        HBox.setHgrow(timeGraph, Priority.ALWAYS);
        HBox.setHgrow(memoryGraph, Priority.ALWAYS);
        
        graphsBox.getChildren().addAll(timeGraph, memoryGraph);
        
        if (resultArea.getChildren().size() == 2) {
            resultArea.getChildren().add(graphsBox);
        } else if (resultArea.getChildren().size() == 3) {
            resultArea.getChildren().set(2, graphsBox);
        }
    }

    private void updateTimeDisplay() {
        if (!resultArea.getChildren().isEmpty()) {
            BorderPane timeBox = (BorderPane) resultArea.getChildren().get(0);
            VBox timeLeft = (VBox) timeBox.getLeft();
            HBox timeValueBox = (HBox) timeLeft.getChildren().get(1);
            Label timeValue = (Label) timeValueBox.getChildren().get(0);
            Label timeUnitStr = (Label) timeValueBox.getChildren().get(1);
            timeValue.setText(formatTimeNumber(lastExecutionTime));
            timeUnitStr.setText(formatTimeUnit());
        }
    }

    private void updateMemoryDisplay() {
        if (!resultArea.getChildren().isEmpty() && resultArea.getChildren().size() > 1) {
            BorderPane memoryBox = (BorderPane) resultArea.getChildren().get(1);
            VBox memoryLeft = (VBox) memoryBox.getLeft();
            HBox memoryValueBox = (HBox) memoryLeft.getChildren().get(1);
            Label memoryValue = (Label) memoryValueBox.getChildren().get(0);
            Label memoryUnitStr = (Label) memoryValueBox.getChildren().get(1);
            memoryValue.setText(formatMemoryNumber(lastMemoryUsed));
            memoryUnitStr.setText(formatMemoryUnit());
        }
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
