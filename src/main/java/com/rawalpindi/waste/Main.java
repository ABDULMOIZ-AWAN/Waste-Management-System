package com.rawalpindi.waste;
import java.util.Scanner;

// ============================================================
// MAIN: ENTRY POINT FOR THE WASTE MANAGEMENT TRACKER
// PROVIDES A FULL INTERACTIVE CONSOLE MENU
// PRELOADS DEMO DATA FOR RAWALPINDI NEIGHBORHOODS
// CS-212 SEMESTER PROJECT — NUST SEECS
// AUTHORS: ABDULLAH AHMAD | UNAIS BIN FAHEEM | ABDUL MOIZ AWAN
// ============================================================
public class Main {

    private static Simulation sim     = new Simulation();
    private static Scanner    scanner = new Scanner(System.in);

    // --------------------------------------------------------
    // PROGRAM ENTRY POINT
    // --------------------------------------------------------
    public static void main(String[] args) {
        printWelcomeBanner();
        loadDemoData();
        runMainMenu();
        printGoodbyeBanner();
        scanner.close();
    }

    // ============================================================
    // WELCOME AND GOODBYE BANNERS
    // ============================================================

    private static void printWelcomeBanner() {
        System.out.println();
        System.out.println("  " + "*".repeat(65));
        System.out.println("  *                                                               *");
        System.out.println("  *        RAWALPINDI WASTE MANAGEMENT TRACKER v1.0               *");
        System.out.println("  *        Rawalpindi Municipal Simulation System                 *");
        System.out.println("  *                                                               *");
        System.out.println("  *        NUST SEECS | CS-212 | Section A | Spring 2026         *");
        System.out.println("  *        Abdullah Ahmad | Unais Bin Faheem | Abdul Moiz Awan   *");
        System.out.println("  *                                                               *");
        System.out.println("  " + "*".repeat(65));
        System.out.println();
        System.out.println("  Rawalpindi generates 1,200 tons of waste daily.");
        System.out.println("  Less than 20% is currently recycled. This system");
        System.out.println("  simulates how compliance and fines can change that.");
        System.out.println();
    }

    private static void printGoodbyeBanner() {
        System.out.println();
        System.out.println("  " + "=".repeat(65));
        System.out.println("  Thank you for using the Rawalpindi Waste Management Tracker.");
        System.out.println("  Sort smart. Dispose cleanly. Measure change.");
        System.out.println("  " + "=".repeat(65));
        System.out.println();
    }

    // ============================================================
    // DEMO DATA LOADER
    // PRELOADS REALISTIC RAWALPINDI HOUSEHOLD AND TRUCK DATA
    // ============================================================

    private static void loadDemoData() {
        System.out.println("  [SYSTEM] Loading Rawalpindi demo data...");
        System.out.println();

        // --------------------------------------------------------
        // REGISTER HOUSEHOLDS ACROSS THREE RAWALPINDI ZONES
        // --------------------------------------------------------
        Household h1 = sim.addHousehold("Ahmed Khan",    "House 14, Street 5, Saddar",         "SADDAR");
        Household h2 = sim.addHousehold("Fatima Malik",  "Flat 3B, Chaklala Scheme 3",          "CHAKLALA");
        Household h3 = sim.addHousehold("Bilal Rao",     "House 7, Westridge Block C",          "WESTRIDGE");
        Household h4 = sim.addHousehold("Sana Hussain",  "House 22, Committee Chowk",           "COMMITTEE CHOWK");
        Household h5 = sim.addHousehold("Tariq Mehmood", "House 9, Lalazar Colony",             "LALAZAR");
        Household h6 = sim.addHousehold("Nadia Sheikh",  "Flat 12A, Bahria Town Phase 7",       "BAHRIA");

        System.out.println();

        // --------------------------------------------------------
        // HOUSEHOLD 1 — AHMED KHAN: GOOD SORTER
        // --------------------------------------------------------
        sim.addOrganicWaste   (1, 3.5, "FOOD");
        sim.addOrganicWaste   (1, 1.2, "GARDEN");
        sim.addPlasticWaste   (1, 2.0, "PET");
        sim.addPlasticWaste   (1, 0.8, "HDPE");
        sim.addElectronicWaste(1, 0.5, "MOBILE",   "Samsung");
        sim.addGlassWaste     (1, 1.5, "CLEAR",    false);
        sim.addMetalWaste     (1, 2.0, "ALUMINUM", false);

        // --------------------------------------------------------
        // HOUSEHOLD 2 — FATIMA MALIK: MODERATE COMPLIANCE
        // --------------------------------------------------------
        sim.addOrganicWaste   (2, 4.0, "FOOD");
        sim.addPlasticWaste   (2, 3.5, "PVC");       // NON-RECYCLABLE GRADE
        sim.addElectronicWaste(2, 1.2, "BATTERY",  "Osaka");
        sim.addGlassWaste     (2, 0.8, "BROWN",    true);   // BROKEN GLASS
        sim.addMetalWaste     (2, 1.5, "STEEL",    true);   // RUSTED

        // --------------------------------------------------------
        // HOUSEHOLD 3 — BILAL RAO: POOR SORTER — WILL GET FINED
        // DELIBERATELY ADDS PLASTIC TO ORGANIC BIN (MISMATCH)
        // --------------------------------------------------------
        sim.addOrganicWaste   (3, 2.0, "FOOD");
        // INTENTIONAL MISMATCH: PLASTIC ADDED TO WRONG BIN
        Household h3ref = sim.findHousehold(3);
        PlasticWaste mismatch = new PlasticWaste(999, 1.5, "PVC");
        h3ref.addWaste(mismatch, "Organic");   // ADDED TO WRONG BIN ON PURPOSE
        sim.addElectronicWaste(3, 2.0, "TV",       "PEL");
        sim.addMetalWaste     (3, 3.0, "COPPER",   false);

        // --------------------------------------------------------
        // HOUSEHOLD 4 — SANA HUSSAIN: AVERAGE COMPLIANCE
        // --------------------------------------------------------
        sim.addOrganicWaste   (4, 5.0, "FOOD");
        sim.addOrganicWaste   (4, 2.0, "PAPER");
        sim.addPlasticWaste   (4, 1.5, "PP");
        sim.addGlassWaste     (4, 2.0, "GREEN",    false);
        sim.addMetalWaste     (4, 1.0, "BRASS",    false);

        // --------------------------------------------------------
        // HOUSEHOLD 5 — TARIQ MEHMOOD: HIGH COMPLIANCE
        // --------------------------------------------------------
        sim.addOrganicWaste   (5, 6.0, "GARDEN");
        sim.addPlasticWaste   (5, 2.5, "PET");
        sim.addElectronicWaste(5, 0.8, "LAPTOP",  "Dell");
        sim.addGlassWaste     (5, 3.0, "CLEAR",   false);
        sim.addMetalWaste     (5, 4.0, "IRON",    false);

        // --------------------------------------------------------
        // HOUSEHOLD 6 — NADIA SHEIKH: VERY GOOD SORTER
        // --------------------------------------------------------
        sim.addOrganicWaste   (6, 3.0, "FOOD");
        sim.addPlasticWaste   (6, 1.0, "HDPE");
        sim.addElectronicWaste(6, 0.3, "MOBILE", "Apple");
        sim.addMetalWaste     (6, 2.5, "COPPER",  false);

        System.out.println();

        // --------------------------------------------------------
        // REGISTER MUNICIPAL TRUCKS — ONE PER ZONE
        // --------------------------------------------------------
        MunicipalTruck truck1 = sim.addTruck(500.0, "RWP-1234", "SADDAR");
        MunicipalTruck truck2 = sim.addTruck(400.0, "RWP-5678", "CHAKLALA");
        MunicipalTruck truck3 = sim.addTruck(350.0, "RWP-9012", "WESTRIDGE");

        System.out.println();

        // --------------------------------------------------------
        // CREATE COLLECTION ROUTES AND ASSIGN HOUSEHOLDS
        // --------------------------------------------------------
        CollectionRoute route1 = sim.createRoute(truck1, "SADDAR");
        route1.addStop(h1);
        route1.addStop(h4);

        CollectionRoute route2 = sim.createRoute(truck2, "CHAKLALA");
        route2.addStop(h2);
        route2.addStop(h5);

        CollectionRoute route3 = sim.createRoute(truck3, "WESTRIDGE");
        route3.addStop(h3);
        route3.addStop(h6);

        System.out.println();
        System.out.println("  [SYSTEM] Demo data loaded successfully!");
        System.out.println("  [SYSTEM] " + sim.getHouseholds().size() + " households, " +
                sim.getTrucks().size() + " trucks, 3 collection routes registered.");
        System.out.println();
    }

    // ============================================================
    // MAIN INTERACTIVE MENU
    // ============================================================

    private static void runMainMenu() {
        boolean running = true;

        while (running) {
            printMenu();
            int choice = readInt("  Enter your choice: ");

            switch (choice) {

                // ------------------------------------------------
                // SIMULATION CONTROLS
                // ------------------------------------------------
                case 1:
                    System.out.println();
                    System.out.println("  Running Week " + (sim.getCurrentWeek() + 1) + "...");
                    sim.runWeek();
                    break;

                // ------------------------------------------------
                // HOUSEHOLD MANAGEMENT
                // ------------------------------------------------
                case 2:
                    menuAddHousehold();
                    break;

                case 3:
                    menuAddWaste();
                    break;

                case 4:
                    sim.displayAllHouseholds();
                    break;

                // ------------------------------------------------
                // TRUCK AND ROUTE MANAGEMENT
                // ------------------------------------------------
                case 5:
                    sim.displayAllTrucks();
                    break;

                // ------------------------------------------------
                // FINES AND REPORTS
                // ------------------------------------------------
                case 6:
                    sim.displayFines();
                    break;

                case 7:
                    Report report = new Report(
                            sim.getHouseholds(),
                            sim.getFineManager(),
                            sim.getCurrentWeek()
                    );
                    report.generateFullReport();
                    break;

                // ------------------------------------------------
                // QUICK DEMO — RUNS 3 WEEKS AUTOMATICALLY
                // ------------------------------------------------
                case 8:
                    System.out.println();
                    System.out.println("  Running 3-week automatic simulation demo...");
                    for (int i = 0; i < 3; i++) {
                        sim.runWeek();
                        pauseForEffect();
                    }
                    break;

                // ------------------------------------------------
                // EXIT
                // ------------------------------------------------
                case 0:
                    running = false;
                    break;

                default:
                    System.out.println("  [ERROR] Invalid choice. Please enter a number from the menu.");
            }
        }
    }

    // ============================================================
    // MENU DISPLAY
    // ============================================================

    private static void printMenu() {
        System.out.println();
        System.out.println("  " + "=".repeat(55));
        System.out.println("   RAWALPINDI WASTE MANAGEMENT TRACKER — MAIN MENU");
        System.out.println("   Current Week: " + sim.getCurrentWeek() +
                " | Households: " + sim.getHouseholds().size());
        System.out.println("  " + "=".repeat(55));
        System.out.println("   [1] Run Next Week Simulation");
        System.out.println("   [2] Add New Household");
        System.out.println("   [3] Add Waste to Household");
        System.out.println("   [4] View All Households");
        System.out.println("   [5] View Truck Fleet Status");
        System.out.println("   [6] View All Fines");
        System.out.println("   [7] Generate Full Report");
        System.out.println("   [8] Auto-Run 3-Week Demo");
        System.out.println("   [0] Exit");
        System.out.println("  " + "=".repeat(55));
    }

    // ============================================================
    // SUB-MENU: ADD HOUSEHOLD
    // ============================================================

    private static void menuAddHousehold() {
        System.out.println();
        System.out.println("  ADD NEW HOUSEHOLD");
        System.out.println("  " + "-".repeat(40));

        String name    = readString("  Owner Name    : ");
        String address = readString("  Address       : ");

        System.out.println("  Zones: SADDAR | CHAKLALA | WESTRIDGE | COMMITTEE CHOWK | LALAZAR | BAHRIA");
        String zone = readString("  Zone          : ");

        sim.addHousehold(name, address, zone);
    }

    // ============================================================
    // SUB-MENU: ADD WASTE TO HOUSEHOLD
    // ============================================================

    private static void menuAddWaste() {
        System.out.println();
        System.out.println("  ADD WASTE TO HOUSEHOLD");
        System.out.println("  " + "-".repeat(40));

        // SHOW AVAILABLE HOUSEHOLD IDs
        System.out.print("  Available Household IDs: ");
        for (Household h : sim.getHouseholds()) {
            System.out.print("#" + h.getHouseholdID() + " (" + h.getOwnerName() + ")  ");
        }
        System.out.println();

        int householdID = readInt("  Household ID  : ");

        System.out.println("  Waste Types: 1-Organic  2-Plastic  3-Electronic  4-Glass  5-Metal");
        int wasteTypeChoice = readInt("  Waste Type    : ");

        double weight = readDouble("  Weight (kg)   : ");

        switch (wasteTypeChoice) {

            case 1:
                System.out.println("  Categories: FOOD | GARDEN | PAPER");
                String category = readString("  Category      : ");
                sim.addOrganicWaste(householdID, weight, category);
                break;

            case 2:
                System.out.println("  Grades: PET | HDPE | PVC | LDPE | PP | PS");
                String grade = readString("  Plastic Grade : ");
                sim.addPlasticWaste(householdID, weight, grade);
                break;

            case 3:
                System.out.println("  Devices: MOBILE | LAPTOP | BATTERY | APPLIANCE | TV");
                String device = readString("  Device Type   : ");
                String brand  = readString("  Brand Name    : ");
                sim.addElectronicWaste(householdID, weight, device, brand);
                break;

            case 4:
                System.out.println("  Colors: CLEAR | GREEN | BROWN");
                String color   = readString("  Glass Color   : ");
                String broken  = readString("  Is Broken? (y/n): ");
                sim.addGlassWaste(householdID, weight, color, broken.equalsIgnoreCase("y"));
                break;

            case 5:
                System.out.println("  Types: ALUMINUM | STEEL | COPPER | IRON | BRASS");
                String metalType = readString("  Metal Type    : ");
                String rusted    = readString("  Is Rusted? (y/n): ");
                sim.addMetalWaste(householdID, weight, metalType, rusted.equalsIgnoreCase("y"));
                break;

            default:
                System.out.println("  [ERROR] Invalid waste type selection.");
        }
    }

    // ============================================================
    // INPUT HELPER METHODS — SAFE READING WITH ERROR HANDLING
    // ============================================================

    // --------------------------------------------------------
    // READS AN INTEGER FROM THE USER — RETRIES ON BAD INPUT
    // --------------------------------------------------------
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  [ERROR] Please enter a valid whole number.");
            }
        }
    }

    // --------------------------------------------------------
    // READS A DOUBLE FROM THE USER — RETRIES ON BAD INPUT
    // --------------------------------------------------------
    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  [ERROR] Please enter a valid number (e.g. 1.5).");
            }
        }
    }

    // --------------------------------------------------------
    // READS A NON-EMPTY STRING FROM THE USER
    // --------------------------------------------------------
    private static String readString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.println("  [ERROR] Input cannot be empty.");
        }
    }

    // --------------------------------------------------------
    // BRIEF PAUSE BETWEEN AUTO-RUN WEEKS FOR READABILITY
    // --------------------------------------------------------
    private static void pauseForEffect() {
        try { Thread.sleep(500); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}

