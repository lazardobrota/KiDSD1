package domain.threads;

import domain.utils.FileUtils;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class InMemoryWorker implements Runnable{
    @Override
    public void run() { //TODO what happeneds when this is working but file is changed,  there should be two InMemoryWorkers at the same time, first one should be canceled
        ExecutorService fillInMemoryMap = Executors.newFixedThreadPool(4);

        File folder = new File(FileUtils.defaultFolder);
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles()))
            fillInMemoryMap.submit(new FileChangesWorker(fileEntry.getPath()));

        fillInMemoryMap.shutdown();

        try {
            fillInMemoryMap.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
