package ca1;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** A burger item that inherits the common food details. */
public final class Burger extends FoodItem {
    /** Creates a burger with a date-only expiry and an exact placement timestamp. */
    Burger(int weightGrams, LocalDate bestBefore, LocalDateTime storedAt) {
        super(weightGrams, bestBefore, storedAt);
    }

    /** Supplies the fixed name for this food subtype. */
    @Override
    public String getName() {
        return "Burger";
    }
}
