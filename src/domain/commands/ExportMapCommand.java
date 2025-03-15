package domain.commands;

import domain.Main;
import domain.arguments.ArgumentSet;
import domain.other.TemperatureInfo;
import domain.utils.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class ExportMapCommand extends Command {
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
        File file = new File(FileUtils.defaultExportMapOutputFile);
        file.delete();
        file.createNewFile();

        try(FileWriter fileWriter = new FileWriter(FileUtils.defaultExportMapOutputFile)) {
            fileWriter.write("Letter, Station count, Sum\n");
            for (TemperatureInfo item : inMemoryMapCopy.values()) {
                fileWriter.write(item.toString().replaceAll(" - ", ", ").replaceAll(" : ", ", ") + "\n");
            }
        }
        catch (Exception e) {
            throw new Exception("Invalid ExportMap output file");
        }

    }
}
