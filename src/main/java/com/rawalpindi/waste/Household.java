package com.rawalpindi.waste;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// ============================================================
// HOUSEHOLD: REPRESENTS A SINGLE HOME IN RAWALPINDI
// CONTAINS MULTIPLE WASTE BINS (AGGREGATION RELATIONSHIP)
// TRACKS COMPLIANCE, FINES, AND WASTE HISTORY OVER WEEKS
// ============================================================
public class Household {

    private int               householdID;
    private String            ownerName;
    private String            address;
    private String            zone;           // "SADDAR", "CHAKLALA", "COMMITTEE CHOWK" ETC.
    private String            password;
    private double            monthlyCharges;
    private ArrayList<WasteBin> bins;
    private ArrayList<Double> weeklyCompliance; // STORES COMPLIANCE SCORE PER WEEK
    private double            totalFinesPaid;
    private int               weeksTracked;
    private ArrayList<String> activityLog;    // RECORDS EACH WASTE ADDITION ACTION

    // --------------------------------------------------------
    // CONSTRUCTOR: AUTOMATICALLY SETS UP THE 5 STANDARD BINS
    // EACH BIN HAS A 20KG CAPACITY (TYPICAL FOR A HOUSEHOLD)
    // --------------------------------------------------------
    public Household(int householdID, String ownerName, String address, String zone, String password) {
        this.householdID      = householdID;
        this.ownerName        = ownerName;
        this.address          = address;
        this.zone             = zone.toUpperCase();
        this.password         = password;
        this.monthlyCharges   = 1500.0;
        this.bins             = new ArrayList<>();
        this.weeklyCompliance = new ArrayList<>();
        this.totalFinesPaid   = 0.0;
        this.weeksTracked     = 0;
        this.activityLog      = new ArrayList<>();

        // INITIALIZE THE FIVE STANDARD WASTE BINS
        setupDefaultBins();
    }

    // --------------------------------------------------------
    // CREATES 5 DEFAULT BINS — ONE PER WASTE CATEGORY
    // BIN IDs ARE BASED ON householdID FOR UNIQUENESS
    // --------------------------------------------------------
    private void setupDefaultBins() {
        int baseID = householdID * 10;
        bins.add(new WasteBin(baseID + 1, "Organic",    20.0));
        bins.add(new WasteBin(baseID + 2, "Plastic",    15.0));
        bins.add(new WasteBin(baseID + 3, "Electronic", 10.0));
        bins.add(new WasteBin(baseID + 4, "Glass",      15.0));
        bins.add(new WasteBin(baseID + 5, "Metal",      20.0));
    }

    // --------------------------------------------------------
    // ADDS WASTE TO THE BIN MATCHING THE GIVEN TYPE
    // IF NO MATCHING BIN EXISTS, WARNS THE USER
    // HANDLES BOTH OVERFLOW AND INVALID WASTE EXCEPTIONS
    // --------------------------------------------------------
    public void addWaste(WasteItem item, String targetBinType) {
        boolean binFound = false;

        for (WasteBin bin : bins) {
            if (bin.getBinType().equalsIgnoreCase(targetBinType)) {
                binFound = true;
                try {
                    bin.addWaste(item);
                    // ---- RECORD ACTIVITY ----
                    logActivity(item, targetBinType, true);
                    System.out.println("  [OK] Added " + item.getWasteType() +
                            " waste (" + item.getWeight() + " kg) to " +
                            bin.getBinType() + " bin.");
                } catch (BinOverflowException e) {
                    logActivity(item, targetBinType, false);
                    System.out.println("  [ERROR] " + e.getMessage());
                } catch (InvalidWasteException e) {
                    logActivity(item, targetBinType, false);
                    System.out.println("  [ERROR] " + e.getMessage());
                }
                break;
            }
        }

        if (!binFound) {
            System.out.println("  [ERROR] No bin of type '" + targetBinType +
                    "' found in Household #" + householdID);
        }
    }

    // --------------------------------------------------------
    // RECORDS A SINGLE WASTE ADDITION INTO THE ACTIVITY LOG
    // --------------------------------------------------------
    private void logActivity(WasteItem item, String binType, boolean success) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        String status = success ? "✔" : "✘ FAILED";
        String entry = String.format("%s  %s  %s: %s  %.2f kg  [%s]",
                timestamp, status, item.getWasteType(), item.getPrimaryDetail(), item.getWeight(), binType);
        activityLog.add(0, entry);  // newest first
        // Keep at most 50 entries
        if (activityLog.size() > 50) activityLog.remove(activityLog.size() - 1);
    }

    // --------------------------------------------------------
    // CALCULATES WEEKLY COMPLIANCE SCORE (0-100%)
    // CHECKS IF EACH ITEM IS IN THE CORRECT BIN TYPE
    // STORES SCORE IN HISTORY FOR TREND ANALYSIS
    // --------------------------------------------------------
    public double calculateCompliance() {
        double correct = 0;
        double total   = 0;

        for (WasteBin bin : bins) {
            for (WasteItem item : bin.getWasteItems()) {
                total++;
                if (item.getWasteType().equalsIgnoreCase(bin.getBinType())) {
                    correct++;
                }
            }
        }

        double score = (total == 0) ? 100.0 : (correct / total) * 100.0;
        weeklyCompliance.add(score);
        weeksTracked++;
        return score;
    }

    // --------------------------------------------------------
    // COMPUTES COMPLIANCE WITHOUT UPDATING WEEKLY HISTORY.
    // Useful for UI/reporting/saving where we must not mutate state.
    // --------------------------------------------------------
    public double computeComplianceSnapshot() {
        double correct = 0;
        double total   = 0;

        for (WasteBin bin : bins) {
            for (WasteItem item : bin.getWasteItems()) {
                total++;
                if (item.getWasteType().equalsIgnoreCase(bin.getBinType())) {
                    correct++;
                }
            }
        }

        return (total == 0) ? 100.0 : (correct / total) * 100.0;
    }

    // --------------------------------------------------------
    // CALCULATES TOTAL WEIGHT OF WASTE ACROSS ALL BINS
    // --------------------------------------------------------
    public double getTotalWasteWeight() {
        double total = 0.0;
        for (WasteBin bin : bins) {
            total += bin.getCurrentLoad();
        }
        return total;
    }

    // --------------------------------------------------------
    // CALCULATES TOTAL RECYCLE VALUE ACROSS ALL BINS
    // --------------------------------------------------------
    public double getTotalRecycleValue() {
        double total = 0.0;
        for (WasteBin bin : bins) {
            total += bin.getTotalRecycleValue();
        }
        return total;
    }

    // --------------------------------------------------------
    // CALCULATES WEIGHT OF MISPLACED WASTE BY TYPE
    // USED BY FINEMANAGER TO DETERMINE PER-KG FINES
    // --------------------------------------------------------
    public double getMisplacedWeightByType(String wasteType) {
        double misplaced = 0.0;
        for (WasteBin bin : bins) {
            if (!bin.getBinType().equalsIgnoreCase(wasteType)) {
                for (WasteItem item : bin.getWasteItems()) {
                    if (item.getWasteType().equalsIgnoreCase(wasteType)) {
                        misplaced += item.getWeight();
                    }
                }
            }
        }
        return misplaced;
    }

    // --------------------------------------------------------
    // RETURNS TRUE IF ANY BIN IS NEARLY FULL
    // USED BY SIMULATION TO FLAG OVERFLOW RISK
    // --------------------------------------------------------
    public boolean hasOverflowRisk() {
        for (WasteBin bin : bins) {
            if (bin.isNearlyFull()) return true;
        }
        return false;
    }

    // --------------------------------------------------------
    // CALCULATES AVERAGE COMPLIANCE OVER ALL TRACKED WEEKS
    // --------------------------------------------------------
    public double getAverageCompliance() {
        if (weeklyCompliance.isEmpty()) return 0.0;
        double sum = 0.0;
        for (double c : weeklyCompliance) sum += c;
        return sum / weeklyCompliance.size();
    }

    // --------------------------------------------------------
    // DISPLAYS FULL HOUSEHOLD SUMMARY WITH ALL BINS
    // --------------------------------------------------------
    public void displayDetails() {
        System.out.println();
        System.out.println("  HOUSEHOLD #" + householdID + " — " + ownerName);
        System.out.println("  Address : " + address);
        System.out.println("  Zone    : " + zone);
        System.out.printf ("  Total Waste    : %.2f kg%n", getTotalWasteWeight());
        System.out.printf ("  Recycle Value  : PKR %.0f%n", getTotalRecycleValue());
        System.out.printf ("  Avg Compliance : %.1f%%%n",   getAverageCompliance());
        System.out.println("  Bins:");

        for (WasteBin bin : bins) {
            bin.displayBinDetails();
        }
    }

    // --------------------------------------------------------
    // GETTERS
    // --------------------------------------------------------
    public int                householdID()         { return householdID;      }
    public int                getHouseholdID()      { return householdID;      }
    public String             getOwnerName()        { return ownerName;        }
    public String             getAddress()          { return address;          }
    public String             getZone()             { return zone;             }
    public String             getPassword()         { return password;         }
    public double             getMonthlyCharges()   { return monthlyCharges;   }
    public ArrayList<WasteBin> getBins()            { return bins;             }
    public ArrayList<Double>  getWeeklyCompliance() { return weeklyCompliance; }
    public ArrayList<String> getActivityLog()    { return activityLog;      }
    public int                getWeeksTracked()     { return weeksTracked;     }

    // --------------------------------------------------------
    // RECORDS A FINE PAYMENT AMOUNT
    // --------------------------------------------------------
    public void recordFinePaid(double amount) { totalFinesPaid += amount; }
    public double getTotalFinesPaid()         { return totalFinesPaid;    }

    // --------------------------------------------------------
    // RESETS DATA AFTER REPORT GENERATION
    // --------------------------------------------------------
    public void resetData() {
        for (WasteBin bin : bins) {
            bin.emptyBin();
        }
        activityLog.clear();
        // monthlyCharges stays at 1500 (fixed monthly fee)
    }

    // --------------------------------------------------------
    // SETTERS FOR UPDATE INFO
    // --------------------------------------------------------
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public void setAddress(String address)     { this.address = address;     }
}

