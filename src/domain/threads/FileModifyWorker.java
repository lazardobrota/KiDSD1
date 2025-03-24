package domain.threads;

import domain.Main;
import domain.utils.FileUtils;
import domain.utils.ProgramUtils;

import java.io.File;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FileModifyWorker implements Runnable {

    private final Map<String, Long> modifiedDates = new HashMap<>();
    private static final ExecutorService fillInMemoryMap = Executors.newFixedThreadPool(4);
    private static List<Future<String>> runningTasks = new ArrayList<>();

    @Override
    public void run() {
        File folder = new File(FileUtils.defaultFolder);
        boolean modified;
        while (ProgramUtils.running.get()) {
            modified = false;

            for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
                if (!fileEntry.getPath().endsWith(".txt") && !fileEntry.getPath().endsWith(".csv"))
                    continue;

                if (fileEntry.lastModified() > modifiedDates.getOrDefault(fileEntry.getPath(), -1L)) {
                    modified = true;
                    modifiedDates.put(fileEntry.getPath(), fileEntry.lastModified());
                }
            }

            if (modified) {
                ProgramUtils.inMemoryFilled.set(false);
                handleFileChanges();
                ProgramUtils.inMemoryFilled.set(true);
            }
        }

        if (!ProgramUtils.running.get()) {
            System.out.println("Interrupted File Modify Worker");
            fillInMemoryMap.shutdown();
        }
    }

    private void handleFileChanges() {
        Main.inMemoryMap.clear();
        File folder = new File(FileUtils.defaultFolder);
        List<Callable<String>> tasks = new ArrayList<>();
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (fileEntry.getPath().endsWith(".txt") || fileEntry.getPath().endsWith(".csv"))
                tasks.add(new LoadInMemoryWorker(fileEntry.getPath()));
        }

        for (Future<String> runningTask : runningTasks)
            runningTask.cancel(true);

        try {
            runningTasks = fillInMemoryMap.invokeAll(tasks);
        } catch (InterruptedException e) {
            fillInMemoryMap.shutdown();
        }
    }
}
