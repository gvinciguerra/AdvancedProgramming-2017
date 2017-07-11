package model;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class ImplicationConstraint<L, R> implements Constraint {
    private final Predicate<L> premise;
    private final Predicate<R> filter;
    private final Variable<L> lvariable;
    private final Variable<R> rvariable;
    private final Set<Variable> variables;

    public ImplicationConstraint(Variable<L> lvar, Predicate<L> premise, Variable<R> rvar, Predicate<R> filter) {
        this.lvariable = lvar;
        this.rvariable = rvar;
        this.filter = filter;
        this.premise = premise;
        this.variables = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(lvariable, rvariable)));
    }

    @Override
    public Set<Variable> propagate() throws InconsistencyException {
        if (lvariable.isAssigned()
                && premise.test(lvariable.getValue())
                && rvariable.removeIf(filter) && rvariable.getCurrentDomain().isEmpty())
            throw new InconsistencyException();
        return Collections.singleton(rvariable);
    }

    @Override
    public boolean satisfied() {
        return !lvariable.isAssigned()
                || !premise.test(lvariable.getValue())
                || rvariable.getCurrentDomain().stream().allMatch(filter.negate());
    }

    @Override
    public Set<Variable> getVariables() {
        return this.variables;
    }
}
