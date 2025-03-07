import commands.Command;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

public class ScanWorker implements Callable<String> {

    private static final Object lock = new Object();
    private static int index = 0;

    private final Queue<Long> partsStartQueue;
    private final String outputFilePath;
    private final String filePath;
    private final long partSize;
    private final long fileSize;

    public ScanWorker(Queue<Long> partsStartQueue, String outputFilePath, String filePath, long partSize, long fileSize) {
        this.partsStartQueue = partsStartQueue;
        this.outputFilePath = outputFilePath;
        this.filePath = filePath;
        this.partSize = partSize;
        this.fileSize = fileSize;
    }

    @Override
    public String call() throws Exception {
        //TODO This should be filter commands in the future
        double min = -13;
        double max = -15;
        String letter = "";


        Long start;
        long end;
        File file = new File(outputFilePath);
        file.delete();
        while ((start = partsStartQueue.poll()) != null) {

            end = Math.min(start + partSize, fileSize);

            try (RandomAccessFile randomAccessFile = new RandomAccessFile(filePath, "r")) {
                file.createNewFile();

                try (FileWriter myWriter = new FileWriter(file, true)) {

                    randomAccessFile.seek(start);
                    StringBuilder stringBuilder = new StringBuilder();
                    while (randomAccessFile.getFilePointer() < end) {
                        String line = randomAccessFile.readLine();

                        if (line == null)
                            break;

                        String[] nameAndTemp = line.split(";");
                        if (nameAndTemp.length < 2)
                            continue;

                        double temp;
                        try {
                            temp = Double.parseDouble(nameAndTemp[1]);
                        } catch (Exception e) {
                            continue;
                        }


                        if (max > 0 && temp > max)
                            continue;
                        if (min > 0 && temp < min)
                            continue;
                        if (!letter.isBlank() && !nameAndTemp[0].toLowerCase().startsWith(letter))
                            continue;

                        stringBuilder.append(nameAndTemp[0]).append(" ").append(nameAndTemp[1]).append("\n");
                    }

                    synchronized (lock) {
                        myWriter.write(stringBuilder.toString());
                    }
                }
            } catch (IOException e) {
                System.out.println("There is no file on path: " + filePath);
                break;
            }
        }

        return "";
    }
}
