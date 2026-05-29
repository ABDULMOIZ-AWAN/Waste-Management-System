package com.rawalpindi.waste;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.Shape3D;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class LoginScreen extends StackPane {

    private final Simulation sim;
    private final FxMain app;

    // ── 3D background state ──────────────────────────────────
    private final Group world = new Group();
    private final ArrayList<FallingItem> fallingItems = new ArrayList<>();
    private final Random rand = new Random();
    private long lastSpawnNano = 0;
    private long nextSpawnInterval; // nanoseconds
    private AnimationTimer timer;
    private Box logoBox;
    private Rotate logoRotateY;

    // Scene dimensions (updated on resize)
    private double sceneW = 1180;
    private double sceneH = 720;

    // Bin geometry constants
    private static final double BIN_WIDTH = 90;
    private static final double BIN_HEIGHT = 120;
    private static final double BIN_DEPTH = 70;
    private static final double BIN_GAP = 40; // gap between the two bins
    private static final double GROUND_Y = 0; // will be computed

    // Bin X centres (computed once layout is known)
    private double bin1CenterX;
    private double bin2CenterX;
    private double binTopY; // Y where top of bin sits

    // ── Inner record-like class for falling items ────────────
    private static class FallingItem {
        Shape3D node;
        double speedY; // pixels per second
        double rotSpeedX, rotSpeedY, rotSpeedZ; // degrees per second
        Rotate rx, ry, rz;
        boolean landed = false;
        double fadeTimer = 0; // seconds since landing
        boolean inBin = false;
        double startX;
    }

    // ═══════════════════════════════════════════════════════════
    // CONSTRUCTOR
    // ═══════════════════════════════════════════════════════════
    public LoginScreen(Simulation sim, FxMain app) {
        this.sim = sim;
        this.app = app;
        this.nextSpawnInterval = randomSpawnInterval();

        // ── Build the 3D SubScene ────────────────────────────
        SubScene subScene3D = build3DScene();

        // ── Build the login UI overlay ───────────────────────
        VBox overlay = buildLoginUI();

        // ── Stack them ───────────────────────────────────────
        getChildren().addAll(subScene3D, overlay);
        setStyle("-fx-background-color: #0a0a0a;");

        // Bind SubScene size to this StackPane
        subScene3D.widthProperty().bind(widthProperty());
        subScene3D.heightProperty().bind(heightProperty());

        // Track size changes to reposition bins
        widthProperty().addListener((o, ov, nv) -> {
            sceneW = nv.doubleValue();
            repositionBins();
        });
        heightProperty().addListener((o, ov, nv) -> {
            sceneH = nv.doubleValue();
            repositionBins();
        });

        // Start animation
        startAnimation();
    }

    // ═══════════════════════════════════════════════════════════
    // 3D SCENE BUILDER
    // ═══════════════════════════════════════════════════════════
    private SubScene build3DScene() {
        // ── Ground plane (invisible, just for reference) ─────
        // We'll use sceneH as the "ground" Y (bottom of viewport)

        // ── Create two bins ──────────────────────────────────
        Group bin1 = createBin();
        Group bin2 = createBin();
        bin1.setId("bin1");
        bin2.setId("bin2");
        world.getChildren().addAll(bin1, bin2);

        // ── 3D Logo ──────────────────────────────────────────
        logoBox = new Box(400, 150, 10);
        PhongMaterial logoMat = new PhongMaterial();
        try {
            logoMat.setDiffuseMap(new Image(getClass().getResourceAsStream("/images/logo.png")));
        } catch (Exception e) {
            logoMat.setDiffuseColor(Color.web("#22c55e"));
        }
        logoBox.setMaterial(logoMat);
        
        logoRotateY = new Rotate(0, Rotate.Y_AXIS);
        logoBox.getTransforms().add(logoRotateY);
        world.getChildren().add(logoBox);

        // ── Lighting ─────────────────────────────────────────
        PointLight mainLight = new PointLight(Color.rgb(255, 255, 255, 0.8));
        mainLight.setTranslateY(-500);
        mainLight.setTranslateX(sceneW / 2);
        mainLight.setTranslateZ(-800);
        mainLight.setId("mainLight");
        world.getChildren().add(mainLight);

        PointLight accentLight = new PointLight(Color.rgb(59, 130, 246, 0.8)); // Professional Blue glow
        accentLight.setTranslateY(sceneH);
        accentLight.setTranslateX(sceneW / 2);
        accentLight.setTranslateZ(-200);
        accentLight.setId("accentLight");
        world.getChildren().add(accentLight);

        AmbientLight ambient = new AmbientLight(Color.rgb(60, 60, 75));
        world.getChildren().add(ambient);

        // ── SubScene ─────────────────────────────────────────
        SubScene sub = new SubScene(world, sceneW, sceneH, true, SceneAntialiasing.BALANCED);
        sub.setFill(Color.web("#0a0a0a"));

        // ── PerspectiveCamera for depth ──────────────────────
        PerspectiveCamera camera = new PerspectiveCamera(false);
        sub.setCamera(camera);

        return sub;
    }

    // ── Create a single garbage bin (more 3D and professional) ─────────────
    private Group createBin() {
        Group binGroup = new Group();

        // Body
        Box body = new Box(BIN_WIDTH, BIN_HEIGHT, BIN_DEPTH);
        PhongMaterial bodyMat = new PhongMaterial();
        bodyMat.setDiffuseColor(Color.web("#166534")); // Green bin body
        bodyMat.setSpecularColor(Color.web("#4ade80"));
        body.setMaterial(bodyMat);

        // Rim (cylinder on top)
        Cylinder rim = new Cylinder(BIN_WIDTH / 2 + 8, 10);
        PhongMaterial rimMat = new PhongMaterial();
        rimMat.setDiffuseColor(Color.web("#22c55e")); // Bright green rim
        rimMat.setSpecularColor(Color.web("#86efac"));
        rim.setMaterial(rimMat);
        rim.setTranslateY(-BIN_HEIGHT / 2 - 5);

        // Inner dark void (cylinder to match rim)
        Cylinder innerVoid = new Cylinder(BIN_WIDTH / 2 + 2, 12);
        PhongMaterial voidMat = new PhongMaterial();
        voidMat.setDiffuseColor(Color.web("#052e16"));
        innerVoid.setMaterial(voidMat);
        innerVoid.setTranslateY(-BIN_HEIGHT / 2 - 4);

        // Glow effect
        DropShadow glow = new DropShadow();
        glow.setColor(Color.web("#22c55e"));
        glow.setRadius(25);
        glow.setSpread(0.2);
        rim.setEffect(glow);

        binGroup.getChildren().addAll(body, rim, innerVoid);
        binGroup.setRotationAxis(Rotate.Y_AXIS);
        return binGroup;
    }

    // ── Position bins at bottom-corners ───────────────────────
    private void repositionBins() {
        double margin = 100; // Distance from the edges
        bin1CenterX = margin + BIN_WIDTH / 2;
        bin2CenterX = sceneW - margin - BIN_WIDTH / 2;
        binTopY = sceneH - BIN_HEIGHT / 2 - 40; // 40px margin from bottom

        for (Node n : world.getChildren()) {
            if ("bin1".equals(n.getId())) {
                n.setTranslateX(bin1CenterX);
                n.setTranslateY(binTopY);
            } else if ("bin2".equals(n.getId())) {
                n.setTranslateX(bin2CenterX);
                n.setTranslateY(binTopY);
            }
        }
        // Update lights
        if (logoBox != null) {
            logoBox.setTranslateX(sceneW / 2);
            logoBox.setTranslateY(180);
            logoBox.setTranslateZ(-150);
        }
        for (Node n : world.getChildren()) {
            if (n instanceof PointLight) {
                PointLight pl = (PointLight) n;
                pl.setTranslateX(sceneW / 2);
                if ("accentLight".equals(n.getId())) {
                    pl.setTranslateY(sceneH);
                }
            }
        }
    }

    // ═══════════════════════════════════════════════════════════
    // ANIMATION
    // ═══════════════════════════════════════════════════════════
    private void startAnimation() {
        repositionBins();

        timer = new AnimationTimer() {
            long prevTime = 0;

            @Override
            public void handle(long now) {
                if (prevTime == 0) {
                    prevTime = now;
                    lastSpawnNano = now;
                    return;
                }

                double dt = (now - prevTime) / 1_000_000_000.0; // seconds
                prevTime = now;


                // ── Spawn new items ──────────────────────────
                if (now - lastSpawnNano >= nextSpawnInterval) {
                    spawnGarbageItem();
                    lastSpawnNano = now;
                    nextSpawnInterval = randomSpawnInterval();
                }

                // Rotate the logo
                if (logoRotateY != null) {
                    logoRotateY.setAngle(logoRotateY.getAngle() + 25 * dt);
                }

                // ── Update all items ─────────────────────────
                Iterator<FallingItem> it = fallingItems.iterator();
                while (it.hasNext()) {
                    FallingItem fi = it.next();

                    if (!fi.landed) {
                        // Move down
                        fi.node.setTranslateY(fi.node.getTranslateY() + fi.speedY * dt);

                        // Rotate on all axes
                        fi.rx.setAngle(fi.rx.getAngle() + fi.rotSpeedX * dt);
                        fi.ry.setAngle(fi.ry.getAngle() + fi.rotSpeedY * dt);
                        fi.rz.setAngle(fi.rz.getAngle() + fi.rotSpeedZ * dt);

                        double itemY = fi.node.getTranslateY();
                        double itemX = fi.node.getTranslateX();

                        // Check if over a bin
                        double binTopEdge = binTopY - BIN_HEIGHT / 2;
                        boolean overBin1 = Math.abs(itemX - bin1CenterX) < BIN_WIDTH / 2;
                        boolean overBin2 = Math.abs(itemX - bin2CenterX) < BIN_WIDTH / 2;

                        if ((overBin1 || overBin2) && itemY >= binTopEdge) {
                            fi.landed = true;
                            fi.inBin = true;
                            fi.fadeTimer = 0;
                        } else if (itemY >= sceneH - 30) {
                            // Hit the ground
                            fi.landed = true;
                            fi.inBin = false;
                            fi.fadeTimer = 0;
                        }
                    } else {
                        // Post-landing animation
                        fi.fadeTimer += dt;

                        if (fi.inBin) {
                            // Shrink and sink into bin
                            double progress = Math.min(fi.fadeTimer / 0.6, 1.0);
                            double scale = 1.0 - progress;
                            fi.node.setScaleX(scale);
                            fi.node.setScaleY(scale);
                            fi.node.setScaleZ(scale);
                            fi.node.setTranslateY(fi.node.getTranslateY() + 40 * dt);
                            fi.node.setOpacity(1.0 - progress);
                        } else {
                            // Fade out on ground
                            double progress = Math.min(fi.fadeTimer / 0.8, 1.0);
                            fi.node.setOpacity(1.0 - progress);
                        }

                        // Remove after fade
                        if (fi.fadeTimer > 1.0) {
                            world.getChildren().remove(fi.node);
                            it.remove();
                        }
                    }
                }
            }
        };
        timer.start();
    }

    // ── Spawn a random garbage shape ─────────────────────────
    private void spawnGarbageItem() {
        FallingItem fi = new FallingItem();

        PhongMaterial mat = new PhongMaterial();

        // Random muted colour palette for wrappers/pages
        Color[] garbageColors = {
                Color.web("#FFFFFF"), // white paper
                Color.web("#F5F5DC"), // beige paper
                Color.web("#FF4500"), // red wrapper
                Color.web("#1E90FF"), // blue wrapper
                Color.web("#FFD700"), // yellow wrapper
                Color.web("#32CD32"), // green wrapper
                Color.web("#C0C0C0")  // silver wrapper
        };
        Color baseColor = garbageColors[rand.nextInt(garbageColors.length)];
        mat.setDiffuseColor(baseColor);
        mat.setSpecularColor(baseColor.brighter());

        int shapeType = rand.nextInt(3); // 0=page, 1=small wrapper, 2=crumpled wrapper
        switch (shapeType) {
            case 0: // Page (thin flat box)
                Box page = new Box(15 + rand.nextInt(10), 0.5, 20 + rand.nextInt(10));
                page.setMaterial(mat);
                fi.node = page;
                break;
            case 1: // Small wrapper (thin rectangle)
                Box wrapper1 = new Box(10 + rand.nextInt(8), 2 + rand.nextInt(2), 5 + rand.nextInt(5));
                wrapper1.setMaterial(mat);
                fi.node = wrapper1;
                break;
            case 2: // Crumpled wrapper (small sphere or cylinder)
                if (rand.nextBoolean()) {
                    Sphere crumpled = new Sphere(4 + rand.nextInt(4));
                    crumpled.setMaterial(mat);
                    fi.node = crumpled;
                } else {
                    Cylinder cylWrapper = new Cylinder(3 + rand.nextInt(3), 8 + rand.nextInt(6));
                    cylWrapper.setMaterial(mat);
                    fi.node = cylWrapper;
                }
                break;
        }

        // Spawn directly above one of the bins so it falls into it
        if (rand.nextBoolean()) {
            fi.startX = bin1CenterX + (rand.nextDouble() * (BIN_WIDTH / 3) - (BIN_WIDTH / 6));
        } else {
            fi.startX = bin2CenterX + (rand.nextDouble() * (BIN_WIDTH / 3) - (BIN_WIDTH / 6));
        }
        fi.node.setTranslateX(fi.startX);
        fi.node.setTranslateY(-30); // start above viewport
        fi.node.setTranslateZ(rand.nextInt(40) - 20); // slight depth variation

        // Falling speed
        fi.speedY = 80 + rand.nextDouble() * 100; // 80-180 px/sec

        // Enable rotation so it looks like falling pages/wrappers
        fi.rotSpeedX = rand.nextDouble() * 360 - 180;
        fi.rotSpeedY = rand.nextDouble() * 360 - 180;
        fi.rotSpeedZ = rand.nextDouble() * 360 - 180;

        // Add Rotate transforms
        fi.rx = new Rotate(0, Rotate.X_AXIS);
        fi.ry = new Rotate(0, Rotate.Y_AXIS);
        fi.rz = new Rotate(0, Rotate.Z_AXIS);
        fi.node.getTransforms().addAll(fi.rx, fi.ry, fi.rz);

        world.getChildren().add(fi.node);
        fallingItems.add(fi);
    }

    // ── Random spawn interval 800–1200ms in nanoseconds ──────
    private long randomSpawnInterval() {
        return (800 + rand.nextInt(401)) * 1_000_000L;
    }

    // ═══════════════════════════════════════════════════════════
    // LOGIN UI (preserved exactly from original)
    // ═══════════════════════════════════════════════════════════
    private VBox buildLoginUI() {
        VBox outerBox = new VBox();
        outerBox.setAlignment(Pos.CENTER);
        outerBox.setPickOnBounds(false); // let mouse pass through to 3D if needed

        // Semi-transparent dark overlay panel
        VBox loginBox = new VBox(15);
        loginBox.getStyleClass().add("card");
        loginBox.setMaxWidth(550);
        loginBox.setAlignment(Pos.CENTER);
        loginBox.setPadding(new Insets(30));
        loginBox.setStyle(
                "-fx-background-color: rgba(13, 13, 13, 0.88);" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-color: rgba(34, 197, 94, 0.4);" +
                        "-fx-border-radius: 16;" +
                        "-fx-border-width: 1;");

        // ── Title ────────────────────────────────────────────
        Label title = new Label("Welcome to Rawalpindi Waste Management Company");
        title.getStyleClass().add("screen-title");
        title.setWrapText(true);
        title.setAlignment(Pos.CENTER);
        title.setTextAlignment(TextAlignment.CENTER);

        // ── Role toggles ────────────────────────────────────
        ToggleGroup roleGroup = new ToggleGroup();
        ToggleButton userToggle = new ToggleButton("User");
        userToggle.setToggleGroup(roleGroup);
        userToggle.setSelected(true);
        userToggle.getStyleClass().add("ghost-button");

        ToggleButton adminToggle = new ToggleButton("Admin");
        adminToggle.setToggleGroup(roleGroup);
        adminToggle.getStyleClass().add("ghost-button");

        HBox toggleBox = new HBox(10, userToggle, adminToggle);
        toggleBox.setAlignment(Pos.CENTER);

        // Add listeners to adjust styles for the "red" active state
        roleGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) {
                oldVal.setSelected(true); // Prevent deselecting both
                return;
            }
            if (userToggle.isSelected()) {
                userToggle.setStyle("-fx-background-color: #22c55e; -fx-text-fill: #050507;");
                adminToggle.setStyle("-fx-background-color: rgba(255, 255, 255, 0.08); -fx-text-fill: #f5f5f5;");
            } else {
                adminToggle.setStyle("-fx-background-color: #22c55e; -fx-text-fill: #050507;");
                userToggle.setStyle("-fx-background-color: rgba(255, 255, 255, 0.08); -fx-text-fill: #f5f5f5;");
            }
        });
        // Initial state
        userToggle.setStyle("-fx-background-color: #22c55e; -fx-text-fill: #050507;");

        // ── Input fields ─────────────────────────────────────
        TextField usernameField = new TextField();
        usernameField.setPromptText("Household ID / Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        // ── Buttons ──────────────────────────────────────────
        Button loginBtn = new Button("Login");
        loginBtn.getStyleClass().add("primary-button");
        loginBtn.setMaxWidth(Double.MAX_VALUE);

        Button registerBtn = new Button("Register New Account");
        registerBtn.getStyleClass().add("ghost-button");
        registerBtn.setMaxWidth(Double.MAX_VALUE);

        loginBox.getChildren().addAll(title, toggleBox, usernameField, passwordField, loginBtn, registerBtn);

        // ── Login action ─────────────────────────────────────
        loginBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                showError("Please enter both username/ID and password.");
                return;
            }

            if (adminToggle.isSelected()) {
                // Admin Login
                ArrayList<String> admins = DataManager.loadAdmins();
                boolean auth = false;
                ArrayList<String> adminZones = new ArrayList<>();
                for (String adminStr : admins) {
                    String[] parts = adminStr.split("\\|");
                    if (parts.length >= 2 && parts[0].equals(username) && parts[1].equals(password)) {
                        auth = true;
                        if (parts.length >= 3) {
                            String[] zns = parts[2].split(",");
                            for (String z : zns)
                                adminZones.add(z.trim().toUpperCase());
                        }
                        break;
                    }
                }
                if (auth) {
                    stopAnimation();
                    app.showMainWindow(true, null, adminZones);
                } else {
                    showError("Invalid admin credentials.");
                }
            } else {
                // User Login (Household ID or Owner Name)
                Household userHousehold = null;
                try {
                    int id = Integer.parseInt(username);
                    userHousehold = sim.findHousehold(id);
                } catch (NumberFormatException nfe) {
                    // Try owner name fallback
                    for (Household h : sim.getHouseholds()) {
                        if (h.getOwnerName().equalsIgnoreCase(username)) {
                            userHousehold = h;
                            break;
                        }
                    }
                }

                if (userHousehold != null && userHousehold.getPassword().equals(password)) {
                    stopAnimation();
                    app.showMainWindow(false, userHousehold, null);
                } else {
                    showError("Invalid user credentials or Household not found.");
                }
            }
        });

        registerBtn.setOnAction(e -> {
            stopAnimation();
            app.showRegistrationScreen();
        });

        outerBox.getChildren().add(loginBox);
        return outerBox;
    }

    // ── Stop the animation timer (cleanup on navigate away) ──
    private void stopAnimation() {
        if (timer != null) {
            timer.stop();
        }
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Login Failed");
        alert.setContentText(msg);
        alert.show();
    }
}
