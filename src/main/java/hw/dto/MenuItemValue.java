package hw.dto;

public class MenuItemValue {
    private int id;
    private int servings;
    private String title;
    private String imageType;

    public MenuItemValue(int id, int servings, String title, String imageType) {
        this.id = id;
        this.servings = servings;
        this.title = title;
        this.imageType = imageType;
    }

    public int getId() {
        return id;
    }

    public int getServings() {
        return servings;
    }

    public String getTitle() {
        return title;
    }

    public String getImageType() {
        return imageType;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }
}
