package com.rawalpindi.waste;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public final class ReportTextCapture {
    private ReportTextCapture() {}

    public static String capture(Simulation sim) {
        PrintStream original = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);

        try {
            System.setOut(ps);

            Report report = new Report(
                    sim.getHouseholds(),
                    sim.getFineManager(),
                    sim.getCurrentWeek()
            );
            report.generateFullReport();
        } finally {
            System.setOut(original);
            ps.flush();
        }

        return baos.toString();
    }
}

