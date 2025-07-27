package main.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.*;
import main.core.AnalysisResult;
import main.core.CodeAnalyzer;


public class CodeAnalyzerUI extends Application {
    private TextArea codeInputArea;
    private TextField inputSizeField;
    private VBox resultArea;
    private Label analyzingLabel;
    private CodeAnalyzer analyzer;
    private ComboBox<String> timeUnitComboBox;
    private ComboBox<String> memoryUnitComboBox;
    private double lastExecutionTime = 0.0;
    private double lastMemoryUsed = 0.0;
    private final List<Double> executionTimes = new ArrayList<>();
    private final List<Double> memoryUsages = new ArrayList<>();
    private final List<Integer> inputSizes = new ArrayList<>();
    private GraphManager graphManager;
    private String currentInput = "";
    private String currentOutput = "";
    private Stage inputDataStage;
    private Stage outputDataStage;

    private RadioButton manualInputRadio;
    private RadioButton randomInputRadio;
    private TextArea manualInputArea;
    private Label manualInputLabel;
    private RadioButton hardcodedInputRadio;

    private TextField minSizeField;
    private TextField maxSizeField;
    private TextField stepSizeField;
    private RadioButton singleInputRadio;
    private RadioButton rangeInputRadio;
    private VBox rangeInputBox;
    private ComboBox<String> arrayTypeComboBox;

    @Override
    public void start(Stage primaryStage) {
        try {
            Image icon = new Image(getClass().getResourceAsStream("/JAVA CODE PERFORMACE ANALYZER.png"));
            primaryStage.getIcons().add(icon);
        } catch (Exception e) {
            System.err.println("Could not load application icon: " + e.getMessage());
        }
        analyzer = new CodeAnalyzer();
        graphManager = new GraphManager(executionTimes, memoryUsages, inputSizes);

        HBox mainLayout = new HBox(20);
        mainLayout.getStyleClass().add("main-layout");
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(20));

        VBox leftPane = createLeftPane();
        HBox.setHgrow(leftPane, Priority.ALWAYS);

        VBox rightPane = createRightPane();
        HBox.setHgrow(rightPane, Priority.ALWAYS);

        mainLayout.getChildren().addAll(leftPane, rightPane);

        Scene scene = new Scene(mainLayout, 1200, 800);

        String css = getClass().getResource("/styles.css").toExternalForm();
        scene.getStylesheets().add(css);

        primaryStage.setTitle("Java Code Performance Analyzer");
        primaryStage.setScene(scene);
        primaryStage.show();

        toggleInputFields();
    }

    private void clearPreviousData() {
        executionTimes.clear();
        memoryUsages.clear();
        inputSizes.clear();
    }

    private VBox createLeftPane() {
        VBox leftPane = new VBox(10);
        leftPane.getStyleClass().add("left-pane");
        leftPane.setPadding(new Insets(20));
        leftPane.setPrefWidth(600);

        Label titleLabel = new Label("Code Input");
        titleLabel.getStyleClass().add("title-label");

        codeInputArea = new TextArea();
        codeInputArea.getStyleClass().add("code-input");
        codeInputArea.setWrapText(true);
        codeInputArea.setPrefRowCount(20);
        codeInputArea.setStyle("-fx-control-inner-background: #333333; -fx-text-fill: #ffffff; -fx-font-family: 'Consolas', monospace;");


        ToggleGroup inputTypeGroup = new ToggleGroup();
        singleInputRadio = new RadioButton("Single Input");
        rangeInputRadio = new RadioButton("Input Range");
        singleInputRadio.getStyleClass().add("radio-button");
        rangeInputRadio.getStyleClass().add("radio-button");
        singleInputRadio.setToggleGroup(inputTypeGroup);
        rangeInputRadio.setToggleGroup(inputTypeGroup);
        singleInputRadio.setSelected(true);

        HBox inputTypeBox = new HBox(10, singleInputRadio, rangeInputRadio);
        inputTypeBox.setAlignment(Pos.CENTER_LEFT);
        inputTypeBox.getStyleClass().add("input-section-box");


        manualInputRadio = new RadioButton("Manual Input");
        randomInputRadio = new RadioButton("Random Input");
        hardcodedInputRadio = new RadioButton("Hardcoded Input");
        manualInputRadio.getStyleClass().add("radio-button");
        randomInputRadio.getStyleClass().add("radio-button");
        hardcodedInputRadio.getStyleClass().add("radio-button");
        ToggleGroup singleInputGroup = new ToggleGroup();
        manualInputRadio.setToggleGroup(singleInputGroup);
        randomInputRadio.setToggleGroup(singleInputGroup);
        hardcodedInputRadio.setToggleGroup(singleInputGroup);
        randomInputRadio.setSelected(true);

        HBox singleInputBox = new HBox(10, manualInputRadio, randomInputRadio, hardcodedInputRadio);
        singleInputBox.setAlignment(Pos.CENTER_LEFT);
        singleInputBox.getStyleClass().add("input-section-box");


        HBox arrayTypeBox = new HBox(10);
        arrayTypeBox.setAlignment(Pos.CENTER_LEFT);
        Label arrayTypeLabel = new Label("Array Type:");
        arrayTypeLabel.setStyle("-fx-text-fill: #f0f0f0;");
        arrayTypeComboBox = new ComboBox<>();
        arrayTypeComboBox.getItems().addAll("Random", "Sorted", "Nearly Sorted");
        arrayTypeComboBox.setValue("Random");
        arrayTypeComboBox.setStyle("-fx-text-fill: black; -fx-background-color: white;");
        arrayTypeBox.getChildren().addAll(arrayTypeLabel, arrayTypeComboBox);


        HBox inputSizeBox = new HBox(10);
        inputSizeBox.setAlignment(Pos.CENTER_LEFT);
        Label sizeLabel = new Label("Input Size:");
        sizeLabel.setStyle("-fx-text-fill: #f0f0f0;");
        inputSizeField = new TextField();
        inputSizeField.setPrefWidth(100);
        inputSizeField.setStyle("-fx-text-fill: black; -fx-background-color: white;");
        inputSizeBox.getChildren().addAll(sizeLabel, inputSizeField);


        Label minSizeLabel = new Label("Min Size:");
        minSizeLabel.getStyleClass().add("text-field-label");
        minSizeField = new TextField();
        minSizeField.setPrefWidth(100);
        minSizeField.getStyleClass().add("text-field");

        Label maxSizeLabel = new Label("Max Size:");
        maxSizeLabel.getStyleClass().add("text-field-label");
        maxSizeField = new TextField();
        maxSizeField.setPrefWidth(100);
        maxSizeField.getStyleClass().add("text-field");

        Label stepSizeLabel = new Label("Step Size:");
        stepSizeLabel.getStyleClass().add("text-field-label");
        stepSizeField = new TextField();
        stepSizeField.setPrefWidth(100);
        stepSizeField.getStyleClass().add("text-field");

        GridPane rangeInputGrid = new GridPane();
        rangeInputGrid.setHgap(10);
        rangeInputGrid.setVgap(5);
        rangeInputGrid.getStyleClass().add("range-input-grid");
        rangeInputGrid.add(minSizeLabel, 0, 0);
        rangeInputGrid.add(minSizeField, 1, 0);
        rangeInputGrid.add(maxSizeLabel, 2, 0);
        rangeInputGrid.add(maxSizeField, 3, 0);
        rangeInputGrid.add(stepSizeLabel, 0, 1);
        rangeInputGrid.add(stepSizeField, 1, 1);

        rangeInputBox = new VBox(5, rangeInputGrid);
        rangeInputBox.getStyleClass().add("input-section-box");
        rangeInputBox.setVisible(false);


        manualInputLabel = new Label("Manual Input Data:");
        manualInputLabel.getStyleClass().add("text-field-label");
        manualInputLabel.setStyle("-fx-text-fill: #f0f0f0;");
        manualInputArea = new TextArea();
        manualInputArea.setWrapText(true);
        manualInputArea.setPrefRowCount(5);
        manualInputArea.getStyleClass().add("manual-input-area");
        manualInputArea.setDisable(true);
        manualInputArea.setVisible(false);


        singleInputRadio.setOnAction(e -> toggleInputMode());
        rangeInputRadio.setOnAction(e -> toggleInputMode());
        manualInputRadio.setOnAction(e -> toggleInputFields());
        randomInputRadio.setOnAction(e -> toggleInputFields());
        hardcodedInputRadio.setOnAction(e -> toggleInputFields());

        Button analyzeButton = new Button("Analyze");
        analyzeButton.getStyleClass().add("analyze-button");
        analyzeButton.setOnAction(e -> analyzeCode());

        Button clearButton = new Button("Clear");
        clearButton.getStyleClass().add("clear-button");
        clearButton.setOnAction(e -> clearInputs());

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(analyzeButton, clearButton);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        HBox userManualBox = new HBox();
        userManualBox.setAlignment(Pos.CENTER_RIGHT);
        Button userManualButton = new Button("User Manual");
        userManualButton.getStyleClass().add("user-manual-button");
        userManualButton.setOnAction(e -> showUserManual());
        userManualBox.getChildren().add(userManualButton);

        leftPane.getChildren().addAll(
                titleLabel,
                codeInputArea,
                inputTypeBox,
                singleInputBox,
                arrayTypeBox,
                rangeInputBox,
                manualInputLabel,
                manualInputArea,
                inputSizeBox,
                buttonBox,
                spacer,
                userManualBox
        );

        return leftPane;
    }

    private void showUserManual() {
        UserManualUI userManual = new UserManualUI();
        userManual.show();
    }

    private void toggleInputFields() {
        if (manualInputRadio.isSelected()) {
            manualInputArea.setDisable(false);
            manualInputArea.setVisible(true);
            manualInputLabel.setVisible(true);
            inputSizeField.setDisable(true);
            inputSizeField.setVisible(false);
        } else if (randomInputRadio.isSelected()) {
            manualInputArea.setDisable(true);
            manualInputArea.setVisible(false);
            manualInputLabel.setVisible(false);
            inputSizeField.setDisable(false);
            inputSizeField.setVisible(true);
            inputSizeField.setStyle("-fx-text-fill: black; -fx-background-color: white;");
        } else { // Hardcoded input selected
            manualInputArea.setDisable(true);
            manualInputArea.setVisible(false);
            manualInputLabel.setVisible(false);
            inputSizeField.setDisable(true);
            inputSizeField.setVisible(false);
        }
    }

    private void toggleInputMode() {
        if (singleInputRadio.isSelected()) {
            rangeInputBox.setVisible(false);
            manualInputRadio.setDisable(false);
            randomInputRadio.setDisable(false);
            hardcodedInputRadio.setDisable(false);
            toggleInputFields();
        } else if (rangeInputRadio.isSelected()) {
            rangeInputBox.setVisible(true);
            manualInputRadio.setDisable(true);
            randomInputRadio.setDisable(true);
            hardcodedInputRadio.setDisable(true);
            manualInputArea.setDisable(true);
            manualInputArea.setVisible(false);
            manualInputLabel.setVisible(false);
            inputSizeField.setDisable(true);
            inputSizeField.setVisible(false);
        }
    }

    private VBox createRightPane() {
        VBox rightPane = new VBox(10);
        rightPane.getStyleClass().add("right-pane");
        rightPane.setPadding(new Insets(20));
        rightPane.setPrefWidth(600);

        Label resultTitle = new Label("Analysis Results");
        resultTitle.getStyleClass().add("title-label");

        resultArea = new VBox(10);
        resultArea.getStyleClass().add("result-area");

        analyzingLabel = new Label("Analyzing...");
        analyzingLabel.getStyleClass().add("analyzing-label");
        analyzingLabel.setVisible(false);

        timeUnitComboBox = new ComboBox<>();
        timeUnitComboBox.getItems().addAll("Milliseconds", "Seconds", "Minutes");
        timeUnitComboBox.setValue("Milliseconds");
        timeUnitComboBox.setStyle("-fx-background-color: #3c3c3c; -fx-text-fill: white;");

        timeUnitComboBox.setOnMouseEntered(e ->
                timeUnitComboBox.setStyle("-fx-background-color: #4c4c4c; -fx-text-fill: white; -fx-cursor: hand;"));
        timeUnitComboBox.setOnMouseExited(e ->
                timeUnitComboBox.setStyle("-fx-background-color: #3c3c3c; -fx-text-fill: white;"));

        timeUnitComboBox.setOnAction(e -> updateTimeDisplay());

        timeUnitComboBox.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item);
                    setStyle("-fx-text-fill: white; -fx-font-family: 'Segoe UI Light'; -fx-font-size: 13px;");
                }
            }
        });

        timeUnitComboBox.setCellFactory(listView -> {
            ListCell<String> cell = new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null) {
                        setText(item);
                        setStyle("-fx-text-fill: white; -fx-font-family: 'Segoe UI Light'; -fx-font-size: 13px; -fx-background-color: #2b2b2b;");

                        // Add hover effect for each cell
                        setOnMouseEntered(e ->
                                setStyle("-fx-text-fill: white; -fx-font-family: 'Segoe UI Light'; -fx-font-size: 13px; -fx-background-color: #4c4c4c; -fx-cursor: hand;"));
                        setOnMouseExited(e ->
                                setStyle("-fx-text-fill: white; -fx-font-family: 'Segoe UI Light'; -fx-font-size: 13px; -fx-background-color: #2b2b2b;"));
                    }
                }
            };
            return cell;
        });

        memoryUnitComboBox = new ComboBox<>();
        memoryUnitComboBox.getItems().addAll("Bytes", "Kilobytes", "Megabytes");
        memoryUnitComboBox.setValue("Bytes");
        memoryUnitComboBox.setStyle("-fx-background-color: #3c3c3c; -fx-text-fill: white;");

        memoryUnitComboBox.setOnMouseEntered(e ->
                memoryUnitComboBox.setStyle("-fx-background-color: #4c4c4c; -fx-text-fill: white; -fx-cursor: hand;"));
        memoryUnitComboBox.setOnMouseExited(e ->
                memoryUnitComboBox.setStyle("-fx-background-color: #3c3c3c; -fx-text-fill: white;"));

        memoryUnitComboBox.setOnAction(e -> updateMemoryDisplay());

        memoryUnitComboBox.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item);
                    setStyle("-fx-text-fill: white; -fx-font-family: 'Segoe UI Light'; -fx-font-size: 13px;");
                }
            }
        });

        memoryUnitComboBox.setCellFactory(listView -> {
            ListCell<String> cell = new ListCell<String>() {
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
            };
            return cell;
        });

        HBox dataButtonsBox = new HBox(10);
        dataButtonsBox.setAlignment(Pos.CENTER);

        Button showInputButton = new Button("Show Input Data");
        showInputButton.getStyleClass().add("data-button");
        showInputButton.setOnAction(e -> showInputData());

        Button showOutputButton = new Button("Show Output Data");
        showOutputButton.getStyleClass().add("data-button");
        showOutputButton.setOnAction(e -> showOutputData());

        dataButtonsBox.getChildren().addAll(showInputButton, showOutputButton);

        displayDefaultResults();

        rightPane.getChildren().addAll(resultTitle, resultArea, analyzingLabel, dataButtonsBox);
        return rightPane;
    }

    private void showInputData() {
        if (currentInput.isEmpty()) {
            showAlert("No Data", "No input data available. Please run the analysis first.");
            return;
        }

        if (inputDataStage == null) {
            inputDataStage = new Stage();
            inputDataStage.setTitle("Input Data");
            inputDataStage.initModality(Modality.NONE);
        }

        VBox content = createDataDisplayContent("Input Data", currentInput);
        Scene scene = new Scene(content, 400, 500);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        inputDataStage.setScene(scene);
        inputDataStage.show();
    }

    private void showOutputData() {
        if (currentOutput.isEmpty()) {
            showAlert("No Data", "No output data available. Please run the analysis first.");
            return;
        }

        if (outputDataStage == null) {
            outputDataStage = new Stage();
            outputDataStage.setTitle("Output Data");
            outputDataStage.initModality(Modality.NONE);
        }

        VBox content = createDataDisplayContent("Output Data", currentOutput);
        Scene scene = new Scene(content, 400, 500);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        outputDataStage.setScene(scene);
        outputDataStage.show();
    }

    private VBox createDataDisplayContent(String title, String data) {
        VBox content = new VBox(10);
        content.getStyleClass().add("data-display");
        content.setPadding(new Insets(20));

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("title-label");
        titleLabel.setStyle("-fx-text-fill: #f0f0f0;");

        TextArea dataArea = new TextArea(data);
        dataArea.setEditable(false);
        dataArea.setWrapText(true);
        dataArea.setPrefRowCount(20);
        dataArea.getStyleClass().add("data-area");
        // Set text and background colors for better visibility
        dataArea.setStyle("-fx-control-inner-background: #2b2b2b; -fx-text-fill: #ffffff; -fx-font-family: 'Consolas', monospace; -fx-font-size: 13px;");

        Button closeButton = new Button("Close");
        closeButton.getStyleClass().add("close-button");
        closeButton.setOnAction(e -> {
            if (title.contains("Input")) {
                inputDataStage.close();
            } else {
                outputDataStage.close();
            }
        });

        content.getChildren().addAll(titleLabel, dataArea, closeButton);
        return content;
    }

    private void analyzeCode() {
        String code = codeInputArea.getText();
        if (code.isEmpty()) {
            showError("Please enter code to analyze.");
            return;
        }

        if (!analyzer.hasMainMethod(code)) {
            showError("No main method found! Please add a main method to your code.");
            return;
        }

        if (rangeInputRadio.isSelected()) {
            analyzeCodeWithRange(code);
            return;
        }

        String manualInput = manualInputArea.getText();
        String inputSizeText = inputSizeField.getText();

        try {

            final String finalInput;
            final int finalInputSize;

            if (manualInputRadio.isSelected()) {
                if (manualInput.isEmpty()) {
                    showError("Please enter manual input data.");
                    return;
                }
                finalInput = manualInput;
                finalInputSize = 0;
            } else if (randomInputRadio.isSelected()) {
                int inputSize = Integer.parseInt(inputSizeText);
                if (inputSize <= 0) {
                    showError("Input size must be greater than 0.");
                    return;
                }
                if (inputSize > 100000) {
                    showError("Input size cannot exceed 100,000");
                    return;
                }
                String arrayType = "random";
                switch (arrayTypeComboBox.getValue()) {
                    case "Sorted Array":
                        arrayType = "sorted";
                        break;
                    case "Nearly Sorted Array":
                        arrayType = "nearly-sorted";
                        break;
                    default:
                        arrayType = "random";
                }
                finalInput = analyzer.generateInput(code, inputSize, arrayType);
                finalInputSize = inputSize;
            } else {
                if (!analyzer.hasHardcodedInput(code)) {
                    showError("No hardcoded input found in the code. Please use hardcoded values or choose a different input type.");
                    return;
                }
                finalInput = "HARDCODED";
                finalInputSize = -1;
            }

            clearPreviousData();
            analyzingLabel.setVisible(true);

            Task<AnalysisResult> analysisTask = new Task<AnalysisResult>() {
                @Override
                protected AnalysisResult call() throws Exception {
                    AnalysisResult result = analyzer.analyzeCode(code, finalInput);
                    Platform.runLater(() -> {
                        currentInput = finalInput;
                        currentOutput = analyzer.getExecutionOutput();
                    });
                    return result;
                }
            };

            analysisTask.setOnSucceeded(e -> {
                analyzingLabel.setVisible(false);
                AnalysisResult result = analysisTask.getValue();
                executionTimes.add(Double.valueOf(result.getExecutionTime()));
                memoryUsages.add(Double.valueOf(result.getMemoryUsed()));
                inputSizes.add(Integer.valueOf(finalInputSize));
                displayResults(result);
            });

            analysisTask.setOnFailed(e -> {
                analyzingLabel.setVisible(false);
                Throwable exception = analysisTask.getException();
                showError(exception.getMessage());
            });

            new Thread(analysisTask).start();

        } catch (NumberFormatException e) {
            showError("Please enter a valid input size.");
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void analyzeCodeWithRange(String code) {
        try {
            int minSize = Integer.parseInt(minSizeField.getText());
            int maxSize = Integer.parseInt(maxSizeField.getText());
            int stepSize = Integer.parseInt(stepSizeField.getText());

            if (minSize <= 0 || maxSize <= 0 || stepSize <= 0) {
                showError("All size values must be greater than 0.");
                return;
            }

            if (minSize >= maxSize) {
                showError("Maximum size must be greater than minimum size.");
                return;
            }

            if (maxSize > 100000) {
                showError("Maximum size cannot exceed 100,000");
                return;
            }

            if (stepSize >= (maxSize - minSize)) {
                showError("Step size must be smaller than the range between min and max size.");
                return;
            }

            clearPreviousData();
            analyzingLabel.setVisible(true);

            Task<List<AnalysisResult>> analysisTask = new Task<List<AnalysisResult>>() {
                @Override
                protected List<AnalysisResult> call() throws Exception {
                    List<AnalysisResult> results = new ArrayList<>();
                    for (int currentSize = minSize; currentSize <= maxSize; currentSize += stepSize) {
                        final int finalSize = currentSize; // Create effectively final copy
                        updateMessage(String.format("Analyzing size: %d", finalSize));
                        String input = analyzer.generateInput(code, finalSize);
                        AnalysisResult result = analyzer.analyzeCode(code, input);
                        results.add(result);

                        // Update UI with effectively final variable
                        Platform.runLater(() -> {
                            executionTimes.add(result.getExecutionTime());
                            memoryUsages.add(result.getMemoryUsed());
                            inputSizes.add(finalSize);
                        });
                    }
                    // Set the input data for display
                    currentInput = analyzer.getGeneratedInput();
                    return results;
                }
            };

            analysisTask.messageProperty().addListener((obs, oldMsg, newMsg) -> {
                analyzingLabel.setText(newMsg);
            });

            analysisTask.setOnSucceeded(e -> {
                analyzingLabel.setVisible(false);
                analyzingLabel.setText("Analyzing...");
                List<AnalysisResult> results = analysisTask.getValue();
                if (!results.isEmpty()) {
                    // Store the last input/output data
                    AnalysisResult lastResult = results.get(results.size() - 1);
                    currentOutput = analyzer.getExecutionOutput();
                    displayResults(lastResult); // Display the last result
                }
            });

            analysisTask.setOnFailed(e -> {
                analyzingLabel.setVisible(false);
                Throwable exception = analysisTask.getException();
                showError(exception.getMessage());
            });

            new Thread(analysisTask).start();

        } catch (NumberFormatException e) {
            showError("Please enter valid numbers for min size, max size, and step size.");
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void displayResults(AnalysisResult result) {
        lastExecutionTime = result.getExecutionTime();
        lastMemoryUsed = result.getMemoryUsed();

        resultArea.getChildren().clear();

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
        timeGraphBtn.setOnAction(e -> showTimeGraph());
        timeBox.getChildren().addAll(timeLabel, timeUnitBox, timeGraphBtn);

        VBox memoryBox = new VBox(5);
        memoryBox.getStyleClass().add("result-box");
        Label memoryLabel = new Label(formatMemoryValue(lastMemoryUsed));
        memoryLabel.setStyle("-fx-text-fill: #b2ebf2;");

        HBox memoryUnitBox = new HBox(10);
        memoryUnitBox.setAlignment(Pos.CENTER_LEFT);
        Label memoryUnitLabel = new Label("Memory Unit:");
        memoryUnitLabel.setStyle("-fx-text-fill: #f0f0f0;");
        memoryUnitBox.getChildren().addAll(memoryUnitLabel, memoryUnitComboBox);

        Button memoryGraphBtn = new Button("Show Memory Graph");
        memoryGraphBtn.setStyle("-fx-text-fill: #f0f0f0;");
        memoryGraphBtn.setOnAction(e -> showMemoryGraph());
        memoryBox.getChildren().addAll(memoryLabel, memoryUnitBox, memoryGraphBtn);

        VBox timeComplexityBox = new VBox(5);
        timeComplexityBox.getStyleClass().add("result-box");
        Label timeComplexityLabel = new Label(String.format("Time Complexity: %s", result.getTimeComplexity()));
        timeComplexityLabel.setStyle("-fx-text-fill: #b2ebf2;");
        timeComplexityBox.getChildren().add(timeComplexityLabel);

        VBox spaceComplexityBox = new VBox(5);
        spaceComplexityBox.getStyleClass().add("result-box");
        Label spaceComplexityLabel = new Label(String.format("Space Complexity: %s", result.getSpaceComplexity()));
        spaceComplexityLabel.setStyle("-fx-text-fill: #b2ebf2;");
        spaceComplexityBox.getChildren().add(spaceComplexityLabel);

        resultArea.getChildren().addAll(timeBox, memoryBox, timeComplexityBox, spaceComplexityBox);
    }

    private void displayDefaultResults() {
        lastExecutionTime = 0.0;
        lastMemoryUsed = 0.0;

        resultArea.getChildren().clear();

        VBox timeBox = new VBox(5);
        timeBox.getStyleClass().add("result-box");
        Label timeLabel = new Label(formatTimeValue(0.0));
        timeLabel.setStyle("-fx-text-fill: #b2ebf2;");

        HBox timeUnitBox = new HBox(10);
        timeUnitBox.setAlignment(Pos.CENTER_LEFT);
        Label timeUnitLabel = new Label("Time Unit:");
        timeUnitLabel.setStyle("-fx-text-fill: #f0f0f0;");
        timeUnitBox.getChildren().addAll(timeUnitLabel, timeUnitComboBox);

        Button timeGraphBtn = new Button("Show Time Graph");
        timeGraphBtn.setStyle("-fx-text-fill: #f0f0f0;");
        timeBox.getChildren().addAll(timeLabel, timeUnitBox, timeGraphBtn);

        VBox memoryBox = new VBox(5);
        memoryBox.getStyleClass().add("result-box");
        Label memoryLabel = new Label(formatMemoryValue(0.0));
        memoryLabel.setStyle("-fx-text-fill: #b2ebf2;");

        HBox memoryUnitBox = new HBox(10);
        memoryUnitBox.setAlignment(Pos.CENTER_LEFT);
        Label memoryUnitLabel = new Label("Memory Unit:");
        memoryUnitLabel.getStyleClass().add("memory-unit-label");
        memoryUnitBox.getChildren().addAll(memoryUnitLabel, memoryUnitComboBox);

        Button memoryGraphBtn = new Button("Show Memory Graph");
        memoryGraphBtn.setStyle("-fx-text-fill: #f0f0f0;");
        memoryBox.getChildren().addAll(memoryLabel, memoryUnitBox, memoryGraphBtn);

        VBox timeComplexityBox = new VBox(5);
        timeComplexityBox.getStyleClass().add("result-box");
        Label timeComplexityLabel = new Label("Time Complexity: N/A");
        timeComplexityLabel.setStyle("-fx-text-fill: #b2ebf2;");
        timeComplexityBox.getChildren().add(timeComplexityLabel);

        VBox spaceComplexityBox = new VBox(5);
        spaceComplexityBox.getStyleClass().add("result-box");
        Label spaceComplexityLabel = new Label("Space Complexity: N/A");
        spaceComplexityLabel.setStyle("-fx-text-fill: #b2ebf2;");
        spaceComplexityBox.getChildren().add(spaceComplexityLabel);

        resultArea.getChildren().addAll(timeBox, memoryBox, timeComplexityBox, spaceComplexityBox);
    }

    private void updateTimeDisplay() {
        if (!resultArea.getChildren().isEmpty()) {
            VBox timeBox = (VBox) resultArea.getChildren().get(0);
            Label timeLabel = (Label) timeBox.getChildren().get(0);
            timeLabel.setText(formatTimeValue(lastExecutionTime));
        }
    }

    private void updateMemoryDisplay() {
        if (!resultArea.getChildren().isEmpty()) {
            VBox memoryBox = (VBox) resultArea.getChildren().get(1);
            Label memoryLabel = (Label) memoryBox.getChildren().get(0);
            memoryLabel.setText(formatMemoryValue(lastMemoryUsed));
        }
    }

    private String formatTimeValue(double timeInMs) {
        String unit = timeUnitComboBox.getValue();
        String baseFormat = "Average Execution Time: %.2f %s";
        switch (unit) {
            case "Seconds":
                return String.format(baseFormat, timeInMs / 1000.0, "s");
            case "Minutes":
                return String.format(baseFormat, timeInMs / (1000.0 * 60), "min");
            default: // Milliseconds
                return String.format(baseFormat, timeInMs, "ms");
        }
    }

    private String formatMemoryValue(double bytes) {
        String unit = memoryUnitComboBox.getValue();
        String baseFormat = "Average Memory Used: %.2f %s";
        switch (unit) {
            case "Kilobytes":
                return String.format(baseFormat, bytes / 1024.0, "KB");
            case "Megabytes":
                return String.format(baseFormat, bytes / (1024.0 * 1024), "MB");
            default: // Bytes
                return String.format(baseFormat, bytes, "bytes");
        }
    }

    private void clearInputs() {
        codeInputArea.clear();
        inputSizeField.clear();
        displayDefaultResults();
        analyzingLabel.setVisible(false);
        clearPreviousData();
    }

    private void showTimeGraph() {
        try {
            graphManager.showTimeGraph();
        } catch (IllegalStateException e) {
            showAlert("Error", e.getMessage());
        }
    }
    private void showMemoryGraph() {
        try {
            graphManager.showMemoryGraph();
        } catch (IllegalStateException e) {
            showAlert("Error", e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    public static void main(String[] args) {
        launch(args);
    }
}









