package ca1;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.NoSuchElementException;

/** Small dependency-free checks for storage behavior and input validation. */
public final class BoundedFoodStorageTest {
    /** Fixed time makes date checks and stored timestamps reproducible. */
    private static final Clock CLOCK = Clock.fixed(
            Instant.parse("2026-01-01T10:15:30Z"), ZoneOffset.UTC);

    /** Runs all checks and prints success only when every assertion passes. */
    public static void main(String[] args) {
        testFactoryValidationAndTimestamp();
        testStackAndQueueRemoval();
        testCapacitySearchAndCircularWraparound();
        testEmptyStorageErrors();
        System.out.println("All BoundedFoodStorage tests passed.");
    }

    /** Checks subtype creation, recorded time, weight validation, and date bounds. */
    private static void testFactoryValidationAndTimestamp() {
        FoodItem burger = create(FoodType.BURGER, 180, LocalDate.now(CLOCK));
        check(burger.getName().equals("Burger"), "Factory should create the selected type.");
        check(burger.getStoredAt().toString().equals("2026-01-01T10:15:30"),
                "Factory should record the placement time.");
        expectThrows(IllegalArgumentException.class,
                () -> create(FoodType.PIZZA, 0, LocalDate.now(CLOCK)));
        expectThrows(IllegalArgumentException.class,
                () -> create(FoodType.PIZZA, 200, LocalDate.now(CLOCK).minusDays(1)));
        expectThrows(IllegalArgumentException.class,
                () -> create(FoodType.PIZZA, 200, LocalDate.now(CLOCK).plusDays(15)));
        check(create(FoodType.PIZZA, 200, LocalDate.now(CLOCK).plusDays(14))
                        .getBestBefore().equals(LocalDate.now(CLOCK).plusDays(14)),
                "The last permitted date, 14 days from today, should be accepted.");
    }

    /** Confirms that same-side removal is LIFO and opposite-side removal is FIFO. */
    private static void testStackAndQueueRemoval() {
        BoundedFoodStorage stack = new BoundedFoodStorage();
        FoodItem burger = create(FoodType.BURGER, 180, LocalDate.now(CLOCK));
        FoodItem pizza = create(FoodType.PIZZA, 300, LocalDate.now(CLOCK));
        stack.addFromLoadingSide(burger);
        stack.addFromLoadingSide(pizza);
        check(stack.peekNext(BoundedFoodStorage.RemovalMode.LIFO) == pizza,
                "LIFO peek should return the most recently added item.");
        check(stack.removeNext(BoundedFoodStorage.RemovalMode.LIFO) == pizza,
                "LIFO removal should remove the most recently added item.");
        check(stack.removeNext(BoundedFoodStorage.RemovalMode.LIFO) == burger,
                "LIFO should then remove the remaining item.");

        BoundedFoodStorage queue = new BoundedFoodStorage();
        queue.addFromLoadingSide(burger);
        queue.addFromLoadingSide(pizza);
        check(queue.peekNext(BoundedFoodStorage.RemovalMode.FIFO) == burger,
                "FIFO peek should return the earliest added item.");
        check(queue.removeNext(BoundedFoodStorage.RemovalMode.FIFO) == burger,
                "FIFO removal should remove the earliest added item.");
    }

    /** Checks name search, full-capacity rejection, and wraparound ordering. */
    private static void testCapacitySearchAndCircularWraparound() {
        BoundedFoodStorage storage = new BoundedFoodStorage();
        FoodItem burger = create(FoodType.BURGER, 180, LocalDate.now(CLOCK));
        FoodItem pizza = create(FoodType.PIZZA, 300, LocalDate.now(CLOCK));
        FoodItem fries = create(FoodType.FRIES, 120, LocalDate.now(CLOCK));
        FoodItem hotdog = create(FoodType.HOTDOG, 160, LocalDate.now(CLOCK));
        storage.addFromLoadingSide(burger);
        storage.addFromLoadingSide(pizza);
        storage.addFromLoadingSide(fries);
        check(storage.searchByName("pIzZa").size() == 1, "Search should ignore name case.");
        check(storage.searchByName("Sandwich").isEmpty(), "Missing searches should be empty.");
        check(storage.removeNext(BoundedFoodStorage.RemovalMode.FIFO) == burger,
                "FIFO should remove from the opposite side.");
        storage.addFromLoadingSide(hotdog);
        check(storage.peekNext(BoundedFoodStorage.RemovalMode.FIFO) == pizza,
                "Queue order should survive circular wraparound.");

        BoundedFoodStorage full = new BoundedFoodStorage();
        for (int i = 0; i < BoundedFoodStorage.CAPACITY; i++) {
            full.addFromLoadingSide(burger);
        }
        check(full.isFull(), "Storage should report full at capacity.");
        expectThrows(IllegalStateException.class, () -> full.addFromLoadingSide(burger));
    }

    /** Confirms empty removals and peeks, plus blank searches, fail explicitly. */
    private static void testEmptyStorageErrors() {
        BoundedFoodStorage storage = new BoundedFoodStorage();
        expectThrows(NoSuchElementException.class,
                () -> storage.removeNext(BoundedFoodStorage.RemovalMode.LIFO));
        expectThrows(NoSuchElementException.class,
                () -> storage.peekNext(BoundedFoodStorage.RemovalMode.FIFO));
        expectThrows(IllegalArgumentException.class, () -> storage.searchByName(" "));
    }

    /** Uses the fixed clock so test records have deterministic dates and times. */
    private static FoodItem create(FoodType type, int weight, LocalDate bestBefore) {
        return FoodItemFactory.create(type, weight, bestBefore, CLOCK);
    }

    /** Throws an assertion error with context when a condition is false. */
    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    /** Verifies that an action fails with the expected exception type. */
    private static void expectThrows(Class<? extends RuntimeException> expectedType, Runnable action) {
        try {
            action.run();
        } catch (RuntimeException exception) {
            if (expectedType.isInstance(exception)) {
                return;
            }
            throw new AssertionError("Expected " + expectedType.getSimpleName()
                    + " but got " + exception.getClass().getSimpleName(), exception);
        }
        throw new AssertionError("Expected " + expectedType.getSimpleName() + " to be thrown.");
    }
}
