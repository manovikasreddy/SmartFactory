package model;

public class Quality {

    private String materialId;
    private String materialName;
    private int quantity;
    private int minimumRequired;
    private String supplier;

    public Quality(String materialId, String materialName,
                     int quantity, int minimumRequired,
                     String supplier) {

        this.materialId = materialId;
        this.materialName = materialName;
        this.quantity = quantity;
        this.minimumRequired = minimumRequired;
        this.supplier = supplier;
    }

    public Quality(String trim, String trim2, String trim3, String trim4, String trim5, String trim6) {
		// TODO Auto-generated constructor stub
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