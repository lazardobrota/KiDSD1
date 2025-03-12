package commands;

import java.util.Set;

public abstract class Command {

    protected String command;
    protected ArgumentSet arguments;

    public Command(String command, ArgumentSet arguments) {
        this.command = command;
        this.arguments = arguments;
    }

    public abstract void execution(String[] args) throws Exception;

    public String getCommand() {
        return command;
    }

    public ArgumentSet getArguments() {
        return arguments;
    }
}
