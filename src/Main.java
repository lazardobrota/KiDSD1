import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Map<Keyword, Set<Command>> map = Map.ofEntries(
                Map.entry(Keyword.SCAN, Set.of(Command.SCAN_MIN, Command.SCAN_MAX, Command.SCAN_JOB, Command.SCAN_LETTER, Command.SCAN_OUTPUT)),
                Map.entry(Keyword.STATUS, Set.of(Command.STATUS_JOB)),
                Map.entry(Keyword.SHUTDOWN, Set.of(Command.SHUTDOWN_SAVE_JOB)),
                Map.entry(Keyword.START, Set.of(Command.START_LOAD_JOB)),
                Map.entry(Keyword.MAP, Set.of()),
                Map.entry(Keyword.EXPORT_MAP, Set.of())
        );

//        while (true) {
//            String[] newCommand = scanner.nextLine().split(" ");
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
//                    Command command = Command.convertOrThrow(keyword, newCommand[i]);
//                    System.out.println(command.parseArgument(newCommand[i + 1]));
//                } catch (Exception e) {
//                    System.out.println(e.getMessage());
//                    break;
//                }
//            }
//        }
        scanner.close();

        int readThreadsCount = 1;
        long partSize = 1024 * 1024 * 128; //128 MB
        ExecutorService readFiles = Executors.newFixedThreadPool(readThreadsCount);

        String filePath = "files/measurements_small.txt";
//        String filePath = "files/super_small.txt";
        String outputFilePath = "files/output.txt";

        File outpuFile = new File(outputFilePath);
        try {
            outpuFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        File file = new File(filePath);
        long fileSize = file.length();
        long numOfParts = (fileSize / partSize) + 1;

        BlockingQueue<Long> partsStartQueue = new LinkedBlockingQueue<>();
        for (int i = 0; i < numOfParts; i++)
            partsStartQueue.add((long) i * partSize);

        System.out.println("q: " + partsStartQueue.size());
        for (int i = 0; i < readThreadsCount; i++)
            readFiles.submit(new TxtWorker(i,  partsStartQueue, filePath, partSize, fileSize));


        readFiles.shutdown();
    }
}