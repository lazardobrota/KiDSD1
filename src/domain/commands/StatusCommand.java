package domain.commands;

import domain.Main;
import domain.arguments.Argument;
import domain.arguments.ArgumentSet;
import domain.utils.CommandUtils;

import java.util.HashMap;
import java.util.Map;

public class StatusCommand extends Command {

    private final Map<Argument, String> argumentAndValue;

    public StatusCommand() {
        super(ECommand.STATUS.getValue(), new ArgumentSet(Argument.STATUS_JOB));
        argumentAndValue = null;
    }

    public StatusCommand(Map<Argument, String> argumentAndValue) {
        super(ECommand.STATUS.getValue(), new ArgumentSet(Argument.STATUS_JOB));
        this.argumentAndValue = argumentAndValue;
    }

    @Override
    public Command parse(String[] args) throws Exception {
        Map<Argument, String> argumentAndValue = CommandUtils.argumentsToMap(this, args);

        if (!argumentAndValue.containsKey(Argument.STATUS_JOB))
            throw new Exception("Missing '%s' ('%s') argument".formatted(Argument.STATUS_JOB.getLongArgument(), Argument.STATUS_JOB.getShortArgument()));


        return new StatusCommand(argumentAndValue);
    }

    @Override
    public void execution() throws Exception {
        if (argumentAndValue == null)
            throw new Exception("ArgumentAndValue can't be null");

        String jobName = argumentAndValue.get(Argument.STATUS_JOB);

        if (!Main.jobsMap.containsKey(jobName))
            throw new Exception("No Job with name '%s' ".formatted(jobName));

        System.out.println(jobName + " is " + Main.jobsMap.get(jobName).getJobStatus().toString().toLowerCase());
    }
}
