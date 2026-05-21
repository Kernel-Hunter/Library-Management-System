package com.university.models;

import com.university.util.Persistable;

public abstract class LibraryItem implements Persistable {

    protected String itemId;
    protected String title;
    protected boolean isAvailable;

    public LibraryItem(String itemId, String title) {
        this.itemId = itemId;
        this.title = title;
        this.isAvailable = true;
    }

    // Abstract method - every subclass MUST implement this
    public abstract String getDetails();

    // Getters and Setters
    public String getItemId() { return itemId; }
    public String getTitle()  { return title; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { this.isAvailable = available; }
}