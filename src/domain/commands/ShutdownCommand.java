package domain.commands;

import domain.Main;
import domain.arguments.Argument;
import domain.arguments.ArgumentSet;
import domain.utils.FileUtils;
import domain.utils.ProgramUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

public class ShutdownCommand extends Command {

    private final Set<Argument> argumentsHash;

    public ShutdownCommand() {
        super(ECommand.SHUTDOWN.getValue(), new ArgumentSet(Argument.SHUTDOWN_SAVE_JOB));
        argumentsHash = new HashSet<>();
    }

    public ShutdownCommand(Set<Argument> argumentsHash) {
        super(ECommand.SHUTDOWN.getValue(), new ArgumentSet(Argument.SHUTDOWN_SAVE_JOB));
        this.argumentsHash = argumentsHash;
    }

    @Override
    public Command parse(String[] args) throws Exception {
        Set<Argument> argumentsHash = new HashSet<>();
        for (int i = 0; i < args.length; i += 2) {
            if (i + 1 < args.length)
                throw new Exception("Argument '%s' can't have values for command '%s'".formatted(args[i], getCommand()));

            Argument argument = getArguments().get(args[i]);
            if (argument == null)
                throw new Exception("Invalid argument '%s' for command '%s'".formatted(args[i], getCommand()));

            argumentsHash.add(argument);
        }


        return new ShutdownCommand(argumentsHash);
    }

    @Override
    public void execution() throws Exception {
        if (argumentsHash == null)
            throw new Exception("ArgumentAndValue can't be null");

        System.out.println("Shutting down, please wait...");

        if (!argumentsHash.contains(Argument.SHUTDOWN_SAVE_JOB))
            return;

        File file = new File(FileUtils.defaultLoadJobsFile);
        file.getParentFile().mkdirs();
        file.delete();
        file.createNewFile();

        try (BufferedWriter bufferedWriter  = new BufferedWriter(new FileWriter(file))) {
            Queue<Command> queue = new LinkedList<>(Main.hashSyncCommands);
            queue.addAll(Main.queue);
            System.out.println("AAAAAAAAAAAAAAA");
            while (!queue.isEmpty()) {
                if (queue.poll() instanceof ScanCommand scanCommand) {
                    bufferedWriter.write(scanCommand.getJob().getWholeCommand() + "\n");
                }
            }
        } catch (Exception e) {
            throw new Exception("Invalid ExportMap output file");
        }

        ProgramUtils.running.set(false);
    }

    @Override
    public Void call() throws Exception {
        execution();
        return null;
    }
}
