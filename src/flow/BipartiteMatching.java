package flow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Maximum Bipartite Matching for Industrial Resource Assignment.
 * Solves the assignment of qualified technicians/operators to eligible machines
 * or production lines to maximize concurrent factory operation.
 */
public class BipartiteMatching {

    public static class AssignmentResult {
        private final int maxMatches;
        private final Map<String, String> workerToMachine;
        private final List<String> unassignedWorkers;
        private final List<String> unassignedMachines;

        public AssignmentResult(int maxMatches, Map<String, String> workerToMachine,
                                List<String> unassignedWorkers, List<String> unassignedMachines) {
            this.maxMatches = maxMatches;
            this.workerToMachine = workerToMachine;
            this.unassignedWorkers = unassignedWorkers;
            this.unassignedMachines = unassignedMachines;
        }

        public int getMaxMatches() {
            return maxMatches;
        }

        public Map<String, String> getWorkerToMachine() {
            return workerToMachine;
        }

        public List<String> getUnassignedWorkers() {
            return unassignedWorkers;
        }

        public List<String> getUnassignedMachines() {
            return unassignedMachines;
        }
    }

    /**
     * Computes the maximum bipartite matching between workers and machines.
     *
     * @param workers list of worker names/IDs
     * @param machines list of machine names/IDs
     * @param eligibilities map from worker name to list of machines they are certified to operate
     * @return AssignmentResult containing maximum matching pairs and unassigned entities
     */
    public static AssignmentResult solveAssignment(
            List<String> workers,
            List<String> machines,
            Map<String, List<String>> eligibilities) {

        int numWorkers = workers.size();
        int numMachines = machines.size();

        // Node indexing:
        // Source = 0
        // Workers: 1 .. numWorkers
        // Machines: (numWorkers + 1) .. (numWorkers + numMachines)
        // Sink = numWorkers + numMachines + 1
        int source = 0;
        int sink = numWorkers + numMachines + 1;
        int totalNodes = sink + 1;

        FlowNetwork network = new FlowNetwork(totalNodes);
        network.setNodeLabel(source, "Workforce Dispatcher (Source)");
        network.setNodeLabel(sink, "Plant Operation (Sink)");

        // Source to each worker with capacity 1
        for (int i = 0; i < numWorkers; i++) {
            int workerNode = 1 + i;
            String worker = workers.get(i);
            network.setNodeLabel(workerNode, "Worker: " + worker);
            network.addEdge(new FlowEdge(source, workerNode, 1.0, "Deploy " + worker));
        }

        // Each machine to sink with capacity 1
        for (int j = 0; j < numMachines; j++) {
            int machineNode = 1 + numWorkers + j;
            String machine = machines.get(j);
            network.setNodeLabel(machineNode, "Machine: " + machine);
            network.addEdge(new FlowEdge(machineNode, sink, 1.0, "Run " + machine));
        }

        // Worker eligibility edges to machines
        for (int i = 0; i < numWorkers; i++) {
            String worker = workers.get(i);
            int workerNode = 1 + i;
            List<String> eligibleMachines = eligibilities.getOrDefault(worker, Collections.emptyList());

            for (String machine : eligibleMachines) {
                int machineIndex = machines.indexOf(machine);
                if (machineIndex >= 0) {
                    int machineNode = 1 + numWorkers + machineIndex;
                    network.addEdge(new FlowEdge(workerNode, machineNode, 1.0, worker + " -> " + machine));
                }
            }
        }

        // Solve using Edmonds-Karp / Dinic
        EdmondsKarp maxFlow = new EdmondsKarp(network, source, sink);
        int matchedCount = (int) Math.round(maxFlow.value());

        // Extract assignments from edges carrying flow
        Map<String, String> assignmentMap = new HashMap<>();
        for (FlowEdge e : network.edges()) {
            if (e.from() >= 1 && e.from() <= numWorkers &&
                e.to() >= 1 + numWorkers && e.to() <= numWorkers + numMachines) {
                if (e.flow() > 0.5) {
                    String worker = workers.get(e.from() - 1);
                    String machine = machines.get(e.to() - 1 - numWorkers);
                    assignmentMap.put(worker, machine);
                }
            }
        }

        List<String> unassignedWorkers = new ArrayList<>();
        for (String w : workers) {
            if (!assignmentMap.containsKey(w)) {
                unassignedWorkers.add(w);
            }
        }

        List<String> unassignedMachines = new ArrayList<>();
        for (String m : machines) {
            if (!assignmentMap.containsValue(m)) {
                unassignedMachines.add(m);
            }
        }

        return new AssignmentResult(matchedCount, assignmentMap, unassignedWorkers, unassignedMachines);
    }
}
