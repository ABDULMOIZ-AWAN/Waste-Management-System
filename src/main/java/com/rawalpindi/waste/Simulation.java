package com.rawalpindi.waste;
import java.util.ArrayList;
import java.util.Random;

// ============================================================
// SIMULATION: THE CORE ENGINE OF THE WASTE MANAGEMENT TRACKER
// MANAGES WEEKS, HOUSEHOLDS, TRUCKS, ROUTES, AND FINES
// TIES ALL CLASSES TOGETHER INTO A RUNNING SIMULATION
// ============================================================
public class Simulation {

    private ArrayList<Household>       households;
    private ArrayList<MunicipalTruck>  trucks;
    private ArrayList<CollectionRoute> routes;
    private FineManager                fineManager;
    private int                        currentWeek;
    private int                        nextHouseholdID;
    private int                        nextWasteID;
    private int                        nextTruckID;
    private int                        nextRouteID;
    private Random                     random;

    // --------------------------------------------------------
    // CONSTRUCTOR: INITIALIZES ALL SIMULATION COMPONENTS
    // --------------------------------------------------------
    public Simulation() {
        this.households      = new ArrayList<>();
        this.trucks          = new ArrayList<>();
        this.routes          = new ArrayList<>();
        this.fineManager     = new FineManager();
        this.currentWeek     = 0;
        this.nextHouseholdID = 1;
        this.nextWasteID     = 1;
        this.nextTruckID     = 1;
        this.nextRouteID     = 1;
        this.random          = new Random();
    }

    // ============================================================
    // HOUSEHOLD MANAGEMENT METHODS
    // ============================================================

    // --------------------------------------------------------
    // REGISTERS A NEW HOUSEHOLD IN THE SIMULATION
    public Household addHousehold(String ownerName, String address, String zone) {
        return addHousehold(ownerName, address, zone, "123456");
    }

    public Household addHousehold(String ownerName, String address, String zone, String password) {
        Household h = new Household(nextHouseholdID++, ownerName, address, zone, password);
        households.add(h);
        System.out.println("  [REGISTERED] Household #" + h.getHouseholdID() +
                " — " + ownerName + " | " + address);
        return h;
    }

    // --------------------------------------------------------
    // FINDS A HOUSEHOLD BY ITS ID. RETURNS NULL IF NOT FOUND.
    // --------------------------------------------------------
    public Household findHousehold(int id) {
        for (Household h : households) {
            if (h.getHouseholdID() == id) return h;
        }
        return null;
    }

    // ============================================================
    // WASTE ADDITION HELPERS — EACH TYPE HAS A DEDICATED METHOD
    // SO MAIN.JAVA STAYS CLEAN AND READABLE
    // ============================================================

    public void addOrganicWaste(int householdID, double weight, String category) {
        Household h = findHousehold(householdID);
        if (h == null) { System.out.println("  [ERROR] Household #" + householdID + " not found."); return; }
        OrganicWaste waste = new OrganicWaste(nextWasteID++, weight, category);
        h.addWaste(waste, "Organic");
    }

    public void addPlasticWaste(int householdID, double weight, String grade) {
        Household h = findHousehold(householdID);
        if (h == null) { System.out.println("  [ERROR] Household #" + householdID + " not found."); return; }
        PlasticWaste waste = new PlasticWaste(nextWasteID++, weight, grade);
        h.addWaste(waste, "Plastic");
    }

    public void addElectronicWaste(int householdID, double weight,
                                   String deviceType, String brand) {
        Household h = findHousehold(householdID);
        if (h == null) { System.out.println("  [ERROR] Household #" + householdID + " not found."); return; }
        ElectronicWaste waste = new ElectronicWaste(nextWasteID++, weight, deviceType, brand);
        h.addWaste(waste, "Electronic");
    }

    public void addGlassWaste(int householdID, double weight,
                              String color, boolean isBroken) {
        Household h = findHousehold(householdID);
        if (h == null) { System.out.println("  [ERROR] Household #" + householdID + " not found."); return; }
        GlassWaste waste = new GlassWaste(nextWasteID++, weight, color, isBroken);
        h.addWaste(waste, "Glass");
    }

    public void addMetalWaste(int householdID, double weight,
                              String metalType, boolean isRusted) {
        Household h = findHousehold(householdID);
        if (h == null) { System.out.println("  [ERROR] Household #" + householdID + " not found."); return; }
        MetalWaste waste = new MetalWaste(nextWasteID++, weight, metalType, isRusted);
        h.addWaste(waste, "Metal");
    }

    // ============================================================
    // TRUCK AND ROUTE MANAGEMENT
    // ============================================================

    // --------------------------------------------------------
    // REGISTERS A NEW MUNICIPAL TRUCK
    // --------------------------------------------------------
    public MunicipalTruck addTruck(double capacity, String registration, String zone) {
        MunicipalTruck truck = new MunicipalTruck(
                nextTruckID++, capacity, registration, zone
        );
        trucks.add(truck);
        System.out.println("  [TRUCK ADDED] #" + truck.getVehicleID() +
                " | " + registration + " | Zone: " + zone + " | Capacity: " + capacity + " kg");
        return truck;
    }

    // --------------------------------------------------------
    // CREATES A NEW COLLECTION ROUTE FOR A TRUCK
    // --------------------------------------------------------
    public CollectionRoute createRoute(MunicipalTruck truck, String zone) {
        CollectionRoute route = new CollectionRoute(nextRouteID++, zone, truck);
        routes.add(route);
        return route;
    }

    // ============================================================
    // WEEKLY SIMULATION CYCLE
    // ============================================================

    // --------------------------------------------------------
    // RUNS ONE COMPLETE WEEK OF THE SIMULATION:
    // 1. INCREMENTS WEEK COUNTER
    // 2. TRIGGERS RANDOM EVENTS (OVERFLOW ETC.)
    // 3. EVALUATES COMPLIANCE AND ISSUES FINES
    // 4. DISPATCHES ALL TRUCKS ON THEIR ROUTES
    // 5. GENERATES WEEKLY REPORT
    // --------------------------------------------------------
    public void runWeek() {
        currentWeek++;
        printWeekBanner();

        triggerRandomEvents();
        evaluateAllCompliance();
        dispatchAllRoutes();
        generateWeeklyReport();
    }

    // --------------------------------------------------------
    // PRINTS A DECORATIVE BANNER FOR THE CURRENT WEEK
    // --------------------------------------------------------
    private void printWeekBanner() {
        System.out.println();
        System.out.println("  " + "#".repeat(60));
        System.out.println("  ##   SIMULATION WEEK " + currentWeek + " STARTING");
        System.out.println("  " + "#".repeat(60));
    }

    // --------------------------------------------------------
    // RANDOM EVENTS: EACH HOUSEHOLD HAS A 20% CHANCE OF AN
    // OVERFLOW EVENT WHICH TRIGGERS AN AUTOMATIC FINE
    // --------------------------------------------------------
    private void triggerRandomEvents() {
        System.out.println();
        System.out.println("  [EVENTS] Checking for random events this week...");

        for (Household h : households) {
            if (random.nextInt(100) < 20) {
                System.out.println("  [EVENT] Bin overflow detected at Household #" +
                        h.getHouseholdID() + " — " + h.getOwnerName());
                fineManager.issueOverflowFine(h.getHouseholdID());
            }
        }
    }

    // --------------------------------------------------------
    // EVALUATES COMPLIANCE FOR EVERY HOUSEHOLD
    // PASSES MISPLACED PLASTIC AND E-WASTE KG TO FINEMANAGER
    // --------------------------------------------------------
    private void evaluateAllCompliance() {
        System.out.println();
        System.out.println("  [COMPLIANCE] Evaluating all households...");

        for (Household h : households) {
            double compliance         = h.calculateCompliance();
            double misplacedPlastic   = h.getMisplacedWeightByType("Plastic");
            double misplacedEwaste    = h.getMisplacedWeightByType("Electronic");

            System.out.printf("  Household #%-3d %-20s Compliance: %.1f%%%n",
                    h.getHouseholdID(), h.getOwnerName(), compliance);

            fineManager.evaluateCompliance(
                    h.getHouseholdID(), compliance, misplacedPlastic, misplacedEwaste
            );
        }
    }

    // --------------------------------------------------------
    // DISPATCHES ALL REGISTERED ROUTES — TRUCKS COLLECT WASTE
    // --------------------------------------------------------
    private void dispatchAllRoutes() {
        System.out.println();
        System.out.println("  [DISPATCH] Sending trucks on collection routes...");

        for (CollectionRoute route : routes) {
            route.executeRoute();
        }
    }

    // --------------------------------------------------------
    // GENERATES AND PRINTS THE FULL WEEKLY REPORT
    // --------------------------------------------------------
    private void generateWeeklyReport() {
        Report report = new Report(households, fineManager, currentWeek);
        report.generateFullReport();
    }

    // ============================================================
    // DISPLAY HELPERS
    // ============================================================

    // --------------------------------------------------------
    // DISPLAYS ALL HOUSEHOLDS AND THEIR CURRENT STATUS
    // --------------------------------------------------------
    public void displayAllHouseholds() {
        System.out.println();
        System.out.println("  ALL REGISTERED HOUSEHOLDS (" + households.size() + " total)");
        System.out.println("  " + "=".repeat(50));
        for (Household h : households) {
            h.displayDetails();
        }
    }

    // --------------------------------------------------------
    // DISPLAYS STATUS OF ALL TRUCKS
    // --------------------------------------------------------
    public void displayAllTrucks() {
        System.out.println();
        System.out.println("  MUNICIPAL TRUCK FLEET (" + trucks.size() + " trucks)");
        System.out.println("  " + "-".repeat(50));
        for (MunicipalTruck t : trucks) {
            System.out.printf("  Truck #%-3d | Reg: %-12s | Zone: %-15s | Status: %s | Fuel: %.0f%%%n",
                    t.getVehicleID(), t.getRegistrationNo(), t.getTruckZone(),
                    t.getStatus(), t.getFuelLevel());
        }
    }

    // --------------------------------------------------------
    // DISPLAYS ALL FINES ISSUED SO FAR
    // --------------------------------------------------------
    public void displayFines() {
        System.out.println();
        System.out.println("  ALL FINES ISSUED");
        fineManager.displayAllFines();
    }

    // --------------------------------------------------------
    // GETTERS FOR SIMULATION STATE
    // --------------------------------------------------------
    public int                        getCurrentWeek()  { return currentWeek;  }
    public ArrayList<Household>       getHouseholds()   { return households;   }
    public ArrayList<MunicipalTruck>  getTrucks()       { return trucks;       }
    public int                        getTruckCount()   { return trucks.size(); }
    public FineManager                getFineManager()  { return fineManager;  }

    // --------------------------------------------------------
    // LOAD HELPERS FOR GUI (REPLACES IN-MEMORY STATE)
    // --------------------------------------------------------
    public void replaceHouseholds(ArrayList<Household> loaded) {
        households.clear();
        if (loaded != null) households.addAll(loaded);

        int maxId = 0;
        for (Household h : households) {
            if (h.getHouseholdID() > maxId) maxId = h.getHouseholdID();
        }
        nextHouseholdID = maxId + 1;
    }

    // --------------------------------------------------------
    // RESETS ALL COLLECTABLE DATA (WASTE, CHARGES, FINES)
    // --------------------------------------------------------
    public void resetAllData() {
        for (Household h : households) {
            h.resetData();
        }
        fineManager.clearAllFines();
    }
}

