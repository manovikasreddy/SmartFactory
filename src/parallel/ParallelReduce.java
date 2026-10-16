package parallel;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import model.FactoryRecord;

/**
 * Parallel Tree Reduction for Large-Scale Factory Data Aggregation.
 * Uses Java ForkJoin Framework to divide-and-conquer data aggregation
 * across multi-core CPU architectures.
 */
public class ParallelReduce {

    private static final int SEQUENTIAL_THRESHOLD = 500;
    private static final ForkJoinPool pool = ForkJoinPool.commonPool();

    public static class AggregateMetrics {
        public double totalEnergy;
        public double maxTemperature;
        public double maxVibration;
        public double totalRpm;
        public int faultCount;
        public int recordCount;

        public AggregateMetrics() {
            this.totalEnergy = 0;
            this.maxTemperature = Double.NEGATIVE_INFINITY;
            this.maxVibration = Double.NEGATIVE_INFINITY;
            this.totalRpm = 0;
            this.faultCount = 0;
            this.recordCount = 0;
        }

        public void merge(AggregateMetrics other) {
            this.totalEnergy += other.totalEnergy;
            this.maxTemperature = Math.max(this.maxTemperature, other.maxTemperature);
            this.maxVibration = Math.max(this.maxVibration, other.maxVibration);
            this.totalRpm += other.totalRpm;
            this.faultCount += other.faultCount;
            this.recordCount += other.recordCount;
        }

        public double getAverageRpm() {
            return recordCount == 0 ? 0 : totalRpm / recordCount;
        }
    }

    private static class ReduceTask extends RecursiveTask<AggregateMetrics> {
        private final List<FactoryRecord> records;
        private final int start;
        private final int end;

        public ReduceTask(List<FactoryRecord> records, int start, int end) {
            this.records = records;
            this.start = start;
            this.end = end;
        }

        @Override
        protected AggregateMetrics compute() {
            int length = end - start;
            if (length <= SEQUENTIAL_THRESHOLD) {
                return sequentialCompute(records, start, end);
            }

            int mid = start + length / 2;
            ReduceTask leftTask = new ReduceTask(records, start, mid);
            ReduceTask rightTask = new ReduceTask(records, mid, end);

            leftTask.fork();
            AggregateMetrics rightRes = rightTask.compute();
            AggregateMetrics leftRes = leftTask.join();

            leftRes.merge(rightRes);
            return leftRes;
        }

        private AggregateMetrics sequentialCompute(List<FactoryRecord> list, int s, int e) {
            AggregateMetrics m = new AggregateMetrics();
            for (int i = s; i < e; i++) {
                FactoryRecord r = list.get(i);
                m.totalEnergy += r.getPower(); // power / energy aggregation
                m.maxTemperature = Math.max(m.maxTemperature, r.getTemperature());
                m.maxVibration = Math.max(m.maxVibration, r.getVibration());
                m.totalRpm += r.getRpm();
                if (r.getFailureType() != null && !r.getFailureType().equalsIgnoreCase("No Fault")) {
                    m.faultCount++;
                }
                m.recordCount++;
            }
            return m;
        }
    }

    public static class ParallelBenchmarkResult {
        private final AggregateMetrics metrics;
        private final long parallelTimeNanos;
        private final long sequentialTimeNanos;
        private final double speedup;
        private final int availableProcessors;

        public ParallelBenchmarkResult(AggregateMetrics metrics, long parallelTimeNanos,
                                       long sequentialTimeNanos, int availableProcessors) {
            this.metrics = metrics;
            this.parallelTimeNanos = parallelTimeNanos;
            this.sequentialTimeNanos = sequentialTimeNanos;
            this.availableProcessors = availableProcessors;
            this.speedup = (parallelTimeNanos == 0) ? 1.0 : (double) sequentialTimeNanos / parallelTimeNanos;
        }

        public AggregateMetrics getMetrics() {
            return metrics;
        }

        public long getParallelTimeNanos() {
            return parallelTimeNanos;
        }

        public long getSequentialTimeNanos() {
            return sequentialTimeNanos;
        }

        public double getSpeedup() {
            return speedup;
        }

        public int getAvailableProcessors() {
            return availableProcessors;
        }
    }

    /**
     * Executes parallel reduction and compares performance against single-threaded execution.
     */
    public static ParallelBenchmarkResult aggregateWithBenchmark(List<FactoryRecord> records) {
        int n = records.size();

        // Warm up and sequential run
        long seqStart = System.nanoTime();
        AggregateMetrics seqResult = new AggregateMetrics();
        for (FactoryRecord r : records) {
            seqResult.totalEnergy += r.getPower();
            seqResult.maxTemperature = Math.max(seqResult.maxTemperature, r.getTemperature());
            seqResult.maxVibration = Math.max(seqResult.maxVibration, r.getVibration());
            seqResult.totalRpm += r.getRpm();
            if (r.getFailureType() != null && !r.getFailureType().equalsIgnoreCase("No Fault")) {
                seqResult.faultCount++;
            }
            seqResult.recordCount++;
        }
        long seqTime = System.nanoTime() - seqStart;

        // Parallel ForkJoin run
        long parStart = System.nanoTime();
        ReduceTask mainTask = new ReduceTask(records, 0, n);
        AggregateMetrics parResult = pool.invoke(mainTask);
        long parTime = System.nanoTime() - parStart;

        int cores = Runtime.getRuntime().availableProcessors();
        return new ParallelBenchmarkResult(parResult, parTime, seqTime, cores);
    }
}
