package com.rawalpindi.waste;
import java.util.ArrayList;

// ============================================================
// VEHICLE: BASE CLASS FOR ALL MUNICIPAL VEHICLES
// PROVIDES CORE PROPERTIES: ID, CAPACITY, FUEL, STATUS
// EXTENDED BY MunicipalTruck FOR WASTE COLLECTION
// ============================================================
public class Vehicle {

    private int    vehicleID;
    private double capacity;       // MAXIMUM LOAD IN KG
    private double fuelLevel;      // FUEL PERCENTAGE 0-100
    private String status;         // "AVAILABLE", "ON_ROUTE", "MAINTENANCE"
    private String registrationNo; // VEHICLE REGISTRATION PLATE

    // --------------------------------------------------------
    // CONSTRUCTOR
    // --------------------------------------------------------
    public Vehicle(int vehicleID, double capacity, String registrationNo) {
        this.vehicleID      = vehicleID;
        this.capacity       = capacity;
        this.fuelLevel      = 100.0;
        this.status         = "AVAILABLE";
        this.registrationNo = registrationNo;
    }

    // --------------------------------------------------------
    // SIMULATES MOVEMENT — CONSUMES 5% FUEL PER CALL
    // --------------------------------------------------------
    public void move() {
        if (fuelLevel <= 0) {
            System.out.println("  [VEHICLE #" + vehicleID + "] OUT OF FUEL! Cannot move.");
            return;
        }
        fuelLevel = Math.max(0, fuelLevel - 5.0);
        System.out.println("  [VEHICLE #" + vehicleID + "] Moving... Fuel: " +
                String.format("%.0f", fuelLevel) + "%");
    }

    // --------------------------------------------------------
    // REFUELS THE VEHICLE BACK TO 100%
    // --------------------------------------------------------
    public void refuel() {
        fuelLevel = 100.0;
        System.out.println("  [VEHICLE #" + vehicleID + "] Refueled to 100%.");
    }

    // --------------------------------------------------------
    // SETS VEHICLE STATUS
    // --------------------------------------------------------
    public void setStatus(String status) { this.status = status; }

    // --------------------------------------------------------
    // GETTERS
    // --------------------------------------------------------
    public int    getVehicleID()      { return vehicleID;      }
    public double getCapacity()       { return capacity;       }
    public double getFuelLevel()      { return fuelLevel;      }
    public String getStatus()         { return status;         }
    public String getRegistrationNo() { return registrationNo; }
}


// ============================================================
// MUNICIPAL TRUCK: INHERITS FROM VEHICLE
// COLLECTS WASTE FROM HOUSEHOLD BINS ALONG A ROUTE
// TRACKS COLLECTED WASTE AND PROCESSES IT AT THE DEPOT
// ============================================================
class MunicipalTruck extends Vehicle {

    private double              currentLoad;      // CURRENT WASTE LOAD IN KG
    private ArrayList<WasteItem> collectedItems;  // ALL WASTE ITEMS ON BOARD
    private String              truckZone;        // ZONE THIS TRUCK SERVES
    private int                 collectionsToday; // HOUSEHOLDS VISITED TODAY
    private double              totalRecycleValue; // VALUE OF WASTE ON BOARD

    // --------------------------------------------------------
    // CONSTRUCTOR
    // --------------------------------------------------------
    public MunicipalTruck(int vehicleID, double capacity,
                          String registrationNo, String zone) {
        super(vehicleID, capacity, registrationNo);
        this.currentLoad       = 0.0;
        this.collectedItems    = new ArrayList<>();
        this.truckZone         = zone.toUpperCase();
        this.collectionsToday  = 0;
        this.totalRecycleValue = 0.0;
    }

    // --------------------------------------------------------
    // COLLECTS ALL WASTE FROM A SINGLE BIN
    // STOPS IF TRUCK REACHES CAPACITY (THROWS EXCEPTION)
    // POLYMORPHIC: ORGANIC WASTE IS COMPACTED (WEIGHT * 0.7)
    // --------------------------------------------------------
    public void collectFromBin(WasteBin bin) throws TruckCapacityException {

        ArrayList<WasteItem> items = bin.emptyBin();
        move(); // FUEL CONSUMED PER BIN VISIT

        for (WasteItem item : items) {

            // ORGANIC WASTE IS COMPACTED — TAKES LESS TRUCK SPACE
            double effectiveWeight = item instanceof OrganicWaste
                    ? item.getWeight() * 0.7
                    : item.getWeight();

            if (currentLoad + effectiveWeight > getCapacity()) {
                throw new TruckCapacityException(getVehicleID());
            }

            collectedItems.add(item);
            currentLoad       += effectiveWeight;
            totalRecycleValue += item.getRecycleValue();
        }

        collectionsToday++;
        System.out.println("  [TRUCK #" + getVehicleID() + "] Collected from Bin #" +
                bin.getBinID() + " | Load: " + String.format("%.1f", currentLoad) +
                "/" + getCapacity() + " kg");
    }

    // --------------------------------------------------------
    // COLLECTS ALL BINS FROM AN ENTIRE HOUSEHOLD
    // --------------------------------------------------------
    public void collectFromHousehold(Household household) {
        System.out.println("  [TRUCK #" + getVehicleID() + "] Collecting from Household #" +
                household.getHouseholdID() + " — " + household.getOwnerName());

        setStatus("ON_ROUTE");

        for (WasteBin bin : household.getBins()) {
            if (bin.getCurrentLoad() > 0) {
                try {
                    collectFromBin(bin);
                } catch (TruckCapacityException e) {
                    System.out.println("  [WARNING] " + e.getMessage());
                    setStatus("FULL");
                    return;
                }
            }
        }

        setStatus("AVAILABLE");
    }

    // --------------------------------------------------------
    // PROCESSES WASTE AT THE DEPOT — RESETS THE TRUCK
    // PRINTS A SUMMARY OF WHAT WAS COLLECTED AND ITS VALUE
    // --------------------------------------------------------
    public void processAtDepot() {
        System.out.println();
        System.out.println("  [DEPOT] Truck #" + getVehicleID() + " processing waste...");
        System.out.printf ("  Total Collected  : %.2f kg%n",  currentLoad);
        System.out.printf ("  Recycle Value    : PKR %.0f%n", totalRecycleValue);
        System.out.println("  Items Processed  : " + collectedItems.size());
        System.out.println("  Households Served: " + collectionsToday);

        // RESET TRUCK FOR NEXT ROUTE
        collectedItems.clear();
        currentLoad       = 0.0;
        totalRecycleValue = 0.0;
        collectionsToday  = 0;
        refuel();
        setStatus("AVAILABLE");

        System.out.println("  [DEPOT] Truck #" + getVehicleID() + " ready for next route.");
    }

    // --------------------------------------------------------
    // GETTERS
    // --------------------------------------------------------
    public double getCurrentLoad()      { return currentLoad;       }
    public String getTruckZone()        { return truckZone;         }
    public int    getCollectionsToday() { return collectionsToday;  }
    public double getTotalRecycleValue(){ return totalRecycleValue; }

    // --------------------------------------------------------
    // RETURNS TRUE IF TRUCK IS FULL OR NEARLY FULL (90%+)
    // --------------------------------------------------------
    public boolean isNearlyFull() {
        return (currentLoad / getCapacity()) >= 0.9;
    }
}

