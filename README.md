# AdvancedProgramming-2017
A [Constraint Satisfaction Problem](https://en.wikipedia.org/wiki/Constraint_satisfaction_problem) solver in Java with:
- a recursive descent parser for a [DSL](https://en.wikipedia.org/wiki/Domain-specific_language) that allows defining variables and constraints;
- a backtracking search algorithm;
- a solutions+explanations iterator with forward checking.

Final term for an Advanced Programming course ([assignment text here](http://didawiki.di.unipi.it/lib/exe/fetch.php/magistraleinformatica/pa/termfinal17-08.pdf)).

# Usage
## With the DSL
```java
String input = "x = { x1, x2, x3 }\n"
        + "y = { y1, y2, y3 }\n"
        + "z = { z1, z2, z3 }\n"
        + "{ (y1, z1), (y2, z2), (y3, z3) }\n"
        + "!{ (x1, y2), (x1, y3), (x2, y1), (x2, y3), (x3, y1), (x3, y2) }";
Lexer lexer = new Lexer(new StringReader(input));
Parser parser = new Parser(lexer);
parser.parse();
Solver solver = new Solver(parser.getVariables(), parser.getConstraints());
solver.solutionsIterator().forEachRemaining(System.out::println);
```

## Without the DSL
```java
List<String> yDomain = Arrays.asList("a", "b");
List<Integer> zDomain = Arrays.asList(-2, -1, 0, 1, 2);
Variable<String> y = new Variable<>("y", new HashSet<>(yDomain), String.class);
Variable<Integer> z = new Variable<>("z", new HashSet<>(zDomain), Integer.class);
Constraint c1 = new ImplicationConstraint<>(y, v -> v.equals("a"), z, w -> w <= 0);
Solver solver = new Solver(Arrays.asList(y, z), Collections.singletonList(c1));
solver.solutionsIterator().forEachRemaining(System.out::println);
```
