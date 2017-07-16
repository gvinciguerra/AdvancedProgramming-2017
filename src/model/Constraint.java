package model;

import java.util.Map;
import java.util.Set;

public interface Constraint {
    Map<Variable, Set> propagate() throws InconsistencyException;

    boolean satisfied();

    Set<Variable> getVariables();

    Set<Variable> getTriggerVariables();
}
