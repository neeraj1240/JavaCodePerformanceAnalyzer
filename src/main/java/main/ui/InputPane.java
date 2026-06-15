package main.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class InputPane extends VBox {
    private TextArea codeInputArea;
    private ToggleButton singleInputBtn;
    private ToggleButton rangeInputBtn;
    
    private ToggleButton manualInputBtn;
    private ToggleButton randomInputBtn;
    private ToggleButton hardcodedInputBtn;
    
    private ComboBox<String> arrayTypeComboBox;
    
    private TextField inputSizeField;
    private TextField minSizeField;
    private TextField maxSizeField;
    private TextField stepSizeField;
    
    private TextArea manualInputArea;
    
    private VBox rangeInputBox;
    private VBox inputSizeBox;

    private Runnable onAnalyze;
    private Runnable onClear;

    public InputPane() {
        super(20);
        getStyleClass().add("left-pane");
        setPadding(new Insets(25));
        setPrefWidth(600);

        HBox titleBox = new HBox(10);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        Label titleLabel = new Label("</> CODE INPUT");
        titleLabel.getStyleClass().add("title-label");
        titleBox.getChildren().add(titleLabel);

        codeInputArea = new TextArea();
        codeInputArea.getStyleClass().add("code-input");
        codeInputArea.setWrapText(true);
        codeInputArea.setPrefRowCount(15);

        // Input Mode
        Label inputModeLabel = new Label("INPUT MODE");
        inputModeLabel.getStyleClass().add("section-label-header");
        
        ToggleGroup inputTypeGroup = new ToggleGroup();
        singleInputBtn = new ToggleButton("Single Input");
        rangeInputBtn = new ToggleButton("Input Range");
        singleInputBtn.getStyleClass().add("segment-button");
        rangeInputBtn.getStyleClass().add("segment-button");
        singleInputBtn.setToggleGroup(inputTypeGroup);
        rangeInputBtn.setToggleGroup(inputTypeGroup);
        singleInputBtn.setSelected(true);

        singleInputBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(singleInputBtn, Priority.ALWAYS);
        rangeInputBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(rangeInputBtn, Priority.ALWAYS);

        singleInputBtn.setTooltip(new Tooltip("Analyze the code for a specific input size"));
        rangeInputBtn.setTooltip(new Tooltip("Analyze the code across a range of input sizes to generate complexity graphs"));

        HBox inputTypeBox = new HBox(singleInputBtn, rangeInputBtn);
        inputTypeBox.getStyleClass().add("segment-box");

        // Data Source
        Label dataSourceLabel = new Label("DATA SOURCE");
        dataSourceLabel.getStyleClass().add("section-label-header");

        ToggleGroup singleInputGroup = new ToggleGroup();
        manualInputBtn = new ToggleButton("📝 Manual Input");
        randomInputBtn = new ToggleButton("🔀 Random Input");
        hardcodedInputBtn = new ToggleButton("🛠 Hardcoded Input");
        manualInputBtn.getStyleClass().add("segment-button");
        randomInputBtn.getStyleClass().add("segment-button");
        hardcodedInputBtn.getStyleClass().add("segment-button");
        manualInputBtn.setToggleGroup(singleInputGroup);
        randomInputBtn.setToggleGroup(singleInputGroup);
        hardcodedInputBtn.setToggleGroup(singleInputGroup);
        randomInputBtn.setSelected(true);

        manualInputBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(manualInputBtn, Priority.ALWAYS);
        randomInputBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(randomInputBtn, Priority.ALWAYS);
        hardcodedInputBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(hardcodedInputBtn, Priority.ALWAYS);

        manualInputBtn.setTooltip(new Tooltip("Provide your own specific input data manually"));
        randomInputBtn.setTooltip(new Tooltip("Automatically generate random input data based on size and type"));
        hardcodedInputBtn.setTooltip(new Tooltip("Use input data that is already hardcoded within your Java code"));

        HBox singleInputBox = new HBox(manualInputBtn, randomInputBtn, hardcodedInputBtn);
        singleInputBox.getStyleClass().add("segment-box");

        // Array Type
        Label arrayTypeLabel = new Label("ARRAY TYPE");
        arrayTypeLabel.getStyleClass().add("section-label-header");
        
        arrayTypeComboBox = new ComboBox<>();
        arrayTypeComboBox.getItems().addAll("Random", "Sorted", "Nearly Sorted");
        arrayTypeComboBox.setValue("Random");
        arrayTypeComboBox.setTooltip(new Tooltip("Select the characteristics of the generated array data"));
        arrayTypeComboBox.getStyleClass().add("combo-box-dark");
        arrayTypeComboBox.setMaxWidth(Double.MAX_VALUE);
        
        VBox arrayTypeBox = new VBox(5, arrayTypeLabel, arrayTypeComboBox);

        // Input Size
        Label sizeLabel = new Label("INPUT SIZE");
        sizeLabel.getStyleClass().add("section-label-header");
        
        inputSizeField = new TextField();
        inputSizeField.setTooltip(new Tooltip("Enter the number of elements or size of the input to generate"));
        inputSizeField.getStyleClass().add("text-field-dark");
        inputSizeField.setMaxWidth(Double.MAX_VALUE);
        inputSizeBox = new VBox(5, sizeLabel, inputSizeField);

        // Range Input
        Label minSizeLabel = new Label("Min Size:");
        minSizeLabel.getStyleClass().add("text-field-label");
        minSizeField = new TextField();
        minSizeField.setTooltip(new Tooltip("Starting input size for range analysis"));
        minSizeField.getStyleClass().add("text-field-dark");

        Label maxSizeLabel = new Label("Max Size:");
        maxSizeLabel.getStyleClass().add("text-field-label");
        maxSizeField = new TextField();
        maxSizeField.setTooltip(new Tooltip("Maximum input size for range analysis"));
        maxSizeField.getStyleClass().add("text-field-dark");

        Label stepSizeLabel = new Label("Step Size:");
        stepSizeLabel.getStyleClass().add("text-field-label");
        stepSizeField = new TextField();
        stepSizeField.setTooltip(new Tooltip("Increment amount between input sizes in the range"));
        stepSizeField.getStyleClass().add("text-field-dark");

        GridPane rangeInputGrid = new GridPane();
        rangeInputGrid.setHgap(10);
        rangeInputGrid.setVgap(10);
        rangeInputGrid.add(minSizeLabel, 0, 0);
        rangeInputGrid.add(minSizeField, 1, 0);
        rangeInputGrid.add(maxSizeLabel, 2, 0);
        rangeInputGrid.add(maxSizeField, 3, 0);
        rangeInputGrid.add(stepSizeLabel, 0, 1);
        rangeInputGrid.add(stepSizeField, 1, 1);

        rangeInputBox = new VBox(5, rangeInputGrid);
        rangeInputBox.setVisible(false);
        rangeInputBox.setManaged(false);

        // Manual Input Area
        manualInputArea = new TextArea();
        manualInputArea.setWrapText(true);
        manualInputArea.setPrefRowCount(3);
        manualInputArea.getStyleClass().add("code-input");
        manualInputArea.setVisible(false);
        manualInputArea.setManaged(false);

        singleInputBtn.setOnAction(e -> {
            if (!singleInputBtn.isSelected()) singleInputBtn.setSelected(true); // Prevent unselecting
            toggleInputMode();
        });
        rangeInputBtn.setOnAction(e -> {
            if (!rangeInputBtn.isSelected()) rangeInputBtn.setSelected(true);
            toggleInputMode();
        });
        manualInputBtn.setOnAction(e -> {
            if (!manualInputBtn.isSelected()) manualInputBtn.setSelected(true);
            toggleInputFields();
        });
        randomInputBtn.setOnAction(e -> {
            if (!randomInputBtn.isSelected()) randomInputBtn.setSelected(true);
            toggleInputFields();
        });
        hardcodedInputBtn.setOnAction(e -> {
            if (!hardcodedInputBtn.isSelected()) hardcodedInputBtn.setSelected(true);
            toggleInputFields();
        });

        // Bottom Buttons
        Button analyzeButton = new Button("▶ Analyze");
        analyzeButton.setTooltip(new Tooltip("Compile, run, and benchmark the provided code"));
        analyzeButton.getStyleClass().add("analyze-button");
        analyzeButton.setOnAction(e -> { if (onAnalyze != null) onAnalyze.run(); });

        Button clearButton = new Button("🗑 Clear");
        clearButton.setTooltip(new Tooltip("Clear all input, results, and graphs"));
        clearButton.getStyleClass().add("clear-button");
        clearButton.setOnAction(e -> { if (onClear != null) onClear.run(); });

        Button userManualButton = new Button("📖 User Manual");
        userManualButton.setTooltip(new Tooltip("Open the user manual for instructions"));
        userManualButton.getStyleClass().add("user-manual-button");
        userManualButton.setOnAction(e -> showUserManual());

        HBox buttonBox = new HBox(15, analyzeButton, clearButton);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        HBox bottomBox = new HBox(buttonBox, spacer, userManualButton);
        bottomBox.setAlignment(Pos.CENTER);

        VBox contentBox = new VBox(15, 
                inputModeLabel, inputTypeBox, 
                dataSourceLabel, singleInputBox, 
                arrayTypeBox, 
                rangeInputBox, manualInputArea, inputSizeBox);

        getChildren().addAll(
                titleBox,
                codeInputArea,
                contentBox,
                new Region(), // Spacer
                bottomBox
        );
        
        VBox.setVgrow(getChildren().get(3), Priority.ALWAYS); // Make spacer grow

        toggleInputFields();
    }

    private void toggleInputFields() {
        if (manualInputBtn.isSelected()) {
            manualInputArea.setManaged(true);
            manualInputArea.setVisible(true);
            inputSizeBox.setManaged(false);
            inputSizeBox.setVisible(false);
        } else if (randomInputBtn.isSelected()) {
            manualInputArea.setManaged(false);
            manualInputArea.setVisible(false);
            inputSizeBox.setManaged(true);
            inputSizeBox.setVisible(true);
        } else { // Hardcoded input selected
            manualInputArea.setManaged(false);
            manualInputArea.setVisible(false);
            inputSizeBox.setManaged(false);
            inputSizeBox.setVisible(false);
        }
    }

    private void toggleInputMode() {
        if (singleInputBtn.isSelected()) {
            rangeInputBox.setManaged(false);
            rangeInputBox.setVisible(false);
            manualInputBtn.setDisable(false);
            randomInputBtn.setDisable(false);
            hardcodedInputBtn.setDisable(false);
            toggleInputFields();
        } else if (rangeInputBtn.isSelected()) {
            rangeInputBox.setManaged(true);
            rangeInputBox.setVisible(true);
            manualInputBtn.setDisable(true);
            randomInputBtn.setDisable(true);
            hardcodedInputBtn.setDisable(true);
            manualInputArea.setManaged(false);
            manualInputArea.setVisible(false);
            inputSizeBox.setManaged(false);
            inputSizeBox.setVisible(false);
        }
    }

    private void showUserManual() {
        UserManualUI userManual = new UserManualUI();
        userManual.show();
    }

    public void setOnAnalyze(Runnable onAnalyze) { this.onAnalyze = onAnalyze; }
    public void setOnClear(Runnable onClear) { this.onClear = onClear; }

    public String getCode() { return codeInputArea.getText(); }
    public void clearCode() { codeInputArea.clear(); }

    public boolean isRangeInput() { return rangeInputBtn.isSelected(); }
    public boolean isManualInput() { return manualInputBtn.isSelected(); }
    public boolean isRandomInput() { return randomInputBtn.isSelected(); }

    public String getManualInputText() { return manualInputArea.getText(); }
    public String getInputSizeText() { return inputSizeField.getText(); }
    public void clearInputSize() { inputSizeField.clear(); }

    public String getArrayType() { return arrayTypeComboBox.getValue(); }

    public String getMinSizeText() { return minSizeField.getText(); }
    public String getMaxSizeText() { return maxSizeField.getText(); }
    public String getStepSizeText() { return stepSizeField.getText(); }
}
