package hw.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MealPlanItemRequest {
    private long date;
    private int slot;
    private int position;
    private String type;
    @JsonProperty("value")
    private MenuItemValue menuItemValue;

    public MealPlanItemRequest(long date, int slot, int position, String type, MenuItemValue menuItemValue) {
        this.date = date;
        this.slot = slot;
        this.position = position;
        this.type = type;
        this.menuItemValue = menuItemValue;
    }

    public long getDate() {
        return date;
    }

    public int getSlot() {
        return slot;
    }

    public int getPosition() {
        return position;
    }

    public String getType() {
        return type;
    }

    public MenuItemValue getMenuItemValue() {
        return menuItemValue;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setMenuItemValue(MenuItemValue menuItemValue) {
        this.menuItemValue = menuItemValue;
    }
}
