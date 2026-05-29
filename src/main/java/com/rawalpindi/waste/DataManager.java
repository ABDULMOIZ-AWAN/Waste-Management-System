package com.rawalpindi.waste;

import java.io.*;
import java.util.ArrayList;

public class DataManager {

    // Base directory: project directory (where the app runs)
    private static final String BASE_DIR = System.getProperty("user.dir") + File.separator;
    private static final String HOUSEHOLDS_FILE = BASE_DIR + "households.txt";
    private static final String FINES_FILE = BASE_DIR + "fines.txt";

    // ===================== SAVE HOUSEHOLDS =====================
    public static void saveHouseholds(ArrayList<Household> households) {

        try (PrintWriter writer = new PrintWriter(new FileWriter(HOUSEHOLDS_FILE))) {

            for (Household h : households) {
                writer.println(
                        h.getHouseholdID() + "|" +
                                h.getOwnerName() + "|" +
                                h.getAddress() + "|" +
                                h.getZone() + "|" +
                                h.calculateCompliance() + "|" +
                                h.getPassword());
            }

            System.out.println("[SAVED] Households saved.");

        } catch (IOException e) {
            System.out.println("[ERROR] Saving households: " + e.getMessage());
        }
    }

    // ===================== LOAD HOUSEHOLDS =====================
    public static ArrayList<Household> loadHouseholds() {

        ArrayList<Household> households = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(HOUSEHOLDS_FILE))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] parts = line.split("\\|");

                int id = Integer.parseInt(parts[0]);
                String name = parts[1];
                String address = parts[2];
                String zone = parts[3];
                String password = parts.length > 5 ? parts[5] : "123456";

                // Create object
                Household h = new Household(id, name, address, zone, password);

                households.add(h);
            }

            System.out.println("[LOADED] " + households.size() + " households.");

        } catch (FileNotFoundException e) {
            System.out.println("[INFO] No saved households found.");
        } catch (IOException e) {
            System.out.println("[ERROR] Loading households: " + e.getMessage());
        }

        return households;
    }

    // ===================== SAVE FINES =====================
    public static void saveFines(ArrayList<Fine> fines) {

        try (PrintWriter writer = new PrintWriter(new FileWriter(FINES_FILE))) {

            for (Fine f : fines) {
                writer.println(
                        f.getFineID() + "|" +
                                f.getHouseholdID() + "|" +
                                f.getAmount() + "|" +
                                f.getReason() + "|" +
                                f.getDate() + "|" +
                                f.isPaid());
            }

            System.out.println("[SAVED] Fines saved.");

        } catch (IOException e) {
            System.out.println("[ERROR] Saving fines: " + e.getMessage());
        }
    }

    // ===================== LOAD FINES =====================
    public static ArrayList<Fine> loadFines() {

        ArrayList<Fine> fines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FINES_FILE))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] parts = line.split("\\|");

                int fineID = Integer.parseInt(parts[0]);
                int householdID = Integer.parseInt(parts[1]);
                double amount = Double.parseDouble(parts[2]);
                String reason = parts[3];
                String date = parts[4];
                boolean isPaid = Boolean.parseBoolean(parts[5]);

                // Create object
                Fine f = new Fine(fineID, householdID, amount, reason);
                f.setHouseholdID(householdID);
                f.setPaid(isPaid);

                fines.add(f);
            }

            System.out.println("[LOADED] " + fines.size() + " fines.");

        } catch (FileNotFoundException e) {
            System.out.println("[INFO] No saved fines found.");
        } catch (IOException e) {
            System.out.println("[ERROR] Loading fines: " + e.getMessage());
        }

        return fines;
    }

    // ===================== SAVE ADMINS =====================
    public static void saveAdmins(ArrayList<String> admins) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(BASE_DIR + "admins.txt"))) {
            for (String adminLine : admins) {
                writer.println(adminLine);
            }
            System.out.println("[SAVED] Admins saved.");
        } catch (IOException e) {
            System.out.println("[ERROR] Saving admins: " + e.getMessage());
        }
    }

    // ===================== LOAD ADMINS =====================
    public static ArrayList<String> loadAdmins() {
        ArrayList<String> admins = new ArrayList<>();
        File file = new File(BASE_DIR + "admins.txt");

        if (!file.exists()) {
            // Setup default admins if file doesn't exist
            admins.add("abdulmoiz|1234567|SADDAR,WESTRIDGE");
            admins.add("unaisbinfaheem|1234567|CHAKLALA,LALAZAR");
            admins.add("abdullahsethi|1234567|BAHRIA,COMMITTEE CHOWK");
            saveAdmins(admins);
            return admins;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                admins.add(line);
            }
        } catch (IOException e) {
            System.out.println("[ERROR] Loading admins: " + e.getMessage());
        }
        return admins;
    }
}
