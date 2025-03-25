package domain.commands;

import domain.Main;
import domain.arguments.ArgumentSet;
import domain.other.TemperatureInfo;
import domain.utils.FileUtils;
import domain.utils.ProgramUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class ExportMapCommand extends Command {

    private static final Object lock = new Object();

    public ExportMapCommand() {
        super(ECommand.EXPORT_MAP.getValue(), new ArgumentSet());
    }

    @Override
    public Command parse(String[] args) throws Exception {
        if (args.length > 0)
            throw new Exception("Command '%s' takes no domain arguments".formatted(command));

        return new ExportMapCommand();
    }

    @Override
    public void execution() throws Exception {
        Map<Character, TemperatureInfo> inMemoryMapCopy = new HashMap<>(Main.inMemoryMap);

        synchronized (lock) {
            File file = new File(FileUtils.defaultExportMapOutputFile);
            file.getParentFile().mkdirs();
            file.delete();
            file.createNewFile();

            try (BufferedWriter bufferedWriter  = new BufferedWriter(new FileWriter(FileUtils.defaultExportMapOutputFile))) {
                bufferedWriter.write("Letter, Station count, Sum\n");
                for (TemperatureInfo item : inMemoryMapCopy.values()) {
                    bufferedWriter.write(item.toString().replaceAll(" - ", ", ").replaceAll(" : ", ", ") + "\n");
                }
            } catch (Exception e) {
                throw new Exception("Invalid ExportMap output file");
            }
        }


    }

    @Override
    public Void call() throws Exception {
        if (!ProgramUtils.inMemoryFilled.get()) {
            System.out.println("In Memory map is still not available");
            return null;
        }

        execution();

        return null;
    }
}
