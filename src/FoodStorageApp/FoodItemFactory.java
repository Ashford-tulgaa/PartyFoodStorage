package ca1;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/** Validates food details and constructs the matching food-item subtype. */
public final class FoodItemFactory {
    /** Prevents callers from creating an unused factory instance. */
    private FoodItemFactory() {
    }

    /** Creates the selected subtype after checking its required values. */
    public static FoodItem create(FoodType type, int weightGrams, LocalDate bestBefore, Clock clock) {
        Objects.requireNonNull(type, "Food type is required.");
        Objects.requireNonNull(clock, "Clock is required.");
        if (weightGrams <= 0) {
            throw new IllegalArgumentException("Weight must be greater than 0 grams.");
        }
        validateBestBefore(bestBefore, clock);

        // Capture the placement timestamp once so every created item records its own insertion time.
        LocalDateTime storedAt = LocalDateTime.now(clock);
        // Select the matching subclass so the common storage can handle all types polymorphically.
        switch (type) {
            case BURGER:
                return new Burger(weightGrams, bestBefore, storedAt);
            case PIZZA:
                return new Pizza(weightGrams, bestBefore, storedAt);
            case FRIES:
                return new Fries(weightGrams, bestBefore, storedAt);
            case SANDWICH:
                return new Sandwich(weightGrams, bestBefore, storedAt);
            case HOTDOG:
                return new Hotdog(weightGrams, bestBefore, storedAt);
            default:
                throw new IllegalArgumentException("Unsupported food type: " + type);
        }
    }

    /** Rejects dates before today or more than 14 days after today. */
    public static void validateBestBefore(LocalDate bestBefore, Clock clock) {
        Objects.requireNonNull(bestBefore, "Best-before date is required.");
        Objects.requireNonNull(clock, "Clock is required.");

        LocalDate today = LocalDate.now(clock);
        if (bestBefore.isBefore(today) || bestBefore.isAfter(today.plusWeeks(2))) {
            throw new IllegalArgumentException(
                    "Best-before date must be today or within the next 14 days.");
        }
    }
}
