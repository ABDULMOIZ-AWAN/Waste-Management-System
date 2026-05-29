package com.rawalpindi.waste;
// ============================================================
// CUSTOM EXCEPTIONS USED ACROSS THE PROJECT
// NOTE: File is NOT named Exception.java to avoid conflict with
// java.lang.Exception and Java's filename/classname rules.
// ============================================================

class BinOverflowException extends java.lang.Exception {

    private int binID;
    private double excessWeight;

    // --------------------------------------------------------
    // CONSTRUCTOR WITH BIN ID AND EXCESS WEIGHT FOR CONTEXT
    // --------------------------------------------------------
    public BinOverflowException(int binID, double excessWeight) {
        super(String.format(
                "BIN #%d OVERFLOW! Excess weight: %.2f kg. Please empty or use another bin.",
                binID, excessWeight
        ));
        this.binID        = binID;
        this.excessWeight = excessWeight;
    }

    public int    getBinID()        { return binID;        }
    public double getExcessWeight() { return excessWeight; }
}


// ============================================================
// CUSTOM EXCEPTION: THROWN WHEN WASTE DATA IS INVALID
// E.G. NEGATIVE WEIGHT, NULL TYPE, EMPTY FIELDS
// ============================================================
class InvalidWasteException extends java.lang.Exception {

    private String fieldName;

    // --------------------------------------------------------
    // CONSTRUCTOR WITH FIELD NAME FOR PRECISE ERROR REPORTING
    // --------------------------------------------------------
    public InvalidWasteException(String fieldName, String reason) {
        super(String.format(
                "INVALID WASTE DATA — Field '%s': %s", fieldName, reason
        ));
        this.fieldName = fieldName;
    }

    public String getFieldName() { return fieldName; }
}


// ============================================================
// CUSTOM EXCEPTION: THROWN WHEN WASTE IS PLACED IN WRONG BIN
// E.G. PLASTIC IN THE ORGANIC BIN
// THIS TRIGGERS A COMPLIANCE PENALTY FOR THE HOUSEHOLD
// ============================================================
class WasteMismatchException extends java.lang.Exception {

    private String wasteType;
    private String binType;

    // --------------------------------------------------------
    // CONSTRUCTOR: RECORDS WHAT WENT IN THE WRONG BIN
    // --------------------------------------------------------
    public WasteMismatchException(String wasteType, String binType) {
        super(String.format(
                "WASTE MISMATCH! '%s' waste cannot go in the '%s' bin. Compliance score will be penalized.",
                wasteType, binType
        ));
        this.wasteType = wasteType;
        this.binType   = binType;
    }

    public String getWasteType() { return wasteType; }
    public String getBinType()   { return binType;   }
}


// ============================================================
// CUSTOM EXCEPTION: THROWN WHEN TRUCK CAPACITY IS EXCEEDED
// SIMULATION CONTINUES BUT EXCESS WASTE STAYS IN BINS
// ============================================================
class TruckCapacityException extends java.lang.Exception {

    private int truckID;

    public TruckCapacityException(int truckID) {
        super(String.format(
                "TRUCK #%d IS FULL! Cannot collect more waste. Remaining waste stays in bins.",
                truckID
        ));
        this.truckID = truckID;
    }

    public int getTruckID() { return truckID; }
}

