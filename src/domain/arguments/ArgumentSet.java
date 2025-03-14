package domain.arguments;

import java.util.HashMap;
import java.util.Map;

public class ArgumentSet {
    private final Map<String, Argument> argumentMap = new HashMap<>();

    public ArgumentSet() {
    }

    public ArgumentSet(Argument... args) {
        for (Argument arg : args)
            add(arg);
    }

    public void add(Argument argument) {
        argumentMap.put(argument.getLongArgument(), argument);
        argumentMap.put(argument.getShortArgument(), argument);
    }

    public Argument get(String argument) {
        return argumentMap.getOrDefault(argument, null);
    }

    public Argument get(Argument argument) {
        return argumentMap.getOrDefault(argument.getLongArgument(), null);
    }

    public boolean contains(String argument) {
        return argumentMap.containsKey(argument);
    }
}
