public class Inventory {

    private String itemId;
    private String itemName;
    private int availableQuantity;

    public Inventory(String itemId, String itemName, int availableQuantity) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.availableQuantity = availableQuantity;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }


    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    @Override
    public String toString() {
        return "itemId='" + itemId + '\'' +
                ", itemName='" + itemName + '\'' +
                ", availableQuantity=" + availableQuantity;
    }
}
