package main.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class CodeAnalyzerUI extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            Image icon = new Image(getClass().getResourceAsStream("/JAVA CODE PERFORMACE ANALYZER.png"));
            primaryStage.getIcons().add(icon);
        } catch (Exception e) {
            System.err.println("Could not load application icon: " + e.getMessage());
        }

        HBox mainLayout = new HBox(20);
        mainLayout.getStyleClass().add("main-layout");
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(20));

        InputPane leftPane = new InputPane();
        HBox.setHgrow(leftPane, Priority.ALWAYS);

        ResultPane rightPane = new ResultPane();
        HBox.setHgrow(rightPane, Priority.ALWAYS);

        // AnalysisController wires up the UI actions and background logic
        new AnalysisController(leftPane, rightPane);

        mainLayout.getChildren().addAll(leftPane, rightPane);

        Scene scene = new Scene(mainLayout, 1200, 800);

        String css = getClass().getResource("/styles.css").toExternalForm();
        scene.getStylesheets().add(css);

        primaryStage.setTitle("Java Code Performance Analyzer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
