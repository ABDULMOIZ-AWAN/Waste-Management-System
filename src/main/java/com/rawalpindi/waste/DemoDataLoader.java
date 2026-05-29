package com.rawalpindi.waste;
public final class DemoDataLoader {
    private DemoDataLoader() {}

    public static void load(Simulation sim) {

        // ============================================================
        // ZONE 1: SADDAR + WESTRIDGE — Admin: abdulmoiz (20 households)
        // ============================================================
        sim.addHousehold("Ahmed Khan",      "House 14, Street 5, Saddar",        "SADDAR",        "123456");
        sim.addHousehold("Bilal Rao",       "House 7, Westridge Block C",        "WESTRIDGE",     "123456");
        sim.addHousehold("Usman Tariq",     "House 18, Haider Road, Saddar",     "SADDAR",        "123456");
        sim.addHousehold("Saad Javed",      "House 23, Askari 14",               "SADDAR",        "123456");
        sim.addHousehold("Fahad Riaz",      "House 12, Range Road",              "WESTRIDGE",     "123456");
        sim.addHousehold("Asad Mehmood",    "House 3, Murree Road",              "SADDAR",        "123456");
        sim.addHousehold("Kamran Yousaf",   "House 41, Westridge Phase 3",       "WESTRIDGE",     "123456");
        sim.addHousehold("Shahbaz Gill",    "House 8, Mall Road",                "SADDAR",        "123456");
        sim.addHousehold("Irfan Awan",      "House 55, Saddar Bazar",            "SADDAR",        "123456");
        sim.addHousehold("Talha Butt",      "House 19, Westridge Colony",        "WESTRIDGE",     "123456");
        sim.addHousehold("Omer Farooq",     "House 62, Circular Road",           "SADDAR",        "123456");
        sim.addHousehold("Zain Malik",      "House 27, Westridge Block A",       "WESTRIDGE",     "123456");
        sim.addHousehold("Faizan Ahmed",    "House 33, Cantonment",              "SADDAR",        "123456");
        sim.addHousehold("Rehan Shah",      "House 71, Lalkurti",                "SADDAR",        "123456");
        sim.addHousehold("Junaid Raza",     "House 5, Westridge Block D",        "WESTRIDGE",     "123456");
        sim.addHousehold("Kashif Imran",    "House 44, Saddar Lane 3",           "SADDAR",        "123456");
        sim.addHousehold("Adeel Hussain",   "House 15, Committee Chowk",         "SADDAR",        "123456");
        sim.addHousehold("Waqar Zaman",     "House 90, Westridge Phase 1",       "WESTRIDGE",     "123456");
        sim.addHousehold("Nabeel Aslam",    "House 34, Saddar Cantonment",       "SADDAR",        "123456");
        sim.addHousehold("Mudassar Ali",    "House 11, Westridge Block B",       "WESTRIDGE",     "123456");

        // ============================================================
        // ZONE 2: CHAKLALA + LALAZAR — Admin: unaisbinfaheem (20 households)
        // ============================================================
        sim.addHousehold("Fatima Malik",    "Flat 3B, Chaklala Scheme 3",        "CHAKLALA",      "123456");
        sim.addHousehold("Tariq Mehmood",   "House 9, Lalazar Colony",           "LALAZAR",       "123456");
        sim.addHousehold("Ayesha Noor",     "Street 12, Chaklala Scheme 1",      "CHAKLALA",      "123456");
        sim.addHousehold("Hiba Qureshi",    "House 55, Chaklala Cantt",          "CHAKLALA",      "123456");
        sim.addHousehold("Waqas Ahmed",     "House 5, Adiala Road",              "LALAZAR",       "123456");
        sim.addHousehold("Hamza Shah",      "House 99, Chaklala Scheme 3",       "CHAKLALA",      "123456");
        sim.addHousehold("Saira Batool",    "House 16, Lalazar Phase 2",         "LALAZAR",       "123456");
        sim.addHousehold("Ali Hassan",      "Flat 2A, Chaklala Scheme 2",        "CHAKLALA",      "123456");
        sim.addHousehold("Rabia Anwar",     "House 38, Lalazar Extension",       "LALAZAR",       "123456");
        sim.addHousehold("Imran Khalid",    "House 72, Chaklala Airbase",        "CHAKLALA",      "123456");
        sim.addHousehold("Noman Ashraf",    "House 21, Lalazar Colony 2",        "LALAZAR",       "123456");
        sim.addHousehold("Anum Sheikh",     "Flat 5D, Chaklala Heights",         "CHAKLALA",      "123456");
        sim.addHousehold("Farhan Javed",    "House 47, Lalazar Block B",         "LALAZAR",       "123456");
        sim.addHousehold("Sadia Perveen",   "House 63, Chaklala Market",         "CHAKLALA",      "123456");
        sim.addHousehold("Usama Riaz",      "House 8, Lalazar Phase 1",          "LALAZAR",       "123456");
        sim.addHousehold("Mehwish Khan",    "House 29, Chaklala Scheme 5",       "CHAKLALA",      "123456");
        sim.addHousehold("Shoaib Akhtar",   "House 51, Lalazar Garden",          "LALAZAR",       "123456");
        sim.addHousehold("Bushra Nawaz",    "Flat 8B, Chaklala Scheme 1",        "CHAKLALA",      "123456");
        sim.addHousehold("Zeeshan Ahmed",   "House 14, Lalazar Block A",         "LALAZAR",       "123456");
        sim.addHousehold("Amna Tariq",      "House 36, Chaklala Scheme 4",       "CHAKLALA",      "123456");

        // ============================================================
        // ZONE 3: BAHRIA + COMMITTEE CHOWK — Admin: abdullahsethi (20 households)
        // ============================================================
        sim.addHousehold("Sana Hussain",    "House 22, Committee Chowk",         "COMMITTEE CHOWK","123456");
        sim.addHousehold("Nadia Sheikh",    "Flat 12A, Bahria Town Phase 7",     "BAHRIA",        "123456");
        sim.addHousehold("Zainab Iqbal",    "House 31, Satellite Town",          "COMMITTEE CHOWK","123456");
        sim.addHousehold("Maryam Khan",     "Flat 9C, Bahria Town Phase 4",      "BAHRIA",        "123456");
        sim.addHousehold("Mahnoor Ali",     "House 88, Tench Bhata",             "COMMITTEE CHOWK","123456");
        sim.addHousehold("Nimra Tariq",     "House 77, PWD Housing Society",     "BAHRIA",        "123456");
        sim.addHousehold("Kiran Farooq",    "House 10, Bahria Town Phase 8",     "BAHRIA",        "123456");
        sim.addHousehold("Taha Qazi",       "House 56, Committee Chowk Lane",    "COMMITTEE CHOWK","123456");
        sim.addHousehold("Yasir Abbas",     "House 101, Bahria Town Phase 3",    "BAHRIA",        "123456");
        sim.addHousehold("Aisha Rahman",    "House 43, Committee Chowk Main",    "COMMITTEE CHOWK","123456");
        sim.addHousehold("Fawad Chaudhry",  "House 67, Bahria Town Phase 6",     "BAHRIA",        "123456");
        sim.addHousehold("Sumera Awan",     "House 15, Pindora",                 "COMMITTEE CHOWK","123456");
        sim.addHousehold("Bilal Ahmed",     "House 200, Bahria Town Phase 1",    "BAHRIA",        "123456");
        sim.addHousehold("Lubna Naeem",     "House 82, Committee Chowk East",    "COMMITTEE CHOWK","123456");
        sim.addHousehold("Owais Siddiqui",  "Flat 4C, Bahria Town Phase 5",      "BAHRIA",        "123456");
        sim.addHousehold("Uzma Khalid",     "House 39, Committee Chowk West",    "COMMITTEE CHOWK","123456");
        sim.addHousehold("Danish Raza",     "House 6, Peshawar Road",            "COMMITTEE CHOWK","123456");
        sim.addHousehold("Saima Akram",     "House 120, Bahria Town Phase 2",    "BAHRIA",        "123456");
        sim.addHousehold("Hassan Raza",     "House 47, Committee Chowk North",   "COMMITTEE CHOWK","123456");
        sim.addHousehold("Asma Bibi",       "House 88, Bahria Town Phase 7",     "BAHRIA",        "123456");

        // ============================================================
        // WASTE DATA: ADD SAMPLE WASTE TO VARIOUS HOUSEHOLDS
        // ============================================================

        // Zone 1 households (IDs 1-20)
        sim.addOrganicWaste   (1, 3.5, "FOOD");
        sim.addOrganicWaste   (1, 1.2, "GARDEN");
        sim.addPlasticWaste   (1, 2.0, "PET");
        sim.addElectronicWaste(1, 0.5, "MOBILE",   "Samsung");
        sim.addGlassWaste     (1, 1.5, "CLEAR",    false);
        sim.addMetalWaste     (1, 2.0, "ALUMINUM", false);

        sim.addOrganicWaste   (2, 2.0, "FOOD");
        // Intentional mismatch for fines demo
        Household h2ref = sim.findHousehold(2);
        if (h2ref != null) {
            PlasticWaste mismatch = new PlasticWaste(999, 1.5, "PVC");
            h2ref.addWaste(mismatch, "Organic");
        }
        sim.addElectronicWaste(2, 2.0, "TV", "PEL");
        sim.addMetalWaste     (2, 3.0, "COPPER", false);

        sim.addOrganicWaste   (3, 4.2, "FOOD");
        sim.addPlasticWaste   (3, 1.8, "PET");
        sim.addGlassWaste     (3, 1.1, "CLEAR", false);

        sim.addOrganicWaste   (4, 2.5, "PAPER");
        sim.addPlasticWaste   (4, 1.3, "HDPE");

        sim.addOrganicWaste   (5, 3.0, "GARDEN");
        sim.addMetalWaste     (5, 2.5, "IRON", false);

        for (int i = 6; i <= 20; i++) {
            sim.addOrganicWaste(i, 1.5 + (i % 3), "FOOD");
            sim.addPlasticWaste(i, 0.8 + (i % 2), "PET");
        }

        // Zone 2 households (IDs 21-40)
        sim.addOrganicWaste   (21, 4.0, "FOOD");
        sim.addPlasticWaste   (21, 3.5, "PVC");
        sim.addElectronicWaste(21, 1.2, "BATTERY", "Osaka");
        sim.addGlassWaste     (21, 0.8, "BROWN", true);
        sim.addMetalWaste     (21, 1.5, "STEEL", true);

        sim.addOrganicWaste   (22, 6.0, "GARDEN");
        sim.addPlasticWaste   (22, 2.5, "PET");
        sim.addElectronicWaste(22, 0.8, "LAPTOP", "Dell");
        sim.addGlassWaste     (22, 3.0, "CLEAR", false);
        sim.addMetalWaste     (22, 4.0, "IRON", false);

        for (int i = 23; i <= 40; i++) {
            sim.addOrganicWaste(i, 2.0 + (i % 4), "FOOD");
            sim.addPlasticWaste(i, 1.0 + (i % 3), "HDPE");
        }

        // Zone 3 households (IDs 41-60)
        sim.addOrganicWaste   (41, 5.0, "FOOD");
        sim.addOrganicWaste   (41, 2.0, "PAPER");
        sim.addPlasticWaste   (41, 1.5, "PP");
        sim.addGlassWaste     (41, 2.0, "GREEN", false);
        sim.addMetalWaste     (41, 1.0, "BRASS", false);

        sim.addOrganicWaste   (42, 3.0, "FOOD");
        sim.addPlasticWaste   (42, 1.0, "HDPE");
        sim.addElectronicWaste(42, 0.3, "MOBILE", "Apple");
        sim.addMetalWaste     (42, 2.5, "COPPER", false);

        for (int i = 43; i <= 60; i++) {
            sim.addOrganicWaste(i, 1.8 + (i % 5), "GARDEN");
            sim.addPlasticWaste(i, 0.9 + (i % 2), "PP");
        }

        // ============================================================
        // SAMPLE FINES — so users can see fine history in the UI
        // ============================================================
        FineManager fm = sim.getFineManager();
        fm.issueFine(1,  500.0, "Unsorted plastic: 1.00 kg");
        fm.issueFine(2,  1000.0, "Compliance below 50% (actual: 42.9%)");
        fm.issueFine(2,  750.0, "Unsorted plastic: 1.50 kg in Organic bin");
        fm.issueFine(3,  200.0, "Bin overflow — inadequate waste management");
        fm.issueFine(5,  300.0, "Missed collection penalty");
        fm.issueFine(8,  500.0, "Compliance below 70% (actual: 65.0%)");
        fm.issueFine(10, 200.0, "Bin overflow — inadequate waste management");
        fm.issueFine(14, 300.0, "Late disposal — missed schedule");
        fm.issueFine(21, 800.0, "Unsorted e-waste: 1.00 kg");
        fm.issueFine(21, 200.0, "Bin overflow — inadequate waste management");
        fm.issueFine(25, 500.0, "Compliance below 70% (actual: 62.5%)");
        fm.issueFine(30, 300.0, "Missed collection penalty");
        fm.issueFine(41, 500.0, "Unsorted plastic: 1.00 kg");
        fm.issueFine(42, 200.0, "Bin overflow — inadequate waste management");
        fm.issueFine(45, 1000.0, "Compliance below 50% (actual: 48.0%)");
        fm.issueFine(50, 300.0, "Missed collection penalty");
        fm.issueFine(55, 200.0, "Bin overflow — inadequate waste management");

        // ============================================================
        // TRUCKS + ROUTES
        // ============================================================
        MunicipalTruck truck1 = sim.addTruck(500.0, "RWP-1234", "SADDAR");
        MunicipalTruck truck2 = sim.addTruck(400.0, "RWP-5678", "CHAKLALA");
        MunicipalTruck truck3 = sim.addTruck(350.0, "RWP-9012", "WESTRIDGE");
        MunicipalTruck truck4 = sim.addTruck(420.0, "RWP-2468", "COMMITTEE CHOWK");
        MunicipalTruck truck5 = sim.addTruck(380.0, "RWP-1357", "LALAZAR");
        MunicipalTruck truck6 = sim.addTruck(450.0, "RWP-8080", "BAHRIA");

        Household h1 = sim.findHousehold(1);
        Household h3 = sim.findHousehold(3);
        Household h21 = sim.findHousehold(21);
        Household h22 = sim.findHousehold(22);
        Household h41 = sim.findHousehold(41);
        Household h42 = sim.findHousehold(42);

        CollectionRoute route1 = sim.createRoute(truck1, "SADDAR");
        if (h1 != null) route1.addStop(h1);
        if (h3 != null) route1.addStop(h3);

        CollectionRoute route2 = sim.createRoute(truck2, "CHAKLALA");
        if (h21 != null) route2.addStop(h21);

        CollectionRoute route3 = sim.createRoute(truck3, "WESTRIDGE");
        if (h3 != null) route3.addStop(h3);

        CollectionRoute route4 = sim.createRoute(truck4, "COMMITTEE CHOWK");
        if (h41 != null) route4.addStop(h41);

        CollectionRoute route5 = sim.createRoute(truck5, "LALAZAR");
        if (h22 != null) route5.addStop(h22);

        CollectionRoute route6 = sim.createRoute(truck6, "BAHRIA");
        if (h42 != null) route6.addStop(h42);
    }
}
