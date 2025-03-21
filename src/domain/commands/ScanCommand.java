package domain.commands;

import domain.Main;
import domain.arguments.Argument;
import domain.arguments.ArgumentSet;
import domain.other.EJob;
import domain.other.Job;
import domain.threads.ScanWorker;
import domain.utils.FileUtils;

import java.io.File;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScanCommand extends Command {

    private static final ExecutorService readFiles = Executors.newFixedThreadPool(4);
    private final Map<Argument, String> argumentAndValue;
    private final Job job;

    public ScanCommand() {
        super(ECommand.SCAN.getValue(), new ArgumentSet(Argument.SCAN_MAX, Argument.SCAN_MIN, Argument.SCAN_JOB, Argument.SCAN_LETTER, Argument.SCAN_OUTPUT));
        argumentAndValue = null;
        job = null;
    }

    public ScanCommand(Map<Argument, String> argumentAndValue, Job job) {
        super(ECommand.SCAN.getValue(), new ArgumentSet(Argument.SCAN_MAX, Argument.SCAN_MIN, Argument.SCAN_JOB, Argument.SCAN_LETTER, Argument.SCAN_OUTPUT));
        this.argumentAndValue = argumentAndValue;
        this.job = job;
    }

    @Override
    public Command parse(String[] args) throws Exception {
        Map<Argument, String> argumentAndValue = new HashMap<>();
        for (int i = 0; i < args.length; i += 2) {
            if (i + 1 >= args.length)
                throw new Exception("No value for argument '%s' for command '%s'".formatted(args[i], command));

            Argument argument = getArguments().get(args[i]);
            if (argument == null)
                throw new Exception("Invalid argument '%s' for command '%s'".formatted(args[i], getCommand()));

            argument.parseOrThrow(args[i + 1], command);
            argumentAndValue.put(argument, args[i + 1]);
        }

        if (!argumentAndValue.containsKey(Argument.SCAN_OUTPUT))
            throw new Exception("Missing '%s' ('%s') argument".formatted(Argument.SCAN_OUTPUT.getLongArgument(), Argument.SCAN_OUTPUT.getShortArgument()));
        if (!argumentAndValue.containsKey(Argument.SCAN_JOB))
            throw new Exception("Missing '%s' ('%s') argument".formatted(Argument.SCAN_JOB.getLongArgument(), Argument.SCAN_JOB.getShortArgument()));
        if (Main.jobsMap.containsKey(argumentAndValue.get(Argument.SCAN_JOB)))
            throw new Exception("Already a job with name '%s' ".formatted(argumentAndValue.get(Argument.SCAN_JOB)));

        Job createJob = new Job(argumentAndValue.get(Argument.SCAN_JOB), command + " " + String.join(" ", args), EJob.PENDING);
        Main.jobsMap.put(argumentAndValue.get(Argument.SCAN_JOB), createJob);
        return new ScanCommand(argumentAndValue, createJob);
    }

    @Override
    public void execution() throws Exception {

        if (argumentAndValue == null)
            throw new Exception("ArgumentAndValue can't be null");
        if (job == null)
            throw new Exception("Job can't be null");

        String folderPath = "files";

        File folder = new File(folderPath);
        if (folder.listFiles() == null) {
            System.out.println("No Files available");
            return;
        }

        long start = System.currentTimeMillis();

        File outputFile = new File(FileUtils.defaultOutputFolder + "/" + argumentAndValue.get(Argument.SCAN_OUTPUT));
        outputFile.delete();
        outputFile.createNewFile();

        List<Callable<String>> tasks = new ArrayList<>();
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (fileEntry.getPath().endsWith(".txt") || fileEntry.getPath().endsWith(".csv"))
                tasks.add(new ScanWorker(argumentAndValue, getArguments(), fileEntry.getPath(), fileEntry.length()));
        }

        job.setJobStatus(EJob.RUNNING);
        readFiles.invokeAll(tasks);
        job.setJobStatus(EJob.COMPLETED);

        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start));

    }

    @Override
    public Void call() throws Exception {
        execution();
        return null;
    }
}
