package domain.commands;

import domain.arguments.Argument;
import domain.arguments.ArgumentSet;

import java.util.Map;

public abstract class Command {

    protected String command;
    protected ArgumentSet arguments;

    public Command(String command, ArgumentSet arguments) {
        this.command = command;
        this.arguments = arguments;
    }

    public abstract void parse(String[] args) throws Exception;
    public abstract void execution() throws Exception;
    public abstract void execution(Map<Argument, String> argumentAndValue) throws Exception;

    public String getCommand() {
        return command;
    }

    public ArgumentSet getArguments() {
        return arguments;
    }
}
