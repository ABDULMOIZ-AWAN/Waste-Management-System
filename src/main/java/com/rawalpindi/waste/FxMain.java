package com.rawalpindi.waste;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FxMain extends Application {

    private Stage stage;
    private Simulation sim;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        sim = new Simulation();
        
        DemoDataLoader.load(sim);

        java.util.ArrayList<Household> loadedHouseholds = DataManager.loadHouseholds();
        if(loadedHouseholds != null && !loadedHouseholds.isEmpty()) {
            sim.replaceHouseholds(loadedHouseholds);
        }

        stage.setTitle("Rawalpindi Waste Management Tracker");
        stage.setMinWidth(1000);
        stage.setMinHeight(650);
        
        showLoginScreen();
        stage.show();
    }

    public void showLoginScreen() {
        LoginScreen loginScreen = new LoginScreen(sim, this);
        Scene scene = new Scene(loginScreen, 1180, 720);
        scene.getStylesheets().add(getClass().getResource("app.css").toExternalForm());
        stage.setScene(scene);
    }

    public void showRegistrationScreen() {
        RegistrationScreen regScreen = new RegistrationScreen(sim, this);
        Scene scene = new Scene(regScreen, 1180, 720);
        scene.getStylesheets().add(getClass().getResource("app.css").toExternalForm());
        stage.setScene(scene);
    }

    public void showMainWindow(boolean isAdmin, Household loggedInUser, java.util.ArrayList<String> adminZones) {
        MainWindow root = new MainWindow(sim, isAdmin, loggedInUser, adminZones, this);
        Scene scene = new Scene(root, 1180, 720);
        scene.getStylesheets().add(getClass().getResource("app.css").toExternalForm());
        stage.setScene(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

