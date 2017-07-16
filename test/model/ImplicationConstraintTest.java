package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ImplicationConstraintTest {
    private Variable<String> x;
    private Variable<String> y;

    @BeforeEach
    void setUp() {
        List<String> xDomain = Arrays.asList("1", "2", "3", "4");
        List<String> yDomain = Arrays.asList("a", "b", "c");
        x = new Variable<>("x", new HashSet<>(xDomain), String.class);
        y = new Variable<>("y", new HashSet<>(yDomain), String.class);
    }

    @Test
    void test1() {
        Constraint c = new ImplicationConstraint<>(x, v -> v.equals("1"), y, w -> !w.equals("a"));
        assertTrue(c.satisfied());
        x.assign("2");
        assertTrue(c.satisfied());
    }

    @Test
    void test2() throws InconsistencyException {
        Constraint c = new ImplicationConstraint<>(x, v -> v.equals("1"), y, w -> !w.equals("a"));
        x.assign("1");
        assertFalse(c.satisfied());
        c.propagate();
        assertTrue(c.satisfied());
    }

    @Test
    void test3() {
        Constraint c = new ImplicationConstraint<>(x, v -> v.equals("1"), y, w -> w.equals("a"));
        x.assign("1");
        y.assign("a");
        assertThrows(InconsistencyException.class, c::propagate);
    }

    @Test
    void test4() {
        Constraint c = new ImplicationConstraint<>(x, v -> v.equals("1"), y, w -> w.equals("a"));
        x.assign("1");
        y.assign("a");
        assertThrows(InconsistencyException.class, c::propagate);
    }
}