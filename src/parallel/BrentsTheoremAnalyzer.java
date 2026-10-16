package parallel;

import java.util.ArrayList;
import java.util.List;

/**
 * Brent's Theorem Evaluation for Parallel Production-Data Processing.
 *
 * Theory:
 * Brent's Theorem (Work-Time Scheduling Principle) provides a rigorous upper bound
 * on the execution time T_p of a parallel algorithm executed on p processors:
 *
 *     T_p <= (T_1 - T_inf) / p + T_inf
 *
 * Where:
 *  - T_1   = Work (total sequential operations executed by 1 processor).
 *  - T_inf = Span / Depth (length of the critical path of dependencies with infinite processors).
 *  - p     = Number of available parallel processors / CPU cores.
 *
 * Key Analytics Derived:
 *  1. Speedup S_p = T_1 / T_p.
 *  2. Parallel Efficiency E_p = S_p / p = T_1 / (p * T_p) <= 1.0.
 *  3. Half-performance core count p_{1/2} = T_1 / T_inf.
 */
public class BrentsTheoremAnalyzer {

    public static class EvaluationPoint {
        private final int processors;
        private final double theoreticalTimeTp;
        private final double lowerBoundTp;
        private final double speedup;
        private final double efficiency;

        public EvaluationPoint(int processors, double theoreticalTimeTp, double lowerBoundTp,
                               double speedup, double efficiency) {
            this.processors = processors;
            this.theoreticalTimeTp = theoreticalTimeTp;
            this.lowerBoundTp = lowerBoundTp;
            this.speedup = speedup;
            this.efficiency = efficiency;
        }

        public int getProcessors() {
            return processors;
        }

        public double getTheoreticalTimeTp() {
            return theoreticalTimeTp;
        }

        public double getLowerBoundTp() {
            return lowerBoundTp;
        }

        public double getSpeedup() {
            return speedup;
        }

        public double getEfficiency() {
            return efficiency;
        }
    }

    public static class WorkloadProfile {
        private final String workloadName;
        private final long workT1;
        private final long spanTinf;
        private final String algorithmModel;

        public WorkloadProfile(String workloadName, long workT1, long spanTinf, String algorithmModel) {
            this.workloadName = workloadName;
            this.workT1 = workT1;
            this.spanTinf = spanTinf;
            this.algorithmModel = algorithmModel;
        }

        public String getWorkloadName() {
            return workloadName;
        }

        public long getWorkT1() {
            return workT1;
        }

        public long getSpanTinf() {
            return spanTinf;
        }

        public String getAlgorithmModel() {
            return algorithmModel;
        }
    }

    /**
     * Evaluates Brent's Bound for a workload across different processor counts (1 to maxProcessors).
     */
    public static List<EvaluationPoint> evaluate(long workT1, long spanTinf, int maxProcessors) {
        List<EvaluationPoint> results = new ArrayList<>();

        for (int p = 1; p <= maxProcessors; p++) {
            // Brent's Upper Bound
            double brentTp = (double) (workT1 - spanTinf) / p + spanTinf;
            // Lower Bound: max(T1/p, T_inf)
            double lowerBound = Math.max((double) workT1 / p, (double) spanTinf);

            double speedup = (double) workT1 / brentTp;
            double efficiency = speedup / p;

            results.add(new EvaluationPoint(p, brentTp, lowerBound, speedup, efficiency));
        }

        return results;
    }

    /**
     * Standard factory processing workload profiles.
     */
    public static List<WorkloadProfile> getFactoryWorkloadProfiles(int dataPoints) {
        List<WorkloadProfile> profiles = new ArrayList<>();

        // Workload 1: Parallel Tree Reduction (Sum/Max on sensor streams)
        // Work: n - 1 ops, Span: log2(n) steps
        long n = Math.max(1024, dataPoints);
        long redWork = n - 1;
        long redSpan = (long) Math.ceil(Math.log(n) / Math.log(2));
        profiles.add(new WorkloadProfile("Real-Time Sensor Tree Reduction", redWork, redSpan,
                "Tree Reduction: T1 = n - 1, T_inf = ceil(log2(n))"));

        // Workload 2: Blelloch Parallel Prefix Scan
        // Work: 2n ops, Span: 2 * log2(n) steps
        long scanWork = 2 * n;
        long scanSpan = 2 * (long) Math.ceil(Math.log(n) / Math.log(2));
        profiles.add(new WorkloadProfile("Cumulative Production Blelloch Scan", scanWork, scanSpan,
                "Work-Efficient Scan: T1 = 2n, T_inf = 2*log2(n)"));

        // Workload 3: Automated Optical Quality Inspection (Matrix operations)
        // Work: O(n * 64), Span: O(64 + log2(n))
        long imgWork = n * 64L;
        long imgSpan = 64L + (long) Math.ceil(Math.log(n) / Math.log(2));
        profiles.add(new WorkloadProfile("Optical Defect Vision Pipeline", imgWork, imgSpan,
                "Pixel Filtering: T1 = 64n, T_inf = 64 + log2(n)"));

        return profiles;
    }
}
