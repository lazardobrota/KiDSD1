package domain;

import domain.commands.*;
import domain.other.Job;
import domain.other.TemperatureInfo;
import domain.threads.BackgroundExportWorker;
import domain.threads.FileModifyWorker;
import domain.threads.ReadAsyncCommandWorker;
import domain.threads.ReadCommandWorker;
import domain.utils.ProgramUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {

    public static Map<Character, TemperatureInfo> inMemoryMap = new ConcurrentHashMap<>();
    public static Map<String, Job> jobsMap = new ConcurrentHashMap<>();
    public static BlockingQueue<Command> queue = new LinkedBlockingQueue<>();
    public static BlockingQueue<Command> asyncQueue = new LinkedBlockingQueue<>();
    public static Map<String, Command> commandMap = Map.ofEntries(
            Map.entry(ECommand.SCAN.getValue(), new ScanCommand()),
            Map.entry(ECommand.MAP.getValue(), new MapCommand()),
            Map.entry(ECommand.EXPORT_MAP.getValue(), new ExportMapCommand())
    );
    public static Map<String, Command> asyncCommandMap = Map.ofEntries(
            Map.entry(ECommand.STATUS.getValue(), new StatusCommand()),
            Map.entry(ECommand.SHUTDOWN.getValue(), new ShutdownCommand()),
            Map.entry(ECommand.START.getValue(), new StartCommand())
    );

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Thread fileModifyWorker = new Thread(new FileModifyWorker());
        Thread readJobWorker = new Thread(new ReadCommandWorker());
        Thread readAsyncJobWorker = new Thread(new ReadAsyncCommandWorker());
        Thread backgroundExportWorker = new Thread(new BackgroundExportWorker());
        fileModifyWorker.start();
        readJobWorker.start();
        readAsyncJobWorker.start();
        backgroundExportWorker.start();

        boolean startCommandCalled = false;
        while (ProgramUtils.running.get()) {
            String[] newCommand = scanner.nextLine().split("\\s+");
            try {
                String command = newCommand[0].toLowerCase();

                if (command.equalsIgnoreCase(ECommand.SHUTDOWN.getValue()))
                    ProgramUtils.running.set(false);

                if (!startCommandCalled && command.equalsIgnoreCase(ECommand.START.getValue())) {
                    startCommandCalled = true;
                    System.out.println("Program started");
                }

                if (!startCommandCalled)
                    throw new Exception("Write '%s' to start program".formatted(ECommand.START.getValue()));

                if (commandMap.containsKey(command))
                    queue.put(commandMap.get(command).parse(Arrays.copyOfRange(newCommand, 1, newCommand.length)));
                else if (asyncCommandMap.containsKey(command))
                    asyncQueue.put(asyncCommandMap.get(command).parse(Arrays.copyOfRange(newCommand, 1, newCommand.length)));
                else
                    throw new Exception("Invalid command '%s'".formatted(newCommand[0]));

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        ProgramUtils.running.set(false);
        fileModifyWorker.interrupt();
        readJobWorker.interrupt();
        readAsyncJobWorker.interrupt();
        backgroundExportWorker.interrupt();
        System.out.println("Shutting down, please wait...");
        scanner.close();
    }
}