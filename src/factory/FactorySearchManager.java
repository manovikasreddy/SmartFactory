package factory;

import java.util.*;

import model.Machine;
import model.Maintenance;

public class FactorySearchManager {

    public static void searchMachines(
            List<Machine> machines,
            String query) {

        System.out.println("\n--- MACHINE SEARCH ---");

        boolean found = false;

        for (Machine machine : machines) {

            String data =
                    machine.getMachineId() + " " +
                    machine.getMachineName();

            if (MachineSearch.search(data, query)) {

                System.out.println(
                        "\n" + machine);

                found = true;
            }
        }

        if (!found)
            System.out.println("No machine found.");
    }

    public static void searchMaintenance(
            List<Maintenance> reports,
            String query) {

        System.out.println(
                "\n--- MAINTENANCE SEARCH ---");

        boolean found = false;

        for (Maintenance report : reports) {

            String text =
                    report.getFault() + " " +
                    report.getDescription();

            if (MaintenancePatternFinder
                    .findPattern(text, query)
                    .size() > 0) {

                System.out.println(
                        "\n" + report);

                found = true;
            }
        }

        if (!found)
            System.out.println(
                    "No matching maintenance report.");
    }

    public static void detectFaults(
            List<Maintenance> reports,
            List<String> keywords) {

        FaultDetection detector =
                new FaultDetection(keywords);

        System.out.println(
                "\n--- FAULT DETECTION ---");

        for (Maintenance report : reports) {

            String text =
                    report.getFault() + " " +
                    report.getDescription();

            List<String> faults =
                    detector.detect(text);

            if (!faults.isEmpty()) {

                System.out.println(
                        "\nMachine: "
                        + report.getMachineId());

                System.out.println(
                        "Detected: " + faults);
            }
        }
    }
}