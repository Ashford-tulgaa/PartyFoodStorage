package ca1;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/** Console user interface for adding, viewing, searching, and removing food items. */
public final class FoodStorageApp {
    /** Reads menu input from the application's standard input. */
    private final Scanner scanner;
    /** Supplies the current date and the item's placement time. */
    private final Clock clock;
    /** Holds items independently of the console interface. */
    private final BoundedFoodStorage storage;
    /** Determines which end supplies the next item to remove or peek at. */
    private BoundedFoodStorage.RemovalMode removalMode;

    /** Creates the application state and starts in same-side LIFO mode. */
    private FoodStorageApp(Scanner scanner, Clock clock) {
        this.scanner = scanner;
        this.clock = clock;
        this.storage = new BoundedFoodStorage();
        this.removalMode = BoundedFoodStorage.RemovalMode.LIFO;
    }

    /** Starts the interactive application using the computer's local clock. */
    public static void main(String[] args) {
        new FoodStorageApp(new Scanner(System.in), Clock.systemDefaultZone()).run();
    }

    /** Repeats the menu until the user exits or input ends. */
    private void run() {
        System.out.println("Party Food Storage (maximum " + BoundedFoodStorage.CAPACITY + " items)");
        while (scanner.hasNextLine()) {
            printMenu();
            String choice = readLine("Choose an option: ");
            if (choice == null) {
                break;
            }

            switch (choice.trim()) {
                case "1":
                    addFoodItem();
                    break;
                case "2":
                    removeFoodItem();
                    break;
                case "3":
                    displayStorage();
                    break;
                case "4":
                    peekFoodItem();
                    break;
                case "5":
                    searchFoodItems();
                    break;
                case "6":
                    changeRemovalMode();
                    break;
                case "0":
                    System.out.println("Application terminated.");
                    return;
                default:
                    System.out.println("Invalid menu option. Choose 0 through 6.");
            }
        }
    }

    /** Prints available actions and identifies the active removal policy. */
    private void printMenu() {
        System.out.println();
        System.out.println("Removal mode: " + removalMode);
        System.out.println("1. Add food item");
        System.out.println("2. Remove next food item");
        System.out.println("3. Display storage");
        System.out.println("4. Peek at next food item");
        System.out.println("5. Search by food name");
        System.out.println("6. Change removal mode (LIFO/FIFO)");
        System.out.println("0. Exit");
    }

    /** Collects validated item details and inserts a new item if capacity allows. */
    private void addFoodItem() {
        if (storage.isFull()) {
            System.out.println("Storage is full; no more than "
                    + BoundedFoodStorage.CAPACITY + " items can be stored.");
            return;
        }

        FoodType type = readFoodType();
        if (type == null) {
            return;
        }
        Integer weight = readPositiveWeight();
        if (weight == null) {
            return;
        }
        LocalDate bestBefore = readBestBeforeDate();
        if (bestBefore == null) {
            return;
        }

        FoodItem item = FoodItemFactory.create(type, weight, bestBefore, clock);
        storage.addFromLoadingSide(item);
        System.out.println("Added: " + item);
    }

    /** Prompts for one of the five supported food categories. */
    private FoodType readFoodType() {
        while (true) {
            System.out.println("Food types:");
            FoodType[] types = FoodType.values();
            for (int i = 0; i < types.length; i++) {
                System.out.println((i + 1) + ". " + types[i].getDisplayName());
            }

            Integer choice = readInteger("Choose a food type: ");
            if (choice == null) {
                return null;
            }
            if (choice >= 1 && choice <= types.length) {
                return types[choice - 1];
            }
            System.out.println("Choose a number from 1 to " + types.length + ".");
        }
    }

    /** Repeats the weight prompt until a positive whole number is entered. */
    private Integer readPositiveWeight() {
        while (true) {
            Integer weight = readInteger("Enter weight in grams: ");
            if (weight == null) {
                return null;
            }
            if (weight > 0) {
                return weight;
            }
            System.out.println("Weight must be greater than 0 grams.");
        }
    }

    /** Parses and validates a date that falls between today and 14 days ahead. */
    private LocalDate readBestBeforeDate() {
        while (true) {
            String input = readLine("Enter best-before date (YYYY-MM-DD, within 14 days): ");
            if (input == null) {
                return null;
            }
            try {
                LocalDate bestBefore = LocalDate.parse(input.trim());
                FoodItemFactory.validateBestBefore(bestBefore, clock);
                return bestBefore;
            } catch (DateTimeParseException exception) {
                System.out.println("Invalid date format. Use YYYY-MM-DD.");
            } catch (IllegalArgumentException exception) {
                System.out.println(exception.getMessage());
            }
        }
    }

    /** Removes and reports the next item, displaying a clear message if empty. */
    private void removeFoodItem() {
        try {
            FoodItem removed = storage.removeNext(removalMode);
            System.out.println("Removed: " + removed);
        } catch (NoSuchElementException exception) {
            System.out.println(exception.getMessage());
        }
    }

    /** Prints all stored records and identifies the next item for removal. */
    private void displayStorage() {
        if (storage.isEmpty()) {
            System.out.println("Storage is empty.");
            return;
        }

        System.out.println("Items from loading side to opposite side:");
        List<FoodItem> items = storage.getItemsFromLoadingSide();
        for (int i = 0; i < items.size(); i++) {
            System.out.println((i + 1) + ". " + items.get(i));
        }
        System.out.println("Top / next to be removed (" + removalMode + "): "
                + storage.peekNext(removalMode).getName());
    }

    /** Shows the next item without changing the storage contents. */
    private void peekFoodItem() {
        try {
            System.out.println("Top / next to be removed (" + removalMode + "): "
                    + storage.peekNext(removalMode));
        } catch (NoSuchElementException exception) {
            System.out.println(exception.getMessage());
        }
    }

    /** Searches by food name and reports either all matches or no matches. */
    private void searchFoodItems() {
        String name = readLine("Enter food name to search: ");
        if (name == null) {
            return;
        }
        if (name.trim().isEmpty()) {
            System.out.println("Search name must not be empty.");
            return;
        }

        List<FoodItem> matches = storage.searchByName(name);
        if (matches.isEmpty()) {
            System.out.println("No stored items named \"" + name.trim() + "\".");
            return;
        }
        System.out.println("Found " + matches.size() + " matching item(s):");
        for (FoodItem item : matches) {
            System.out.println(item);
        }
    }

    /** Switches between same-side LIFO and opposite-side FIFO removal. */
    private void changeRemovalMode() {
        while (true) {
            String choice = readLine("Enter L for same-side removal (LIFO), "
                    + "or F for opposite-side removal (FIFO): ");
            if (choice == null) {
                return;
            }
            if (choice.trim().equalsIgnoreCase("L")) {
                removalMode = BoundedFoodStorage.RemovalMode.LIFO;
                System.out.println("Removal mode set to LIFO (same side).");
                return;
            }
            if (choice.trim().equalsIgnoreCase("F")) {
                removalMode = BoundedFoodStorage.RemovalMode.FIFO;
                System.out.println("Removal mode set to FIFO (opposite side).");
                return;
            }
            System.out.println("Enter L or F.");
        }
    }

    /** Reads and parses an integer, retrying on non-numeric input. */
    private Integer readInteger(String prompt) {
        while (true) {
            String input = readLine(prompt);
            if (input == null) {
                return null;
            }
            try {
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException exception) {
                System.out.println("Enter a whole number.");
            }
        }
    }

    /** Prints a prompt and returns the next line, or null when input has ended. */
    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }
}
