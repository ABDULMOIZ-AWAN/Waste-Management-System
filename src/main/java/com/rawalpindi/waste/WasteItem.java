package com.rawalpindi.waste;
import java.time.LocalDate;

// ============================================================
// ABSTRACT BASE CLASS FOR ALL WASTE TYPES IN THE SYSTEM
// ENCAPSULATES COMMON PROPERTIES: ID, WEIGHT, DATE ADDED
// SUBCLASSES MUST IMPLEMENT getRecycleValue() AND getHazardLevel()
// ============================================================
public abstract class WasteItem {

    private int wasteID;
    private double weight;          // IN KILOGRAMS
    private LocalDate dateAdded;
    private String wasteType;       // HUMAN-READABLE TYPE LABEL

    // --------------------------------------------------------
    // CONSTRUCTOR: INITIALIZES CORE WASTE PROPERTIES
    // DATE IS AUTOMATICALLY SET TO TODAY
    // --------------------------------------------------------
    public WasteItem(int wasteID, double weight, String wasteType) {
        this.wasteID   = wasteID;
        this.weight    = weight;
        this.wasteType = wasteType;
        this.dateAdded = LocalDate.now();
    }

    // --------------------------------------------------------
    // ABSTRACT METHODS: EACH WASTE TYPE DEFINES ITS OWN
    // RECYCLING VALUE (PKR) AND HAZARD LEVEL (1-5 SCALE)
    // --------------------------------------------------------
    public abstract double getRecycleValue();
    public abstract int    getHazardLevel();
    public abstract String getPrimaryDetail();

    // --------------------------------------------------------
    // GETTERS
    // --------------------------------------------------------
    public int       getWasteID()   { return wasteID;   }
    public double    getWeight()    { return weight;     }
    public LocalDate getDateAdded() { return dateAdded;  }
    public String    getWasteType() { return wasteType;  }

    // --------------------------------------------------------
    // SETTER: ALLOWS WEIGHT CORRECTION IF NEEDED
    // --------------------------------------------------------
    public void setWeight(double weight) { this.weight = weight; }

    // --------------------------------------------------------
    // RETURNS A FORMATTED STRING WITH FULL WASTE DETAILS
    // OVERRIDDEN IN SUBCLASSES TO INCLUDE EXTRA ATTRIBUTES
    // --------------------------------------------------------
    @Override
    public String toString() {
        return String.format(
                "  [ID: %d | Type: %-12s | Weight: %.2f kg | Hazard: %d/5 | Recycle Value: PKR %.0f | Date: %s]",
                wasteID, wasteType, weight, getHazardLevel(), getRecycleValue(), dateAdded
        );
    }
}

// ============================================================
// ORGANIC WASTE: FOOD SCRAPS, GARDEN WASTE, PAPER
// LOW HAZARD BUT DECOMPOSES AND CAN PRODUCE COMPOST
// MONETARY RECYCLE VALUE IS ZERO (COMPOSTED, NOT SOLD)
// ============================================================
class OrganicWaste extends WasteItem {

    private int    decompositionDays;   // ESTIMATED DAYS TO DECOMPOSE NATURALLY
    private String compostCategory;     // "FOOD", "GARDEN", OR "PAPER"

    // --------------------------------------------------------
    // CONSTRUCTOR
    // --------------------------------------------------------
    public OrganicWaste(int wasteID, double weight, String compostCategory) {
        super(wasteID, weight, "Organic");
        this.compostCategory    = compostCategory.toUpperCase();
        this.decompositionDays  = assignDecompositionDays(compostCategory);
    }

    // --------------------------------------------------------
    // ASSIGNS REALISTIC DECOMPOSITION DAYS BASED ON CATEGORY
    // FOOD DECOMPOSES FASTEST, PAPER TAKES A BIT LONGER
    // --------------------------------------------------------
    private int assignDecompositionDays(String category) {
        switch (category.toUpperCase()) {
            case "FOOD":   return 14;
            case "GARDEN": return 60;
            case "PAPER":  return 30;
            default:       return 30;
        }
    }

    // --------------------------------------------------------
    // ORGANIC WASTE HAS NO DIRECT MONETARY RECYCLE VALUE
    // IT IS COMPOSTED, NOT SOLD TO RECYCLERS
    // --------------------------------------------------------
    @Override
    public double getRecycleValue() { return 0.0; }

    // --------------------------------------------------------
    // HAZARD LEVEL 1: LOWEST — BIODEGRADABLE AND SAFE
    // --------------------------------------------------------
    @Override
    public int getHazardLevel() { return 1; }

    @Override
    public String getPrimaryDetail() { return compostCategory; }

    // --------------------------------------------------------
    // GETTERS
    // --------------------------------------------------------
    public int    getDecompositionDays() { return decompositionDays; }
    public String getCompostCategory()   { return compostCategory;   }

    // --------------------------------------------------------
    // EXTENDED TOSTRING WITH ORGANIC-SPECIFIC ATTRIBUTES
    // --------------------------------------------------------
    @Override
    public String toString() {
        return super.toString() + String.format(
                "\n    -> Compost Category: %s | Decomposition: %d days",
                compostCategory, decompositionDays
        );
    }
}

// ============================================================
// PLASTIC WASTE: BOTTLES, BAGS, PACKAGING, CONTAINERS
// MEDIUM HAZARD — TAKES 500+ YEARS TO DECOMPOSE
// HAS MONETARY RECYCLE VALUE: PKR 20-40/KG DEPENDING ON GRADE
// ============================================================
class PlasticWaste extends WasteItem {

    private String  plasticGrade;   // "PET", "HDPE", "PVC", "LDPE", "PP", "PS"
    private boolean isRecyclable;   // SOME PLASTIC GRADES ARE NOT RECYCLABLE
    private int     decompositionYears; // HOW MANY YEARS TO DECOMPOSE IN LANDFILL

    // --------------------------------------------------------
    // CONSTRUCTOR
    // --------------------------------------------------------
    public PlasticWaste(int wasteID, double weight, String plasticGrade) {
        super(wasteID, weight, "Plastic");
        this.plasticGrade       = plasticGrade.toUpperCase();
        this.isRecyclable       = determineRecyclability(plasticGrade);
        this.decompositionYears = assignDecompositionYears(plasticGrade);
    }

    // --------------------------------------------------------
    // PET AND HDPE ARE MOST COMMONLY RECYCLED IN PAKISTAN
    // PVC AND PS ARE GENERALLY NOT ACCEPTED BY LOCAL RECYCLERS
    // --------------------------------------------------------
    private boolean determineRecyclability(String grade) {
        switch (grade.toUpperCase()) {
            case "PET":
            case "HDPE":
            case "PP":   return true;
            default:     return false;
        }
    }

    // --------------------------------------------------------
    // ASSIGNS DECOMPOSITION YEARS PER PLASTIC GRADE
    // --------------------------------------------------------
    private int assignDecompositionYears(String grade) {
        switch (grade.toUpperCase()) {
            case "PET":  return 450;
            case "HDPE": return 500;
            case "PVC":  return 1000;
            case "LDPE": return 400;
            case "PP":   return 400;
            case "PS":   return 500;
            default:     return 500;
        }
    }

    // --------------------------------------------------------
    // RECYCLABLE PLASTICS FETCH PKR 30/KG, NON-RECYCLABLE PKR 5/KG
    // --------------------------------------------------------
    @Override
    public double getRecycleValue() {
        return isRecyclable ? getWeight() * 30.0 : getWeight() * 5.0;
    }

    // --------------------------------------------------------
    // HAZARD LEVEL 3: MEDIUM — LEACHES CHEMICALS OVER TIME
    // --------------------------------------------------------
    @Override
    public int getHazardLevel() { return 3; }

    @Override
    public String getPrimaryDetail() { return plasticGrade; }

    // --------------------------------------------------------
    // GETTERS
    // --------------------------------------------------------
    public String  getPlasticGrade()       { return plasticGrade;       }
    public boolean isRecyclable()          { return isRecyclable;        }
    public int     getDecompositionYears() { return decompositionYears; }

    // --------------------------------------------------------
    // EXTENDED TOSTRING WITH PLASTIC-SPECIFIC ATTRIBUTES
    // --------------------------------------------------------
    @Override
    public String toString() {
        return super.toString() + String.format(
                "\n    -> Grade: %s | Recyclable: %s | Decomposes in: %d years",
                plasticGrade, isRecyclable ? "YES" : "NO", decompositionYears
        );
    }
}

// ============================================================
// ELECTRONIC WASTE (E-WASTE): PHONES, BATTERIES, LAPTOPS, APPLIANCES
// HIGHEST HAZARD LEVEL — CONTAINS HEAVY METALS AND TOXINS
// HIGHEST RECYCLE VALUE DUE TO RECOVERABLE METALS (GOLD, COPPER)
// ============================================================
 class ElectronicWaste extends WasteItem {

    private String  deviceType;        // "MOBILE", "LAPTOP", "BATTERY", "APPLIANCE", "TV"
    private boolean containsMercury;   // FLUORESCENT DISPLAYS AND SOME BATTERIES DO
    private String  brandName;         // OPTIONAL BRAND FOR TRACKING

    // --------------------------------------------------------
    // CONSTRUCTOR
    // --------------------------------------------------------
    public ElectronicWaste(int wasteID, double weight, String deviceType, String brandName) {
        super(wasteID, weight, "Electronic");
        this.deviceType      = deviceType.toUpperCase();
        this.brandName       = brandName;
        this.containsMercury = determineMercuryContent(deviceType);
    }

    // --------------------------------------------------------
    // BATTERIES AND OLD TVs (CRT) COMMONLY CONTAIN MERCURY
    // --------------------------------------------------------
    private boolean determineMercuryContent(String device) {
        switch (device.toUpperCase()) {
            case "BATTERY":
            case "TV":       return true;
            default:         return false;
        }
    }

    // --------------------------------------------------------
    // RECYCLE VALUE VARIES BY DEVICE — MOBILES HAVE MOST GOLD
    // BASE RATE: PKR 50/KG, MOBILES: PKR 80/KG, BATTERIES: PKR 20/KG
    // --------------------------------------------------------
    @Override
    public double getRecycleValue() {
        switch (deviceType) {
            case "MOBILE":    return getWeight() * 80.0;
            case "LAPTOP":    return getWeight() * 60.0;
            case "BATTERY":   return getWeight() * 20.0;
            case "APPLIANCE": return getWeight() * 40.0;
            default:          return getWeight() * 50.0;
        }
    }

    // --------------------------------------------------------
    // HAZARD LEVEL 5: HIGHEST — TOXIC METALS, FIRE RISK (BATTERIES)
    // --------------------------------------------------------
    @Override
    public int getHazardLevel() { return 5; }

    @Override
    public String getPrimaryDetail() {
        return deviceType + (brandName == null || brandName.isEmpty() || brandName.equalsIgnoreCase("Unknown") ? "" : " (" + brandName + ")");
    }

    // --------------------------------------------------------
    // GETTERS
    // --------------------------------------------------------
    public String  getDeviceType()      { return deviceType;      }
    public boolean containsMercury()    { return containsMercury; }
    public String  getBrandName()       { return brandName;       }

    // --------------------------------------------------------
    // EXTENDED TOSTRING WITH E-WASTE-SPECIFIC ATTRIBUTES
    // --------------------------------------------------------
    @Override
    public String toString() {
        return super.toString() + String.format(
                "\n    -> Device: %s | Brand: %s | Mercury: %s",
                deviceType, brandName, containsMercury ? "YES (HANDLE WITH CARE)" : "NO"
        );
    }
}

// ============================================================
// GLASS WASTE: BOTTLES, JARS, BROKEN GLASS, WINDOWS
// LOW-MEDIUM HAZARD — INERT BUT SHARP AND SLOW TO DEGRADE
// FULLY RECYCLABLE WITH MODERATE VALUE
// ============================================================
class GlassWaste extends WasteItem {

    private String  glassColor;     // "CLEAR", "GREEN", "BROWN" — AFFECTS RECYCLE VALUE
    private boolean isBroken;       // BROKEN GLASS IS A SAFETY HAZARD FOR COLLECTORS

    // --------------------------------------------------------
    // CONSTRUCTOR
    // --------------------------------------------------------
    public GlassWaste(int wasteID, double weight, String glassColor, boolean isBroken) {
        super(wasteID, weight, "Glass");
        this.glassColor = glassColor.toUpperCase();
        this.isBroken   = isBroken;
    }

    // --------------------------------------------------------
    // CLEAR GLASS HAS HIGHEST VALUE: PKR 15/KG
    // COLORED GLASS FETCHES PKR 8/KG
    // BROKEN GLASS PENALTY: VALUE REDUCED BY HALF
    // --------------------------------------------------------
    @Override
    public double getRecycleValue() {
        double baseRate = glassColor.equals("CLEAR") ? 15.0 : 8.0;
        return isBroken ? getWeight() * baseRate * 0.5 : getWeight() * baseRate;
    }

    // --------------------------------------------------------
    // HAZARD LEVEL 2: LOW-MEDIUM — INERT BUT SHARP WHEN BROKEN
    // --------------------------------------------------------
    @Override
    public int getHazardLevel() { return isBroken ? 3 : 2; }

    @Override
    public String getPrimaryDetail() { return glassColor; }

    // --------------------------------------------------------
    // GETTERS
    // --------------------------------------------------------
    public String  getGlassColor() { return glassColor; }
    public boolean isBroken()      { return isBroken;   }

    @Override
    public String toString() {
        return super.toString() + String.format(
                "\n    -> Color: %s | Broken: %s",
                glassColor, isBroken ? "YES (SAFETY RISK)" : "NO"
        );
    }
}


// ============================================================
// METAL WASTE: CANS, SCRAP METAL, WIRES, PIPES
// LOW HAZARD — HIGHLY RECYCLABLE, STRONG MARKET IN PAKISTAN
// SCRAP METAL DEALERS (KABARI) BUY THIS DIRECTLY
// ============================================================
class MetalWaste extends WasteItem {

    private String metalType;   // "ALUMINUM", "STEEL", "COPPER", "IRON", "BRASS"
    private boolean isRusted;   // RUSTED METAL FETCHES LOWER PRICE

    // --------------------------------------------------------
    // CONSTRUCTOR
    // --------------------------------------------------------
    public MetalWaste(int wasteID, double weight, String metalType, boolean isRusted) {
        super(wasteID, weight, "Metal");
        this.metalType = metalType.toUpperCase();
        this.isRusted  = isRusted;
    }

    // --------------------------------------------------------
    // COPPER AND BRASS HAVE HIGHEST VALUE IN PAKISTAN SCRAP MARKET
    // ALUMINUM CANS ARE MODERATE, STEEL AND IRON ARE LOWER
    // RUSTED METAL GETS 30% LESS VALUE
    // --------------------------------------------------------
    @Override
    public double getRecycleValue() {
        double baseRate;
        switch (metalType) {
            case "COPPER":    baseRate = 120.0; break;
            case "BRASS":     baseRate = 100.0; break;
            case "ALUMINUM":  baseRate =  60.0; break;
            case "STEEL":     baseRate =  30.0; break;
            case "IRON":      baseRate =  25.0; break;
            default:          baseRate =  30.0; break;
        }
        return isRusted ? getWeight() * baseRate * 0.7 : getWeight() * baseRate;
    }

    // --------------------------------------------------------
    // HAZARD LEVEL 2: LOW — SHARP EDGES BUT CHEMICALLY STABLE
    // --------------------------------------------------------
    @Override
    public int getHazardLevel() { return 2; }

    @Override
    public String getPrimaryDetail() { return metalType; }

    // --------------------------------------------------------
    // GETTERS
    // --------------------------------------------------------
    public String  getMetalType() { return metalType; }
    public boolean isRusted()     { return isRusted;  }

    @Override
    public String toString() {
        return super.toString() + String.format(
                "\n    -> Metal Type: %s | Rusted: %s",
                metalType, isRusted ? "YES (REDUCED VALUE)" : "NO"
        );
    }
}
















