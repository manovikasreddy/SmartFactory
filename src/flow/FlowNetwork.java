package flow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.FactoryRecord;

/**
 * Flow network representation using adjacency lists of FlowEdge objects.
 * Models industrial production pipelines, raw material distributions,
 * and multi-stage factory links.
 */
public class FlowNetwork {

    private final int V;
    private int E;
    private final List<List<FlowEdge>> adj;
    private final Map<Integer, String> nodeLabels;

    @SuppressWarnings("unchecked")
    public FlowNetwork(int V) {
        if (V < 0) {
            throw new IllegalArgumentException("Number of vertices must be non-negative");
        }
        this.V = V;
        this.E = 0;
        this.adj = new ArrayList<>(V);
        this.nodeLabels = new HashMap<>();

        for (int v = 0; v < V; v++) {
            adj.add(new ArrayList<>());
        }
    }

    public int V() {
        return V;
    }

    public int E() {
        return E;
    }

    public void addEdge(FlowEdge e) {
        int v = e.from();
        int w = e.to();
        validateVertex(v);
        validateVertex(w);
        adj.get(v).add(e);
        adj.get(w).add(e); // residual backward access
        E++;
    }

    public Iterable<FlowEdge> adj(int v) {
        validateVertex(v);
        return adj.get(v);
    }

    public List<FlowEdge> edges() {
        List<FlowEdge> list = new ArrayList<>();
        for (int v = 0; v < V; v++) {
            for (FlowEdge e : adj.get(v)) {
                if (e.to() != v) {
                    list.add(e);
                }
            }
        }
        return list;
    }

    public void setNodeLabel(int v, String label) {
        validateVertex(v);
        nodeLabels.put(v, label);
    }

    public String getNodeLabel(int v) {
        return nodeLabels.getOrDefault(v, "Stage-" + v);
    }

    public void resetFlows() {
        for (FlowEdge e : edges()) {
            e.resetFlow();
        }
    }

    private void validateVertex(int v) {
        if (v < 0 || v >= V) {
            throw new IllegalArgumentException("Vertex " + v + " is not between 0 and " + (V - 1));
        }
    }

    /**
     * Builds a multi-stage industrial factory production network based on real factory parameters:
     * Node 0: Raw Materials Inflow (Source)
     * Stage 1 (Nodes 1-3): Smelting, Forging, Material Prep
     * Stage 2 (Nodes 4-7): CNC Machining, Milling, Cutting (capacities based on machine parameters)
     * Stage 3 (Nodes 8-10): Sub-assembly & Surface Finishing
     * Stage 4 (Nodes 11-12): Final Testing & Quality Control
     * Node 13: Dispatch / Finished Warehouse (Sink)
     */
    public static FlowNetwork createFactoryProductionNetwork(List<FactoryRecord> records) {
        FlowNetwork net = new FlowNetwork(14);

        net.setNodeLabel(0, "Raw Material Hub (Source)");
        net.setNodeLabel(1, "Smelting & Casting Unit 1");
        net.setNodeLabel(2, "Forging & Stamping Unit 2");
        net.setNodeLabel(3, "Material Prep & Ingot Milling");
        net.setNodeLabel(4, "CNC Center Alpha (M1)");
        net.setNodeLabel(5, "CNC Center Beta (M2)");
        net.setNodeLabel(6, "Precision Lathe Gamma (M3)");
        net.setNodeLabel(7, "Robotic Milling Delta (M4)");
        net.setNodeLabel(8, "Sub-Assembly Line A");
        net.setNodeLabel(9, "Sub-Assembly Line B");
        net.setNodeLabel(10, "Surface Treatment Cell");
        net.setNodeLabel(11, "Automated QA & Sensor Test");
        net.setNodeLabel(12, "Manual Quality Verification");
        net.setNodeLabel(13, "Dispatch Warehouse (Sink)");

        // Derive dynamic capacity factors from dataset if available
        double baseCap = 100.0;
        if (records != null && !records.isEmpty()) {
            double avgRpm = records.stream().mapToDouble(FactoryRecord::getRpm).average().orElse(1500.0);
            baseCap = Math.max(50.0, avgRpm / 25.0);
        }

        // Source -> Stage 1 (Raw Material Feeding)
        net.addEdge(new FlowEdge(0, 1, baseCap * 1.2, "Raw Feeder 1"));
        net.addEdge(new FlowEdge(0, 2, baseCap * 0.9, "Raw Feeder 2"));
        net.addEdge(new FlowEdge(0, 3, baseCap * 1.1, "Raw Feeder 3"));

        // Stage 1 -> Stage 2 (Prepared Stock -> CNC & Milling)
        net.addEdge(new FlowEdge(1, 4, baseCap * 0.7, "Casting to CNC Alpha"));
        net.addEdge(new FlowEdge(1, 5, baseCap * 0.6, "Casting to CNC Beta"));
        net.addEdge(new FlowEdge(2, 5, baseCap * 0.5, "Forging to CNC Beta"));
        net.addEdge(new FlowEdge(2, 6, baseCap * 0.8, "Forging to Lathe Gamma"));
        net.addEdge(new FlowEdge(3, 6, baseCap * 0.4, "Milling to Lathe Gamma"));
        net.addEdge(new FlowEdge(3, 7, baseCap * 0.85, "Milling to Robotic Delta"));

        // Stage 2 -> Stage 3 (Machined Parts -> Sub-Assembly & Treatment)
        net.addEdge(new FlowEdge(4, 8, baseCap * 0.65, "CNC Alpha to Assembly A"));
        net.addEdge(new FlowEdge(5, 8, baseCap * 0.55, "CNC Beta to Assembly A"));
        net.addEdge(new FlowEdge(5, 9, baseCap * 0.50, "CNC Beta to Assembly B"));
        net.addEdge(new FlowEdge(6, 9, baseCap * 0.70, "Lathe Gamma to Assembly B"));
        net.addEdge(new FlowEdge(7, 10, baseCap * 0.80, "Robotic Delta to Treatment"));

        // Stage 3 -> Stage 4 (Assembly -> QA Testing)
        net.addEdge(new FlowEdge(8, 11, baseCap * 0.95, "Assembly A to Auto QA"));
        net.addEdge(new FlowEdge(9, 11, baseCap * 0.60, "Assembly B to Auto QA"));
        net.addEdge(new FlowEdge(9, 12, baseCap * 0.55, "Assembly B to Manual QA"));
        net.addEdge(new FlowEdge(10, 12, baseCap * 0.75, "Treatment to Manual QA"));

        // Stage 4 -> Sink (Tested Goods -> Dispatch Warehouse)
        net.addEdge(new FlowEdge(11, 13, baseCap * 1.3, "Auto QA to Dispatch"));
        net.addEdge(new FlowEdge(12, 13, baseCap * 1.1, "Manual QA to Dispatch"));

        return net;
    }

    /**
     * Constructs a synthetic large-scale factory flow network with configurable stages
     * and parallel lines to test scalable algorithms such as Dinic's Algorithm.
     */
    public static FlowNetwork createLargeFactoryNetwork(int stages, int machinesPerStage) {
        int totalNodes = 2 + stages * machinesPerStage;
        FlowNetwork net = new FlowNetwork(totalNodes);

        int source = 0;
        int sink = totalNodes - 1;

        net.setNodeLabel(source, "Central Factory Input (Source)");
        net.setNodeLabel(sink, "Consolidated Dispatch (Sink)");

        // Source to first stage
        for (int m = 0; m < machinesPerStage; m++) {
            int node = 1 + m;
            net.setNodeLabel(node, "Stage 1 - Machine " + (m + 1));
            net.addEdge(new FlowEdge(source, node, 50 + (m % 5) * 15, "Inflow Link " + (m + 1)));
        }

        // Intermediate stages
        for (int s = 1; s < stages; s++) {
            int stageStart = 1 + (s - 1) * machinesPerStage;
            int nextStageStart = 1 + s * machinesPerStage;

            for (int u = 0; u < machinesPerStage; u++) {
                int fromNode = stageStart + u;
                net.setNodeLabel(fromNode, "Stage " + s + " - Unit " + (u + 1));

                // Connect to adjacent machines in next stage
                int toNode1 = nextStageStart + u;
                int toNode2 = nextStageStart + ((u + 1) % machinesPerStage);

                net.addEdge(new FlowEdge(fromNode, toNode1, 30 + ((u * 7) % 40),
                        "Link S" + s + "U" + u + "->S" + (s + 1) + "U" + u));
                if (toNode1 != toNode2) {
                    net.addEdge(new FlowEdge(fromNode, toNode2, 25 + ((u * 11) % 35),
                            "Link S" + s + "U" + u + "->S" + (s + 1) + "U" + ((u + 1) % machinesPerStage)));
                }
            }
        }

        // Last stage to sink
        int lastStageStart = 1 + (stages - 1) * machinesPerStage;
        for (int m = 0; m < machinesPerStage; m++) {
            int node = lastStageStart + m;
            net.setNodeLabel(node, "Stage " + stages + " - Unit " + (m + 1));
            net.addEdge(new FlowEdge(node, sink, 45 + (m % 4) * 20, "Outflow Link " + (m + 1)));
        }

        return net;
    }
}
