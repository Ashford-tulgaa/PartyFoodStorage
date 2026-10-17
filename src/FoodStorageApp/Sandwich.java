package ca1;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** A sandwich item that inherits the common food details. */
public final class Sandwich extends FoodItem {
    /** Creates a sandwich with a date-only expiry and an exact placement timestamp. */
    Sandwich(int weightGrams, LocalDate bestBefore, LocalDateTime storedAt) {
        super(weightGrams, bestBefore, storedAt);
    }

    /** Supplies the fixed name for this food subtype. */
    @Override
    public String getName() {
        return "Sandwich";
    }
}
