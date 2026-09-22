# Responsible AI record

We used AI to learn notions that were new for us, and to check our work. Every
suggestion we kept was changed for our project, compiled, tested and reviewed in
a pull request.

| # | Purpose | Prompt or question | Output decision | Validation | Attribution |
|---|---------|--------------------|-----------------|------------|-------------|
| 1 | Learn a new notion | How do lambdas, method references and threads work? | Kept as an explanation only | We wrote our own small examples and ran them | Whole team |
| 2 | Write tests | How do we write a JUnit test, and how do we test threads? | Kept and changed | `mvn verify`, 77 tests pass | Whole team, in every pull request |
| 3 | Set up Maven | How do we write a pom.xml for JUnit and Java 21? | Kept and changed | `mvn verify` runs, the CI is green | Eliot, PR #1 |
| 4 | Improve our English | Is this sentence in `docs/` clear and simple? | Kept and changed | Read again by the team | Eliot, the documentation pull requests |
| 5 | Check the UML | Does the class diagram match the real classes? | Kept as a list of differences | We compared each class by hand, then fixed the diagram | Eliot, PR #7 and PR #41 |
| 6 | Draft some code | Show a first version of this class, or find why this test fails | Kept and changed | `mvn verify`, plus one review by another member | The pull requests where the code landed |
