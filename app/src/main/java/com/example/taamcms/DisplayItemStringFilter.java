package com.example.taamcms;

public interface DisplayItemStringFilter {
    /**
     * Determines if an item satisfies the filter, ie should be kept.
     * @param item item to check.
     * @param targetValue value to target the item to.
     * @return if the item should be kept.
     */
    public boolean isWanted(DisplayItem item, String targetValue);
}
