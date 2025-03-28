package domain.threads;

import domain.Main;
import domain.commands.Command;
import domain.utils.ProgramUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ReadAsyncCommandWorker implements Runnable {

    private static final ExecutorService readFiles = Executors.newFixedThreadPool(4);

    @Override
    public void run() {
        while (ProgramUtils.running.get()) {
            try {
                Command command = Main.asyncQueue.poll(10, TimeUnit.SECONDS);
                if (command != null)
                    readFiles.submit(command);
            } catch (InterruptedException e) {
                readFiles.shutdown();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }

        if (!ProgramUtils.running.get()) {
            System.out.println("Interrupted Read Async Command Worker");
            readFiles.shutdown();
        }

    }
}
