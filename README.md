# Restaurant Order Management System (ROMS)

Java console application for restaurant order, kitchen, payment, and table management.

Team Project 7 - CEWP MOD4 Java Programming, Concordia University, Fall 2026.

## Team

| Member  | GitHub                                         |
|---------|------------------------------------------------|
| Romain  | [@Buffaloexe](https://github.com/Buffaloexe)   |
| Tiffany | [@tiffvgch](https://github.com/tiffvgch)       |
| Abigaïl | [@abi-mlrt](https://github.com/abi-mlrt)       |
| Eliot   | [@Tezay](https://github.com/Tezay)             |

## Requirements

- JDK 21 or later
- Maven 3.9 or later (bundled with IntelliJ IDEA; VS Code users need the Extension Pack for Java)

## Build and test

```bash
mvn verify
```

## Run

```bash
mvn compile
java -cp target/classes app.RestaurantApp
```

Start it from the project root: the data paths are relative to the folder where
the program starts. In IntelliJ, open `RestaurantApp` and run its `main`.

## Data files

The program reads `menu_items.txt`, `tables.txt` and `orders.txt` from
`data/seed/` when it starts, and writes them again in `data/runtime/` when you
exit, so the seed files never change. A bad line is skipped and the reason is
printed. The formats are in [Analysis](docs/analysis.md).

## Assumptions

- Money uses `BigDecimal`: tax 14.975%, tip 0, 15, 18 or 20% of the subtotal.
- A table has at most one active order.
- The card payment is simulated.
- One preparation minute lasts 100 ms, so a demo stays short.

## Known limitations

- The kitchen prepares one order at a time.
- One user, no login.
- The reports are printed, not saved.
- The menu and the tables change in the files only, not from the console.
- Nothing is saved if you stop the program without the exit option.

## Design

- [Analysis](docs/analysis.md)
- [Class diagram and packages](docs/design/class-diagram.md)
- [Sequence diagram](docs/design/sequence-diagram.svg)
- [Concurrency view](docs/design/concurrency-view.md)

## Workflow

- `main` only changes through pull requests, merged with squash and one approval.
- Branches: `<type>/<issue>-<short-description>` (example: `feat/12-order-lifecycle`).
- Pull request titles follow [Conventional Commits](https://www.conventionalcommits.org/).
- [Who did what](docs/contributions.md)
- [Responsible AI record](docs/responsible-ai.md)
