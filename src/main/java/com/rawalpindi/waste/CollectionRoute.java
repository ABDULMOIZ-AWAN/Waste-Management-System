package com.rawalpindi.waste;
import java.util.ArrayList;

// ============================================================
// COLLECTION ROUTE: DEFINES AN ORDERED LIST OF HOUSEHOLDS
// ASSIGNED TO A MUNICIPAL TRUCK FOR WEEKLY COLLECTION
// ZONES MIRROR REAL RAWALPINDI NEIGHBORHOODS
// ============================================================
public class CollectionRoute {

    private int                 routeID;
    private String              zoneName;           // E.G. "SADDAR", "CHAKLALA", "WESTRIDGE"
    private ArrayList<Household> stops;             // ORDERED LIST OF HOUSEHOLDS ON THIS ROUTE
    private MunicipalTruck      assignedTruck;
    private boolean             isCompleted;

    // --------------------------------------------------------
    // CONSTRUCTOR
    // --------------------------------------------------------
    public CollectionRoute(int routeID, String zoneName, MunicipalTruck truck) {
        this.routeID       = routeID;
        this.zoneName      = zoneName.toUpperCase();
        this.assignedTruck = truck;
        this.stops         = new ArrayList<>();
        this.isCompleted   = false;
    }

    // --------------------------------------------------------
    // ADDS A HOUSEHOLD AS A STOP ON THIS ROUTE
    // ONLY ADDS IF THE HOUSEHOLD IS IN THE SAME ZONE
    // --------------------------------------------------------
    public void addStop(Household household) {
        stops.add(household);
        System.out.println("  [ROUTE #" + routeID + "] Added stop: Household #" +
                household.getHouseholdID() + " — " + household.getAddress());
    }

    // --------------------------------------------------------
    // EXECUTES THE FULL ROUTE: TRUCK VISITS EVERY HOUSEHOLD
    // PRINTS PROGRESS, HANDLES TRUCK-FULL SCENARIOS
    // MARKS ROUTE AS COMPLETED WHEN DONE
    // --------------------------------------------------------
    public void executeRoute() {

        System.out.println();
        System.out.println("  *** ROUTE #" + routeID + " EXECUTING — ZONE: " + zoneName + " ***");
        System.out.println("  Truck #" + assignedTruck.getVehicleID() +
                " | Registration: " + assignedTruck.getRegistrationNo() +
                " | Capacity: " + assignedTruck.getCapacity() + " kg");
        System.out.println("  Stops: " + stops.size());
        System.out.println();

        int stopsCompleted = 0;

        for (Household household : stops) {
            if ("FULL".equals(assignedTruck.getStatus())) {
                System.out.println("  [ROUTE #" + routeID +
                        "] Truck full — skipping remaining stops. Route incomplete.");
                break;
            }
            assignedTruck.collectFromHousehold(household);
            stopsCompleted++;
        }

        // SEND TRUCK TO DEPOT AFTER ROUTE
        assignedTruck.processAtDepot();
        isCompleted = (stopsCompleted == stops.size());

        System.out.println("  [ROUTE #" + routeID + "] COMPLETED: " +
                stopsCompleted + "/" + stops.size() + " stops.");
    }

    // --------------------------------------------------------
    // DISPLAYS ROUTE SUMMARY
    // --------------------------------------------------------
    public void displayRoute() {
        System.out.println("  Route #" + routeID + " | Zone: " + zoneName +
                " | Truck #" + assignedTruck.getVehicleID() +
                " | Stops: " + stops.size() +
                " | Status: " + (isCompleted ? "COMPLETED" : "PENDING"));
    }

    // --------------------------------------------------------
    // GETTERS
    // --------------------------------------------------------
    public int                  getRouteID()      { return routeID;       }
    public String               getZoneName()     { return zoneName;      }
    public ArrayList<Household> getStops()        { return stops;         }
    public MunicipalTruck       getAssignedTruck(){ return assignedTruck; }
    public boolean              isCompleted()     { return isCompleted;   }
}
