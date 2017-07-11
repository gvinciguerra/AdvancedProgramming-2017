package model;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;

public class Variable<E> {
    private final String name;
    private final Set<E> domain;
    private Set<E> currentDomain;

    public Variable(String name, Set<E> domain) {
        this.name = name;
        this.domain = new HashSet<>(domain);
        this.currentDomain = new LinkedHashSet<>(domain);
    }

    public boolean contains(E o) {
        return currentDomain.contains(o);
    }

    public boolean removeIf(Predicate<E> filter) {
        return currentDomain.removeIf(filter);
    }

    public E getValue() {
        return isAssigned() ? currentDomain.stream().findAny().get() : null;
    }

    public boolean assign(E o) {
        if (getValue() == null && o != null)
            currentDomain.retainAll(Collections.singleton(o));
        return getValue().equals(o);
    }

    public Set<E> getDomain() {
        return domain;
    }

    public Set<E> getCurrentDomain() {
        return currentDomain;
    }

    public void setCurrentDomain(Set<E> currentDomain) {
        assert domain.containsAll(currentDomain);
        this.currentDomain = new HashSet<>(currentDomain);
    }

    public boolean isAssigned() {
        return currentDomain.size() == 1;
    }

    public String getName() {
        return name;
    }
}
