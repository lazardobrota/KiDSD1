package domain.commands;

import domain.Main;
import domain.arguments.Argument;
import domain.arguments.ArgumentSet;
import domain.other.Job;
import domain.other.TemperatureInfo;
import domain.utils.FileUtils;
import domain.utils.ProgramUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class StartCommand extends Command {

    private static boolean programStarted = false;
    private final Map<Argument, String> argumentAndValue;

    public StartCommand() {
        super(ECommand.START.getValue(), new ArgumentSet(Argument.START_LOAD_JOB));
        argumentAndValue = new HashMap<>();
    }

    public StartCommand(Map<Argument, String> argumentAndValue) {
        super(ECommand.START.getValue(), new ArgumentSet(Argument.START_LOAD_JOB));
        this.argumentAndValue = argumentAndValue;
    }

    @Override
    public Command parse(String[] args) throws Exception {
        Map<Argument, String> argumentAndValue = new HashMap<>();
        for (int i = 0; i < args.length; i += 2) {

            Argument argument = getArguments().get(args[i]);
            if (argument == null)
                throw new Exception("Invalid argument '%s' for command '%s'".formatted(args[i], getCommand()));

            argument.parseOrThrow(FileUtils.defaultLoadJobsFile, command);
            argumentAndValue.put(argument, FileUtils.defaultLoadJobsFile);
        }


        return new StartCommand(argumentAndValue);
    }

    @Override
    public void execution() throws Exception {

        if (programStarted) {
            System.out.println("Program has already started");
            return;
        }


        programStarted = true;

        if (!argumentAndValue.containsKey(Argument.START_LOAD_JOB))
            return;

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(argumentAndValue.get(Argument.START_LOAD_JOB)))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] newCommand = line.split("\\s+");
                String command = newCommand[0].toLowerCase();

                if (Main.commandMap.containsKey(command))
                    Main.queue.put(Main.commandMap.get(command).parse(Arrays.copyOfRange(newCommand, 1, newCommand.length)));
                else if (Main.asyncCommandMap.containsKey(command))
                    Main.asyncQueue.put(Main.asyncCommandMap.get(command).parse(Arrays.copyOfRange(newCommand, 1, newCommand.length)));
                else
                    throw new Exception("Invalid command '%s'".formatted(newCommand[0]));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Void call() throws Exception {
        execution();
        return null;
    }
}
