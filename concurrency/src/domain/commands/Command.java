package domain.commands;

import domain.arguments.ArgumentSet;

import java.util.concurrent.Callable;

public abstract class Command implements Callable<Void> {

    protected String command;
    protected ArgumentSet arguments;

    public Command(String command, ArgumentSet arguments) {
        this.command = command;
        this.arguments = arguments;
    }

    public abstract Command parse(String[] args) throws Exception;

    public abstract void execution() throws Exception;


    public String getCommand() {
        return command;
    }

    public ArgumentSet getArguments() {
        return arguments;
    }
}
