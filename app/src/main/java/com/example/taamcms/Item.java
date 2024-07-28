package com.example.taamcms;

public class Item {
    private String id;
    private String name;
    private String category;
    private String period;
    private String description;

    public Item(String id, String title, String author, String genre, String description) {
        this.id = id;
        this.name = title;
        this.category = author;
        this.period = genre;
        this.description = description;
    }

    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
