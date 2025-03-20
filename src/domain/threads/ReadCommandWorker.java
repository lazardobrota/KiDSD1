package domain.threads;

import domain.Main;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReadCommandWorker implements Runnable {

    private static final ExecutorService readFiles = Executors.newFixedThreadPool(1);

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                readFiles.submit(Main.queue.take());
            } catch (Exception e) {
//                if (!(e instanceof InterruptedException))
                System.out.println(e.getMessage());
            }

        }
    }
}
