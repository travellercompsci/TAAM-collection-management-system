package com.example.taamcms;

import org.jetbrains.annotations.NotNull;

public class GeneratePDFMode {
    /**
     * R.string.id_of_hint, set this to -1 if text input isn't used.
     */
    public final int hintId;
    public final boolean requireTextInput;
    public final String dropDownTitle;
    public final DisplayItemStringFilter filter;

    public GeneratePDFMode(String dropDownTitle, int hintId, boolean requireTextInput, DisplayItemStringFilter filter) {
        this.dropDownTitle = dropDownTitle;
        this.hintId = hintId;
        this.requireTextInput = requireTextInput;
        this.filter = filter;
    }

    public String getUrl() {
        return "";
    }

    @NotNull
    @Override
    public String toString() {
        return dropDownTitle;
    }
}
