package model;

public class FactoryRecord {

    private String recordId;
    private String deviceId;
    private String timestamp;

    private double signalStrength;
    private double packetLoss;
    private double temperature;
    private double vibration;
    private double humidity;
    private double pressure;
    private double current;
    private double voltage;
    private double powerFactor;
    private double power;
    private double energy;
    private double rpm;
    private double toolWear;
    private double operatingHours;

    private String loadType;
    private String faultFlag;
    private String failureType;

    public FactoryRecord(
            String recordId,
            String deviceId,
            String timestamp,
            double signalStrength,
            double packetLoss,
            double temperature,
            double vibration,
            double humidity,
            double pressure,
            double current,
            double voltage,
            double powerFactor,
            double power,
            double energy,
            double rpm,
            double toolWear,
            double operatingHours,
            String loadType,
            String faultFlag,
            String failureType) {

        this.recordId = recordId;
        this.deviceId = deviceId;
        this.timestamp = timestamp;
        this.signalStrength = signalStrength;
        this.packetLoss = packetLoss;
        this.temperature = temperature;
        this.vibration = vibration;
        this.humidity = humidity;
        this.pressure = pressure;
        this.current = current;
        this.voltage = voltage;
        this.powerFactor = powerFactor;
        this.power = power;
        this.energy = energy;
        this.rpm = rpm;
        this.toolWear = toolWear;
        this.operatingHours = operatingHours;
        this.loadType = loadType;
        this.faultFlag = faultFlag;
        this.failureType = failureType;
    }

    public String getRecordId() {
        return recordId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getVibration() {
        return vibration;
    }

    public double getCurrent() {
        return current;
    }

    public double getVoltage() {
        return voltage;
    }

    public double getPower() {
        return power;
    }

    public double getRpm() {
        return rpm;
    }

    public double getToolWear() {
        return toolWear;
    }

    public double getOperatingHours() {
        return operatingHours;
    }

    public String getLoadType() {
        return loadType;
    }

    public String getFaultFlag() {
        return faultFlag;
    }

    public String getFailureType() {
        return failureType;
    }
}