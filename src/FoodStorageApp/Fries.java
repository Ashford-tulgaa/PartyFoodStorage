package ca1;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** An order of fries that inherits the common food details. */
public final class Fries extends FoodItem {
    /** Creates fries with a date-only expiry and an exact placement timestamp. */
    Fries(int weightGrams, LocalDate bestBefore, LocalDateTime storedAt) {
        super(weightGrams, bestBefore, storedAt);
    }

    /** Supplies the fixed name for this food subtype. */
    @Override
    public String getName() {
        return "Fries";
    }
}
