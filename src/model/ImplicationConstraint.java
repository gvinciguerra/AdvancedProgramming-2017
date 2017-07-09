package model;

import java.util.function.Predicate;

public class ImplicationConstraint<L, R> implements Constraint {
    private final Predicate<L> premise;
    private final Predicate<R> filter;
    private final Variable<L> lvariable;
    private final Variable<R> rvariable;

    public ImplicationConstraint(Variable<L> lvar, Predicate<L> premise, Variable<R> rvar, Predicate<R> filter) {
        this.lvariable = lvar;
        this.rvariable = rvar;
        this.filter = filter;
        this.premise = premise;
    }

    @Override
    public void propagate() throws InconsistencyException {
        if (lvariable.isAssigned()
                && premise.test(lvariable.getValue())
                && rvariable.removeIf(filter) && rvariable.isEmpty())
            throw new InconsistencyException();
    }

    @Override
    public boolean satisfied() {
        return !lvariable.isAssigned()
                || !premise.test(lvariable.getValue())
                || rvariable.getCurrentDomain().stream().allMatch(filter.negate());
    }
}
