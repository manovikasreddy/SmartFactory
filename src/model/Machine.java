package model;

public class Machine {

    private String machineId;
    private String machineName;
    private String productionLine;
    private String status;
    private int capacity;

    public Machine(String machineId, String machineName,
                   String productionLine, String status,
                   int capacity) {

        this.machineId = machineId;
        this.machineName = machineName;
        this.productionLine = productionLine;
        this.status = status;
        this.capacity = capacity;
    }

    public String getMachineId() {
        return machineId;
    }

    public String getMachineName() {
        return machineName;
    }

    public String getProductionLine() {
        return productionLine;
    }

    public String getStatus() {
        return status;
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public String toString() {
        return "Machine ID       : " + machineId +
               "\nMachine Name     : " + machineName +
               "\nProduction Line  : " + productionLine +
               "\nStatus           : " + status +
               "\nCapacity         : " + capacity;
    }
}