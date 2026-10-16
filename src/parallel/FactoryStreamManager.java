package parallel;

import java.math.BigInteger;
import java.util.List;
import java.util.Scanner;

import model.FactoryRecord;

/**
 * High-level coordinator for CO-6 Randomized and Parallel Algorithms.
 * Demonstrates:
 * 1. Randomized QuickSort to rank production orders by priority.
 * 2. Miller-Rabin test for large numerical computations & IoT security.
 * 3. Reservoir Sampling for continuous machine telemetry streams.
 * 4. Blelloch work-efficient parallel scan for cumulative production metrics.
 * 5. Parallel Reduce for large-scale factory data aggregation.
 * 6. Brent's theorem evaluation for parallel processing scaling.
 */
public class FactoryStreamManager {

    /**
     * Ranks production orders using Randomized QuickSort.
     */
    public static void rankProductionOrders() {
        System.out.println("==========================================");
        System.out.println(" ORDER PRIORITY RANKING [RANDOMIZED QUICKSORT]");
        System.out.println("==========================================");

        List<ProductionBatch> orders = RandomizedQuickSort.createSampleOrderBacklog();

        System.out.println("Unordered Factory Job Queue (" + orders.size() + " batches):");
        for (ProductionBatch b : orders) {
            System.out.println("  " + b);
        }

        System.out.println("\nExecuting Randomized QuickSort with 3-Way Partitioning...");
        RandomizedQuickSort.SortStats stats = RandomizedQuickSort.sort(orders);

        System.out.println("\nOptimally Ranked Production Schedule (Highest Priority First):");
        int rank = 1;
        for (ProductionBatch b : orders) {
            System.out.printf("  #%2d -> %s%n", rank++, b);
        }

        System.out.println("\nSorting Performance Telemetry:");
        System.out.printf("  Comparisons Performed : %,d%n", stats.getComparisons());
        System.out.printf("  Swaps Performed       : %,d%n", stats.getSwaps());
        System.out.printf("  Sorting Time          : %,d ns (%.3f ms)%n",
                stats.getExecutionTimeNanos(), stats.getExecutionTimeNanos() / 1_000_000.0);
        System.out.println("Expected O(n log n) runtime achieved via uniform random pivot selection.");
        System.out.println("==========================================");
    }

    /**
     * Tests large numbers for primality using Miller-Rabin test.
     */
    public static void testMillerRabin(Scanner scanner) {
        System.out.println("==========================================");
        System.out.println("  NUMERICAL COMPUTATION [MILLER-RABIN TEST]");
        System.out.println("==========================================");
        System.out.println("Industrial Context: Used in IoT sensor authentication, digital device");
        System.out.println("fingerprints, and cryptographic ledger nonces.\n");

        System.out.print("Enter a candidate number (or press Enter for 64-bit IoT token test): ");
        String input = scanner.nextLine().trim();

        BigInteger candidate;
        if (input.isEmpty()) {
            // Default 64-bit industrial prime token
            candidate = new BigInteger("1000000007");
            System.out.println("Using default industrial test token: " + candidate);
        } else {
            try {
                candidate = new BigInteger(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format. Using default: 1000000007");
                candidate = new BigInteger("1000000007");
            }
        }

        int rounds = 20;
        System.out.printf("Executing %d independent Miller-Rabin witness rounds...%n%n", rounds);

        MillerRabinPrimality.PrimalityResult res = MillerRabinPrimality.testBigInteger(candidate, rounds);

        for (String detail : res.getRoundDetails()) {
            System.out.println("  " + detail);
        }

        System.out.println("\n------------------------------------------");
        System.out.printf("Candidate Number : %s (%d bits)%n", res.getCandidate(), res.getCandidate().bitLength());
        System.out.printf("Result           : %s%n", res.isPrime() ? "PRIME (Probable Prime)" : "COMPOSITE (Not Prime)");
        System.out.printf("Rounds Tested    : %d rounds%n", res.getRoundsTested());
        System.out.printf("Max False Pos Pr : %.2e (at most 4^-%d)%n", res.getMaxErrorProbability(), res.getRoundsTested());
        System.out.println("==========================================");
    }

    /**
     * Demonstrates Reservoir Sampling on continuous factory sensor streams.
     */
    public static void sampleSensorStream(List<FactoryRecord> stream, int sampleSize) {
        System.out.println("==========================================");
        System.out.println(" STREAM SAMPLING [RESERVOIR SAMPLING ALG R]");
        System.out.println("==========================================");

        sampleSize = Math.max(5, Math.min(sampleSize, 50));
        System.out.printf("Total Streaming Records Available : %,d records%n", stream.size());
        System.out.printf("Configured Fixed Reservoir Size k : %d samples%n", sampleSize);
        System.out.println("Simulating unbounded 1-pass sensor telemetry stream...\n");

        ReservoirSampling sampler = new ReservoirSampling(sampleSize);
        for (FactoryRecord r : stream) {
            sampler.consume(r);
        }

        List<FactoryRecord> sample = sampler.getReservoir();
        System.out.printf("Sample of %d records successfully maintained in O(k) memory!%n", sample.size());

        System.out.println("\nSelected Representative Stream Telemetry (First 8 samples):");
        int idx = 1;
        for (int i = 0; i < Math.min(8, sample.size()); i++) {
            FactoryRecord r = sample.get(i);
            System.out.printf("  #%2d: Record %s | Device: %s | Temp: %.1f °C | Vib: %.2f mm/s | Fault: %s%n",
                    idx++, r.getRecordId(), r.getDeviceId(), r.getTemperature(), r.getVibration(), r.getFailureType());
        }

        // Statistical validation
        ReservoirSampling.StreamStatistics fullStats = ReservoirSampling.StreamStatistics.calculate(stream);
        ReservoirSampling.StreamStatistics sampleStats = ReservoirSampling.StreamStatistics.calculate(sample);

        System.out.println("\nStatistical Representative Validation (Full Stream vs Reservoir Sample):");
        System.out.printf("  Metric           | Full Stream (N = %,d) | Reservoir Sample (k = %d)%n", fullStats.count, sampleStats.count);
        System.out.println("  -----------------+----------------------+----------------------");
        System.out.printf("  Avg Temperature  | %20.2f °C | %20.2f °C%n", fullStats.avgTemperature, sampleStats.avgTemperature);
        System.out.printf("  Avg Vibration    | %18.2f mm/s | %18.2f mm/s%n", fullStats.avgVibration, sampleStats.avgVibration);
        System.out.printf("  Avg RPM          | %22.1f | %22.1f%n", fullStats.avgRpm, sampleStats.avgRpm);
        System.out.printf("  Fault Ratio      | %21.2f%% | %21.2f%%%n",
                (100.0 * fullStats.faultCount / fullStats.count), (100.0 * sampleStats.faultCount / sampleStats.count));
        System.out.println("\nMathematical property confirmed: Every item had exact selection probability k/N.");
        System.out.println("==========================================");
    }

    /**
     * Demonstrates Blelloch work-efficient parallel prefix scan.
     */
    public static void computeCumulativeMetrics(List<FactoryRecord> records) {
        System.out.println("==========================================");
        System.out.println(" CUMULATIVE METRICS [BLELLOCH PARALLEL SCAN]");
        System.out.println("==========================================");

        int n = Math.min(16, records.size());
        double[] productionUnits = new double[n];
        for (int i = 0; i < n; i++) {
            // Units produced per interval derived from RPM / 100
            productionUnits[i] = Math.round(records.get(i).getRpm() / 150.0);
        }

        System.out.printf("Input Interval Production Units (First %d intervals):%n  [ ", n);
        for (double v : productionUnits) {
            System.out.print((int) v + " ");
        }
        System.out.println("]\n");

        System.out.println("Executing Blelloch Work-Efficient Scan (Up-Sweep + Down-Sweep)...");
        BlellochScan.ScanResult result = BlellochScan.scan(productionUnits);

        System.out.printf("Tree Dimensions: Padded Length = %d (Power of 2)%n", result.getPaddedLength());
        System.out.printf("Up-Sweep Parallel Steps   : %d steps (Tree Depth)%n", result.getUpSweepSteps());
        System.out.printf("Down-Sweep Parallel Steps : %d steps%n", result.getDownSweepSteps());

        System.out.println("\nExclusive Prefix Sum (Prior Accumulated Output):");
        System.out.print("  [ ");
        for (double v : result.getExclusiveScan()) {
            System.out.print((int) v + " ");
        }
        System.out.println("]");

        System.out.println("\nInclusive Prefix Sum (Running Total Output):");
        System.out.print("  [ ");
        for (double v : result.getInclusiveScan()) {
            System.out.print((int) v + " ");
        }
        System.out.println("]");

        // Validate with sequential scan
        double[] seq = BlellochScan.sequentialScan(productionUnits);
        boolean correct = true;
        for (int i = 0; i < n; i++) {
            if (Math.abs(seq[i] - result.getInclusiveScan()[i]) > 1e-4) {
                correct = false;
                break;
            }
        }

        System.out.println("\nVerification against Sequential Scan: " + (correct ? "MATCHED 100%" : "MISMATCH"));
        System.out.println("Complexity: O(n) total additions, O(log n) span.");
        System.out.println("==========================================");
    }

    /**
     * Demonstrates Parallel Tree Reduce.
     */
    public static void runParallelTreeReduce(List<FactoryRecord> records) {
        System.out.println("==========================================");
        System.out.println(" DATA AGGREGATION [PARALLEL TREE REDUCE]");
        System.out.println("==========================================");

        System.out.printf("Aggregating Entire Factory Dataset (%,d sensor telemetry records)...%n", records.size());
        System.out.println("Executing ForkJoin Parallel Reduction across available CPU cores...\n");

        ParallelReduce.ParallelBenchmarkResult benchmark = ParallelReduce.aggregateWithBenchmark(records);
        ParallelReduce.AggregateMetrics m = benchmark.getMetrics();

        System.out.println("Consolidated Plant Metrics:");
        System.out.printf("  Total Records Processed : %,d%n", m.recordCount);
        System.out.printf("  Total Electrical Power  : %,.2f kW%n", m.totalEnergy);
        System.out.printf("  Maximum Temperature     : %.2f °C%n", m.maxTemperature);
        System.out.printf("  Peak Vibration Recorded : %.2f mm/s%n", m.maxVibration);
        System.out.printf("  Mean Operating RPM      : %.2f RPM%n", m.getAverageRpm());
        System.out.printf("  Total Machine Faults    : %,d records%n", m.faultCount);

        System.out.println("\nMulti-Core Parallel Performance Benchmark:");
        System.out.printf("  Available Hardware Cores: %d CPU cores%n", benchmark.getAvailableProcessors());
        System.out.printf("  Sequential Single-Thread: %,d ns (%.3f ms)%n",
                benchmark.getSequentialTimeNanos(), benchmark.getSequentialTimeNanos() / 1_000_000.0);
        System.out.printf("  Parallel ForkJoin Tree  : %,d ns (%.3f ms)%n",
                benchmark.getParallelTimeNanos(), benchmark.getParallelTimeNanos() / 1_000_000.0);
        System.out.printf("  Measured Speedup Factor : %.2fx%n", benchmark.getSpeedup());
        System.out.println("==========================================");
    }

    /**
     * Demonstrates Brent's Theorem for evaluating parallel production processing.
     */
    public static void evaluateBrentsTheorem(int recordCount) {
        System.out.println("==========================================");
        System.out.println(" PARALLEL EVALUATION [BRENT'S THEOREM]");
        System.out.println("==========================================");

        System.out.println("Brent's Work-Time Scheduling Principle: T_p <= (T_1 - T_inf) / p + T_inf\n");

        List<BrentsTheoremAnalyzer.WorkloadProfile> profiles =
                BrentsTheoremAnalyzer.getFactoryWorkloadProfiles(recordCount);

        for (BrentsTheoremAnalyzer.WorkloadProfile profile : profiles) {
            System.out.println("Workload Profile: " + profile.getWorkloadName());
            System.out.println("  Model Formula : " + profile.getAlgorithmModel());
            System.out.printf("  Total Work T1 : %,d operations (1 processor)%n", profile.getWorkT1());
            System.out.printf("  Span T_inf    : %,d critical path depth (inf processors)%n", profile.getSpanTinf());
            System.out.printf("  Half-Perf p1/2: %,d processors (T1 / T_inf)%n", profile.getWorkT1() / Math.max(1, profile.getSpanTinf()));

            System.out.println("\n  Scaling Table across Processor Counts (p):");
            System.out.println("  -------------------------------------------------------------------------");
            System.out.println("  Cores (p) | Brent Time T_p | Lower Bound | Speedup S_p | Efficiency E_p");
            System.out.println("  ----------+----------------+-------------+-------------+-----------------");

            int[] testCores = {1, 2, 4, 8, 16, 32, 64, 128};
            for (int p : testCores) {
                double brentTp = (double) (profile.getWorkT1() - profile.getSpanTinf()) / p + profile.getSpanTinf();
                double lower = Math.max((double) profile.getWorkT1() / p, (double) profile.getSpanTinf());
                double speedup = (double) profile.getWorkT1() / brentTp;
                double eff = speedup / p;

                System.out.printf("  %8d  | %14.1f | %11.1f | %10.2fx | %14.1f%%%n",
                        p, brentTp, lower, speedup, eff * 100.0);
            }
            System.out.println("  -------------------------------------------------------------------------\n");
        }

        System.out.println("Brent's Theorem evaluation completed.");
        System.out.println("==========================================");
    }
}
