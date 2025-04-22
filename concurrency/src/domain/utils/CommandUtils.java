package domain.utils;

import domain.arguments.Argument;
import domain.commands.Command;

import java.util.HashMap;
import java.util.Map;

public class CommandUtils {

    public static Map<Argument, String> argumentsToMap(Command command, String[] args) throws Exception {
        Map<Argument, String> argumentAndValue = new HashMap<>();
        for (int i = 0; i < args.length; i += 2) {
            if (i + 1 >= args.length)
                throw new Exception("No value for argument '%s' for command '%s'".formatted(args[i], command.getCommand()));

            Argument argument = command.getArguments().get(args[i]);
            if (argument == null)
                throw new Exception("Invalid argument '%s' for command '%s'".formatted(args[i], command.getCommand()));

            argument.parseOrThrow(args[i + 1], command.getCommand());
            argumentAndValue.put(argument, args[i + 1]);
        }

        return argumentAndValue;
    }
}
