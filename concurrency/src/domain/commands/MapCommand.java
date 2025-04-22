package domain.commands;

import domain.Main;
import domain.arguments.ArgumentSet;
import domain.other.TemperatureInfo;
import domain.utils.ProgramUtils;

import java.util.HashMap;
import java.util.Map;

public class MapCommand extends Command {
    public MapCommand() {
        super(ECommand.MAP.getValue(), new ArgumentSet());
    }

    @Override
    public Command parse(String[] args) throws Exception {
        if (args.length > 0)
            throw new Exception("Command '%s' takes no domain arguments".formatted(command));

        return new MapCommand();
    }

    @Override
    public void execution() throws Exception {
        Map<Character, TemperatureInfo> inMemoryMapCopy = new HashMap<>(Main.inMemoryMap);

        if (!ProgramUtils.inMemoryFilled.get()) {
            System.out.println("In Memory map is still not available");
            return;
        }

        int i = 0;
        int columns = 2;
        for (TemperatureInfo item : inMemoryMapCopy.values()) {
            i++;
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append(item);
            if (i >= columns) {
                stringBuilder.append("\n");
                i %= columns;
            } else
                stringBuilder.append(" | ");

            System.out.print(stringBuilder);
        }
        System.out.println();
    }

    @Override
    public Void call() throws Exception {
        execution();
        return null;
    }
}
