package model;

import java.util.Set;

public interface Constraint {
    Set<Variable> propagate() throws InconsistencyException;

    boolean satisfied();

    Set<Variable> getVariables();
}
