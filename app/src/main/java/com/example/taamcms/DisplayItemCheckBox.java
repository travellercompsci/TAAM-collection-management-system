package com.example.taamcms;

public class DisplayItemCheckBox {
    DisplayItem item;
    boolean isSelected;

    DisplayItemCheckBox(DisplayItem item) {
        this.item = item;
        isSelected = false;
    }

    public boolean isSelected() {
        return isSelected;
    }
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
