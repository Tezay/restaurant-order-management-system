# Testing and debugging

## Tests

| Test | Situation | Expected result | Actual result |
|---|---|---|---|
| Valid order | a free table and available items | correct lines, correct total, correct states | as expected (`RestaurantOrderTest`) |
| Invalid item | an unknown or unavailable item | RestaurantException, the order does not change | as expected (`OrderServiceTest`) |
| Table already busy | a second order on the same table | refused | as expected (`OrderServiceTest`) |
| Ordering | two items with the same name, or the same price | the id decides the order | as expected (`MenuItemComparatorTest`) |
| Payment state | pay before READY, or pay twice | refused, nothing changes | as expected (`OrderServiceTest`) |
| Kitchen | several lines to prepare, plus an interruption | the exact number of finished lines, every cook stops | as expected (`KitchenTest`, `KitchenServiceTest`) |

## Concurrency

Runs of the kitchen test, with new threads each time, result read after the join:

- `KitchenTest`, 10 runs: always 3 finished lines, and after an interrupt every cook stopped.
- `KitchenServiceTest`, 10 runs: the order is READY after waitUntilDone, and back to CONFIRMED after close.

## Defects

### Defect 1

- Area: threads and lifecycle
- Symptom: after the kitchen closed, an order stayed in PREPARING for ever.
- Hypothesis: OrderStatus does not allow PREPARING -> CONFIRMED.
- Evidence: OrderStatusTest failed, expected true but was false.
- Root cause: the PREPARING case allowed only READY.
- Correction: allow READY and CONFIRMED.
- Regression test: OrderStatusTest, "Allowed changes".

### Defect 2

- Area: collections
- Symptom: the same item added twice gave the quantity 1, not 2.
- Hypothesis: the map keeps the new quantity instead of adding it to the old one.
- Evidence: two poutines gave a subtotal of 12.95, not 25.90.
- Root cause: items.put replaces the value when the key is already there.
- Correction: read the old quantity and put the sum.
- Regression test: RestaurantOrderTest, "The same item added twice gives one line
  with a bigger quantity".

### Defect 3

- Area: reading the data files
- Symptom: the program stopped with an ArrayIndexOutOfBoundsException at the start.
- Hypothesis: a line has fewer fields than the code reads.
- Evidence: the exception came from fields[6] in parseMenuItem.
- Root cause: the check accepted 6 fields, but 8 were read. The format was not the
  one written in analysis.md.
- Correction: refuse a line that does not have 8 fields, and use the format of
  analysis.md.
- Regression test: FileStorageTest, "Each kind of bad line is skipped and reported".

## Remaining risks

- The preparation time is a sleep, so a slow machine makes the demo longer.
- We only tested small data files.
