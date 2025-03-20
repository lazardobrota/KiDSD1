package domain.commands;

import domain.Main;
import domain.arguments.ArgumentSet;
import domain.other.TemperatureInfo;

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
    public void execution() throws Exception { //todo for some reason it wont show until FileChangesWorker is finished
        Map<Character, TemperatureInfo> inMemoryMapCopy = new HashMap<>(Main.inMemoryMap);

        if (inMemoryMapCopy.isEmpty()) { //todo probably not this but actually until FileChangesWorker changes
            System.out.println("In Memory map is empty");
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
