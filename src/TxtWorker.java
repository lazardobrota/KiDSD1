import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

public class TxtWorker implements Callable<String> {

    private final int threadId;
    private final BlockingQueue<Long> partsStartQueue;
    private final String filePath;
    private final long partSize;
    private final long fileSize;

    public TxtWorker(int threadId, BlockingQueue<Long> partsStartQueue, String filePath, long partSize, long fileSize) {
        this.threadId = threadId;
        this.partsStartQueue = partsStartQueue;
        this.filePath = filePath;
        this.partSize = partSize;
        this.fileSize = fileSize;
    }

    @Override
    public String call() throws Exception {
        Long start;
        long end;
        File file = new File("output" + threadId + ".txt");
        file.delete();
        while ((start = partsStartQueue.poll()) != null) {

            end = Math.min(start + partSize, fileSize);


            try (RandomAccessFile randomAccessFile = new RandomAccessFile(filePath, "r")) {
                file.createNewFile();

                try (FileWriter myWriter = new FileWriter(file, true);) {

                    randomAccessFile.seek(start);
                    StringBuilder stringBuilder = new StringBuilder();
                    while (randomAccessFile.getFilePointer() < end) {
                        String line = randomAccessFile.readLine();
                        if (line == null)
                            break;

                        String[] nameAndTemp = line.split(";");
                        if (nameAndTemp.length > 1)
                            stringBuilder.append(nameAndTemp[0]).append(" ").append(nameAndTemp[1]).append("\n");
                    }
                    myWriter.write(stringBuilder.toString());
                }
            } catch (IOException e) {
                System.out.println("There is no file on path: " + filePath);
                break;
            }
        }

        return "";
    }
}
