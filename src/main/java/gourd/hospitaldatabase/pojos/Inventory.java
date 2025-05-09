package gourd.hospitaldatabase.pojos;

public class Inventory {
    private int itemId;
    private String status;
    private String name;
    private String category;
    private String location;

    public Inventory(int itemId, String status, String name, String category, String location) {
        this.itemId = itemId;
        this.status = status;
        this.name = name;
        this.category = category;
        this.location = location;
    }

    public Inventory() {}

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
