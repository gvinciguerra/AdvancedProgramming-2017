import model.Constraint;
import model.Variable;

import java.util.*;

public class Solution {
    private final Map<Variable<?>, Object> solution = new LinkedHashMap<>();
    private final Collection<Constraint> explanation;

    public Solution(Collection<Variable<?>> variables) {
        this.explanation = Collections.emptyList();
        variables.forEach(v -> solution.put(v, v.getValue()));
    }

    public Solution(Collection<Variable<?>> variables, Collection<Constraint> explanation) {
        this.explanation = explanation;
        variables.forEach(v -> solution.put(v, v.getValue()));
    }

    public <E> E getValue(Variable<E> variable) {
        // Joshua Bloch. 2008. Effective Java (2nd Edition). Item 29
        return variable.getType().cast(solution.get(variable));
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        solution.forEach((v, o) -> stringBuilder.append(String.format("%s=%s ", v.getName(), o)));
        return stringBuilder.toString();
    }

    public Collection<Constraint> getExplanation() {
        return explanation;
    }
}
