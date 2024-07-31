package com.example.taamcms;

public class DisplayItem {
    private String id;
    private String title;
    private String lot;
    private String category;
    private String period;
    private String description;
    private String image;

    public DisplayItem() {}

    public DisplayItem(String id, String title, String lot, String category, String period, String description, String image) {
        this.id = id;
        this.title = title;
        this.lot = lot;
        this.category = category;
        this.period = period;
        this.description = description;
        this.image = image;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getLot() { return lot; }
    public void setLot(String lot) { this.lot = lot; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
