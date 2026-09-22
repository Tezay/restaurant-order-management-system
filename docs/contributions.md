# Contributions

Every change reached `main` through a pull request, reviewed and approved by
another member. The pull request numbers below are the proof.

## Who did what

| Member  | Work                                                                              | Pull requests                                       |
|---------|-----------------------------------------------------------------------------------|-----------------------------------------------------|
| Romain  | Menu items, order and tables, `Repository` and `Menu`, report service             | #15, #27, #31, #37                                  |
| Tiffany | Menu comparators and their tests, `OrderService`, console menu built by reflection | #26, #28, #32, #39                                  |
| Abigaïl | Cash and card payment, reading and writing the data files                          | #17, #38                                            |
| Eliot   | Repository and CI setup, documents and diagrams, order status and tip rate, kitchen threads, application wiring | #1, #16, #18, #29, #30, #40 to #43 |

## Integration work

- Eliot set up the repository (#1): `main` protected, squash merge, one approval,
  branch names, and the CI that runs `mvn verify` on every pull request.
- We decided the analysis, the class diagram, the empty classes and the seed data
  together at the start (#6 to #9), so the four of us could then write our parts
  at the same time without waiting for each other.
- Eliot wired the parts in `RestaurantApp` (#40) and fixed what the integration
  showed (#42).
