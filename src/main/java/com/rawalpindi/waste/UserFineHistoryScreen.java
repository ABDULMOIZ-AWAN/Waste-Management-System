package com.rawalpindi.waste;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;

public class UserFineHistoryScreen extends VBox {

    private final Simulation sim;
    private final Household loggedInUser;
    private final VBox finesList = new VBox(10);

    public UserFineHistoryScreen(Simulation sim, Household loggedInUser) {
        this.sim = sim;
        this.loggedInUser = loggedInUser;

        getStyleClass().add("screen");
        setPadding(new Insets(20));
        setSpacing(16);

        Label title = new Label("📋 Fine History");
        title.getStyleClass().add("screen-title");

        Label subtitle = new Label("All fines issued to your household");
        subtitle.getStyleClass().add("muted");

        finesList.setPadding(new Insets(4));

        ScrollPane scroll = new ScrollPane(finesList);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        getChildren().addAll(title, subtitle, scroll);
        refresh();
    }

    public void refresh() {
        finesList.getChildren().clear();

        if (loggedInUser == null) return;

        boolean found = false;
        double total = 0.0;
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy");

        for (Fine f : sim.getFineManager().getAllFines()) {
            if (f.getHouseholdID() == loggedInUser.getHouseholdID()) {
                found = true;
                total += f.getAmount();

                HBox row = new HBox(16);
                row.getStyleClass().add("card");
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(12, 16, 12, 16));

                // Fine ID
                Label fineId = new Label("#" + f.getFineID());
                fineId.getStyleClass().add("card-title");
                fineId.setMinWidth(60);

                // Amount
                Label amount = new Label("Rs " + String.format("%.0f", f.getAmount()));
                amount.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 16px; -fx-font-weight: 900;");
                amount.setMinWidth(110);

                // Status badge
                Label status = new Label(f.isPaid() ? "PAID" : "UNPAID");
                status.setStyle(f.isPaid()
                    ? "-fx-background-color: #22c55e22; -fx-text-fill: #22c55e; -fx-background-radius: 8; -fx-padding: 4 10 4 10; -fx-font-weight: 700;"
                    : "-fx-background-color: #ef444422; -fx-text-fill: #ef4444; -fx-background-radius: 8; -fx-padding: 4 10 4 10; -fx-font-weight: 700;");
                status.setMinWidth(80);

                // Reason + Date
                VBox details = new VBox(3);
                HBox.setHgrow(details, Priority.ALWAYS);
                Label reason = new Label(f.getReason());
                reason.setStyle("-fx-text-fill: #f5f5f5; -fx-font-size: 13px;");
                reason.setWrapText(true);
                Label date = new Label(f.getDate() != null ? f.getDate().format(fmt) : "-");
                date.getStyleClass().add("muted");
                details.getChildren().addAll(reason, date);

                row.getChildren().addAll(fineId, amount, status, details);
                finesList.getChildren().add(row);
            }
        }

        if (!found) {
            Label none = new Label("✅  No fines issued to your household. Keep it up!");
            none.setStyle("-fx-text-fill: #22c55e; -fx-font-size: 15px; -fx-font-weight: 700;");
            finesList.getChildren().add(none);
        } else {
            // Total summary card
            HBox totalRow = new HBox();
            totalRow.getStyleClass().add("card");
            totalRow.setPadding(new Insets(12, 16, 12, 16));
            totalRow.setAlignment(Pos.CENTER_RIGHT);
            Label totalLabel = new Label("Total Outstanding: Rs " + String.format("%.0f", total));
            totalLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 15px; -fx-font-weight: 900;");
            totalRow.getChildren().add(totalLabel);
            finesList.getChildren().add(0, totalRow);
        }
    }
}
