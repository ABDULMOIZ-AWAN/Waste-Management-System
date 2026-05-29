package com.rawalpindi.waste;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class HouseholdsScreen extends VBox {

    private final Simulation sim;
    private final java.util.ArrayList<String> adminZones;
    private final ObservableList<HouseholdRow> rows = FXCollections.observableArrayList();
    private final TableView<HouseholdRow> table = new TableView<>(rows);

    public HouseholdsScreen(Simulation sim, java.util.ArrayList<String> adminZones) {
        this.sim = sim;
        this.adminZones = adminZones != null ? adminZones : new java.util.ArrayList<>();

        getStyleClass().add("screen");
        setPadding(new Insets(18));
        setSpacing(12);

        Label title = new Label("Households");
        title.getStyleClass().add("screen-title");

        table.getStyleClass().add("table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TableColumn<HouseholdRow, Integer> id = new TableColumn<>("ID");
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        id.setMaxWidth(80);

        TableColumn<HouseholdRow, String> owner = new TableColumn<>("Owner");
        owner.setCellValueFactory(new PropertyValueFactory<>("owner"));

        TableColumn<HouseholdRow, String> zone = new TableColumn<>("Zone");
        zone.setCellValueFactory(new PropertyValueFactory<>("zone"));
        zone.setMaxWidth(180);

        TableColumn<HouseholdRow, String> compliance = new TableColumn<>("Compliance");
        compliance.setCellValueFactory(new PropertyValueFactory<>("compliance"));
        compliance.setMaxWidth(140);

        TableColumn<HouseholdRow, String> waste = new TableColumn<>("Total Waste");
        waste.setCellValueFactory(new PropertyValueFactory<>("totalWaste"));
        waste.setMaxWidth(160);

        TableColumn<HouseholdRow, String> charges = new TableColumn<>("Charges (PKR)");
        charges.setCellValueFactory(new PropertyValueFactory<>("charges"));
        charges.setMaxWidth(160);

        table.getColumns().addAll(id, owner, zone, compliance, waste, charges);

        // ---- Click a row to view that household's activity log ----
        table.setRowFactory(tv -> {
            TableRow<HouseholdRow> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 1) {
                    HouseholdRow selected = row.getItem();
                    showActivityLog(selected);
                }
            });
            return row;
        });

        getChildren().addAll(title, table);
        refresh();
    }

    public void refresh() {
        rows.clear();
        for (Household h : sim.getHouseholds()) {
            if (adminZones.isEmpty() || adminZones.contains(h.getZone())) {
                rows.add(HouseholdRow.from(h, sim.getFineManager()));
            }
        }
    }

    // ---- Opens a popup showing the selected household's activity log ----
    private void showActivityLog(HouseholdRow selected) {
        // Find the matching Household object
        Household found = null;
        for (Household h : sim.getHouseholds()) {
            if (h.getHouseholdID() == selected.getId()) {
                found = h;
                break;
            }
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Activity Log — " + selected.getOwner());
        dialog.setHeaderText("Household #" + selected.getId() +
                " · " + selected.getOwner() + " · " + selected.getZone());
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        if (found == null || found.getActivityLog().isEmpty()) {
            Label empty = new Label("No waste activity recorded yet for this household.");
            empty.setStyle("-fx-padding: 12; -fx-text-fill: #888;");
            dialog.getDialogPane().setContent(empty);
        } else {
            ObservableList<String> logItems =
                    FXCollections.observableArrayList(found.getActivityLog());

            ListView<String> list = new ListView<>(logItems);
            list.setPrefWidth(540);
            list.setPrefHeight(320);

            // Style each row: success = green tick, failure = red cross
            list.setCellFactory(lv -> new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        setStyle(item.contains("\u2714")
                                ? "-fx-text-fill: #2ecc71; -fx-font-family: monospace;"
                                : "-fx-text-fill: #e74c3c; -fx-font-family: monospace;");
                    }
                }
            });

            Label hint = new Label("Showing most recent activity first  (max 50 entries)");
            hint.setStyle("-fx-font-size: 11; -fx-text-fill: #888; -fx-padding: 4 0 0 0;");

            VBox content = new VBox(6, list, hint);
            content.setPadding(new Insets(8));
            VBox.setVgrow(list, Priority.ALWAYS);
            dialog.getDialogPane().setContent(content);
        }

        dialog.showAndWait();
    }

    public static final class HouseholdRow {
        private final int id;
        private final String owner;
        private final String zone;
        private final String compliance;
        private final String totalWaste;
        private final String charges;

        private HouseholdRow(int id, String owner, String zone, String compliance, String totalWaste, String charges) {
            this.id = id;
            this.owner = owner;
            this.zone = zone;
            this.compliance = compliance;
            this.totalWaste = totalWaste;
            this.charges = charges;
        }

        public static HouseholdRow from(Household h, FineManager fm) {
            double c = h.computeComplianceSnapshot();
            double f = fm.getFinesForHousehold(h.getHouseholdID());
            double total = f + h.getMonthlyCharges(); // Dynamic fee + fines
            return new HouseholdRow(
                    h.getHouseholdID(),
                    h.getOwnerName(),
                    h.getZone(),
                    String.format("%.1f%%", c),
                    String.format("%.2f kg", h.getTotalWasteWeight()),
                    String.format("PKR %.0f", total)
            );
        }

        public int getId() { return id; }
        public String getOwner() { return owner; }
        public String getZone() { return zone; }
        public String getCompliance() { return compliance; }
        public String getTotalWaste() { return totalWaste; }
        public String getCharges() { return charges; }
    }
}

