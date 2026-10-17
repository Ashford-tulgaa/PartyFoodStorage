package ca1;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** A pizza item that inherits the common food details. */
public final class Pizza extends FoodItem {
    /** Creates a pizza with a date-only expiry and an exact placement timestamp. */
    Pizza(int weightGrams, LocalDate bestBefore, LocalDateTime storedAt) {
        super(weightGrams, bestBefore, storedAt);
    }

    /** Supplies the fixed name for this food subtype. */
    @Override
    public String getName() {
        return "Pizza";
    }
}
