import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Map<Keyword, Set<Command>> map = Map.ofEntries(
                Map.entry(Keyword.SCAN,       Set.of(Command.SCAN_MIN, Command.SCAN_MAX, Command.SCAN_JOB, Command.SCAN_LETTER, Command.SCAN_OUTPUT)),
                Map.entry(Keyword.STATUS,     Set.of(Command.STATUS_JOB)),
                Map.entry(Keyword.SHUTDOWN,   Set.of(Command.SHUTDOWN_SAVE_JOB)),
                Map.entry(Keyword.START,      Set.of(Command.START_LOAD_JOB)),
                Map.entry(Keyword.MAP,        Set.of()),
                Map.entry(Keyword.EXPORT_MAP, Set.of())
        );

        while (true) {
            String[] newCommand = scanner.nextLine().split(" ");
            Keyword keyword;
            try {
                keyword = Keyword.convertOrThrow(newCommand[0]);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                continue;
            }

            for (int i = 1; i < newCommand.length; i += 2) {
                try {
                    Command command = Command.convertOrThrow(keyword, newCommand[i]);
                    System.out.println(command.parseArgument(newCommand[i + 1]));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    break;
                }
            }
        }
    }
}