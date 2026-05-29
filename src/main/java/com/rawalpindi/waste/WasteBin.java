package com.rawalpindi.waste;
import java.util.ArrayList;

// ============================================================
// WASTE BIN: HOLDS WASTE ITEMS OF A SPECIFIC TYPE
// EACH HOUSEHOLD HAS MULTIPLE BINS (ORGANIC, PLASTIC, ETC.)
// ENFORCES CAPACITY LIMITS AND TRACKS MISPLACED WASTE
// ============================================================
public class WasteBin {

    private int                 binID;
    private String              binType;        // "ORGANIC", "PLASTIC", "ELECTRONIC", "GLASS", "METAL"
    private double              capacity;       // MAXIMUM WEIGHT IN KG
    private double              currentLoad;    // CURRENT TOTAL WEIGHT IN KG
    private ArrayList<WasteItem> wasteItems;
    private int                 mismatchCount;  // TRACKS HOW MANY WRONG ITEMS WERE ADDED

    // --------------------------------------------------------
    // CONSTRUCTOR
    // --------------------------------------------------------
    public WasteBin(int binID, String binType, double capacity) {
        this.binID        = binID;
        this.binType      = binType.toUpperCase();
        this.capacity     = capacity;
        this.currentLoad  = 0.0;
        this.wasteItems   = new ArrayList<>();
        this.mismatchCount = 0;
    }

    // --------------------------------------------------------
    // ADDS WASTE ITEM TO BIN
    // VALIDATES WEIGHT, CHECKS CAPACITY, TRACKS MISMATCHES
    // THROWS EXCEPTIONS FOR OVERFLOW AND INVALID INPUT
    // --------------------------------------------------------
    public void addWaste(WasteItem item) throws BinOverflowException, InvalidWasteException {

        // VALIDATE WEIGHT IS POSITIVE
        if (item.getWeight() <= 0) {
            throw new InvalidWasteException("weight", "Weight must be greater than 0 kg.");
        }

        // CHECK IF BIN HAS ENOUGH ROOM
        double excessWeight = (currentLoad + item.getWeight()) - capacity;
        if (excessWeight > 0) {
            throw new BinOverflowException(binID, excessWeight);
        }

        // CHECK IF WASTE TYPE MATCHES BIN TYPE — RECORD MISMATCH IF NOT
        if (!item.getWasteType().equalsIgnoreCase(binType)) {
            mismatchCount++;
            System.out.println("  [WARNING] " + item.getWasteType() +
                    " waste added to " + binType + " bin. Compliance will be affected!");
        }

        wasteItems.add(item);
        currentLoad += item.getWeight();
    }

    // --------------------------------------------------------
    // EMPTIES THE BIN AND RETURNS ALL ITEMS FOR TRUCK COLLECTION
    // RESETS LOAD AND CLEARS THE LIST
    // --------------------------------------------------------
    public ArrayList<WasteItem> emptyBin() {
        ArrayList<WasteItem> collected = new ArrayList<>(wasteItems);
        wasteItems.clear();
        currentLoad  = 0.0;
        mismatchCount = 0;
        return collected;
    }

    // --------------------------------------------------------
    // CALCULATES FILL PERCENTAGE FOR OVERFLOW WARNINGS
    // --------------------------------------------------------
    public double getFillPercentage() {
        return (currentLoad / capacity) * 100.0;
    }

    // --------------------------------------------------------
    // RETURNS TRUE IF BIN IS NEAR CAPACITY (80% OR MORE)
    // USED BY SIMULATION TO TRIGGER OVERFLOW EVENTS
    // --------------------------------------------------------
    public boolean isNearlyFull() {
        return getFillPercentage() >= 80.0;
    }

    // --------------------------------------------------------
    // CALCULATES TOTAL RECYCLE VALUE OF ALL ITEMS IN BIN
    // --------------------------------------------------------
    public double getTotalRecycleValue() {
        double total = 0.0;
        for (WasteItem item : wasteItems) {
            total += item.getRecycleValue();
        }
        return total;
    }

    // --------------------------------------------------------
    // DISPLAYS FULL BIN STATUS WITH ALL ITEMS
    // --------------------------------------------------------
    public void displayBinDetails() {
        System.out.println("  +-- Bin #" + binID + " [" + binType + "]");
        System.out.printf ("      Load: %.2f / %.2f kg (%.0f%%)%n",
                currentLoad, capacity, getFillPercentage());
        System.out.println("      Items: " + wasteItems.size() +
                " | Mismatches: " + mismatchCount);

        if (isNearlyFull()) {
            System.out.println("      [!] NEARLY FULL — COLLECTION NEEDED SOON");
        }

        for (WasteItem item : wasteItems) {
            System.out.println("    " + item);
        }
    }

    // --------------------------------------------------------
    // GETTERS
    // --------------------------------------------------------
    public int                  getBinID()        { return binID;        }
    public String               getBinType()      { return binType;      }
    public double               getCapacity()     { return capacity;     }
    public double               getCurrentLoad()  { return currentLoad;  }
    public ArrayList<WasteItem> getWasteItems()   { return wasteItems;   }
    public int                  getMismatchCount(){ return mismatchCount; }
}

