package flow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.FactoryRecord;

/**
 * High-level coordinator for CO-4 Network Flow operations.
 * Connects raw material flows, machine links, large plant networks,
 * worker-machine assignments, and min-cut bottleneck analyses.
 */
public class ProductionFlowManager {

    /**
     * Simulates raw material flow using Ford-Fulkerson.
     */
    public static void simulateRawMaterialFlow(List<FactoryRecord> records) {
        System.out.println("==========================================");
        System.out.println("   RAW MATERIAL FLOW [FORD-FULKERSON]");
        System.out.println("==========================================");

        FlowNetwork net = FlowNetwork.createFactoryProductionNetwork(records);
        int source = 0;
        int sink = net.V() - 1;

        System.out.println("Topology: Multi-Stage Industrial Plant (" + net.V() + " stations, " + net.E() + " transfer links)");
        System.out.println("Source: " + net.getNodeLabel(source));
        System.out.println("Sink  : " + net.getNodeLabel(sink));
        System.out.println("\nExecuting Ford-Fulkerson Augmenting Paths (DFS)...");

        FordFulkerson ff = new FordFulkerson(net, source, sink);

        System.out.println("\nAugmentation Trace (First 10 paths):");
        for (String log : ff.getAugmentationLog()) {
            System.out.println("  " + log);
        }

        System.out.println("\n------------------------------------------");
        System.out.printf("Total Augmentation Iterations: %d%n", ff.getAugmentationCount());
        System.out.printf("Maximum Material Flow Output : %.2f units/hour%n", ff.value());
        System.out.println("Ford-Fulkerson material flow modeling completed.");
        System.out.println("==========================================");
    }

    /**
     * Determines maximum material throughput through capacity-constrained links using Edmonds-Karp.
     */
    public static void optimizeProductionLinks(List<FactoryRecord> records) {
        System.out.println("==========================================");
        System.out.println("  PRODUCTION LINK MAX FLOW [EDMONDS-KARP]");
        System.out.println("==========================================");

        FlowNetwork net = FlowNetwork.createFactoryProductionNetwork(records);
        int source = 0;
        int sink = net.V() - 1;

        System.out.println("Executing Edmonds-Karp (BFS Shortest Augmenting Paths)...");
        System.out.println("Guarantees polynomial O(V * E^2) convergence.\n");

        EdmondsKarp ek = new EdmondsKarp(net, source, sink);

        System.out.println("Step-by-Step BFS Path Telemetry:");
        for (String step : ek.getStepLog()) {
            System.out.println(step);
        }

        System.out.println("\n------------------------------------------");
        System.out.printf("Total Shortest Augmentations : %d%n", ek.getAugmentationCount());
        System.out.printf("Maximum Production Flow Rate : %.2f units/hour%n", ek.value());
        System.out.println("Edmonds-Karp production link optimization completed.");
        System.out.println("==========================================");
    }

    /**
     * Evaluates Dinic's Algorithm on a scalable multi-line factory network.
     */
    public static void runLargeFactoryNetworkDinic(int stages, int machinesPerStage) {
        System.out.println("==========================================");
        System.out.println("   LARGE FACTORY FLOW [DINIC'S ALGORITHM]");
        System.out.println("==========================================");

        stages = Math.max(3, stages);
        machinesPerStage = Math.max(3, machinesPerStage);

        FlowNetwork net = FlowNetwork.createLargeFactoryNetwork(stages, machinesPerStage);
        int source = 0;
        int sink = net.V() - 1;

        System.out.printf("Generated Network: %d Stages x %d Parallel Units (%d Total Stations, %d Directed Links)%n",
                stages, machinesPerStage, net.V(), net.E());
        System.out.println("Executing Dinic's Algorithm (Level Graph BFS + Blocking Flow DFS)...");

        DinicAlgorithm dinic = new DinicAlgorithm(net, source, sink);

        System.out.println("\nExecution Results:");
        System.out.printf("  Level-Graph BFS Phases : %d phases%n", dinic.getPhaseCount());
        System.out.printf("  Execution Time (Nano)  : %,d ns (%.3f ms)%n",
                dinic.getExecutionTimeNanos(), dinic.getExecutionTimeNanos() / 1_000_000.0);
        System.out.printf("  Total Maximum Flow     : %.2f units/hour%n", dinic.getMaxFlow());
        System.out.println("Dinic's algorithm completed successfully for large network.");
        System.out.println("==========================================");
    }

    /**
     * Demonstrates bipartite matching for worker-to-machine assignment.
     */
    public static void assignWorkersToMachines() {
        System.out.println("==========================================");
        System.out.println(" WORKER-MACHINE ASSIGNMENT [BIPARTITE MATCHING]");
        System.out.println("==========================================");

        List<String> workers = Arrays.asList(
                "Alex Rivera (CNC Specialist)",
                "Blake Chen (Milling Tech)",
                "Carlos Gomez (Lathe Operator)",
                "Dana White (QA Analyst)",
                "Elena Rostova (Robotics Tech)",
                "Farhan Malik (Stamping Master)",
                "Grace Hopper (System Overseer)"
        );

        List<String> machines = Arrays.asList(
                "CNC Alpha (M1)",
                "CNC Beta (M2)",
                "Precision Lathe Gamma (M3)",
                "Robotic Milling Delta (M4)",
                "Stamping Unit (M5)",
                "Automated QA Cell (M6)",
                "Thermal Treatment Cell (M7)"
        );

        Map<String, List<String>> skills = new HashMap<>();
        skills.put("Alex Rivera (CNC Specialist)", Arrays.asList("CNC Alpha (M1)", "CNC Beta (M2)"));
        skills.put("Blake Chen (Milling Tech)", Arrays.asList("CNC Beta (M2)", "Robotic Milling Delta (M4)"));
        skills.put("Carlos Gomez (Lathe Operator)", Arrays.asList("Precision Lathe Gamma (M3)"));
        skills.put("Dana White (QA Analyst)", Arrays.asList("Automated QA Cell (M6)"));
        skills.put("Elena Rostova (Robotics Tech)", Arrays.asList("Robotic Milling Delta (M4)", "CNC Alpha (M1)"));
        skills.put("Farhan Malik (Stamping Master)", Arrays.asList("Stamping Unit (M5)"));
        skills.put("Grace Hopper (System Overseer)", Arrays.asList("Automated QA Cell (M6)", "Thermal Treatment Cell (M7)"));

        System.out.println("Active Factory Workforce   : " + workers.size() + " technicians");
        System.out.println("Available Production Units : " + machines.size() + " machines");
        System.out.println("\nComputing Maximum Bipartite Matching...");

        BipartiteMatching.AssignmentResult res = BipartiteMatching.solveAssignment(workers, machines, skills);

        System.out.println("\nOptimal Worker -> Machine Assignments (" + res.getMaxMatches() + " Total Matches):");
        for (Map.Entry<String, String> entry : res.getWorkerToMachine().entrySet()) {
            System.out.printf("  [ASSIGNED] %-34s ===> %s%n", entry.getKey(), entry.getValue());
        }

        if (!res.getUnassignedWorkers().isEmpty()) {
            System.out.println("\nUnassigned Personnel (Standby Pool):");
            for (String w : res.getUnassignedWorkers()) {
                System.out.println("  - " + w);
            }
        }

        if (!res.getUnassignedMachines().isEmpty()) {
            System.out.println("\nIdle Machines (Require additional operators):");
            for (String m : res.getUnassignedMachines()) {
                System.out.println("  - " + m);
            }
        }

        System.out.println("Bipartite matching worker allocation completed.");
        System.out.println("==========================================");
    }

    /**
     * Identifies bottlenecks using Max-Flow Min-Cut Theorem.
     */
    public static void identifyBottlenecks(List<FactoryRecord> records) {
        System.out.println("==========================================");
        System.out.println("  BOTTLENECK IDENTIFICATION [MAX-FLOW MIN-CUT]");
        System.out.println("==========================================");

        FlowNetwork net = FlowNetwork.createFactoryProductionNetwork(records);
        int source = 0;
        int sink = net.V() - 1;

        // First find max flow
        EdmondsKarp ek = new EdmondsKarp(net, source, sink);
        double maxFlow = ek.value();

        // Find min cut
        MinCutBottleneckFinder.BottleneckReport report =
                MinCutBottleneckFinder.findBottlenecks(net, source, sink);

        System.out.printf("Factory Maximum Flow Throughput: %.2f units/hour%n", maxFlow);
        System.out.printf("Minimum Cut Capacity           : %.2f units/hour%n", report.getMinCutCapacity());
        System.out.println("Max-Flow == Min-Cut Duality Verified: " +
                (Math.abs(maxFlow - report.getMinCutCapacity()) < 1e-4 ? "YES (Optimal Cut)" : "NO"));

        System.out.println("\nCRITICAL PRODUCTION BOTTLENECKS (Saturated Cut Edges):");
        int idx = 1;
        for (FlowEdge e : report.getBottleneckEdges()) {
            System.out.printf("  %d. %s [%s] -> %s [%s]%n",
                    idx++,
                    net.getNodeLabel(e.from()), (e.from() == 0 ? "Source" : "Stage"),
                    net.getNodeLabel(e.to()), (e.to() == sink ? "Sink" : "Stage"));
            System.out.printf("     Link: %s | Flow: %.1f / Cap: %.1f (100.0%% Saturated)%n",
                    e.getDescription(), e.flow(), e.capacity());
        }

        System.out.println("\nENGINEERING RECOMMENDATION:");
        System.out.println("Expanding capacity on the above cut links will directly increase");
        System.out.println("overall plant throughput. Expanding non-cut links will have ZERO impact!");
        System.out.println("==========================================");
    }
}
