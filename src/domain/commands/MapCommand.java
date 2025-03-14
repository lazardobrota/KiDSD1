package domain.commands;

import domain.Main;
import domain.arguments.Argument;
import domain.arguments.ArgumentSet;
import domain.arguments.TemperatureInfo;
import domain.threads.FileChangesWorker;
import domain.utils.FileUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MapCommand extends Command{
    public MapCommand() {
        super(ECommand.MAP.getValue(), new ArgumentSet());
    }

    @Override
    public void parse(String[] args) throws Exception {
        if (args.length > 0)
            throw new Exception("Command '%s' takes no domain.arguments".formatted(command));

        execution();
    }

    @Override
    public void execution() throws Exception { //todo for some reason it wont show until FileChangesWorker is finished
        Map<Character, TemperatureInfo> inMemoryMapCopy = new HashMap<>(Main.inMemoryMap);

        int i = 0;
        int columns = 2;
        for (TemperatureInfo item : inMemoryMapCopy.values()) {
            i++;
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append(item);
            if (i >= columns) {
                stringBuilder.append("\n");
                i %= columns;
            }
            else
                stringBuilder.append(" | ");

            System.out.print(stringBuilder);
        }
    }

    @Override
    public void execution(Map<Argument, String> argumentAndValue) throws Exception {
        execution();
    }
}
