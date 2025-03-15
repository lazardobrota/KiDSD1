package domain.threads;

import domain.Main;
import domain.commands.Command;

public class ReadCommandWorker implements Runnable {
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Command command = Main.queue.take();
                command.execution();
            } catch (Exception e) {
//                if (!(e instanceof InterruptedException))
                System.out.println(e.getMessage());
            }

        }
    }
}
