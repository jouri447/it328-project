package com.mycompany.cakehaven;

/**
 * The three product categories in Cake Haven.
 * An enum is used so that a category can never be misspelled or invented by mistake.
 *
 * STATUS: fully implemented.
 */
public enum Category {
    CAKES("Cakes"),
    CUPCAKES("Cupcakes"),
    PASTRIES("Pastries");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    /** Text shown on the category tab (e.g. "Cakes"). */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Converts a text name (for example one received from the server) into a Category.
     * Matching ignores case and surrounding spaces.
     *
     * @return the matching Category, or null if the text is not a known category
     */
    public static Category fromName(String name) {
        if (name == null) {
            return null;
        }
        String trimmed = name.trim();
        for (Category category : values()) {
            if (category.displayName.equalsIgnoreCase(trimmed)
                    || category.name().equalsIgnoreCase(trimmed)) {
                return category;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
