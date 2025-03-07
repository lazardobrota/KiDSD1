import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) {
//        Scanner scanner = new Scanner(System.in);
//        Map<Keyword, Set<Argument>> map = Map.ofEntries(
//                Map.entry(Keyword.SCAN, Set.of(Argument.SCAN_MIN, Argument.SCAN_MAX, Argument.SCAN_JOB, Argument.SCAN_LETTER, Argument.SCAN_OUTPUT)),
//                Map.entry(Keyword.STATUS, Set.of(Argument.STATUS_JOB)),
//                Map.entry(Keyword.SHUTDOWN, Set.of(Argument.SHUTDOWN_SAVE_JOB)),
//                Map.entry(Keyword.START, Set.of(Argument.START_LOAD_JOB)),
//                Map.entry(Keyword.MAP, Set.of()),
//                Map.entry(Keyword.EXPORT_MAP, Set.of())
//        );
//
//        while (true) {
//            String[] newCommand = scanner.nextLine().split("\\s+");
//            Keyword keyword;
//            try {
//                keyword = Keyword.convertOrThrow(newCommand[0]);
//            } catch (Exception e) {
//                System.out.println(e.getMessage());
//                continue;
//            }
//
//            for (int i = 1; i < newCommand.length; i += 2) {
//                try {
//                    Argument argument = Argument.convertOrThrow(keyword, newCommand[i]);
//                    System.out.println(argument.parseArgument(newCommand[i + 1]));
//                } catch (Exception e) {
//                    System.out.println(e.getMessage());
//                    break;
//                }
//            }
//        }
//        scanner.close();

        int readThreadsCount = 4;
        long partSize = 1024 * 1024 * 128; //128 MB
        ExecutorService readFiles = Executors.newFixedThreadPool(readThreadsCount);


        String folderPath = "files";
        String filePath = "files/measurements_small.txt";
//        String filePath = "files/super_small.txt";
        String outputFilePath = "output.txt";

        File folder = new File(folderPath);
        if (folder.listFiles() == null) {
            System.out.println("No Files available");
            return;
        }

        long start = System.currentTimeMillis();
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {

            long fileSize = fileEntry.length();
            long numOfParts = (fileSize / partSize) + 1;

            Queue<Long> partsStartQueue = new LinkedList<>();
            for (int i = 0; i < numOfParts; i++)
                partsStartQueue.add((long) i * partSize);

            System.out.println("q: " + partsStartQueue.size());
            readFiles.submit(new ScanWorker(partsStartQueue, outputFilePath, fileEntry.getPath(), partSize, fileSize));
        }

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