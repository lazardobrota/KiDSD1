package domain.commands;

import domain.arguments.Argument;
import domain.arguments.ArgumentSet;
import domain.threads.ScanWorker;
import domain.utils.FileUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ScanCommand extends Command {

    private final Map<Argument, String> argumentAndValue;

    public ScanCommand() {
        super(ECommand.SCAN.getValue(), new ArgumentSet(Argument.SCAN_MAX, Argument.SCAN_MIN, Argument.SCAN_JOB, Argument.SCAN_LETTER, Argument.SCAN_OUTPUT));
        argumentAndValue = null;
    }

    public ScanCommand(Map<Argument, String> argumentAndValue) {
        super(ECommand.SCAN.getValue(), new ArgumentSet(Argument.SCAN_MAX, Argument.SCAN_MIN, Argument.SCAN_JOB, Argument.SCAN_LETTER, Argument.SCAN_OUTPUT));
        this.argumentAndValue = argumentAndValue;
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

        return new ScanCommand(argumentAndValue);
    }

    @Override
    public void execution() throws Exception { //This should maybe be runnable

        if (argumentAndValue == null)
            throw new Exception("ArgumentAndValue can't be null");

        String folderPath = "files";
//        String filePath = "files/measurements_small.txt";
//        String filePath = "files/super_small.txt";
//        String outputFilePath = "output.txt";

        File folder = new File(folderPath);
        if (folder.listFiles() == null) {
            System.out.println("No Files available");
            return;
        }

        int readThreadsCount = 4;
        ExecutorService readFiles = Executors.newFixedThreadPool(readThreadsCount); //TODO What happens when you shutdown executorService and after you call Scan again
        long start = System.currentTimeMillis();

        File outputFile = new File(FileUtils.defaultOutputFolder + "/" + argumentAndValue.get(Argument.SCAN_OUTPUT));
        outputFile.delete();
        outputFile.createNewFile();

        for (final File fileEntry : Objects.requireNonNull(folder.listFiles()))
            readFiles.submit(new ScanWorker(argumentAndValue, getArguments(), fileEntry.getPath(),  fileEntry.length()));

        readFiles.shutdown();

        try {
            readFiles.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        long end = System.currentTimeMillis();

        System.out.println("Time: " + (end - start));
    }
}
