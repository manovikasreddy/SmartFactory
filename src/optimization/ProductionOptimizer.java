package optimization;

import java.util.*;

import model.Machine;

public class ProductionOptimizer {

    public static void recommendMachines(
            List<Machine> machines,
            int requiredCapacity) {

        if (machines.isEmpty()) {

            System.out.println(
                    "No machines available.");

            return;
        }

        int[] capacities =
                new int[machines.size()];

        for (int i = 0;
             i < machines.size();
             i++) {

            capacities[i] =
                    machines.get(i).getCapacity();
        }

        List<Integer> selected =
                MachineSelection.selectMachines(
                        capacities,
                        requiredCapacity);

        System.out.println(
                "\n--- PRODUCTION OPTIMIZATION ---");

        if (selected.isEmpty()) {

            System.out.println(
                    "No suitable combination found.");

            return;
        }

        int total = 0;

        System.out.println(
                "Required Capacity: "
                + requiredCapacity);

        System.out.println(
                "\nRecommended Machines:");

        for (int index : selected) {

            Machine machine =
                    machines.get(index);

            System.out.println(
                    machine.getMachineId()
                    + " - "
                    + machine.getMachineName()
                    + " : "
                    + machine.getCapacity());

            total += machine.getCapacity();
        }

        System.out.println(
                "\nTotal Capacity: " + total);
    }
}