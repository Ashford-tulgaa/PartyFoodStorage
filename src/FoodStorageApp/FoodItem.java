package ca1;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Common immutable details shared by every food type in the storage. */
public abstract class FoodItem {
    /** Weight of this item, measured in grams. */
    private final int weightGrams;
    /** Expiry is a calendar date only; the user does not enter a time for it. */
    private final LocalDate bestBefore;
    /** Placement is a specific moment, so it includes both date and clock time. */
    private final LocalDateTime storedAt;

    /**
     * Stores shared details. The expiry date has no time component, while the
     * placement timestamp records the exact time the item entered storage.
     */
    protected FoodItem(int weightGrams, LocalDate bestBefore, LocalDateTime storedAt) {
        this.weightGrams = weightGrams;
        this.bestBefore = bestBefore;
        this.storedAt = storedAt;
    }

    /** Returns the human-readable name supplied by the concrete food subtype. */
    public abstract String getName();

    /** Returns the item's weight in grams. */
    public int getWeightGrams() {
        return weightGrams;
    }

    /** Returns the item's best-before date. */
    public LocalDate getBestBefore() {
        return bestBefore;
    }

    /** Returns the date and time when the item was placed into storage. */
    public LocalDateTime getStoredAt() {
        return storedAt;
    }

    /** Formats all tracked item details for display in the console. */
    @Override
    public String toString() {
        return String.format("%s | %d g | Best before: %s | Stored at: %s",
                getName(), weightGrams, bestBefore, storedAt);
    }
}
