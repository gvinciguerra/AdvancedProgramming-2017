package model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class Variable<E> {
    private final String name;
    private final Set<E> domain;
    private final Set<E> currentDomain;

    public Variable(String name, Set<E> domain) {
        this.name = name;
        this.domain = new HashSet<>(domain);
        this.currentDomain = new HashSet<>(domain);
    }

    public boolean contains(E o) {
        return currentDomain.contains(o);
    }

    public boolean removeIf(Predicate<E> filter) {
        return currentDomain.removeIf(filter);
    }

    public boolean isEmpty() {
        return currentDomain.isEmpty();
    }

    public E getValue() {
        return isAssigned() ? currentDomain.stream().findAny().get() : null;
    }

    public boolean assign(E o) {
        return currentDomain.retainAll(Collections.singleton(o));
    }

    public Set<E> getDomain() {
        return domain;
    }

    public Set<E> getCurrentDomain() {
        return currentDomain;
    }

    public boolean isAssigned() {
        return currentDomain.size() == 1;
    }

    public String getName() {
        return name;
    }
}
