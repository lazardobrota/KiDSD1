package domain.threads;

import domain.utils.FileUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FileModifyWorker implements Runnable {

    private final Map<String, Long> modifiedDates = new HashMap<>();

    @Override
    public void run() {
        File folder = new File(FileUtils.defaultFolder);
        boolean modified;
        while (true) {
            modified = false;

            for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
                if (fileEntry.lastModified() > modifiedDates.getOrDefault(fileEntry.getPath(), -1L)) {
                    modified = true;
                    modifiedDates.put(fileEntry.getPath(), fileEntry.lastModified());
                }
            }

            if (modified) {
                Thread inMemoryWorker = new Thread(new InMemoryWorker());
                inMemoryWorker.start();
                try {
                    inMemoryWorker.join(); //TODO this should wait but cancel if the are new modifications and start from beginning
                } catch (InterruptedException e) {
                    System.out.println("Interrupted InMemoryWorker");
                }
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("Interrupted Sleep of InMemoryWorker");
            }
        }
    }
}
