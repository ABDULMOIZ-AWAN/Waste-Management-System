package com.rawalpindi.waste;
import java.time.LocalDate;
import java.util.ArrayList;
public class Fine {

    private int fineID;
    private int householdID;
    private double amount;
    private String reason;
    private LocalDate date;
    private boolean isPaid;
    // CONSTRUCTOR
    public Fine(int fineID, int householdID, double amount, String reason) {
        this.fineID      = fineID;
        this.householdID = householdID;
        this.amount      = amount;
        this.reason      = reason;
        this.date        = LocalDate.now();
        this.isPaid      = false;
    }
    // MARKS THE FINE AS PAID
    public void markAsPaid() {
        this.isPaid = true;
        System.out.println("  Fine #" + fineID + " of PKR " + amount + " marked as PAID.");
    }
    // DISPLAYS FINE SUMMARY
    public void displayFine() {
        System.out.printf(
                "  Fine #%-3d | Household #%-3d | PKR %-8.0f | %-45s | %s | %s%n",
                fineID, householdID, amount, reason, date, isPaid ? "PAID" : "UNPAID"
        );
    }
    // GETTERS
    public int getFineID()      { return fineID; }
    public int getHouseholdID() { return householdID; }
    public double getAmount()      { return amount; }
    public String getReason()      { return reason; }
    public LocalDate getDate()        { return date; }
    public boolean isPaid()         { return isPaid; }


    //SETTERS

    public void setHouseholdID(int householdID) {
        this.householdID = householdID;
    }

    public void setPaid(boolean isPaid) {
        this.isPaid = isPaid;
    }

}
class FineManager {
    // FINE RATES (PKR) — BASED ON RAWALPINDI CDA GUIDELINES
    public static final double RATE_UNSORTED_PLASTIC  = 500.0;  // PKR PER KG
    public static final double RATE_UNSORTED_EWASTE   = 800.0;  // PKR PER KG
    public static final double RATE_LOW_COMPLIANCE    = 1000.0; // FLAT FINE
    public static final double RATE_BIN_OVERFLOW      = 200.0;  // FLAT FINE
    public static final double RATE_MISSED_COLLECTION = 300.0;  // FLAT FINE

    private ArrayList<Fine> fines;
    private int             nextFineID;

    // CONSTRUCTOR
    public FineManager() {
        this.fines      = new ArrayList<>();
        this.nextFineID = 1001;
    }


    public Fine issueFine(int householdID, double amount, String reason) {
        Fine fine = new Fine(nextFineID++, householdID, amount, reason);
        fines.add(fine);
        System.out.println("  [FINE ISSUED] Household #" + householdID +
                " fined PKR " + amount + " — " + reason);
        return fine;
    }


    public void evaluateCompliance(int householdID, double compliance,
                                   double unsortedPlasticKg, double unsortedEwasteKg) {

        if (compliance < 50.0) {
            issueFine(householdID, RATE_LOW_COMPLIANCE,
                    "Compliance below 50% (actual: " + String.format("%.1f", compliance) + "%)");

            if (unsortedPlasticKg > 0) {
                issueFine(householdID, unsortedPlasticKg * RATE_UNSORTED_PLASTIC,
                        "Unsorted plastic: " + String.format("%.2f", unsortedPlasticKg) + " kg");
            }
            if (unsortedEwasteKg > 0) {
                issueFine(householdID, unsortedEwasteKg * RATE_UNSORTED_EWASTE,
                        "Unsorted e-waste: " + String.format("%.2f", unsortedEwasteKg) + " kg");
            }

        } else if (compliance < 70.0) {
            issueFine(householdID, RATE_LOW_COMPLIANCE / 2.0,
                    "Compliance below 70% (actual: " + String.format("%.1f", compliance) + "%)");
        }
    }

    // --------------------------------------------------------
    // ISSUES AN OVERFLOW FINE TO A HOUSEHOLD
    // --------------------------------------------------------
    public void issueOverflowFine(int householdID) {
        issueFine(householdID, RATE_BIN_OVERFLOW,
                "Bin overflow — inadequate waste management");
    }
    // CALCULATES TOTAL UNPAID FINES ACROSS ALL HOUSEHOLDS
    public double getTotalUnpaidFines() {
        double total = 0.0;
        for (Fine f : fines) {
            if (!f.isPaid()) total += f.getAmount();
        }
        return total;
    }
    // RETURNS TOTAL FINES FOR A SPECIFIC HOUSEHOLD
    public double getFinesForHousehold(int householdID) {
        double total = 0.0;
        for (Fine f : fines) {
            if (f.getHouseholdID() == householdID) {
                total += f.getAmount();
            }
        }
        return total;
    }
    // DISPLAYS ALL FINES IN A FORMATTED TABLE
    public void displayAllFines() {
        if (fines.isEmpty()) {
            System.out.println("  No fines issued.");
            return;
        }

        System.out.println("  " + "-".repeat(90));
        System.out.printf("  %-8s %-14s %-12s %-45s %-12s %-8s%n",
                "Fine#", "Household#", "Amount(PKR)", "Reason", "Date", "Status");
        System.out.println("  " + "-".repeat(90));

        for (Fine f : fines) {
            f.displayFine();
        }

        System.out.println("  " + "-".repeat(90));
        System.out.printf("  TOTAL UNPAID FINES: PKR %.2f%n", getTotalUnpaidFines());
    }
    // GETTER FOR ALL FINES LIST
    public ArrayList<Fine> getAllFines() { return fines; }

    public void clearAllFines() {
        fines.clear();
        nextFineID = 1001;
    }

    // --------------------------------------------------------
    // REPLACES ALL FINES (USED BY GUI LOAD FEATURE)
    // --------------------------------------------------------
    public void loadFines(ArrayList<Fine> loaded) {
        fines.clear();
        if (loaded != null) fines.addAll(loaded);

        int max = 1000;
        for (Fine f : fines) {
            if (f.getFineID() > max) max = f.getFineID();
        }
        nextFineID = max + 1;
    }
}
