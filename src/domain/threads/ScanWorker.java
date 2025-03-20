package domain.threads;

import domain.arguments.Argument;
import domain.arguments.ArgumentSet;
import domain.commands.ECommand;
import domain.utils.FileUtils;

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
        double max = (double) Argument.SCAN_MAX.parseOrDefault(argumentAndValue.get(Argument.SCAN_MAX), -1.0);
        double min = (double) Argument.SCAN_MIN.parseOrDefault(argumentAndValue.get(Argument.SCAN_MIN), -1.0);
        String letter = (String) Argument.SCAN_LETTER.parseOrDefault(argumentAndValue.get(Argument.SCAN_LETTER), "");
        String outputFileName = (String) Argument.SCAN_OUTPUT.parseOrThrow(argumentAndValue.get(Argument.SCAN_OUTPUT), ECommand.SCAN.getValue());

        File outputFile = new File(FileUtils.defaultOutputFolder + "/" + outputFileName);

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
             FileWriter outputFileWriter = new FileWriter(outputFile, true)) {

            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {

                String[] nameAndTemp = line.split(";");
                if (nameAndTemp.length < 2)
                    continue;

                double temp;
                try {
                    temp = Double.parseDouble(nameAndTemp[1]);
                } catch (Exception e) {
                    continue;
                }

                if (max > 0 && temp > max)
                    continue;
                if (min > 0 && temp < min)
                    continue;
                if (!letter.isBlank() && !nameAndTemp[0].toLowerCase().startsWith(letter))
                    continue;

                stringBuilder.append(nameAndTemp[0]).append(" ").append(nameAndTemp[1]).append("\n");
            }

            synchronized (lock) {
                outputFileWriter.write(stringBuilder.toString());
                outputFileWriter.flush();
            }
        } catch (IOException e) {
            System.out.println("There is no file on path: " + filePath);
        }
        return "";
    }
}
