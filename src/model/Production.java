package model;

public class Production {

    private String orderId;
    private String product;
    private int quantity;
    private String machineId;
    private String date;
    private String status;

    public Production(String orderId, String product,
                      int quantity, String machineId,
                      String date, String status) {

        this.orderId = orderId;
        this.product = product;
        this.quantity = quantity;
        this.machineId = machineId;
        this.date = date;
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getMachineId() {
        return machineId;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Order ID        : " + orderId +
               "\nProduct         : " + product +
               "\nQuantity        : " + quantity +
               "\nMachine ID      : " + machineId +
               "\nDate            : " + date +
               "\nStatus          : " + status;
    }
}