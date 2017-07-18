package model;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;

public class Variable<E> {
    private E value;
    private final String name;
    private final Set<E> domain;
    private final Class<E> type;
    private Set<E> currentDomain;

    public Variable(String name, Set<E> domain, Class<E> type) {
        this.type = type;
        this.name = name;
        this.domain = Collections.unmodifiableSet(new HashSet<>(domain));
        this.currentDomain = new LinkedHashSet<>(domain);
    }

    public boolean removeIf(Predicate<E> filter) {
        if (isAssigned() && filter.test(value)) {
            currentDomain = Collections.emptySet();
            value = null;
            return true;
        }
        currentDomain.removeIf(filter);
        return currentDomain.isEmpty();
    }

    public E getValue() {
        return value;
    }

    public boolean assign(E o) {
        if (isAssigned() && !value.equals(o) || !currentDomain.contains(o))
            return false;
        value = o;
        return true;
    }

    public void unassign() {
        this.value = null;
    }

    public Set<E> getDomain() {
        return domain;
    }

    public Set<E> getCurrentDomain() {
        return currentDomain;
    }

    public void setCurrentDomain(Set<E> currentDomain) {
        assert domain.containsAll(currentDomain);
        this.currentDomain = new LinkedHashSet<>(currentDomain);
        if (!currentDomain.contains(this.value))
            this.value = null;
    }

    public boolean isAssigned() {
        return value != null;
    }

    public String getName() {
        return name;
    }

    public Class<E> getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.format("%s = %s", this.getName(), this.getValue());
    }
}
