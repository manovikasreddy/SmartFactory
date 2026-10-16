package parallel;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Miller-Rabin Probabilistic Primality Test for Large Numerical Computations.
 *
 * Industrial Context:
 * In smart factories, industrial IoT gateways, cryptographically signed telemetry,
 * mutual machine-to-cloud TLS attestation, and distributed ledger production ledgers
 * require prime generation and verification of large numerical keys and tokens.
 *
 * Mathematical Basis:
 *  Write n - 1 = 2^s * d, where d is odd.
 *  For a random witness base a in [2, n - 2]:
 *   x = a^d mod n
 *   if x == 1 or x == n - 1 -> probably prime (passed this round)
 *   repeat s - 1 times:
 *     x = x^2 mod n
 *     if x == n - 1 -> probably prime (passed this round)
 *   if loop finishes without n - 1, n is composite.
 *
 *  With k independent rounds, the probability of false positive is at most 4^(-k).
 *  For k = 20, error probability < 10^(-12).
 */
public class MillerRabinPrimality {

    private static final SecureRandom random = new SecureRandom();

    public static class PrimalityResult {
        private final boolean isPrime;
        private final BigInteger candidate;
        private final int roundsTested;
        private final double maxErrorProbability;
        private final List<String> roundDetails;

        public PrimalityResult(boolean isPrime, BigInteger candidate, int roundsTested,
                               double maxErrorProbability, List<String> roundDetails) {
            this.isPrime = isPrime;
            this.candidate = candidate;
            this.roundsTested = roundsTested;
            this.maxErrorProbability = maxErrorProbability;
            this.roundDetails = roundDetails;
        }

        public boolean isPrime() {
            return isPrime;
        }

        public BigInteger getCandidate() {
            return candidate;
        }

        public int getRoundsTested() {
            return roundsTested;
        }

        public double getMaxErrorProbability() {
            return maxErrorProbability;
        }

        public List<String> getRoundDetails() {
            return roundDetails;
        }
    }

    /**
     * Tests primality of a 64-bit long integer.
     */
    public static boolean isPrime(long n, int rounds) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        if (n % 2 == 0) return false;

        return testBigInteger(BigInteger.valueOf(n), rounds).isPrime();
    }

    /**
     * Tests primality of an arbitrary-precision BigInteger with step-by-step telemetry.
     */
    public static PrimalityResult testBigInteger(BigInteger n, int kRounds) {
        List<String> details = new ArrayList<>();

        if (n.compareTo(BigInteger.ONE) <= 0) {
            return new PrimalityResult(false, n, 0, 0, List.of("Number <= 1 is not prime"));
        }
        if (n.equals(BigInteger.TWO) || n.equals(BigInteger.valueOf(3))) {
            return new PrimalityResult(true, n, 0, 0, List.of("Trivially prime (2 or 3)"));
        }
        if (!n.testBit(0)) {
            return new PrimalityResult(false, n, 0, 0, List.of("Even number > 2 is composite"));
        }

        // Factor n - 1 as 2^s * d
        BigInteger nMinusOne = n.subtract(BigInteger.ONE);
        BigInteger d = nMinusOne;
        int s = 0;
        while (!d.testBit(0)) {
            d = d.shiftRight(1);
            s++;
        }

        details.add(String.format("Decomposition: (n - 1) = 2^%d * %s", s, d.toString()));

        for (int r = 1; r <= kRounds; r++) {
            // Pick random witness a in [2, n - 2]
            BigInteger a;
            do {
                a = new BigInteger(n.bitLength(), random);
            } while (a.compareTo(BigInteger.TWO) < 0 || a.compareTo(nMinusOne) >= 0);

            // Compute x = a^d mod n
            BigInteger x = a.modPow(d, n);

            if (x.equals(BigInteger.ONE) || x.equals(nMinusOne)) {
                if (details.size() < 12) {
                    details.add(String.format("Round %2d: Witness a = %s... -> a^d mod n == %s (PASS)",
                            r, truncate(a.toString(), 15), x.equals(BigInteger.ONE) ? "1" : "n-1"));
                }
                continue;
            }

            boolean composite = true;
            for (int j = 1; j < s; j++) {
                x = x.modPow(BigInteger.TWO, n);
                if (x.equals(nMinusOne)) {
                    composite = false;
                    if (details.size() < 12) {
                        details.add(String.format("Round %2d: Witness a = %s... -> x^(2^%d) mod n == n - 1 (PASS)",
                                r, truncate(a.toString(), 15), j));
                    }
                    break;
                }
            }

            if (composite) {
                details.add(String.format("Round %2d: Witness a = %s proved n is COMPOSITE!",
                        r, truncate(a.toString(), 20)));
                return new PrimalityResult(false, n, r, 1.0, details);
            }
        }

        double maxError = Math.pow(4.0, -kRounds);
        return new PrimalityResult(true, n, kRounds, maxError, details);
    }

    private static String truncate(String s, int maxLen) {
        if (s.length() <= maxLen) return s;
        return s.substring(0, maxLen) + "...";
    }
}
