package com.rawalpindi.waste;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class UserUpdateInfoScreen extends VBox {

    private final Simulation sim;
    private final Household loggedInUser;
    private final Runnable onUpdated;

    public UserUpdateInfoScreen(Simulation sim, Household loggedInUser, Runnable onUpdated) {
        this.sim = sim;
        this.loggedInUser = loggedInUser;
        this.onUpdated = onUpdated;

        getStyleClass().add("screen");
        setPadding(new Insets(30));
        setSpacing(20);
        setAlignment(Pos.TOP_CENTER);

        Label title = new Label("✏  Update Your Info");
        title.getStyleClass().add("screen-title");

        VBox form = new VBox(14);
        form.getStyleClass().add("card");
        form.setMaxWidth(480);
        form.setPadding(new Insets(24));

        // Current info display
        if (loggedInUser != null) {
            Label currentId = new Label("Household ID: #" + loggedInUser.getHouseholdID());
            currentId.getStyleClass().add("muted");
            form.getChildren().add(currentId);
        }

        Label nameLabel = new Label("Full Name");
        nameLabel.getStyleClass().add("card-title");
        TextField nameField = new TextField(loggedInUser != null ? loggedInUser.getOwnerName() : "");
        nameField.setPromptText("Enter your full name");

        Label addressLabel = new Label("Address / House No.");
        addressLabel.getStyleClass().add("card-title");
        TextField addressField = new TextField(loggedInUser != null ? loggedInUser.getAddress() : "");
        addressField.setPromptText("Enter your address");

        Button saveBtn = new Button("Save Changes");
        saveBtn.getStyleClass().add("primary-button");
        saveBtn.setMaxWidth(Double.MAX_VALUE);

        saveBtn.setOnAction(e -> {
            if (loggedInUser == null) return;
            String newName    = nameField.getText().trim();
            String newAddress = addressField.getText().trim();

            if (newName.isEmpty() || newAddress.isEmpty()) {
                showError("Fields cannot be empty.");
                return;
            }

            loggedInUser.setOwnerName(newName);
            loggedInUser.setAddress(newAddress);
            DataManager.saveHouseholds(sim.getHouseholds());

            if (onUpdated != null) onUpdated.run();
            showSuccess("Your info has been updated successfully!");
        });

        form.getChildren().addAll(nameLabel, nameField, addressLabel, addressField, saveBtn);
        getChildren().addAll(title, form);
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Update Failed");
        alert.setContentText(msg);
        alert.show();
    }

    private void showSuccess(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Info Updated");
        alert.setContentText(msg);
        alert.show();
    }
}
