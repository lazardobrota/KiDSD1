package commands;

import threads.ScanWorker;

import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ScanCommand extends Command {

    public ScanCommand() {
        super("scan", new ArgumentSet(Argument.SCAN_MAX, Argument.SCAN_MIN, Argument.SCAN_JOB, Argument.SCAN_LETTER, Argument.SCAN_OUTPUT));
    }

    @Override
    public void execution(String[] args) throws Exception {
        Map<Argument, String> argumentAndValue = new HashMap<>();
        int readThreadsCount = 1;
        ExecutorService readFiles = Executors.newFixedThreadPool(readThreadsCount); //TODO What happens when you shutdown executorService and after you call Scan again

        for (int i = 0; i < args.length - 1; i += 2) {
            Argument argument = getArguments().get(args[i]);
            if (argument == null)
                throw new Exception("Invalid argument '%s' for command '%s'".formatted(args[i], getCommand()));

            argument.parseOrThrow(args[i + 1], command);
            argumentAndValue.put(argument, args[i + 1]);
        }

        if (!argumentAndValue.containsKey(Argument.SCAN_OUTPUT))
            throw new Exception("Missing value or '%s' / '%s' argument".formatted(Argument.SCAN_OUTPUT.getLongArgument(), Argument.SCAN_OUTPUT.getShortArgument()));
        if (!argumentAndValue.containsKey(Argument.SCAN_JOB))
            throw new Exception("Missing value or '%s' / '%s' argument".formatted(Argument.SCAN_JOB.getLongArgument(), Argument.SCAN_JOB.getShortArgument()));


        String folderPath = "files";
//        String filePath = "files/measurements_small.txt";
//        String filePath = "files/super_small.txt";
//        String outputFilePath = "output.txt";

        File folder = new File(folderPath);
        if (folder.listFiles() == null) {
            System.out.println("No Files available");
            return;
        }

        long start = System.currentTimeMillis();

        File outputFile = new File(argumentAndValue.get(Argument.SCAN_OUTPUT));
        outputFile.delete();

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
