# Party Food Storage (Java)

## Step 1: Choose the data structure

The storage is a bounded, array-based double-ended queue (deque) with a capacity
of **8**. The assignment mentions 10 trays in its scenario but later specifies
a maximum capacity of 8; this implementation follows the specific maximum
capacity requirement.

Items are added at the loading side. Select either removal policy:

- **LIFO (stack):** remove from the loading side, the same side used for adding.
- **FIFO (queue):** remove from the opposite side.

The circular array avoids shifting items. Food records are immutable objects.
`FoodItem` is the parent class; `Burger`, `Pizza`, `Fries`, `Sandwich`, and
`Hotdog` are its subclasses.

## Step 2: Open and run the project

Open this folder in NetBeans as a Maven project; NetBeans detects the
`pom.xml`. The Java sources are under `src\FoodStorageApp` and the test harness
is under `test\ca1`. Select `ca1.FoodStorageApp` as the main class if NetBeans
prompts for one.

From this directory, build with Maven and run with a JDK:

```text
mvn compile exec:java
```

The console menu supports adding, removing, displaying, peeking, searching,
changing removal mode, and exiting. Enter dates as `YYYY-MM-DD`. Weight must be
a positive whole number of grams; best-before dates must be today through 14
days from today. Placement time is recorded automatically.

## Step 3: Verify the data structure

Run the dependency-free test harness after building:

```text
mvn test
java -cp "target\classes;target\test-classes" ca1.BoundedFoodStorageTest
```

The test harness is a standalone `main` class, so run its Java command to
execute the checks; `mvn test` compiles it.

The tests cover both removal policies, capacity and overflow, date and weight
validation, case-insensitive search, circular wraparound, placement time, and
empty-storage errors.

## Operation complexity

Let `n` be the number of stored items (`0 <= n <= 8`).

| Operation | Time | Reason |
|---|---:|---|
| Add | O(1) | Write one slot in the circular array |
| Remove | O(1) | Read and clear one end slot |
| Peek | O(1) | Read the next-to-remove end slot |
| Search | O(n) | Compare each stored name in the worst case |
| Display | O(n) | Visit and print each stored item |

The array occupies O(8) = O(1) space for this fixed-capacity application.
Search results and display ordering use O(n) temporary space.

## Manual validation ideas

1. Try removing, peeking, and displaying before adding anything.
2. Add a food with weight `0`, a non-number weight, or a malformed date.
3. Try a date before today and one 15 days from today; then use today and the
   date 14 days from today.
4. Add 8 items and try adding a ninth.
5. Add two different foods, compare LIFO with FIFO peeking/removal, and search
   for a name using different letter casing.

For the presentation, explain the chosen capacity discrepancy, array/deque
operations, validation, test results, and complexity table in your own words.
Create the poster and record your own 4–5 minute voice explanation; do not
represent generated commits, recordings, or links as your own work.
