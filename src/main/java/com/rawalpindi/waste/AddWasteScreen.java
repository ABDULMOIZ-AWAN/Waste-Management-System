package com.rawalpindi.waste;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AddWasteScreen extends VBox {

    private final Simulation sim;
    private final Runnable onAdded;
    private final Household loggedInUser;

    private final TextField householdIdField = new TextField();
    private final ComboBox<String> wasteType = new ComboBox<>(FXCollections.observableArrayList(
            "Organic", "Plastic", "Electronic", "Glass", "Metal"));
    private final TextField weightField = new TextField();

    // dynamic fields (one extra detail depending on waste type)
    private static final String CUSTOM_OPTION = "-- Enter Custom --";
    private final ComboBox<String> opt1 = new ComboBox<>();
    private final TextField customCategoryField = new TextField();
    private final TextField opt2 = new TextField();
    private final ComboBox<String> boolOpt = new ComboBox<>(FXCollections.observableArrayList("No", "Yes"));

    private final Label opt1Label = new Label("Category");
    private final Label customCategoryLabel = new Label("Custom Category");
    private final Label opt2Label = new Label("Brand");
    private final Label boolLabel = new Label("Flag");

    public AddWasteScreen(Simulation sim, Runnable onAdded, Household loggedInUser) {
        this.sim = sim;
        this.onAdded = onAdded;
        this.loggedInUser = loggedInUser;

        getStyleClass().add("screen");
        setPadding(new Insets(18));
        setSpacing(14);

        Label title = new Label("Add Waste");
        title.getStyleClass().add("screen-title");

        GridPane form = new GridPane();
        form.getStyleClass().add("card");
        form.setPadding(new Insets(16));
        form.setHgap(12);
        form.setVgap(10);

        householdIdField.setPromptText("e.g. 1");
        // If a user is logged in, pre-fill and lock their ID
        if (loggedInUser != null) {
            householdIdField.setText(String.valueOf(loggedInUser.getHouseholdID()));
            householdIdField.setEditable(false);
            householdIdField.setStyle("-fx-opacity: 0.7;");
        }
        weightField.setPromptText("e.g. 2.5");
        wasteType.getSelectionModel().selectFirst();

        customCategoryField.setPromptText("Type your custom category...");
        customCategoryField.setVisible(false);
        customCategoryField.setManaged(false);
        customCategoryLabel.setVisible(false);
        customCategoryLabel.setManaged(false);

        // Show / hide custom field when ComboBox selection changes
        opt1.setOnAction(e -> {
            boolean isCustom = CUSTOM_OPTION.equals(opt1.getValue());
            customCategoryField.setVisible(isCustom);
            customCategoryField.setManaged(isCustom);
            customCategoryLabel.setVisible(isCustom);
            customCategoryLabel.setManaged(isCustom);
            if (isCustom) customCategoryField.requestFocus();
        });

        configureDynamicFields();
        wasteType.setOnAction(e -> configureDynamicFields());

        int r = 0;
        form.add(new Label("Household ID"), 0, r);
        form.add(householdIdField, 1, r++);

        form.add(new Label("Waste Type"), 0, r);
        form.add(wasteType, 1, r++);

        form.add(new Label("Weight (kg)"), 0, r);
        form.add(weightField, 1, r++);

        form.add(opt1Label, 0, r);
        form.add(opt1, 1, r++);

        form.add(customCategoryLabel, 0, r);
        form.add(customCategoryField, 1, r++);

        form.add(opt2Label, 0, r);
        form.add(opt2, 1, r++);

        form.add(boolLabel, 0, r);
        form.add(boolOpt, 1, r++);

        Button add = new Button("Add Waste");
        add.getStyleClass().add("primary-button");
        add.setOnAction(e -> submit());

        HBox actions = new HBox(add);
        actions.setAlignment(Pos.CENTER_LEFT);

        getChildren().addAll(title, form, actions);
    }

    private void configureDynamicFields() {
        String type = wasteType.getValue();

        opt1.getItems().clear();
        opt2.clear();
        boolOpt.getSelectionModel().select("No");
        // Reset custom field whenever the waste type changes
        customCategoryField.clear();
        customCategoryField.setVisible(false);
        customCategoryField.setManaged(false);
        customCategoryLabel.setVisible(false);
        customCategoryLabel.setManaged(false);

        switch (type) {
            case "Organic":
                opt1Label.setText("Category");
                opt1.setItems(FXCollections.observableArrayList("FOOD", "GARDEN", "PAPER", CUSTOM_OPTION));
                opt1.getSelectionModel().selectFirst();

                opt2Label.setText("Notes (optional)");
                opt2.setPromptText("optional");

                boolLabel.setText("N/A");
                boolOpt.setDisable(true);
                break;

            case "Plastic":
                opt1Label.setText("Plastic Grade");
                opt1.setItems(FXCollections.observableArrayList("PET", "HDPE", "PVC", "LDPE", "PP", "PS", CUSTOM_OPTION));
                opt1.getSelectionModel().selectFirst();

                opt2Label.setText("Notes (optional)");
                opt2.setPromptText("optional");

                boolLabel.setText("N/A");
                boolOpt.setDisable(true);
                break;

            case "Electronic":
                opt1Label.setText("Device Type");
                opt1.setItems(FXCollections.observableArrayList("MOBILE", "LAPTOP", "BATTERY", "APPLIANCE", "TV", CUSTOM_OPTION));
                opt1.getSelectionModel().selectFirst();

                opt2Label.setText("Brand");
                opt2.setPromptText("e.g. Samsung");

                boolLabel.setText("N/A");
                boolOpt.setDisable(true);
                break;

            case "Glass":
                opt1Label.setText("Glass Color");
                opt1.setItems(FXCollections.observableArrayList("CLEAR", "GREEN", "BROWN", CUSTOM_OPTION));
                opt1.getSelectionModel().selectFirst();

                opt2Label.setText("Notes (optional)");
                opt2.setPromptText("optional");

                boolLabel.setText("Broken?");
                boolOpt.setDisable(false);
                break;

            case "Metal":
                opt1Label.setText("Metal Type");
                opt1.setItems(FXCollections.observableArrayList("ALUMINUM", "STEEL", "COPPER", "IRON", "BRASS", CUSTOM_OPTION));
                opt1.getSelectionModel().selectFirst();

                opt2Label.setText("Notes (optional)");
                opt2.setPromptText("optional");

                boolLabel.setText("Rusted?");
                boolOpt.setDisable(false);
                break;
        }
    }

    private void submit() {
        int hhId;
        double kg;
        try {
            hhId = Integer.parseInt(householdIdField.getText().trim());
            kg = Double.parseDouble(weightField.getText().trim());
        } catch (Exception ex) {
            error("Invalid input", "Please enter a valid Household ID and Weight.");
            return;
        }

        if (sim.findHousehold(hhId) == null) {
            error("Not found", "Household #" + hhId + " does not exist.");
            return;
        }

        String type = wasteType.getValue();
        // Resolve category: use custom text field if "-- Enter Custom --" is selected
        String v1;
        if (CUSTOM_OPTION.equals(opt1.getValue())) {
            v1 = customCategoryField.getText().trim().toUpperCase();
            if (v1.isEmpty()) {
                error("Missing Input", "Please type a custom category value.");
                return;
            }
        } else {
            v1 = opt1.getValue();
        }
        boolean flagYes = "Yes".equalsIgnoreCase(boolOpt.getValue());

        switch (type) {
            case "Organic":
                sim.addOrganicWaste(hhId, kg, v1);
                break;
            case "Plastic":
                sim.addPlasticWaste(hhId, kg, v1);
                break;
            case "Electronic":
                String brand = opt2.getText().trim();
                if (brand.isEmpty())
                    brand = "Unknown";
                sim.addElectronicWaste(hhId, kg, v1, brand);
                break;
            case "Glass":
                sim.addGlassWaste(hhId, kg, v1, flagYes);
                break;
            case "Metal":
                sim.addMetalWaste(hhId, kg, v1, flagYes);
                break;
        }

        info("Added", "Waste added successfully to Household #" + hhId);
        if (onAdded != null)
            onAdded.run();
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
