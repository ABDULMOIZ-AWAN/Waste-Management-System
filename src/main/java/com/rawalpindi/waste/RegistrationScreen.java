package com.rawalpindi.waste;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class RegistrationScreen extends VBox {

    private final Simulation sim;
    private final FxMain app;

    public RegistrationScreen(Simulation sim, FxMain app) {
        this.sim = sim;
        this.app = app;

        getStyleClass().add("root");
        setAlignment(Pos.CENTER);
        setSpacing(20);
        setPadding(new Insets(40));

        VBox regBox = new VBox(15);
        regBox.getStyleClass().add("card");
        regBox.setMaxWidth(400);
        regBox.setAlignment(Pos.CENTER);
        regBox.setPadding(new Insets(30));

        Label title = new Label("Register Household");
        title.getStyleClass().add("screen-title");

        TextField nameField = new TextField();
        nameField.setPromptText("Owner Name");

        TextField addressField = new TextField();
        addressField.setPromptText("Address / House No.");

        // ---- Dynamically load zones from admin allocations ----
        ComboBox<String> zoneBox = new ComboBox<>();
        Set<String> availableZones = new LinkedHashSet<>();
        ArrayList<String> admins = DataManager.loadAdmins();
        for (String adminLine : admins) {
            String[] parts = adminLine.split("\\|");
            if (parts.length >= 3) {
                String[] zones = parts[2].split(",");
                for (String z : zones) {
                    availableZones.add(z.trim().toUpperCase());
                }
            }
        }
        zoneBox.getItems().addAll(availableZones);
        zoneBox.setPromptText("Select Zone (Admin Area)");
        zoneBox.setMaxWidth(Double.MAX_VALUE);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button registerBtn = new Button("Register");
        registerBtn.getStyleClass().add("primary-button");
        registerBtn.setMaxWidth(Double.MAX_VALUE);

        Button backBtn = new Button("Back to Login");
        backBtn.getStyleClass().add("ghost-button");
        backBtn.setMaxWidth(Double.MAX_VALUE);

        regBox.getChildren().addAll(title, nameField, addressField, zoneBox, passwordField, registerBtn, backBtn);

        registerBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String address = addressField.getText().trim();
            String zone = zoneBox.getValue();
            String password = passwordField.getText().trim();

            if (name.isEmpty() || address.isEmpty() || zone == null || password.isEmpty()) {
                showError("Please fill in all fields.");
                return;
            }

            // Register the household in the simulation
            Household h = sim.addHousehold(name, address, zone, password);

            // Auto-save ALL data to project directory:
            // 1) households.txt  (login credentials + address + zone)
            DataManager.saveHouseholds(sim.getHouseholds());
            // 2) fines.txt       (preserve any existing fines)
            DataManager.saveFines(sim.getFineManager().getAllFines());

            showSuccess(
                "Registration Successful!\n" +
                "Household ID: " + h.getHouseholdID() + "\n" +
                "Zone: " + zone + "\n" +
                "Your data has been saved automatically."
            );
            app.showLoginScreen();
        });

        backBtn.setOnAction(e -> {
            app.showLoginScreen();
        });

        getChildren().add(regBox);
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Registration Failed");
        alert.setContentText(msg);
        alert.show();
    }

    private void showSuccess(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Welcome to RWP Waste Management");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
