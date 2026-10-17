package ca1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

/** Fixed-capacity circular deque supporting LIFO and FIFO removal policies. */
public final class BoundedFoodStorage {
    /** Assignment capacity: the array never stores more than eight food items. */
    public static final int CAPACITY = 8;

    /** Chooses whether removal occurs at the loading side or the opposite side. */
    public enum RemovalMode {
        LIFO,
        FIFO
    }

    /** Circular buffer holding the food records. */
    private final FoodItem[] items = new FoodItem[CAPACITY];
    /** Array index of the item currently nearest the loading side. */
    private int loadingSide;
    /** Number of occupied slots in the circular buffer. */
    private int size;

    /** Returns the current number of stored items. */
    public int size() {
        return size;
    }

    /** Reports whether the storage currently contains no items. */
    public boolean isEmpty() {
        return size == 0;
    }

    /** Reports whether all available storage slots are occupied. */
    public boolean isFull() {
        return size == CAPACITY;
    }

    /** Adds an item at the loading side without shifting existing array entries. */
    public void addFromLoadingSide(FoodItem item) {
        Objects.requireNonNull(item, "Food item is required.");
        if (isFull()) {
            throw new IllegalStateException("Storage is full (capacity: " + CAPACITY + ").");
        }

        // Move one slot backward, wrapping to the last slot when the index reaches zero.
        loadingSide = (loadingSide - 1 + CAPACITY) % CAPACITY;
        items[loadingSide] = item;
        size++;
    }

    /** Removes from the loading side for LIFO or the opposite side for FIFO. */
    public FoodItem removeNext(RemovalMode mode) {
        Objects.requireNonNull(mode, "Removal mode is required.");
        if (isEmpty()) {
            throw new NoSuchElementException("Storage is empty; there is nothing to remove.");
        }

        if (mode == RemovalMode.LIFO) {
            FoodItem item = items[loadingSide];
            items[loadingSide] = null;
            loadingSide = (loadingSide + 1) % CAPACITY;
            size--;
            return item;
        }

        // The oldest item is at the far end of the occupied circular-array segment.
        int oppositeSide = (loadingSide + size - 1) % CAPACITY;
        FoodItem item = items[oppositeSide];
        items[oppositeSide] = null;
        size--;
        return item;
    }

    /** Returns the next item for the selected policy without removing it. */
    public FoodItem peekNext(RemovalMode mode) {
        Objects.requireNonNull(mode, "Removal mode is required.");
        if (isEmpty()) {
            throw new NoSuchElementException("Storage is empty; there is no item to peek at.");
        }

        // LIFO reads the loading side; FIFO reads the opposite end of the occupied segment.
        int nextIndex = mode == RemovalMode.LIFO
                ? loadingSide
                : (loadingSide + size - 1) % CAPACITY;
        return items[nextIndex];
    }

    /** Finds every stored item with the requested name, ignoring letter case. */
    public List<FoodItem> searchByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Search name must not be empty.");
        }

        List<FoodItem> matches = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            FoodItem item = items[(loadingSide + i) % CAPACITY];
            if (item.getName().equalsIgnoreCase(name.trim())) {
                matches.add(item);
            }
        }
        return Collections.unmodifiableList(matches);
    }

    /** Returns an unmodifiable view of the logical order from loading side to opposite side. */
    public List<FoodItem> getItemsFromLoadingSide() {
        List<FoodItem> orderedItems = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            orderedItems.add(items[(loadingSide + i) % CAPACITY]);
        }
        return Collections.unmodifiableList(orderedItems);
    }
}
