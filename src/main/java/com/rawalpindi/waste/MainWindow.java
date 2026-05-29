package com.rawalpindi.waste;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.ArrayList;

public class MainWindow extends BorderPane {

    private final Simulation sim;

    private final StackPane contentHost = new StackPane();
    private final DashboardScreen dashboardScreen;
    private final AddWasteScreen addWasteScreen;
    private final HouseholdsScreen householdsScreen;
    private final ReportScreen reportScreen;
    private final UserFineHistoryScreen fineHistoryScreen;
    private final UserUpdateInfoScreen updateInfoScreen;

    private final Label headerTitle = new Label("Dashboard");
    private final boolean isAdmin;
    private final Household loggedInUser;
    private final ArrayList<String> adminZones;
    private final FxMain app;

    public MainWindow(Simulation sim, boolean isAdmin, Household loggedInUser, ArrayList<String> adminZones, FxMain app) {
        this.sim = sim;
        this.isAdmin = isAdmin;
        this.loggedInUser = loggedInUser;
        this.adminZones = adminZones != null ? adminZones : new ArrayList<>();
        this.app = app;
        getStyleClass().add("root");

        dashboardScreen  = new DashboardScreen(sim, isAdmin, loggedInUser, this.adminZones);
        addWasteScreen   = new AddWasteScreen(sim, this::refreshAll, loggedInUser);
        householdsScreen = new HouseholdsScreen(sim, this.adminZones);
        reportScreen     = new ReportScreen(sim);
        fineHistoryScreen  = new UserFineHistoryScreen(sim, loggedInUser);
        updateInfoScreen   = new UserUpdateInfoScreen(sim, loggedInUser, this::refreshAll);

        setLeft(buildSidebar());
        setTop(buildHeader());
        setCenter(contentHost);

        showScreen("Dashboard", dashboardScreen);
    }

    private Node buildHeader() {
        HBox header = new HBox(12);
        header.getStyleClass().add("header");
        header.setPadding(new Insets(14, 18, 14, 18));
        header.setAlignment(Pos.CENTER_LEFT);

        headerTitle.getStyleClass().add("header-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Show zone info for admins
        Label zoneLabel = new Label("");
        if (isAdmin && !adminZones.isEmpty()) {
            zoneLabel.getStyleClass().add("muted");
            zoneLabel.setText("Zones: " + String.join(", ", adminZones));
        }

        Button refresh = new Button("Refresh");
        refresh.getStyleClass().add("ghost-button");
        refresh.setOnAction(e -> refreshAll());

        header.getChildren().addAll(headerTitle, spacer, zoneLabel, refresh);
        return header;
    }

    private Node buildSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new Insets(16));

        Label brand = new Label("RWP Waste");
        brand.getStyleClass().add("brand");

        Button dashboard = navButton("🏠  Dashboard", () -> showScreen("Dashboard", dashboardScreen));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button save = new Button("Save Data");
        save.getStyleClass().addAll("nav-button", "nav-button-secondary");
        save.setMaxWidth(Double.MAX_VALUE);
        save.setOnAction(e -> {
            DataManager.saveHouseholds(sim.getHouseholds());
            DataManager.saveFines(sim.getFineManager().getAllFines());
            info("Saved", "Data saved to households.txt and fines.txt");
        });

        Button load = new Button("Load Data");
        load.getStyleClass().addAll("nav-button", "nav-button-secondary");
        load.setMaxWidth(Double.MAX_VALUE);
        load.setOnAction(e -> {
            ArrayList<Household> loadedHouseholds = DataManager.loadHouseholds();
            ArrayList<Fine> loadedFines = DataManager.loadFines();
            sim.replaceHouseholds(loadedHouseholds);
            sim.getFineManager().loadFines(loadedFines);
            refreshAll();
            info("Loaded", "Loaded " + loadedHouseholds.size() + " households and " + loadedFines.size() + " fines.");
        });

        Button logout = new Button("🚪  Logout");
        logout.getStyleClass().addAll("nav-button", "ghost-button");
        logout.setMaxWidth(Double.MAX_VALUE);
        logout.setOnAction(e -> app.showLoginScreen());

        sidebar.getChildren().addAll(brand, dashboard);

        if (isAdmin) {
            // Admin sees: Households, Report — NO Add Waste
            Button households = navButton("🏘  Households", () -> showScreen("Households", householdsScreen));
            Button report     = navButton("📊  Report",     () -> showScreen("Report", reportScreen));
            sidebar.getChildren().addAll(households, report);
        } else {
            // User sees: Add Waste, Fine History, Update Info
            Button addWaste   = navButton("🗑  Add Waste",  () -> showScreen("Add Waste", addWasteScreen));
            Button fineHist   = navButton("📋  Fine History", () -> showScreen("Fine History", fineHistoryScreen));
            Button updateInfo = navButton("✏   Update Info",  () -> showScreen("Update Info", updateInfoScreen));
            sidebar.getChildren().addAll(addWaste, fineHist, updateInfo);
        }

        sidebar.getChildren().addAll(spacer, save, load, logout);
        return sidebar;
    }

    private Button navButton(String text, Runnable action) {
        Button b = new Button(text);
        b.getStyleClass().add("nav-button");
        b.setMaxWidth(Double.MAX_VALUE);
        b.setOnAction(e -> action.run());
        return b;
    }

    private void showScreen(String title, Node screen) {
        headerTitle.setText(title);

        if (!contentHost.getChildren().isEmpty()) {
            Node old = contentHost.getChildren().get(0);
            FadeTransition out = new FadeTransition(Duration.millis(140), old);
            out.setFromValue(1);
            out.setToValue(0);
            out.setOnFinished(ev -> {
                contentHost.getChildren().setAll(screen);
                FadeTransition in = new FadeTransition(Duration.millis(180), screen);
                in.setFromValue(0);
                in.setToValue(1);
                in.play();
            });
            out.play();
        } else {
            contentHost.getChildren().setAll(screen);
        }
    }

    private void refreshAll() {
        dashboardScreen.refresh();
        householdsScreen.refresh();
        reportScreen.refresh();
        fineHistoryScreen.refresh();
    }

    private void info(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
