import model.Constraint;
import model.ImplicationConstraint;
import model.Variable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import parser.Lexer;
import parser.Parser;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SolverTest {
    private Variable<String> x;
    private Variable<String> y;
    private Variable<Integer> z;

    @BeforeEach
    void setUp() {
        List<String> xDomain = Arrays.asList("1", "2", "3", "4");
        List<String> yDomain = Arrays.asList("a", "b");
        List<Integer> zDomain = Arrays.asList(-2, -1, 0, 1, 2);
        x = new Variable<>("x", new HashSet<>(xDomain), String.class);
        y = new Variable<>("y", new HashSet<>(yDomain), String.class);
        z = new Variable<>("z", new HashSet<>(zDomain), Integer.class);
    }

    @Test
    void backtrackingSearch1() {
        Constraint c = new ImplicationConstraint<>(x, v -> v.equals("1"), y, w -> w.equals("a"));
        Solver solver = new Solver(Arrays.asList(x, y), Collections.singletonList(c));
        Solution solution = solver.backtrackingSearch();
        assertNotNull(solution);
        assertTrue(x.getDomain().contains(solution.getValue(x)));
        assertTrue(y.getDomain().contains(solution.getValue(y)));
    }

    @Test
    void backtrackingSearch2() {
        Constraint c1 = new ImplicationConstraint<>(y, v -> v.equals("a"), x, w -> !w.equals("1"));
        Constraint c2 = new ImplicationConstraint<>(y, v -> v.equals("b"), x, w -> !w.equals("1"));
        Constraint c3 = new ImplicationConstraint<>(x, v -> v.equals("1"), y, w -> !w.equals("a"));
        Constraint c4 = new ImplicationConstraint<>(x, v -> v.equals("1"), y, w -> !w.equals("b"));
        Solver solver = new Solver(Arrays.asList(x, y), Arrays.asList(c1, c2, c3, c4));
        assertNull(solver.backtrackingSearch());
    }

    @Test
    void solutionsIterator1() {
        Constraint c1 = new ImplicationConstraint<>(y, v -> v.equals("a"), z, w -> w <= 0);
        Solver solver = new Solver(Arrays.asList(y, z), Collections.singletonList(c1));
        int c = 0;
        Iterator<Solution> iterator = solver.solutionsIterator();
        while (iterator.hasNext()) {
            Solution solution = iterator.next();
            assertTrue(y.getDomain().contains(solution.getValue(y)));
            assertTrue(z.getDomain().contains(solution.getValue(z)));
            c++;
        }
        assertEquals(7, c);
    }

    @Test
    void solutionsIterator2() {
        Constraint c1 = new ImplicationConstraint<>(z, z -> false, z, z -> true); // useless
        Solver solver = new Solver(Arrays.asList(y, z), Collections.singletonList(c1));
        int c = 0;
        for (Iterator<Solution> iterator = solver.solutionsIterator(); iterator.hasNext(); c++)
            iterator.next();
        assertEquals(y.getDomain().size() * z.getDomain().size(), c);
    }

    @Test
    void solutionsIterator3() {
        Constraint c1 = new ImplicationConstraint<>(z, z -> true, z, z -> true);
        Solver solver = new Solver(Arrays.asList(z, z), Collections.singletonList(c1));
        Iterator<Solution> iterator = solver.solutionsIterator();
        assertFalse(iterator.hasNext());
    }

    @Test
    void solutionsIterator4() {
        Constraint c1 = new ImplicationConstraint<>(x, x -> x.equals("1"), y, y -> y.equals("b"));
        Constraint c2 = new ImplicationConstraint<>(y, y -> y.equals("b"), z, z -> z.equals(1));
        Constraint c3 = new ImplicationConstraint<>(x, x -> true, z, z -> !z.equals(1));
        Constraint c4 = new ImplicationConstraint<>(x, x -> true, x, x -> !x.equals("1"));
        Constraint c5 = new ImplicationConstraint<>(x, x -> true, y, y -> false); // useless
        List<Constraint> constraints = Arrays.asList(c1, c2, c3, c4, c5);
        Solver solver = new Solver(Arrays.asList(x, y, z), constraints);
        Iterator<Solution> iterator = solver.solutionsIterator();
        Solution solution = iterator.next();
        assertEquals("1", solution.getValue(x));
        assertEquals("a", solution.getValue(y));
        assertEquals(new Integer(1), solution.getValue(z));
        assertFalse(iterator.hasNext());
        assertTrue(solution.getExplanation().size() < constraints.size()); // 1 is useless
    }

    @Test
    void solutionsIterator5() {
        Constraint c1 = new ImplicationConstraint<>(x, x -> false, y, y -> true);
        Constraint c2 = new ImplicationConstraint<>(y, y -> y.equals("∞"), x, x -> false);
        Solver solver = new Solver(Arrays.asList(x, y), Arrays.asList(c1, c2));
        int c = 0;
        for (Iterator<Solution> iterator = solver.solutionsIterator(); iterator.hasNext(); c++)
            assertEquals(0, iterator.next().getExplanation().size()); // both constraints are useless
        assertEquals(x.getDomain().size() * y.getDomain().size(), c);
    }

    @Test
    void solutionsIterator6() throws IOException, ParseException {
        String problem = "x11 = {a1, a2, a3, a4}\n"
                + "x12 = {b1, b2, b3, b4}\n"
                + "x21 = {c1, c2, c3, c4}\n"
                + "x22 = {d1, d2, d3, d4}\n"
                + "{}\n!{"
                + "(a1, b1), (a1, c1), (a2, b2), (a2, c2), (a3, b3), (a3, c3), (a4, b4), (a4, c4),"
                + "(b1, a1), (b1, d1), (b2, a2), (b2, d2), (b3, a3), (b3, d3), (b4, a4), (b4, d4),"
                + "(c1, a1), (c1, d1), (c2, a2), (c2, d2), (c3, a3), (c3, d3), (c4, a4), (c4, d4),"
                + "(d1, b1), (d1, c1), (d2, b2), (d2, c2), (d3, b3), (d3, c3), (d4, b4), (d4, c4)}";
        Parser parser = new Parser(new Lexer(new StringReader(problem)));
        parser.parse();
        Solver solver = new Solver(parser.getVariables(), parser.getConstraints());
        int c = 0;
        for (Iterator<Solution> iterator = solver.solutionsIterator(); iterator.hasNext(); c++)
            assertTrue(iterator.next().getExplanation().size() > 0);
        assertEquals(84, c);
    }
}