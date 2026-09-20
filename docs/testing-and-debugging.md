# Testing and debugging

## Tests

| Test | Situation | Expected result | Actual result |
|---|---|---|---|
| Valid order | a free table and available items | correct lines, correct total, correct states | |
| Invalid item | an unknown or unavailable item | RestaurantException, the order does not change | |
| Table already busy | a second order on the same table | refused | |
| Ordering | two items with the same name, or the same price | the id decides the order | as expected (`MenuItemComparatorTest`) |
| Payment state | pay before READY, or pay twice | refused, nothing changes | |
| Kitchen | several lines to prepare, plus an interruption | the exact number of finished lines, every cook stops | as expected (`KitchenTest`, `KitchenServiceTest`) |

## Concurrency

Runs of the kitchen test, with new threads each time, result read after the join:

- `KitchenTest`, 10 runs: always 3 finished lines, and after an interrupt every cook stopped.
- `KitchenServiceTest`, 10 runs: the order is READY after waitUntilDone, and back to CONFIRMED after close.

## Defects

### Defect 1

- Area: threads and lifecycle
- Symptom: an order could not go back from PREPARING to CONFIRMED. After the kitchen
  closed, an order would stay in PREPARING for ever.
- Hypothesis: the allowed changes in OrderStatus do not match analysis.md.
- Evidence: OrderStatusTest failed with "expected: <true> but was: <false>".
- Root cause: the case PREPARING only allowed READY.
- Correction: case PREPARING -> next == READY || next == CONFIRMED.
- Regression test: OrderStatusTest, "Allowed changes".

### Defect 2

- Area:
- Symptom:
- Hypothesis:
- Evidence:
- Root cause:
- Correction:
- Regression test:

### Defect 3

- Area:
- Symptom:
- Hypothesis:
- Evidence:
- Root cause:
- Correction:
- Regression test:

## Remaining risks

- The kitchen threads are not written yet.
- No test reads a real data file yet.
