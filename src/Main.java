import commands.Command;
import commands.ECommand;
import commands.ScanCommand;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Map<String, Command> commandMap = Map.ofEntries(
                Map.entry(ECommand.SCAN.getValue(), new ScanCommand())
        );

        while (true) {
            String[] newCommand = scanner.nextLine().split("\\s+");
            try {
                if (newCommand[0].equalsIgnoreCase("exit"))
                    break;
                commandMap.get(newCommand[0].toLowerCase()).execution(Arrays.copyOfRange(newCommand, 1, newCommand.length));
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        System.out.println("Shutting down, please wait...");
        scanner.close();
    }
}