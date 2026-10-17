package ca1;

/** The five food categories accepted by the application. */
public enum FoodType {
    BURGER("Burger"),
    PIZZA("Pizza"),
    FRIES("Fries"),
    SANDWICH("Sandwich"),
    HOTDOG("Hotdog");

    /** Name shown in the interactive type-selection menu. */
    private final String displayName;

    /** Associates each enum value with its menu-friendly name. */
    FoodType(String displayName) {
        this.displayName = displayName;
    }

    /** Returns the name displayed to the user. */
    public String getDisplayName() {
        return displayName;
    }
}
