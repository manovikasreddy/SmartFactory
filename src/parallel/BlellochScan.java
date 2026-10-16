package parallel;

import java.util.Arrays;

/**
 * Blelloch Work-Efficient Parallel Prefix Sum (Scan).
 *
 * Industrial Context:
 * In smart manufacturing, real-time analytics require continuous cumulative metrics:
 *  - Running count of units produced across shifts.
 *  - Cumulative kilowatt-hours (kWh) consumed by production cells.
 *  - Integrated thermal and vibration exposure over machine operating lifespans.
 *
 * Algorithmic Features:
 *  - Work Complexity: O(n) total additions (optimal, matches sequential scan).
 *  - Span (Depth)   : O(log n) parallel steps.
 *  - Phase 1 (Up-Sweep / Reduce): Builds balanced binary sum tree.
 *  - Phase 2 (Down-Sweep / Distribute): Traverses from root down, distributing prefix sums.
 */
public class BlellochScan {

    public static class ScanResult {
        private final double[] original;
        private final double[] exclusiveScan;
        private final double[] inclusiveScan;
        private final int paddedLength;
        private final int upSweepSteps;
        private final int downSweepSteps;

        public ScanResult(double[] original, double[] exclusiveScan, double[] inclusiveScan,
                          int paddedLength, int upSweepSteps, int downSweepSteps) {
            this.original = original;
            this.exclusiveScan = exclusiveScan;
            this.inclusiveScan = inclusiveScan;
            this.paddedLength = paddedLength;
            this.upSweepSteps = upSweepSteps;
            this.downSweepSteps = downSweepSteps;
        }

        public double[] getOriginal() {
            return original;
        }

        public double[] getExclusiveScan() {
            return exclusiveScan;
        }

        public double[] getInclusiveScan() {
            return inclusiveScan;
        }

        public int getPaddedLength() {
            return paddedLength;
        }

        public int getUpSweepSteps() {
            return upSweepSteps;
        }

        public int getDownSweepSteps() {
            return downSweepSteps;
        }
    }

    /**
     * Executes Blelloch work-efficient parallel scan on the given array.
     */
    public static ScanResult scan(double[] input) {
        int n = input.length;
        if (n == 0) {
            return new ScanResult(new double[0], new double[0], new double[0], 0, 0, 0);
        }

        // Pad to next power of 2
        int m = nextPowerOfTwo(n);
        double[] tree = new double[m];
        System.arraycopy(input, 0, tree, 0, n);

        int logM = Integer.numberOfTrailingZeros(m);

        // ==========================================
        // Phase 1: Up-Sweep (Parallel Reduction)
        // ==========================================
        int upSteps = 0;
        for (int d = 0; d < logM; d++) {
            int step = 1 << (d + 1);
            int half = 1 << d;
            for (int k = 0; k < m; k += step) {
                tree[k + step - 1] += tree[k + half - 1];
            }
            upSteps++;
        }

        // Save total sum and set root to zero for exclusive scan
        double totalSum = tree[m - 1];
        tree[m - 1] = 0;

        // ==========================================
        // Phase 2: Down-Sweep (Distribution)
        // ==========================================
        int downSteps = 0;
        for (int d = logM - 1; d >= 0; d--) {
            int step = 1 << (d + 1);
            int half = 1 << d;
            for (int k = 0; k < m; k += step) {
                double leftChildVal = tree[k + half - 1];
                tree[k + half - 1] = tree[k + step - 1];
                tree[k + step - 1] += leftChildVal;
            }
            downSteps++;
        }

        // Extract exclusive and inclusive scans
        double[] exclusive = Arrays.copyOf(tree, n);
        double[] inclusive = new double[n];
        for (int i = 0; i < n; i++) {
            inclusive[i] = exclusive[i] + input[i];
        }

        return new ScanResult(input, exclusive, inclusive, m, upSteps, downSteps);
    }

    /**
     * Standard single-threaded sequential scan for verification.
     */
    public static double[] sequentialScan(double[] input) {
        double[] res = new double[input.length];
        double running = 0;
        for (int i = 0; i < input.length; i++) {
            running += input[i];
            res[i] = running;
        }
        return res;
    }

    private static int nextPowerOfTwo(int x) {
        if (x <= 1) return 1;
        return Integer.highestOneBit(x - 1) << 1;
    }
}
