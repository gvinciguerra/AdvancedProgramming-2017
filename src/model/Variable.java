package model;

import java.util.*;
import java.util.function.Predicate;

public class Variable<E> {
    private E value;
    private final String name;
    private final Set<E> domain;
    private Set<E> currentDomain;
    private final Class<E> type;


    public Variable(String name, Set<E> domain, Class<E> type) {
        this.name = name;
        this.domain = Collections.unmodifiableSet(new HashSet<>(domain));
        this.currentDomain = new LinkedHashSet<>(domain);
        this.type = type;
    }

    public boolean removeIf(Predicate<E> filter) {
        if (isAssigned() && filter.test(value)) {
            currentDomain = Collections.emptySet();
            value = null;
            return true;
        }
        return currentDomain.removeIf(filter);
    }

    public E getValue() {
        return value;
    }

    public boolean assign(E o) {
        if (currentDomain.contains(o))
            this.value = o;
        return value.equals(o);
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
}
