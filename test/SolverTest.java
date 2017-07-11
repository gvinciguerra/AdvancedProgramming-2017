import model.Constraint;
import model.ImplicationConstraint;
import model.Variable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SolverTest {
    private Variable<String> x;
    private Variable<String> y;

    @BeforeEach
    void setUp() {
        List<String> xDomain = Arrays.asList("1", "2", "3", "4");
        List<String> yDomain = Arrays.asList("a", "b");
        x = new Variable<>("x", new HashSet<>(xDomain));
        y = new Variable<>("y", new HashSet<>(yDomain));
    }

    @Test
    void backtrackingSearch1() {
        Constraint c = new ImplicationConstraint<>(x, v -> v.equals("1"), y, w -> w.equals("a"));
        Solver solver = new Solver(Arrays.asList(x, y), Collections.singletonList(c));
        List<Variable> result = solver.backtrackingSearch();
        assertNotNull(result);
        result.forEach(v -> assertTrue(v.isAssigned()));
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
}