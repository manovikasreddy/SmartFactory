package model;

public class Inventory {

    private String materialId;
    private String materialName;
    private int quantity;
    private int minimumRequired;
    private String supplier;

    public Inventory(String materialId, String materialName,
                     int quantity, int minimumRequired,
                     String supplier) {

        this.materialId = materialId;
        this.materialName = materialName;
        this.quantity = quantity;
        this.minimumRequired = minimumRequired;
        this.supplier = supplier;
    }

    public String getMaterialId() {
        return materialId;
    }

    public String getMaterialName() {
        return materialName;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getMinimumRequired() {
        return minimumRequired;
    }

    public String getSupplier() {
        return supplier;
    }

    @Override
    public String toString() {
        return "Material ID     : " + materialId +
               "\nMaterial Name   : " + materialName +
               "\nQuantity        : " + quantity +
               "\nMinimum Required: " + minimumRequired +
               "\nSupplier        : " + supplier;
    }
}