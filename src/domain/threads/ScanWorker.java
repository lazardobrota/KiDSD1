package domain.threads;

import domain.arguments.Argument;
import domain.arguments.ArgumentSet;
import domain.commands.ECommand;
import domain.utils.FileUtils;
import domain.utils.ProgramUtils;

import java.io.*;
import java.util.Map;
import java.util.concurrent.Callable;

public class ScanWorker implements Callable<String> {

    private static final Object lock = new Object();

    private final Map<Argument, String> argumentAndValue;
    private final ArgumentSet argumentMoreInfo;
    private final String filePath;
    private final long fileSize;

    public ScanWorker(Map<Argument, String> argumentAndValue, ArgumentSet argumentMoreInfo, String filePath, long fileSize) {
        this.argumentAndValue = argumentAndValue;
        this.argumentMoreInfo = argumentMoreInfo;
        this.filePath = filePath;
        this.fileSize = fileSize;
    }

    @Override
    public String call() throws Exception {
        double max = (double) Argument.SCAN_MAX.parseOrDefault(argumentAndValue.get(Argument.SCAN_MAX), Double.MAX_VALUE);
        double min = (double) Argument.SCAN_MIN.parseOrDefault(argumentAndValue.get(Argument.SCAN_MIN), Double.MIN_VALUE);
        String letter = (String) Argument.SCAN_LETTER.parseOrDefault(argumentAndValue.get(Argument.SCAN_LETTER), "");
        String outputFileName = (String) Argument.SCAN_OUTPUT.parseOrThrow(argumentAndValue.get(Argument.SCAN_OUTPUT), ECommand.SCAN.getValue());

        File outputFile = new File(FileUtils.defaultOutputFolder + "/" + outputFileName);

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
             BufferedWriter outputBufferedWriter = new BufferedWriter(new FileWriter(outputFile, true))) {

            if (filePath.endsWith(".csv"))
                bufferedReader.readLine();

            String line;
            while (ProgramUtils.running.get() && (line = bufferedReader.readLine()) != null) {

                String[] nameAndTemp = line.split(";");
                if (nameAndTemp.length < 2)
                    continue;

                double temp;
                try {
                    temp = Double.parseDouble(nameAndTemp[1]);
                } catch (NumberFormatException e) {
                    continue;
                }

                if (temp > max || temp < min)
                    continue;
                if (!letter.isEmpty() && !nameAndTemp[0].toLowerCase().startsWith(letter))
                    continue;

                synchronized (lock) {
                    outputBufferedWriter.write(nameAndTemp[0] + " " + nameAndTemp[1] + System.lineSeparator());
                    outputBufferedWriter.flush();
                }
//                Thread.sleep(100);
            }
        } catch (IOException e) {
            System.out.println("There is no file on path: " + filePath);
        }

        if (!ProgramUtils.running.get())
            System.out.println("Interrupted Scan Worker");

        return "";
    }
}
