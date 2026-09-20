# Module evidence

| Module | Topic | Class and method | Test | Where we explain it |
|---|---|---|---|---|
| 1 | OOP review and Scanner | `MenuItem`, `FoodItem`, `Beverage`: private fields and getters | `MenuItemTest` | |
| 2 | Inner classes and exceptions | `RestaurantException`, thrown by `CashPayment.pay` and `CardPayment.pay` | `PaymentTest` | |
| 3 | Comparable and Comparator | `MenuItem.compareTo`, `MenuItemComparators.byPrice` and `byName` | `MenuItemTest`, `MenuItemComparatorTest` | |
| 4 | Collections Framework | `Repository.items`: a `Map` by id, `KitchenBoard.pending`: a `Queue` of the lines to prepare | `RepositoryMenuTest`, `KitchenTest` | |
| 5 | Iterable and Iterator | `Menu` implements `Iterable<MenuItem>`, `Menu.iterator` | `RepositoryMenuTest` | |
| 6 | Generics and wildcards | `Repository<T extends Identifiable>`, `Menu extends Repository<MenuItem>` | `RepositoryMenuTest` | |
| 7 | Lambdas and Stream API | | | |
| 8 | Reflection and annotations | | | |
| 9 | Concurrency | `Cook.run` (Runnable), `KitchenBoard` (synchronized), `KitchenService.start`, `waitUntilDone` and `close` (start, join, interrupt) | `KitchenTest`, `KitchenServiceTest` | |
