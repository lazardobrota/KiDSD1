package domain.commands;

import domain.arguments.Argument;
import domain.arguments.ArgumentSet;
import domain.other.Job;
import domain.utils.ProgramUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ShutdownCommand extends Command {

    private final Map<Argument, String> argumentAndValue;

    public ShutdownCommand() {
        super(ECommand.SHUTDOWN.getValue(), new ArgumentSet(Argument.SHUTDOWN_SAVE_JOB));
        argumentAndValue = new HashMap<>();
    }

    public ShutdownCommand(Map<Argument, String> argumentAndValue) {
        super(ECommand.SHUTDOWN.getValue(), new ArgumentSet(Argument.SHUTDOWN_SAVE_JOB));
        this.argumentAndValue = argumentAndValue;
    }

    @Override
    public Command parse(String[] args) throws Exception {
        Map<Argument, String> argumentAndValue = new HashMap<>();
        for (int i = 0; i < args.length; i += 2) {
            if (i + 1 >= args.length)
                throw new Exception("No value for argument '%s' for command '%s'".formatted(args[i], getCommand()));

            Argument argument = getArguments().get(args[i]);
            if (argument == null)
                throw new Exception("Invalid argument '%s' for command '%s'".formatted(args[i], getCommand()));

            argument.parseOrThrow(args[i + 1], command);
            argumentAndValue.put(argument, args[i + 1]);
        }


        return new ShutdownCommand(argumentAndValue);
    }

    @Override
    public void execution() throws Exception {
        if (argumentAndValue == null)
            throw new Exception("ArgumentAndValue can't be null");


        if (!argumentAndValue.containsKey(Argument.SHUTDOWN_SAVE_JOB))
            return;


    }

    @Override
    public Void call() throws Exception {
        execution();
        return null;
    }
}
