package com.rawalpindi.waste;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ReportScreen extends VBox {

    private final Simulation sim;
    private final TextArea output = new TextArea();

    public ReportScreen(Simulation sim) {
        this.sim = sim;

        getStyleClass().add("screen");
        setPadding(new Insets(18));
        setSpacing(12);

        Label title = new Label("Report");
        title.getStyleClass().add("screen-title");

        Button generate = new Button("Generate Report");
        generate.getStyleClass().add("primary-button");
        generate.setOnAction(e -> generate());

        Button save = new Button("Save Report");
        save.getStyleClass().add("ghost-button");
        save.setOnAction(e -> saveReport());

        HBox actions = new HBox(10, generate, save);
        actions.setAlignment(Pos.CENTER_LEFT);

        output.getStyleClass().add("report-output");
        output.setWrapText(false);
        output.setPromptText("Click Generate Report...");
        output.setPrefHeight(600); // Increase preferred height
        VBox.setVgrow(output, javafx.scene.layout.Priority.ALWAYS); // Fill remaining space

        getChildren().addAll(title, actions, output);
    }

    public void refresh() {
        // keep current output; no-op for now
    }

    private void generate() {
        // Capture the existing console-style report output into the TextArea
        String text = ReportTextCapture.capture(sim);
        output.setText(text);
        
        // Reset simulation data as requested: waste, charges, and fines to zero
        sim.resetAllData();
        info("Report Generated", "Data has been reset to zero for the next period.");
    }

    private void saveReport() {
        if (output.getText().trim().isEmpty()) {
            info("Nothing to save", "Generate a report first.");
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Report");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text file", "*.txt"));
        chooser.setInitialFileName("weekly-report.txt");

        File file = chooser.showSaveDialog(getScene().getWindow());
        if (file == null) return;

        try (FileWriter fw = new FileWriter(file)) {
            fw.write(output.getText());
            info("Saved", "Report saved to:\n" + file.getAbsolutePath());
        } catch (IOException ex) {
            error("Save failed", ex.getMessage());
        }
    }

    private void info(String title, String content) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(title);
        a.setContentText(content);
        a.showAndWait();
    }

    private void error(String title, String content) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(title);
        a.setContentText(content);
        a.showAndWait();
    }
}

