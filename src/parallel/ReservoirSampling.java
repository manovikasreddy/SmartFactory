package parallel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import model.FactoryRecord;

/**
 * Reservoir Sampling (Algorithm R) for Continuous Industrial Sensor Streams.
 *
 * Industrial Context:
 * Machines on factory floors generate high-frequency IoT sensor telemetry
 * (vibration, temperature, power factor, pressure) continuously 24/7.
 * The total number of records N is vast and potentially unbounded.
 * Reservoir Sampling allows maintaining an unbiased, uniform sample of size k
 * using O(k) memory and single-pass O(N) time.
 *
 * Mathematical Guarantee:
 * Every incoming stream element has an exact probability of k / N of being selected
 * into the final reservoir at any point in time.
 */
public class ReservoirSampling {

    private final int k;
    private final List<FactoryRecord> reservoir;
    private long totalStreamCount;
    private final Random random;

    public ReservoirSampling(int sampleSize) {
        if (sampleSize <= 0) {
            throw new IllegalArgumentException("Sample size k must be positive");
        }
        this.k = sampleSize;
        this.reservoir = new ArrayList<>(sampleSize);
        this.totalStreamCount = 0;
        this.random = new Random();
    }

    /**
     * Consumes one element from the continuous machine stream.
     */
    public void consume(FactoryRecord record) {
        totalStreamCount++;

        if (reservoir.size() < k) {
            // Step 1: Fill the reservoir initially
            reservoir.add(record);
        } else {
            // Step 2: For i-th element (1-based), pick random j in [0, i-1]
            long j = random.nextLong(totalStreamCount);
            if (j < k) {
                reservoir.set((int) j, record);
            }
        }
    }

    /**
     * Samples an entire collection or batch stream.
     */
    public static List<FactoryRecord> sampleFromStream(List<FactoryRecord> stream, int k) {
        ReservoirSampling sampler = new ReservoirSampling(k);
        for (FactoryRecord r : stream) {
            sampler.consume(r);
        }
        return sampler.getReservoir();
    }

    public List<FactoryRecord> getReservoir() {
        return Collections.unmodifiableList(reservoir);
    }

    public long getTotalStreamCount() {
        return totalStreamCount;
    }

    public int getSampleSize() {
        return k;
    }

    public static class StreamStatistics {
        public double avgTemperature;
        public double avgVibration;
        public double avgRpm;
        public double avgPower;
        public int faultCount;
        public int count;

        public static StreamStatistics calculate(List<FactoryRecord> records) {
            StreamStatistics s = new StreamStatistics();
            s.count = records.size();
            if (s.count == 0) return s;

            double tempSum = 0;
            double vibSum = 0;
            double rpmSum = 0;
            double powSum = 0;

            for (FactoryRecord r : records) {
                tempSum += r.getTemperature();
                vibSum += r.getVibration();
                rpmSum += r.getRpm();
                powSum += r.getPower();
                if (r.getFailureType() != null && !r.getFailureType().equalsIgnoreCase("No Fault")) {
                    s.faultCount++;
                }
            }

            s.avgTemperature = tempSum / s.count;
            s.avgVibration = vibSum / s.count;
            s.avgRpm = rpmSum / s.count;
            s.avgPower = powSum / s.count;
            return s;
        }
    }
}
