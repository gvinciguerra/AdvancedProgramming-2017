import model.Variable;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class Solution {
    private final Map<Variable<?>, Object> solution = new LinkedHashMap<>();

    public Solution(Collection<Variable<?>> variables) {
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
}
