package model;

public interface Constraint {
    void propagate() throws InconsistencyException;
    boolean satisfied();
}
