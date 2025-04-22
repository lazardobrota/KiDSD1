package domain.threads;

import domain.commands.Command;
import domain.commands.ExportMapCommand;
import domain.utils.ProgramUtils;

public class BackgroundExportWorker implements Runnable{

    private final Command exportCommand = new ExportMapCommand();

    @Override
    public void run() {
        while (ProgramUtils.running.get()) {

            try {
                Thread.sleep(1_000 * 60);
                exportCommand.execution();
            } catch (InterruptedException e) {
                System.out.println("Interrupted BackgroundExport Worker");
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

    }
}
