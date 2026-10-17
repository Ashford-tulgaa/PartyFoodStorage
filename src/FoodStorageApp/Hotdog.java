package ca1;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** A hotdog item that inherits the common food details. */
public final class Hotdog extends FoodItem {
    /** Creates a hotdog with a date-only expiry and an exact placement timestamp. */
    Hotdog(int weightGrams, LocalDate bestBefore, LocalDateTime storedAt) {
        super(weightGrams, bestBefore, storedAt);
    }

    /** Supplies the fixed name for this food subtype. */
    @Override
    public String getName() {
        return "Hotdog";
    }
}
