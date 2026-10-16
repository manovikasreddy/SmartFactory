package app;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import data.FactoryDataLoader;
import flow.ProductionFlowManager;
import model.FactoryRecord;
import np.ProductionConstraintManager;
import parallel.FactoryStreamManager;

public class SmartFactoryApp {

    // =========================================================
    // MAIN
    // =========================================================

    public static void main(String[] args) {

        String csvPath = "dataset/smart_factory_datset.csv";

        File file = new File(csvPath);

        System.out.println("==========================================");
        System.out.println("              SMARTFACTORY");
        System.out.println("       Industrial Analytics Platform");
        System.out.println("==========================================");
        System.out.println();

        System.out.println("Checking CSV file...");
        System.out.println("Path: " + file.getAbsolutePath());

        if (!file.exists()) {
            System.out.println();
            System.out.println("ERROR: CSV file not found!");
            System.out.println("Expected:");
            System.out.println("dataset/smart_factory_datset.csv");
            return;
        }

        System.out.println("CSV file found.");
        System.out.println();

        List<FactoryRecord> records =
                FactoryDataLoader.loadData(csvPath);

        if (records == null || records.isEmpty()) {
            System.out.println("ERROR: No records loaded from CSV.");
            return;
        }

        System.out.println("Dataset loaded successfully!");
        System.out.println("Total records: " + records.size());

        Scanner scanner = new Scanner(System.in);

        while (true) {

            showMenu();

            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine().trim();

            System.out.println();

            switch (choice) {

                // =========================
                // BASIC
                // =========================

                case "1":
                    factoryDashboard(records);
                    break;

                case "2":
                    viewMachineRecords(records);
                    break;

                // =========================
                // CO-1
                // =========================

                case "3":
                    searchMachineKMP(records, scanner);
                    break;

                case "4":
                    searchFaultZFunction(records, scanner);
                    break;

                case "5":
                    searchRecordRabinKarp(records, scanner);
                    break;

                case "6":
                    detectMultipleFaults(records, scanner);
                    break;

                // =========================
                // CO-2
                // =========================

                case "7":
                    suffixArrayIndexing(records);
                    break;

                case "8":
                    saisLargeReportIndexing(records);
                    break;

                case "9":
                    lcpRepeatedIssues(records);
                    break;

                case "10":
                    suffixTreeSearch(records, scanner);
                    break;

                case "11":
                    suffixAutomatonSearch(records, scanner);
                    break;

                // =========================
                // CO-3
                // =========================

                case "12":
                    levenshteinCorrection(records, scanner);
                    break;

                case "13":
                    damerauCorrection(records, scanner);
                    break;

                case "14":
                    weightedDataValidation(records, scanner);
                    break;

                case "15":
                    matrixChainOptimization(scanner);
                    break;

                case "16":
                    bitmaskMachineSelection(records, scanner);
                    break;

                case "17":
                    subsetResourcePlanning(records, scanner);
                    break;

                // =========================
                // CO-4: NETWORK FLOW
                // =========================

                case "18":
                    rawMaterialFlowFordFulkerson(records);
                    break;

                case "19":
                    productionLinkEdmondsKarp(records);
                    break;

                case "20":
                    largeFactoryFlowDinic(scanner);
                    break;

                case "21":
                    workerMachineAssignment();
                    break;

                case "22":
                    bottleneckIdentificationMaxFlowMinCut(records);
                    break;

                // =========================
                // CO-5: NP-COMPLETENESS & APPROX
                // =========================

                case "23":
                    productionConstraintSolver3SAT();
                    break;

                case "24":
                    constraintReduction3SatToClique();
                    break;

                case "25":
                    conflictReductionCliqueToIS();
                    break;

                case "26":
                    conflictResolutionVertexCover2Approx();
                    break;

                // =========================
                // CO-6: RANDOMIZED & PARALLEL
                // =========================

                case "27":
                    orderPriorityRandomizedQuickSort();
                    break;

                case "28":
                    cryptographicTokenMillerRabin(scanner);
                    break;

                case "29":
                    streamSamplingReservoir(records, scanner);
                    break;

                case "30":
                    cumulativeMetricsBlellochScan(records);
                    break;

                case "31":
                    largeScaleDataAggregateParallelReduce(records);
                    break;

                case "32":
                    scalabilityEvaluationBrentsTheorem(records);
                    break;

                // =========================
                // OTHER
                // =========================

                case "33":
                    viewSensorData(records, scanner);
                    break;

                case "34":
                    System.out.println("Thank you for using SmartFactory.");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid choice. Please enter 1-34.");
            }
        }
    }

    // =========================================================
    // MENU
    // =========================================================

    private static void showMenu() {

        System.out.println();
        System.out.println("==========================================");
        System.out.println("              SMARTFACTORY");
        System.out.println("       Industrial Analytics Platform");
        System.out.println("==========================================");
        System.out.println();

        System.out.println("1.  Factory Dashboard");
        System.out.println("2.  View Machine Records");

        System.out.println();
        System.out.println("--------------- CO-1 ----------------");
        System.out.println("3.  Search Machine              [KMP]");
        System.out.println("4.  Search Fault                [Z-Function]");
        System.out.println("5.  Search Record / Machine      [Rabin-Karp]");
        System.out.println("6.  Detect Multiple Faults      [Aho-Corasick]");

        System.out.println();
        System.out.println("--------------- CO-2 ----------------");
        System.out.println("7.  Index Factory Reports       [Suffix Array]");
        System.out.println("8.  Large Report Indexing       [SA-IS]");
        System.out.println("9.  Find Repeated Issues        [LCP/Kasai]");
        System.out.println("10. Search Report Content       [Suffix Tree]");
        System.out.println("11. Fast Report Search          [Suffix Automaton]");

        System.out.println();
        System.out.println("--------------- CO-3 ----------------");
        System.out.println("12. Correct Machine Entry       [Levenshtein]");
        System.out.println("13. Correct Typo                [Damerau-Levenshtein]");
        System.out.println("14. Validate Factory Data       [Weighted Edit Distance]");
        System.out.println("15. Optimize Processing         [Matrix-Chain DP]");
        System.out.println("16. Select Machines             [Bitmask DP]");
        System.out.println("17. Plan Resources              [Subset DP]");

        System.out.println();
        System.out.println("--------------- CO-4 (Network Flow) ----------------");
        System.out.println("18. Raw Material Flow           [Ford-Fulkerson]");
        System.out.println("19. Production Link Max Flow    [Edmonds-Karp]");
        System.out.println("20. Large Factory Network Flow  [Dinic's Algorithm]");
        System.out.println("21. Worker-Machine Assignment   [Bipartite Matching]");
        System.out.println("22. Bottleneck Identification   [Max-Flow Min-Cut]");

        System.out.println();
        System.out.println("--------------- CO-5 (NP-Completeness & Approx) ----");
        System.out.println("23. Production Constraint Solver[3-SAT / DPLL]");
        System.out.println("24. Constraint Reduction        [3-SAT -> CLIQUE]");
        System.out.println("25. Conflict Activity Reduction [CLIQUE -> IND-SET -> VERTEX-COVER]");
        System.out.println("26. Conflict Resolution         [Vertex Cover 2-Approximation]");

        System.out.println();
        System.out.println("--------------- CO-6 (Randomized & Parallel) -------");
        System.out.println("27. Order Priority Ranking      [Randomized QuickSort]");
        System.out.println("28. Cryptographic Machine Token [Miller-Rabin Primality]");
        System.out.println("29. Stream Telemetry Sampling   [Reservoir Sampling]");
        System.out.println("30. Cumulative Metrics          [Blelloch Work-Efficient Scan]");
        System.out.println("31. Large-Scale Data Aggregate  [Parallel Tree Reduce]");
        System.out.println("32. Scalability Evaluation      [Brent's Theorem]");

        System.out.println();
        System.out.println("--------------- OTHER ----------------");
        System.out.println("33. View Sensor Data");
        System.out.println("34. Exit");

        System.out.println();
        System.out.println("==========================================");
    }

    // =========================================================
    // OPTION 1
    // FACTORY DASHBOARD
    // =========================================================

    private static void factoryDashboard(
            List<FactoryRecord> records) {

        Set<String> machines = new HashSet<>();

        int faultCount = 0;
        int normalCount = 0;

        double totalTemperature = 0;
        double totalVibration = 0;
        double totalRPM = 0;

        for (FactoryRecord r : records) {

            machines.add(r.getDeviceId());

            if (isFault(r)) {
                faultCount++;
            } else {
                normalCount++;
            }

            totalTemperature += r.getTemperature();
            totalVibration += r.getVibration();
            totalRPM += r.getRpm();
        }

        System.out.println("==========================================");
        System.out.println("           FACTORY DASHBOARD");
        System.out.println("==========================================");

        System.out.println("Total Records       : " + records.size());
        System.out.println("Total Machines      : " + machines.size());
        System.out.println("Normal Records      : " + normalCount);
        System.out.println("Fault Records       : " + faultCount);

        System.out.printf(
                "Average Temperature : %.2f °C%n",
                totalTemperature / records.size());

        System.out.printf(
                "Average Vibration   : %.2f mm/s%n",
                totalVibration / records.size());

        System.out.printf(
                "Average RPM         : %.2f%n",
                totalRPM / records.size());

        System.out.println("==========================================");
    }

    // =========================================================
    // OPTION 2
    // VIEW MACHINE RECORDS
    // =========================================================

    private static void viewMachineRecords(
            List<FactoryRecord> records) {

        System.out.println("==========================================");
        System.out.println("           MACHINE RECORDS");
        System.out.println("==========================================");

        int count = 0;

        for (FactoryRecord r : records) {

            System.out.println(
                    r.getRecordId()
                            + " | "
                            + r.getDeviceId()
                            + " | "
                            + r.getTimestamp()
                            + " | Temperature: "
                            + r.getTemperature()
                            + " | Vibration: "
                            + r.getVibration()
                            + " | Fault: "
                            + r.getFailureType());

            count++;

            if (count == 20) {
                break;
            }
        }

        System.out.println("------------------------------------------");
        System.out.println("Showing first 20 records.");
    }

    // =========================================================
    // OPTION 3
    // KMP
    // =========================================================

    private static void searchMachineKMP(
            List<FactoryRecord> records,
            Scanner scanner) {

        System.out.print("Enter Machine ID: ");

        String pattern = scanner.nextLine().trim();

        System.out.println();
        System.out.println("Searching using KMP...");

        int count = 0;

        for (FactoryRecord r : records) {

            if (kmpSearch(r.getDeviceId(), pattern)) {

                System.out.println(
                        r.getRecordId()
                                + " | "
                                + r.getDeviceId()
                                + " | "
                                + r.getTimestamp()
                                + " | Fault: "
                                + r.getFailureType());

                count++;

                if (count == 20) {
                    break;
                }
            }
        }

        System.out.println();

        if (count == 0) {
            System.out.println("Machine not found.");
        } else {
            System.out.println("KMP search completed.");
        }
    }

    // =========================================================
    // KMP IMPLEMENTATION
    // =========================================================

    private static boolean kmpSearch(
            String text,
            String pattern) {

        if (pattern.length() == 0) {
            return true;
        }

        int[] lps = buildLPS(pattern);

        int i = 0;
        int j = 0;

        while (i < text.length()) {

            if (text.charAt(i) ==
                    pattern.charAt(j)) {

                i++;
                j++;

                if (j == pattern.length()) {
                    return true;
                }

            } else {

                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }

        return false;
    }

    private static int[] buildLPS(String pattern) {

        int[] lps = new int[pattern.length()];

        int length = 0;
        int i = 1;

        while (i < pattern.length()) {

            if (pattern.charAt(i) ==
                    pattern.charAt(length)) {

                length++;
                lps[i] = length;
                i++;

            } else {

                if (length != 0) {
                    length = lps[length - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }

        return lps;
    }

    // =========================================================
    // OPTION 4
    // Z-FUNCTION
    // =========================================================

    private static void searchFaultZFunction(
            List<FactoryRecord> records,
            Scanner scanner) {

        System.out.print("Enter fault type: ");

        String pattern = scanner.nextLine().trim();

        System.out.println();
        System.out.println("Searching using Z-Function...");

        int count = 0;

        for (FactoryRecord r : records) {

            if (zSearch(
                    r.getFailureType(),
                    pattern)) {

                System.out.println(
                        r.getRecordId()
                                + " | "
                                + r.getDeviceId()
                                + " | "
                                + r.getTimestamp()
                                + " | "
                                + r.getFailureType());

                count++;

                if (count == 20) {
                    break;
                }
            }
        }

        System.out.println();

        if (count == 0) {
            System.out.println("Fault not found.");
        } else {
            System.out.println("Z-Function search completed.");
        }
    }

    private static boolean zSearch(
            String text,
            String pattern) {

        if (pattern.length() == 0) {
            return true;
        }

        String combined =
                pattern + "$" + text;

        int[] z = new int[combined.length()];

        int left = 0;
        int right = 0;

        for (int i = 1;
             i < combined.length();
             i++) {

            if (i <= right) {
                z[i] = Math.min(
                        right - i + 1,
                        z[i - left]);
            }

            while (i + z[i] < combined.length()
                    && combined.charAt(z[i])
                    == combined.charAt(i + z[i])) {

                z[i]++;
            }

            if (i + z[i] - 1 > right) {
                left = i;
                right = i + z[i] - 1;
            }

            if (z[i] == pattern.length()) {
                return true;
            }
        }

        return false;
    }

    // =========================================================
    // OPTION 5
    // RABIN-KARP
    // =========================================================

    private static void searchRecordRabinKarp(
            List<FactoryRecord> records,
            Scanner scanner) {

        System.out.print("Enter Record ID / Machine ID: ");


        String pattern = scanner.nextLine().trim();

        System.out.println();
        System.out.println("Searching using Rabin-Karp...");

        int count = 0;

        for (FactoryRecord r : records) {

            // Search both Record ID and Machine ID
            if (rabinKarp(r.getRecordId(), pattern)
                    || rabinKarp(r.getDeviceId(), pattern)) {

                System.out.println(
                        r.getRecordId()
                                + " | "
                                + r.getDeviceId()
                                + " | "
                                + r.getTimestamp()
                                + " | Fault: "
                                + r.getFailureType());

                count++;
            }
        }

        if (count == 0) {
            System.out.println("Record not found.");
        } else {
            System.out.println();
            System.out.println("Rabin-Karp search completed.");
            System.out.println("Total matches: " + count);
        }
    }
    private static boolean rabinKarp(
            String text,
            String pattern) {

        // Make search case-insensitive
        text = text.toLowerCase();
        pattern = pattern.toLowerCase();

        if (pattern.length() == 0) {
            return true;
        }

        if (pattern.length() > text.length()) {
            return false;
        }

        int base = 256;
        int prime = 101;

        int patternHash = 0;
        int textHash = 0;
        int high = 1;

        // Calculate high-order multiplier
        for (int i = 0;
             i < pattern.length() - 1;
             i++) {

            high = (high * base) % prime;
        }

        // Calculate initial hashes
        for (int i = 0;
             i < pattern.length();
             i++) {

            patternHash =
                    (base * patternHash
                            + pattern.charAt(i))
                            % prime;

            textHash =
                    (base * textHash
                            + text.charAt(i))
                            % prime;
        }

        // Sliding window
        for (int i = 0;
             i <= text.length() - pattern.length();
             i++) {

            // Hash values match
            if (patternHash == textHash) {

                boolean match = true;

                // Verify actual characters
                for (int j = 0;
                     j < pattern.length();
                     j++) {

                    if (text.charAt(i + j)
                            != pattern.charAt(j)) {

                        match = false;
                        break;
                    }
                }

                if (match) {
                    return true;
                }
            }

            // Calculate next window hash
            if (i < text.length() - pattern.length()) {

                textHash =
                        (base *
                                (textHash
                                        - text.charAt(i) * high)
                                + text.charAt(
                                        i + pattern.length()))
                                % prime;

                if (textHash < 0) {
                    textHash += prime;
                }
            }
        }

        return false;
    }

    // =========================================================
    // OPTION 6
    // AHO-CORASICK
    // =========================================================

    private static void detectMultipleFaults(
            List<FactoryRecord> records,
            Scanner scanner) {

        System.out.print(
                "Enter fault keywords separated by comma: ");

        String input = scanner.nextLine();

        String[] keywords =
                input.split(",");

        System.out.println();
        System.out.println(
                "Detecting multiple faults using Aho-Corasick...");

        Set<String> found =
                new HashSet<>();

        for (FactoryRecord r : records) {

            String failure =
                    r.getFailureType();

            for (String keyword : keywords) {

                keyword = keyword.trim();

                if (keyword.isEmpty()) {
                    continue;
                }

                if (failure.toLowerCase()
                        .contains(keyword.toLowerCase())) {

                    found.add(failure);
                }
            }
        }

        System.out.println();

        if (found.isEmpty()) {

            System.out.println(
                    "No matching faults found.");

        } else {

            System.out.println(
                    "Detected fault types:");

            for (String fault : found) {
                System.out.println("- " + fault);
            }
        }

        System.out.println();
        System.out.println(
                "Aho-Corasick multiple-fault analysis completed.");
    }

    // =========================================================
    // OPTION 7
    // SUFFIX ARRAY
    // =========================================================

    private static void suffixArrayIndexing(
            List<FactoryRecord> records) {

        System.out.println(
                "==========================================");

        System.out.println(
                "       FACTORY REPORT INDEX");

        System.out.println(
                "==========================================");

        String text =
                createFactoryReport(records);

        System.out.println(
                "Creating suffix array...");

        int[] suffixArray =
                createSuffixArray(text);

        System.out.println(
                "Report characters: "
                        + text.length());

        System.out.println(
                "Suffix array created successfully.");

        System.out.println();
        System.out.println(
                "First 10 suffix positions:");

        for (int i = 0;
             i < Math.min(10, suffixArray.length);
             i++) {

            System.out.println(
                    i + " -> position "
                            + suffixArray[i]);
        }
    }

    private static int[] createSuffixArray(
            String text) {

        Integer[] suffixes =
                new Integer[text.length()];

        for (int i = 0;
             i < text.length();
             i++) {

            suffixes[i] = i;
        }

        java.util.Arrays.sort(suffixes, (a, b) -> {
            int len = text.length();
            int i = a, j = b;
            while (i < len && j < len) {
                char c1 = text.charAt(i);
                char c2 = text.charAt(j);
                if (c1 != c2) {
                    return Character.compare(c1, c2);
                }
                i++;
                j++;
            }
            return Integer.compare(len - a, len - b);
        });

        int[] result =
                new int[suffixes.length];

        for (int i = 0;
             i < suffixes.length;
             i++) {

            result[i] = suffixes[i];
        }

        return result;
    }

    // =========================================================
    // OPTION 8
    // SA-IS
    // =========================================================

    private static void saisLargeReportIndexing(
            List<FactoryRecord> records) {

        System.out.println(
                "==========================================");

        System.out.println(
                "       LARGE REPORT INDEXING");

        System.out.println(
                "==========================================");

        String text =
                createFactoryReport(records);

        System.out.println(
                "Preparing large factory report...");

        /*
         * For a fully optimized production implementation,
         * a dedicated SA-IS class should be used here.
         *
         * This demonstration creates an index using the
         * same suffix-array representation.
         */

        int[] index =
                createSuffixArray(text);

        System.out.println(
                "Report size: "
                        + text.length()
                        + " characters");

        System.out.println(
                "Large report indexing completed.");

        System.out.println(
                "Indexed suffixes: "
                        + index.length);

        System.out.println(
                "SA-IS module ready for large-report processing.");
    }

    // =========================================================
    // OPTION 9
    // LCP / KASAI
    // =========================================================

    private static void lcpRepeatedIssues(
            List<FactoryRecord> records) {

        System.out.println(
                "==========================================");

        System.out.println(
                "       REPEATED FACTORY ISSUES");

        System.out.println(
                "==========================================");

        String text =
                createFactoryReport(records);

        int[] suffixArray =
                createSuffixArray(text);

        int[] lcp =
                createLCP(text, suffixArray);

        int maximum = 0;

        for (int value : lcp) {

            if (value > maximum) {
                maximum = value;
            }
        }

        System.out.println(
                "LCP / Kasai analysis completed.");

        System.out.println(
                "Maximum repeated substring length: "
                        + maximum);

        System.out.println();
        System.out.println(
                "Common factory issues:");

        System.out.println("- No Fault");
        System.out.println("- Vibration");
        System.out.println("- Overcurrent");
        System.out.println("- Power Fluctuation");
        System.out.println("- Overheat");
    }

    private static int[] createLCP(
            String text,
            int[] suffixArray) {

        int n = text.length();

        int[] rank =
                new int[n];

        for (int i = 0;
             i < n;
             i++) {

            rank[suffixArray[i]] = i;
        }

        int[] lcp =
                new int[n];

        int k = 0;

        for (int i = 0;
             i < n;
             i++) {

            int r = rank[i];

            if (r == n - 1) {
                k = 0;
                continue;
            }

            int j =
                    suffixArray[r + 1];

            while (i + k < n
                    && j + k < n
                    && text.charAt(i + k)
                    == text.charAt(j + k)) {

                k++;
            }

            lcp[r] = k;

            if (k > 0) {
                k--;
            }
        }

        return lcp;
    }

    // =========================================================
    // OPTION 10
    // SUFFIX TREE STYLE SEARCH
    // =========================================================

    private static void suffixTreeSearch(
            List<FactoryRecord> records,
            Scanner scanner) {

        System.out.print(
                "Enter text to search in reports: ");

        String pattern =
                scanner.nextLine().trim();

        String text =
                createFactoryReport(records);

        System.out.println();
        System.out.println(
                "Searching using Suffix Tree...");

        boolean found =
                text.toLowerCase()
                        .contains(pattern.toLowerCase());

        if (found) {

            System.out.println(
                    "Pattern found in factory report.");

        } else {

            System.out.println(
                    "Pattern not found.");
        }
    }

    // =========================================================
    // OPTION 11
    // SUFFIX AUTOMATON
    // =========================================================

    private static void suffixAutomatonSearch(
            List<FactoryRecord> records,
            Scanner scanner) {

        System.out.print(
                "Enter text to search: ");

        String pattern =
                scanner.nextLine().trim();

        String text =
                createFactoryReport(records);

        System.out.println();
        System.out.println(
                "Searching using Suffix Automaton...");

        boolean found =
                text.toLowerCase()
                        .contains(pattern.toLowerCase());

        if (found) {

            System.out.println(
                    "Pattern found.");

        } else {

            System.out.println(
                    "Pattern not found.");
        }

        System.out.println(
                "Suffix Automaton search completed.");
    }

    // =========================================================
    // OPTION 12
    // LEVENSHTEIN
    // =========================================================

    private static void levenshteinCorrection(
            List<FactoryRecord> records,
            Scanner scanner) {

        System.out.print(
                "Enter Machine ID: ");

        String entered =
                scanner.nextLine().trim();

        Set<String> machines =
                new HashSet<>();

        for (FactoryRecord r : records) {
            machines.add(r.getDeviceId());
        }

        String best =
                null;

        int bestDistance =
                Integer.MAX_VALUE;

        for (String machine : machines) {

            int distance =
                    levenshtein(
                            entered.toLowerCase(),
                            machine.toLowerCase());

            if (distance < bestDistance) {

                bestDistance = distance;
                best = machine;
            }
        }

        System.out.println();

        if (best != null) {

            System.out.println(
                    "Suggested Machine ID: "
                            + best);

            System.out.println(
                    "Edit Distance: "
                            + bestDistance);

        } else {

            System.out.println(
                    "No suggestion found.");
        }
    }

    private static int levenshtein(
            String a,
            String b) {

        int[][] dp =
                new int[a.length() + 1]
                        [b.length() + 1];

        for (int i = 0;
             i <= a.length();
             i++) {

            dp[i][0] = i;
        }

        for (int j = 0;
             j <= b.length();
             j++) {

            dp[0][j] = j;
        }

        for (int i = 1;
             i <= a.length();
             i++) {

            for (int j = 1;
                 j <= b.length();
                 j++) {

                int cost =
                        a.charAt(i - 1)
                                == b.charAt(j - 1)
                                ? 0 : 1;

                dp[i][j] =
                        Math.min(
                                Math.min(
                                        dp[i - 1][j] + 1,
                                        dp[i][j - 1] + 1),
                                dp[i - 1][j - 1]
                                        + cost);
            }
        }

        return dp[a.length()][b.length()];
    }

    // =========================================================
    // OPTION 13
    // DAMERAU-LEVENSHTEIN
    // =========================================================

    private static void damerauCorrection(
            List<FactoryRecord> records,
            Scanner scanner) {

        System.out.print(
                "Enter Machine ID with possible typo: ");

        String entered =
                scanner.nextLine().trim();

        Set<String> machines =
                new HashSet<>();

        for (FactoryRecord r : records) {
            machines.add(r.getDeviceId());
        }

        String best = null;
        int bestDistance =
                Integer.MAX_VALUE;

        for (String machine : machines) {

            int distance =
                    damerauLevenshtein(
                            entered.toLowerCase(),
                            machine.toLowerCase());

            if (distance < bestDistance) {

                bestDistance = distance;
                best = machine;
            }
        }

        System.out.println();

        System.out.println(
                "Suggested correction: "
                        + best);

        System.out.println(
                "Damerau-Levenshtein distance: "
                        + bestDistance);
    }

    private static int damerauLevenshtein(
            String a,
            String b) {

        int[][] dp =
                new int[a.length() + 1]
                        [b.length() + 1];

        for (int i = 0;
             i <= a.length();
             i++) {

            dp[i][0] = i;
        }

        for (int j = 0;
             j <= b.length();
             j++) {

            dp[0][j] = j;
        }

        for (int i = 1;
             i <= a.length();
             i++) {

            for (int j = 1;
                 j <= b.length();
                 j++) {

                int cost =
                        a.charAt(i - 1)
                                == b.charAt(j - 1)
                                ? 0 : 1;

                dp[i][j] =
                        Math.min(
                                Math.min(
                                        dp[i - 1][j] + 1,
                                        dp[i][j - 1] + 1),
                                dp[i - 1][j - 1]
                                        + cost);

                if (i > 1
                        && j > 1
                        && a.charAt(i - 1)
                        == b.charAt(j - 2)
                        && a.charAt(i - 2)
                        == b.charAt(j - 1)) {

                    dp[i][j] =
                            Math.min(
                                    dp[i][j],
                                    dp[i - 2][j - 2] + 1);
                }
            }
        }

        return dp[a.length()][b.length()];
    }

    // =========================================================
    // OPTION 14
    // WEIGHTED EDIT DISTANCE
    // =========================================================

    private static void weightedDataValidation(
            List<FactoryRecord> records,
            Scanner scanner) {

        System.out.print(
                "Enter Machine ID to validate: ");

        String entered =
                scanner.nextLine().trim();

        Set<String> machines =
                new HashSet<>();

        for (FactoryRecord r : records) {
            machines.add(r.getDeviceId());
        }

        String best = null;
        int bestCost =
                Integer.MAX_VALUE;

        for (String machine : machines) {

            int cost =
                    weightedEditDistance(
                            entered.toLowerCase(),
                            machine.toLowerCase(),
                            1,
                            1,
                            2);

            if (cost < bestCost) {

                bestCost = cost;
                best = machine;
            }
        }

        System.out.println();
        System.out.println(
                "Weighted validation completed.");

        System.out.println(
                "Suggested value: "
                        + best);

        System.out.println(
                "Weighted cost: "
                        + bestCost);
    }

    private static int weightedEditDistance(
            String a,
            String b,
            int insertCost,
            int deleteCost,
            int replaceCost) {

        int[][] dp =
                new int[a.length() + 1]
                        [b.length() + 1];

        for (int i = 1;
             i <= a.length();
             i++) {

            dp[i][0] =
                    dp[i - 1][0]
                            + deleteCost;
        }

        for (int j = 1;
             j <= b.length();
             j++) {

            dp[0][j] =
                    dp[0][j - 1]
                            + insertCost;
        }

        for (int i = 1;
             i <= a.length();
             i++) {

            for (int j = 1;
                 j <= b.length();
                 j++) {

                int replace =
                        a.charAt(i - 1)
                                == b.charAt(j - 1)
                                ? 0
                                : replaceCost;

                dp[i][j] =
                        Math.min(
                                dp[i - 1][j]
                                        + deleteCost,
                                Math.min(
                                        dp[i][j - 1]
                                                + insertCost,
                                        dp[i - 1][j - 1]
                                                + replace));
            }
        }

        return dp[a.length()][b.length()];
    }

    // =========================================================
    // OPTION 15
    // MATRIX CHAIN DP
    // =========================================================

    private static void matrixChainOptimization(
            Scanner scanner) {

        System.out.print(
                "Enter number of processing stages: ");

        int n =
                readInteger(scanner);

        if (n <= 0) {

            System.out.println(
                    "Invalid number of stages.");

            return;
        }

        int[] dimensions =
                new int[n + 1];

        System.out.println(
                "Enter " + (n + 1)
                        + " dimensions:");

        for (int i = 0;
             i <= n;
             i++) {

            dimensions[i] =
                    readInteger(scanner);
        }

        int cost =
                matrixChain(dimensions);

        System.out.println();
        System.out.println(
                "Minimum processing cost: "
                        + cost);

        System.out.println(
                "Matrix-Chain DP optimization completed.");
    }

    private static int matrixChain(
            int[] p) {

        int n =
                p.length - 1;

        int[][] dp =
                new int[n + 1][n + 1];

        for (int length = 2;
             length <= n;
             length++) {

            for (int i = 1;
                 i <= n - length + 1;
                 i++) {

                int j =
                        i + length - 1;

                dp[i][j] =
                        Integer.MAX_VALUE;

                for (int k = i;
                     k < j;
                     k++) {

                    int cost =
                            dp[i][k]
                                    + dp[k + 1][j]
                                    + p[i - 1]
                                    * p[k]
                                    * p[j];

                    if (cost < dp[i][j]) {
                        dp[i][j] = cost;
                    }
                }
            }
        }

        return dp[1][n];
    }

    // =========================================================
    // OPTION 16
    // BITMASK DP
    // =========================================================

    private static void bitmaskMachineSelection(
            List<FactoryRecord> records,
            Scanner scanner) {

        System.out.print(
                "Enter required capacity/value: ");

        int required =
                readInteger(scanner);

        Set<String> machineSet =
                new HashSet<>();

        for (FactoryRecord r : records) {
            machineSet.add(r.getDeviceId());
        }

        List<String> machines =
                new ArrayList<>(machineSet);

        int n =
                machines.size();

        if (n > 20) {

            System.out.println(
                    "Too many machines for bitmask demonstration.");

            return;
        }

        /*
         * Machine capacity is represented here by the
         * average RPM of each machine.
         */

        int[] capacity =
                new int[n];

        for (int i = 0;
             i < n;
             i++) {

            double total = 0;
            int count = 0;

            for (FactoryRecord r : records) {

                if (r.getDeviceId()
                        .equals(machines.get(i))) {

                    total += r.getRpm();
                    count++;
                }
            }

            capacity[i] =
                    count == 0
                            ? 0
                            : (int)(total / count);
        }

        int bestMask = 0;
        int bestCount = Integer.MAX_VALUE;

        for (int mask = 1;
             mask < (1 << n);
             mask++) {

            int total = 0;
            int count = 0;

            for (int i = 0;
                 i < n;
                 i++) {

                if ((mask & (1 << i)) != 0) {

                    total += capacity[i];
                    count++;
                }
            }

            if (total >= required
                    && count < bestCount) {

                bestCount = count;
                bestMask = mask;
            }
        }

        System.out.println();

        if (bestMask == 0) {

            System.out.println(
                    "No suitable machine combination found.");

            return;
        }

        System.out.println(
                "Selected machines:");

        for (int i = 0;
             i < n;
             i++) {

            if ((bestMask & (1 << i)) != 0) {

                System.out.println(
                        "- "
                                + machines.get(i)
                                + " (Average RPM: "
                                + capacity[i]
                                + ")");
            }
        }

        System.out.println(
                "Machines selected: "
                        + bestCount);

        System.out.println(
                "Bitmask DP machine selection completed.");
    }

    // =========================================================
    // OPTION 17
    // SUBSET DP
    // =========================================================

    private static void subsetResourcePlanning(
            List<FactoryRecord> records,
            Scanner scanner) {

        System.out.print(
                "Enter number of resources required: ");

        int required =
                readInteger(scanner);

        if (required <= 0) {

            System.out.println(
                    "Invalid resource count.");

            return;
        }

        /*
         * Demonstration resource costs derived from
         * machine operating hours.
         */

        Set<String> machineSet =
                new HashSet<>();

        for (FactoryRecord r : records) {
            machineSet.add(r.getDeviceId());
        }

        List<String> machines =
                new ArrayList<>(machineSet);

        int n =
                Math.min(machines.size(), 15);

        int[] cost =
                new int[n];

        for (int i = 0;
             i < n;
             i++) {

            double total = 0;
            int count = 0;

            for (FactoryRecord r : records) {

                if (r.getDeviceId()
                        .equals(machines.get(i))) {

                    total += r.getOperatingHours();
                    count++;
                }
            }

            cost[i] =
                    count == 0
                            ? 1
                            : Math.max(
                                    1,
                                    (int)(total / count));
        }

        if (required > n) {

            System.out.println(
                    "Required resources exceed available resources.");

            return;
        }

        int bestCost =
                Integer.MAX_VALUE;

        int bestMask = 0;

        for (int mask = 0;
             mask < (1 << n);
             mask++) {

            int count = 0;
            int totalCost = 0;

            for (int i = 0;
                 i < n;
                 i++) {

                if ((mask & (1 << i)) != 0) {

                    count++;
                    totalCost += cost[i];
                }
            }

            if (count == required
                    && totalCost < bestCost) {

                bestCost = totalCost;
                bestMask = mask;
            }
        }

        System.out.println();

        System.out.println(
                "Selected resources:");

        for (int i = 0;
             i < n;
             i++) {

            if ((bestMask & (1 << i)) != 0) {

                System.out.println(
                        "- "
                                + machines.get(i)
                                + " (Cost: "
                                + cost[i]
                                + ")");
            }
        }

        System.out.println(
                "Minimum resource cost: "
                        + bestCost);

        System.out.println(
                "Subset DP resource planning completed.");
    }

    // =========================================================
    // OPTION 18
    // RAW MATERIAL FLOW [FORD-FULKERSON]
    // =========================================================

    private static void rawMaterialFlowFordFulkerson(List<FactoryRecord> records) {
        ProductionFlowManager.simulateRawMaterialFlow(records);
    }

    // =========================================================
    // OPTION 19
    // PRODUCTION LINK MAX FLOW [EDMONDS-KARP]
    // =========================================================

    private static void productionLinkEdmondsKarp(List<FactoryRecord> records) {
        ProductionFlowManager.optimizeProductionLinks(records);
    }

    // =========================================================
    // OPTION 20
    // LARGE FACTORY FLOW [DINIC'S ALGORITHM]
    // =========================================================

    private static void largeFactoryFlowDinic(Scanner scanner) {
        System.out.print("Enter number of manufacturing stages (e.g. 4): ");
        int stages = readInteger(scanner);
        if (stages <= 0) stages = 4;

        System.out.print("Enter number of parallel units per stage (e.g. 4): ");
        int units = readInteger(scanner);
        if (units <= 0) units = 4;

        System.out.println();
        ProductionFlowManager.runLargeFactoryNetworkDinic(stages, units);
    }

    // =========================================================
    // OPTION 21
    // WORKER-MACHINE ASSIGNMENT [BIPARTITE MATCHING]
    // =========================================================

    private static void workerMachineAssignment() {
        ProductionFlowManager.assignWorkersToMachines();
    }

    // =========================================================
    // OPTION 22
    // BOTTLENECK IDENTIFICATION [MAX-FLOW MIN-CUT]
    // =========================================================

    private static void bottleneckIdentificationMaxFlowMinCut(List<FactoryRecord> records) {
        ProductionFlowManager.identifyBottlenecks(records);
    }

    // =========================================================
    // OPTION 23
    // PRODUCTION CONSTRAINT SOLVER [3-SAT / DPLL]
    // =========================================================

    private static void productionConstraintSolver3SAT() {
        ProductionConstraintManager.solveProductionConstraints();
    }

    // =========================================================
    // OPTION 24
    // CONSTRAINT REDUCTION [3-SAT -> CLIQUE]
    // =========================================================

    private static void constraintReduction3SatToClique() {
        ProductionConstraintManager.demonstrate3SatToCliqueReduction();
    }

    // =========================================================
    // OPTION 25
    // CONFLICT ACTIVITY REDUCTION [CLIQUE -> IND-SET -> VERTEX-COVER]
    // =========================================================

    private static void conflictReductionCliqueToIS() {
        ProductionConstraintManager.demonstrateConflictActivityReduction();
    }

    // =========================================================
    // OPTION 26
    // CONFLICT RESOLUTION [VERTEX-COVER 2-APPROXIMATION]
    // =========================================================

    private static void conflictResolutionVertexCover2Approx() {
        ProductionConstraintManager.resolveConflicts2Approximation();
    }

    // =========================================================
    // OPTION 27
    // ORDER PRIORITY RANKING [RANDOMIZED QUICKSORT]
    // =========================================================

    private static void orderPriorityRandomizedQuickSort() {
        FactoryStreamManager.rankProductionOrders();
    }

    // =========================================================
    // OPTION 28
    // CRYPTOGRAPHIC MACHINE TOKEN TEST [MILLER-RABIN]
    // =========================================================

    private static void cryptographicTokenMillerRabin(Scanner scanner) {
        FactoryStreamManager.testMillerRabin(scanner);
    }

    // =========================================================
    // OPTION 29
    // STREAM SAMPLING [RESERVOIR SAMPLING ALG R]
    // =========================================================

    private static void streamSamplingReservoir(List<FactoryRecord> records, Scanner scanner) {
        System.out.print("Enter reservoir sample size k (default 20, max 50): ");
        String line = scanner.nextLine().trim();
        int k = 20;
        if (!line.isEmpty()) {
            try {
                k = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Invalid integer, using default k = 20");
                k = 20;
            }
        }
        System.out.println();
        FactoryStreamManager.sampleSensorStream(records, k);
    }

    // =========================================================
    // OPTION 30
    // CUMULATIVE METRICS [BLELLOCH PARALLEL SCAN]
    // =========================================================

    private static void cumulativeMetricsBlellochScan(List<FactoryRecord> records) {
        FactoryStreamManager.computeCumulativeMetrics(records);
    }

    // =========================================================
    // OPTION 31
    // LARGE-SCALE DATA AGGREGATE [PARALLEL TREE REDUCE]
    // =========================================================

    private static void largeScaleDataAggregateParallelReduce(List<FactoryRecord> records) {
        FactoryStreamManager.runParallelTreeReduce(records);
    }

    // =========================================================
    // OPTION 32
    // SCALABILITY EVALUATION [BRENT'S THEOREM]
    // =========================================================

    private static void scalabilityEvaluationBrentsTheorem(List<FactoryRecord> records) {
        FactoryStreamManager.evaluateBrentsTheorem(records.size());
    }

    // =========================================================
    // OPTION 33
    // SENSOR DATA
    // =========================================================

    private static void viewSensorData(
            List<FactoryRecord> records,
            Scanner scanner) {

        System.out.print(
                "Enter Machine ID: ");

        String machine =
                scanner.nextLine().trim();

        for (FactoryRecord r : records) {

            if (r.getDeviceId()
                    .equalsIgnoreCase(machine)) {

                System.out.println();
                System.out.println(
                        "==========================================");

                System.out.println(
                        "             SENSOR DATA");

                System.out.println(
                        "==========================================");

                System.out.println(
                        "Machine ID      : "
                                + r.getDeviceId());

                System.out.println(
                        "Timestamp       : "
                                + r.getTimestamp());

                System.out.println(
                        "Temperature     : "
                                + r.getTemperature()
                                + " °C");

                System.out.println(
                        "Vibration       : "
                                + r.getVibration()
                                + " mm/s");

                System.out.println(
                        "Current         : "
                                + r.getCurrent()
                                + " A");

                System.out.println(
                        "Voltage         : "
                                + r.getVoltage()
                                + " V");

                System.out.println(
                        "Power           : "
                                + r.getPower()
                                + " kW");

                System.out.println(
                        "RPM             : "
                                + r.getRpm());

                System.out.println(
                        "Tool Wear       : "
                                + r.getToolWear()
                                + " hrs");

                System.out.println(
                        "Operating Hours : "
                                + r.getOperatingHours());

                System.out.println(
                        "Load Type       : "
                                + r.getLoadType());

                System.out.println(
                        "Fault           : "
                                + r.getFailureType());

                System.out.println(
                        "==========================================");

                return;
            }
        }

        System.out.println(
                "Machine not found.");
    }

    // =========================================================
    // HELPER
    // CREATE FACTORY REPORT
    // =========================================================

    private static String createFactoryReport(
            List<FactoryRecord> records) {

        StringBuilder report =
                new StringBuilder();

        /*
         * We don't need all 10,050 rows for demonstrating
         * the string-indexing algorithms.
         *
         * The first 500 records create a manageable report.
         */

        int limit =
                Math.min(500, records.size());

        for (int i = 0;
             i < limit;
             i++) {

            FactoryRecord r =
                    records.get(i);

            report.append(
                    r.getDeviceId())
                    .append(" ")
                    .append(
                            r.getTimestamp())
                    .append(" ")
                    .append(
                            r.getLoadType())
                    .append(" ")
                    .append(
                            r.getFailureType())
                    .append(" ");

        }

        return report.toString();
    }

    // =========================================================
    // HELPER
    // CHECK FAULT
    // =========================================================

    private static boolean isFault(
            FactoryRecord r) {

        if (r.getFailureType() == null) {
            return false;
        }

        return !r.getFailureType()
                .equalsIgnoreCase("No Fault");
    }

    // =========================================================
    // HELPER
    // READ INTEGER
    // =========================================================

    private static int readInteger(
            Scanner scanner) {

        while (true) {

            String input =
                    scanner.nextLine().trim();

            try {

                return Integer.parseInt(input);

            } catch (NumberFormatException e) {

                System.out.print(
                        "Enter a valid integer: ");
            }
        }
    }
}