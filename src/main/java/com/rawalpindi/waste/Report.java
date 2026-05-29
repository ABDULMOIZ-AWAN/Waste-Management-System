package com.rawalpindi.waste;
import java.util.ArrayList;
import java.time.LocalDate;

// ============================================================
// REPORT: GENERATES ANALYTICAL REPORTS FOR THE SIMULATION
// COVERS RECYCLING RATES, LANDFILL PROJECTIONS, FINE SUMMARIES
// AND HOUSEHOLD PERFORMANCE RANKINGS
// ============================================================
public class Report {

    private ArrayList<Household> households;
    private FineManager          fineManager;
    private int                  weekNumber;

    // --------------------------------------------------------
    // CONSTANTS FOR LANDFILL PROJECTION MATH
    // RAWALPINDI GENERATES ~1200 TONS/DAY PER CDA REPORTS
    // --------------------------------------------------------
    private static final double CITY_DAILY_WASTE_TONS = 1200.0;
    private static final double CURRENT_RECYCLE_RATE  = 0.20;   // 20% CURRENTLY RECYCLED
    private static final double TARGET_RECYCLE_RATE   = 0.40;   // PROJECT TARGETS 40%

    // --------------------------------------------------------
    // CONSTRUCTOR
    // --------------------------------------------------------
    public Report(ArrayList<Household> households, FineManager fineManager, int weekNumber) {
        this.households  = households;
        this.fineManager = fineManager;
        this.weekNumber  = weekNumber;
    }

    // --------------------------------------------------------
    // PRINTS THE FULL WEEKLY MUNICIPAL WASTE REPORT
    // INCLUDES ALL SECTIONS: SUMMARY, HOUSEHOLDS, FINES, PROJECTIONS
    // --------------------------------------------------------
    public void generateFullReport() {
        printHeader();
        printSummarySection();
        printHouseholdRankings();
        printFinesSummary();
        printLandfillProjection();
        printFooter();
    }

    // --------------------------------------------------------
    // PRINTS THE REPORT HEADER BANNER
    // --------------------------------------------------------
    private void printHeader() {
        System.out.println();
        System.out.println("  " + "=".repeat(70));
        System.out.println("       RAWALPINDI MUNICIPAL WASTE MANAGEMENT TRACKER");
        System.out.println("              WEEKLY SIMULATION REPORT — WEEK " + weekNumber);
        System.out.println("       Generated: " + LocalDate.now());
        System.out.println("  " + "=".repeat(70));
    }

    // --------------------------------------------------------
    // PRINTS AGGREGATE SUMMARY ACROSS ALL HOUSEHOLDS
    // --------------------------------------------------------
    private void printSummarySection() {
        double totalWaste        = 0.0;
        double totalRecycleValue = 0.0;
        double totalCompliance   = 0.0;
        int    householdCount    = households.size();

        for (Household h : households) {
            totalWaste        += h.getTotalWasteWeight();
            totalRecycleValue += h.getTotalRecycleValue();
            totalCompliance   += h.calculateCompliance();
        }

        double avgCompliance = householdCount > 0 ? totalCompliance / householdCount : 0;
        double recycleRate   = totalWaste > 0
                ? (totalRecycleValue / (totalWaste * 50)) * 100 : 0; // NORMALIZED ESTIMATE

        System.out.println();
        System.out.println("  SECTION 1: OVERALL SUMMARY");
        System.out.println("  " + "-".repeat(50));
        System.out.printf ("  Total Households Tracked : %d%n",          householdCount);
        System.out.printf ("  Total Waste Generated    : %.2f kg%n",      totalWaste);
        System.out.printf ("  Total Recycle Value      : PKR %.0f%n",     totalRecycleValue);
        System.out.printf ("  Average Compliance Score : %.1f%%%n",       avgCompliance);
        System.out.printf ("  Total Fines Issued       : PKR %.0f%n",     fineManager.getTotalUnpaidFines());
        System.out.println();

        // COMPLIANCE GRADE
        String grade;
        if      (avgCompliance >= 90) grade = "A+ (EXCELLENT)";
        else if (avgCompliance >= 75) grade = "B  (GOOD)";
        else if (avgCompliance >= 60) grade = "C  (AVERAGE)";
        else if (avgCompliance >= 50) grade = "D  (POOR)";
        else                          grade = "F  (CRITICAL — ACTION REQUIRED)";

        System.out.println("  City Compliance Grade    : " + grade);
    }

    // --------------------------------------------------------
    // RANKS ALL HOUSEHOLDS BY COMPLIANCE SCORE (BEST FIRST)
    // USES SIMPLE BUBBLE SORT FOR DEMONSTRATION
    // --------------------------------------------------------
    private void printHouseholdRankings() {
        System.out.println();
        System.out.println("  SECTION 2: HOUSEHOLD PERFORMANCE RANKINGS");
        System.out.println("  " + "-".repeat(70));
        System.out.printf ("  %-5s %-12s %-25s %-12s %-12s %s%n",
                "Rank", "HH ID", "Owner", "Zone", "Compliance", "Fine (PKR)");
        System.out.println("  " + "-".repeat(70));

        // COPY LIST TO SORT WITHOUT MODIFYING ORIGINAL
        ArrayList<Household> sorted = new ArrayList<>(households);

        // BUBBLE SORT BY AVERAGE COMPLIANCE — DESCENDING
        for (int i = 0; i < sorted.size() - 1; i++) {
            for (int j = 0; j < sorted.size() - 1 - i; j++) {
                if (sorted.get(j).getAverageCompliance() <
                        sorted.get(j + 1).getAverageCompliance()) {
                    Household temp = sorted.get(j);
                    sorted.set(j, sorted.get(j + 1));
                    sorted.set(j + 1, temp);
                }
            }
        }

        int rank = 1;
        for (Household h : sorted) {
            String medal = rank == 1 ? " [TOP]" : rank == sorted.size() ? " [LAST]" : "";
            System.out.printf("  %-5d %-12d %-25s %-12s %-12.1f PKR %.0f%s%n",
                    rank++,
                    h.getHouseholdID(),
                    h.getOwnerName(),
                    h.getZone(),
                    h.getAverageCompliance(),
                    fineManager.getFinesForHousehold(h.getHouseholdID()),
                    medal
            );
        }
    }

    // --------------------------------------------------------
    // PRINTS ALL FINES ISSUED THIS WEEK
    // --------------------------------------------------------
    private void printFinesSummary() {
        System.out.println();
        System.out.println("  SECTION 3: FINES SUMMARY");
        System.out.println("  " + "-".repeat(50));
        fineManager.displayAllFines();
    }

    // --------------------------------------------------------
    // PROJECTS LANDFILL IMPACT BASED ON CURRENT RECYCLING RATE
    // COMPARES CURRENT STATE VS PROJECT TARGET (20% -> 40%)
    // --------------------------------------------------------
    private void printLandfillProjection() {
        double currentLandfillTons = CITY_DAILY_WASTE_TONS * (1 - CURRENT_RECYCLE_RATE);
        double targetLandfillTons  = CITY_DAILY_WASTE_TONS * (1 - TARGET_RECYCLE_RATE);
        double savedPerDay         = currentLandfillTons - targetLandfillTons;
        double savedPerYear        = savedPerDay * 365;

        System.out.println();
        System.out.println("  SECTION 4: RAWALPINDI LANDFILL PROJECTION");
        System.out.println("  " + "-".repeat(50));
        System.out.printf ("  City Daily Waste         : %.0f tons%n",  CITY_DAILY_WASTE_TONS);
        System.out.printf ("  Current Recycle Rate     : %.0f%%%n",      CURRENT_RECYCLE_RATE * 100);
        System.out.printf ("  Current Landfill Load    : %.0f tons/day%n", currentLandfillTons);
        System.out.println();
        System.out.printf ("  Project Target Rate      : %.0f%%%n",      TARGET_RECYCLE_RATE * 100);
        System.out.printf ("  Projected Landfill Load  : %.0f tons/day%n", targetLandfillTons);
        System.out.printf ("  Landfill Reduction       : %.0f tons/day%n", savedPerDay);
        System.out.printf ("  Annual Saving            : %.0f tons/year%n", savedPerYear);
        System.out.println();
        System.out.println("  [NOTE] Achieving 40% recycling would reduce Nullah Leh");
        System.out.println("         pollution by an estimated 20% annually.");
    }

    // --------------------------------------------------------
    // PRINTS THE REPORT FOOTER
    // --------------------------------------------------------
    private void printFooter() {
        System.out.println();
        System.out.println("  " + "=".repeat(70));
        System.out.println("       END OF REPORT — WEEK " + weekNumber);
        System.out.println("       Rawalpindi Waste Management Tracker v1.0");
        System.out.println("       NUST SEECS — CS-212 Semester Project");
        System.out.println("  " + "=".repeat(70));
        System.out.println();
    }
}
