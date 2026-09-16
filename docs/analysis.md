# Analysis

## What we build

A console application for a restaurant. Staff load the menu and the tables from
files, open an order on a table, add items, confirm the order, let the kitchen
prepare it, take the payment, close the table, print reports and save the data.

## What we do not build

No window, no database, no real payment, no network.

## Actors

- Server: opens an order, adds items, confirms it, takes the payment, closes the table.
- Kitchen worker: prepares the confirmed lines. Each worker = a thread.
- Manager: prints the reports.

## Main entities

- MenuItem, with FoodItem and Beverage: one dish or one drink.
- Menu: all the menu items.
- DiningTable: one table, with a number and a capacity.
- RestaurantOrder: one order on one table. It holds its OrderLine objects.
- CashPayment and CardPayment: the two ways to pay.

## Services

- OrderService: opens, changes, confirms, pays and closes an order.
- KitchenService: gives the confirmed lines to the cooks and waits for them.
- ReportService: builds the reports.
- Repository: keeps the objects and finds them by id.

## Order states

open -> confirmed -> preparing -> ready -> paid -> closed

An open order can also be cancelled.
If the kitchen closes before all lines are ready, the order goes back from preparing to confirmed (can be sent again later).
Items can be added or removed only when the order is open. Other changes are refused.

## Rules that must always be true

| Rule | Class that keeps it |
|---|---|
| Two objects never have the same id | Repository |
| An operation that fails changes nothing | OrderService |
| An order changes state only if the new state is allowed | OrderStatus |
| A total is always computed from the lines, never stored in a field | RestaurantOrder |
| A table has at most one active order | OrderService (a Set of the table ids that have an active order) |

## Money

- We use BigDecimal, with 2 decimals (with HALF_UP), never double.
- Tax: one rate of 14.975% (GST 5% + QST 9.975%).
- Tip: the server picks 0%, 15%, 18% or 20%.
- The tip is computed on the subtotal, before tax.
- Total = subtotal + tax + tip.

## IDs

- Menu items: `M001`. Tables: `T01`. Orders: `O0001`.
- When we load the orders file, we keep the biggest order id read.
  The next new order starts from there so IDs stay unique after a restart.

## Files

- We read `data/seed/menu_items.txt`, `tables.txt` and `orders.txt` when the program starts.
- We write the new data in `data/runtime/`, so the seed files never change.
- One line per object and fields separated by ";".
- A bad line is skipped. At the end of the loading, the program prints how many
  lines were skipped, with the file name, the line number and the reason.

### menu_items.txt

`id;type;name;price;category;available;field7;field8`

The type is FOOD or DRINK. The last two fields depend on the type:

- FOOD: preparation time in minutes, then one dietary tag. The tag is NONE when the dish has none.
- DRINK: volume in millilitres, then true if the drink has alcohol.

```
M001;FOOD;Poutine;12.50;MAIN;true;8;NONE
M002;FOOD;Green salad;7.90;STARTER;false;3;VEGAN
M004;DRINK;Sparkling water;3.50;DRINK;true;500;false
M005;DRINK;Beer;8.00;DRINK;true;330;true
```

### tables.txt

`id;capacity`

```
T01;2
T02;4
T03;6
```

### orders.txt

`id;tableId;status;tipPercent;lines`

One order per line. The lines field holds the ordered items, separated by "|".
Each one is an item id, then "*" then the quantity. Empty when the order has no item.

```
O0001;T01;OPEN;0;M001*2|M004*1
O0002;T02;PAID;15;M002*1|M005*2
O0003;T03;OPEN;0;
```

## Order rules

- One active order per table (using a Set for the ids of the tables that have an active order).
- The same item added twice gives one line with a bigger quantity.
- An item can be ordered only if it is available (true/false flag).

## Payment

- Cash: the server types the amount given. The program refuses if it is too small, and gives the change.
- Card: simulated. The card number must have 16 digits, or the payment is refused.

## Kitchen

- The kitchen prepares one order at a time.
- 2 cooks. Each cook = a thread. They take the confirmed lines one by one.
- The preparation time will be simulated with a sleep.
- A console option closes the kitchen: it interrupts the cooks, waits for them and the order goes back to confirmed.

## Data shared between threads

The two cooks share one KitchenBoard: the line queue and the number of finished lines.
All access uses synchronized methods.

Only the main thread changes the order status, before the cooks start and after they join.

## Reports

- Total revenue.
- Sales by category.
- Available items filtered by a dietary tag.

## Console

The console menu is built from the methods marked with our own annotation, @MenuOption.

## Risks

- The user types wrong data and the program is not ready for it.
- We use double for money and the totals are wrong.
- Someone keeps a branch open too long and we get conflicts.

## Definition of done

An issue is done when:
- the code compiles
- the tests pass
- the CI is green
- someone else reviewed the pull request
- the module evidence table is updated if the task covers a module.
