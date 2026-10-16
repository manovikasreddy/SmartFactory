package parallel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Randomized QuickSort for Ranking Industrial Production Orders.
 * Uses a uniform random pivot to eliminate worst-case O(n^2) degradation on
 * pre-sorted or clustered batch orders.
 * Employs 3-way partitioning (Dutch National Flag) to efficiently handle
 * large batches sharing identical priority tiers.
 */
public class RandomizedQuickSort {

    private static final Random random = new Random();

    public static class SortStats {
        private final long comparisons;
        private final long swaps;
        private final long executionTimeNanos;

        public SortStats(long comparisons, long swaps, long executionTimeNanos) {
            this.comparisons = comparisons;
            this.swaps = swaps;
            this.executionTimeNanos = executionTimeNanos;
        }

        public long getComparisons() {
            return comparisons;
        }

        public long getSwaps() {
            return swaps;
        }

        public long getExecutionTimeNanos() {
            return executionTimeNanos;
        }
    }

    private static long comparisonCounter = 0;
    private static long swapCounter = 0;

    /**
     * Sorts the given list of production batches in-place by priority.
     */
    public static SortStats sort(List<ProductionBatch> batches) {
        comparisonCounter = 0;
        swapCounter = 0;
        long startTime = System.nanoTime();

        if (batches != null && batches.size() > 1) {
            randomizedQuickSort(batches, 0, batches.size() - 1);
        }

        long timeNanos = System.nanoTime() - startTime;
        return new SortStats(comparisonCounter, swapCounter, timeNanos);
    }

    private static void randomizedQuickSort(List<ProductionBatch> list, int low, int high) {
        if (low >= high) {
            return;
        }

        // Pick random pivot index in [low, high]
        int randomIndex = low + random.nextInt(high - low + 1);
        swap(list, low, randomIndex);

        ProductionBatch pivot = list.get(low);

        // 3-way partitioning (Dijkstra's Dutch National Flag)
        int lt = low;       // list[low..lt-1] < pivot
        int gt = high;      // list[gt+1..high] > pivot
        int i = low + 1;    // list[lt..i-1] == pivot

        while (i <= gt) {
            comparisonCounter++;
            int cmp = list.get(i).compareTo(pivot);
            if (cmp < 0) {
                swap(list, lt++, i++);
            } else if (cmp > 0) {
                swap(list, i, gt--);
            } else {
                i++;
            }
        }

        // Recurse on sub-arrays
        randomizedQuickSort(list, low, lt - 1);
        randomizedQuickSort(list, gt + 1, high);
    }

    private static void swap(List<ProductionBatch> list, int i, int j) {
        if (i == j) return;
        ProductionBatch temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
        swapCounter++;
    }

    /**
     * Generates a sample backlog of industrial production orders for demonstration.
     */
    public static List<ProductionBatch> createSampleOrderBacklog() {
        List<ProductionBatch> orders = new ArrayList<>();
        orders.add(new ProductionBatch("ORD-701", "Aerospace Turbine Disc", 1, 25, 6, "CNC Alpha (M1)"));
        orders.add(new ProductionBatch("ORD-702", "Automotive Engine Block", 4, 300, 24, "CNC Beta (M2)"));
        orders.add(new ProductionBatch("ORD-703", "Medical Implant Pins", 1, 150, 8, "Lathe Gamma (M3)"));
        orders.add(new ProductionBatch("ORD-704", "Conveyor Roller Bushing", 8, 800, 72, "Lathe Gamma (M3)"));
        orders.add(new ProductionBatch("ORD-705", "Hydraulic Cylinder Rod", 2, 80, 12, "Robotic Milling Delta (M4)"));
        orders.add(new ProductionBatch("ORD-706", "Electronic Enclosure", 6, 500, 48, "Stamping Unit (M5)"));
        orders.add(new ProductionBatch("ORD-707", "Gearbox Drive Shaft", 3, 120, 18, "CNC Alpha (M1)"));
        orders.add(new ProductionBatch("ORD-708", "Precision Bearing Ring", 2, 450, 10, "Lathe Gamma (M3)"));
        orders.add(new ProductionBatch("ORD-709", "Standard Sheet Bracket", 9, 1200, 96, "Stamping Unit (M5)"));
        orders.add(new ProductionBatch("ORD-710", "High-Torque Camshaft", 1, 40, 4, "CNC Beta (M2)"));
        orders.add(new ProductionBatch("ORD-711", "Exhaust Manifold Flange", 5, 220, 36, "Robotic Milling Delta (M4)"));
        orders.add(new ProductionBatch("ORD-712", "Sensor Heat Sink", 7, 650, 60, "Stamping Unit (M5)"));
        return orders;
    }
}
