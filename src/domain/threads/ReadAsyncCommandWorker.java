package domain.threads;

import domain.Main;
import domain.commands.Command;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReadAsyncCommandWorker implements Runnable {

    private static final ExecutorService readFiles = Executors.newFixedThreadPool(4);

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Command command = Main.asyncQueue.take();
                readFiles.submit(command);
            } catch (Exception e) {
//                if (!(e instanceof InterruptedException))
                System.out.println(e.getMessage());
            }

        }
    }
}
