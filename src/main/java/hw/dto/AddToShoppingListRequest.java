package hw.dto;

public class AddToShoppingListRequest {
    private String item;
    private String aisle;
    private boolean parse = true;

    public AddToShoppingListRequest(String item, String aisle) {
        this.item = item;
        this.aisle = aisle;
    }

    public String getItem() {
        return item;
    }

    public String getAisle() {
        return aisle;
    }

    public boolean isParse() {
        return parse;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public void setAisle(String aisle) {
        this.aisle = aisle;
    }

    public void setParse(boolean parse) {
        this.parse = parse;
    }
}
