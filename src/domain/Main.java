package domain;

import domain.other.Job;
import domain.other.TemperatureInfo;
import domain.commands.*;
import domain.threads.FileModifyWorker;
import domain.threads.ReadCommandWorker;

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

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Map<String, Command> commandMap = Map.ofEntries(
                Map.entry(ECommand.SCAN.getValue(), new ScanCommand()),
                Map.entry(ECommand.MAP.getValue(), new MapCommand()),
                Map.entry(ECommand.EXPORT_MAP.getValue(), new ExportMapCommand()),
                Map.entry(ECommand.STATUS.getValue(), new StatusCommand())
        );

        Thread fileModifyWorker = new Thread(new FileModifyWorker());
        Thread readJobWorker = new Thread(new ReadCommandWorker());
        fileModifyWorker.start();
        readJobWorker.start();

        while (true) {
            String[] newCommand = scanner.nextLine().split("\\s+");
            try {
                if (newCommand[0].equalsIgnoreCase("exit"))
                    break;
                if (!commandMap.containsKey(newCommand[0]))
                    throw new Exception("Invalid command '%s'".formatted(newCommand[0]));

                queue.put(commandMap.get(newCommand[0].toLowerCase()).parse(Arrays.copyOfRange(newCommand, 1, newCommand.length)));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        fileModifyWorker.interrupt();
        readJobWorker.interrupt();
        System.out.println("Shutting down, please wait...");
        scanner.close();
    }
}