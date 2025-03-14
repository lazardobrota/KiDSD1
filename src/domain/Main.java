package domain;

import domain.arguments.TemperatureInfo;
import domain.commands.*;
import domain.threads.FileChangesWorker;
import domain.threads.FileModifyWorker;
import domain.threads.InMemoryWorker;
import domain.utils.FileUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static Map<Character, TemperatureInfo> inMemoryMap = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Map<String, Command> commandMap = Map.ofEntries(
                Map.entry(ECommand.SCAN.getValue(), new ScanCommand()),
                Map.entry(ECommand.MAP.getValue(), new MapCommand()),
                Map.entry(ECommand.EXPORT_MAP.getValue(), new ExportMapCommand())
        );

        Thread fileModifyWorker = new Thread(new FileModifyWorker());
        fileModifyWorker.start();

        while (true) {
            String[] newCommand = scanner.nextLine().split("\\s+");
            try {
                if (newCommand[0].equalsIgnoreCase("exit"))
                    break;
                if (!commandMap.containsKey(newCommand[0]))
                    throw new Exception("Invalid command '%s'".formatted(newCommand[0]));

                commandMap.get(newCommand[0].toLowerCase()).parse(Arrays.copyOfRange(newCommand, 1, newCommand.length));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println("Shutting down, please wait...");
        scanner.close();
    }
}