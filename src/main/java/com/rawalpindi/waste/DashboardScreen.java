package com.rawalpindi.waste;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PathTransition;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.util.ArrayList;

public class DashboardScreen extends VBox {

    private final Simulation sim;
    private final boolean isAdmin;
    private final Household loggedInUser;
    private final ArrayList<String> adminZones;

    // Admin stats labels
    private final Label statHouseholds  = new Label("-");
    private final Label statWaste       = new Label("-");
    private final Label statCharges     = new Label("-");
    private final Label statFines       = new Label("-");
    private final Label statTrucks      = new Label("-");

    // User stats labels
    private final Label statMyCharges   = new Label("-");
    private final Label statMyFines     = new Label("-");
    private final Label statCompliance  = new Label("-");

    public DashboardScreen(Simulation sim, boolean isAdmin, Household loggedInUser, ArrayList<String> adminZones) {
        this.sim = sim;
        this.isAdmin = isAdmin;
        this.loggedInUser = loggedInUser;
        this.adminZones = adminZones != null ? adminZones : new ArrayList<>();

        getStyleClass().add("screen");
        setPadding(new Insets(18));
        setSpacing(14);

        HBox topRow = new HBox(14);
        topRow.setAlignment(Pos.TOP_LEFT);

        VBox leftCol = new VBox(14);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        // Greeting
        Label greeting;
        if (isAdmin) {
            greeting = new Label("Admin Dashboard");
        } else {
            String name = loggedInUser != null ? loggedInUser.getOwnerName() : "Resident";
            greeting = new Label("Welcome, " + name + "!");
        }
        greeting.getStyleClass().add("screen-title");
        leftCol.getChildren().add(greeting);

        if (isAdmin) {
            // 5-stat cards for admin
            FlowPane adminStats = new FlowPane(12, 12);
            adminStats.getChildren().addAll(
                statCard("🏠 Total Households",  statHouseholds),
                statCard("♻ Total Waste", statWaste),
                statCard("💰 Total Value",  statCharges),
                statCard("⚠ Total Fines",    statFines),
                statCard("🚛 Active Trucks",  statTrucks)
            );
            leftCol.getChildren().addAll(adminStats, buildAdminHintPanel());
        } else {
            // 3-stat cards for user
            HBox userStats = new HBox(12,
                statCard("Monthly Fee", statMyCharges),
                statCard("Total Fines",     statMyFines),
                statCard("My Compliance",   statCompliance)
            );
            leftCol.getChildren().addAll(userStats, buildUserHintPanel());
        }

        Button refreshBtn = new Button("Refresh Stats");
        refreshBtn.getStyleClass().add("primary-button");
        refreshBtn.setOnAction(e -> refresh());
        leftCol.getChildren().add(refreshBtn);

        StackPane hero3d = buildLogoHero();
        hero3d.setMinWidth(340);
        hero3d.setPrefWidth(380);

        topRow.getChildren().addAll(leftCol, hero3d);
        getChildren().add(topRow);
        refresh();
    }

    private VBox statCard(String title, Label value) {
        VBox card = new VBox(6);
        card.getStyleClass().add("card");
        card.setMinWidth(160);
        Label t = new Label(title);
        t.getStyleClass().add("card-title");
        value.getStyleClass().add("card-value");
        card.getChildren().addAll(t, value);
        return card;
    }

    private StackPane buildLogoHero() {
        ImageView logoView = new ImageView();
        try {
            logoView.setImage(new Image(getClass().getResourceAsStream("/images/logo.png")));
        } catch (Exception e) {
            System.err.println("Logo image not found in /images/logo.png");
        }
        logoView.setFitWidth(360);
        logoView.setPreserveRatio(true);

        RotateTransition rotate = new RotateTransition(Duration.seconds(8), logoView);
        rotate.setByAngle(360);
        rotate.setAxis(Rotate.Y_AXIS);
        rotate.setCycleCount(Animation.INDEFINITE);
        rotate.play();

        StackPane wrapper = new StackPane(logoView);
        wrapper.getStyleClass().add("hero");
        return wrapper;
    }

    private VBox buildAdminHintPanel() {
        VBox panel = new VBox(8);
        panel.getStyleClass().add("panel");
        Label title = new Label("Admin Controls");
        title.getStyleClass().add("panel-title");
        
        Label body = new Label(
            "• Households: view all residents in your zones\n" +
            "• Report: generate and save a weekly waste report\n" +
            "• Save/Load: persist data to disk\n" +
            "• Stats update live from registered household data"
        );
        body.getStyleClass().add("panel-body");
        
        Label fineTitle = new Label("Issue Fine to Household");
        fineTitle.getStyleClass().add("panel-title");
        fineTitle.setPadding(new Insets(10, 0, 0, 0));

        ComboBox<String> householdCombo = new ComboBox<>();
        householdCombo.setPromptText("Select Household");
        householdCombo.setMaxWidth(Double.MAX_VALUE);
        
        for (Household h : sim.getHouseholds()) {
            if (adminZones.isEmpty() || adminZones.contains(h.getZone())) {
                householdCombo.getItems().add(h.getHouseholdID() + " - " + h.getOwnerName());
            }
        }

        TextField amountField = new TextField();
        amountField.setPromptText("Fine Amount (Rs)");

        TextField reasonField = new TextField();
        reasonField.setPromptText("Reason");

        Button issueBtn = new Button("Issue Fine");
        issueBtn.getStyleClass().add("primary-button");
        issueBtn.setMaxWidth(Double.MAX_VALUE);

        Label msgLabel = new Label();
        
        issueBtn.setOnAction(e -> {
            String selected = householdCombo.getValue();
            if (selected == null || amountField.getText().isEmpty() || reasonField.getText().isEmpty()) {
                msgLabel.setText("Please fill all fields.");
                msgLabel.setStyle("-fx-text-fill: #ef4444;");
                return;
            }
            try {
                int hid = Integer.parseInt(selected.split(" - ")[0]);
                double amt = Double.parseDouble(amountField.getText());
                sim.getFineManager().issueFine(hid, amt, reasonField.getText());
                DataManager.saveFines(sim.getFineManager().getAllFines());
                msgLabel.setText("Fine issued!");
                msgLabel.setStyle("-fx-text-fill: #22c55e;");
                amountField.clear();
                reasonField.clear();
                householdCombo.setValue(null);
                refresh();
            } catch (Exception ex) {
                msgLabel.setText("Invalid amount.");
                msgLabel.setStyle("-fx-text-fill: #ef4444;");
            }
        });

        panel.getChildren().addAll(title, body, fineTitle, householdCombo, amountField, reasonField, issueBtn, msgLabel);
        return panel;
    }

    private VBox buildUserHintPanel() {
        VBox panel = new VBox(8);
        panel.getStyleClass().add("panel");
        Label title = new Label("Your Dashboard");
        title.getStyleClass().add("panel-title");
        Label body = new Label(
            "• Add Waste: log your household's waste disposal (with animation!)\n" +
            "• Fine History: view all fines issued to your household\n" +
            "• Update Info: change your name or address"
        );
        body.getStyleClass().add("panel-body");
        
        Label recentFineTitle = new Label("Recent Fine");
        recentFineTitle.getStyleClass().add("panel-title");
        recentFineTitle.setPadding(new Insets(10, 0, 0, 0));
        
        Label recentFineDetails = new Label("No recent fines.");
        recentFineDetails.getStyleClass().add("panel-body");
        
        if (loggedInUser != null) {
            Fine latest = null;
            for (Fine f : sim.getFineManager().getAllFines()) {
                if (f.getHouseholdID() == loggedInUser.getHouseholdID()) {
                    latest = f;
                }
            }
            if (latest != null) {
                recentFineDetails.setText(String.format("Rs %.0f - %s\nStatus: %s", 
                    latest.getAmount(), latest.getReason(), latest.isPaid() ? "PAID" : "UNPAID"));
                if (!latest.isPaid()) {
                    recentFineDetails.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                }
            }
        }

        panel.getChildren().addAll(title, body, recentFineTitle, recentFineDetails);
        return panel;
    }

    public void refresh() {
        if (isAdmin) {
            // Filter to admin's zones if set
            ArrayList<Household> filtered = new ArrayList<>();
            for (Household h : sim.getHouseholds()) {
                if (adminZones.isEmpty() || adminZones.contains(h.getZone())) {
                    filtered.add(h);
                }
            }

            int hhCount = filtered.size();
            double wasteKg = 0;
            double totalCharges = 0;

            double totalMonthlyFees = 0;
            for (Household h : filtered) {
                wasteKg += h.getTotalWasteWeight();
                totalMonthlyFees += h.getMonthlyCharges();
            }
            
            // Total fines for these households
            double totalFinesAmount = 0;
            for (Fine f : sim.getFineManager().getAllFines()) {
                for (Household h : filtered) {
                    if (f.getHouseholdID() == h.getHouseholdID()) {
                        totalFinesAmount += f.getAmount();
                        break;
                    }
                }
            }

            // Total Value = sum of fines + sum of monthly fees
            totalCharges = totalFinesAmount + totalMonthlyFees;

            int activeTrucks = sim.getTruckCount();

            statHouseholds.setText(String.valueOf(hhCount));
            statWaste.setText(String.format("%.1f kg", wasteKg));
            statCharges.setText(String.format("Rs %,.0f", totalCharges));
            statFines.setText(String.format("Rs %,.0f", totalFinesAmount));
            statTrucks.setText(String.valueOf(activeTrucks));
            
            // Rebuild the admin hint panel to refresh combobox
            if(getChildren().size() > 0 && ((HBox)getChildren().get(0)).getChildren().size() > 0) {
                 VBox leftCol = (VBox)((HBox)getChildren().get(0)).getChildren().get(0);
                 if(leftCol.getChildren().size() >= 3 && leftCol.getChildren().get(2) instanceof VBox) {
                     leftCol.getChildren().set(2, buildAdminHintPanel());
                 }
            }

        } else {
            if (loggedInUser != null) {
                double myFines = 0.0;
                for (Fine f : sim.getFineManager().getAllFines()) {
                    if (f.getHouseholdID() == loggedInUser.getHouseholdID()) {
                        myFines += f.getAmount();
                    }
                }
                statMyCharges.setText(String.format("Rs %.0f", loggedInUser.getMonthlyCharges()));
                statMyFines.setText(String.format("Rs %.0f", myFines));
                statCompliance.setText(String.format("%.1f%%", loggedInUser.computeComplianceSnapshot()));
                
                // Rebuild the user hint panel to refresh recent fine
                if(getChildren().size() > 0 && ((HBox)getChildren().get(0)).getChildren().size() > 0) {
                     VBox leftCol = (VBox)((HBox)getChildren().get(0)).getChildren().get(0);
                     if(leftCol.getChildren().size() >= 3 && leftCol.getChildren().get(2) instanceof VBox) {
                         leftCol.getChildren().set(2, buildUserHintPanel());
                     }
                }
        }
    }
}
}
