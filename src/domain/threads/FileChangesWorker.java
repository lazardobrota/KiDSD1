package domain.threads;

import domain.Main;
import domain.other.TemperatureInfo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Callable;

public class FileChangesWorker implements Callable<String> {

    private static final Object lock = new Object();

    private final String filePath;

    public FileChangesWorker(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String call() {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {

            String line;
            while ((line = bufferedReader.readLine()) != null) {

                String[] nameAndTemp = line.split(";");
                if (nameAndTemp.length < 2)
                    continue;

                double temperature;
                try {
                    temperature = Double.parseDouble(nameAndTemp[1]);
                } catch (Exception e) {
                    continue;
                }

                char letter = nameAndTemp[0].toLowerCase().charAt(0);

                Main.inMemoryMap.compute(letter, (key, value) -> {
                    if (value == null)
                        value = new TemperatureInfo(letter, 1, temperature);
                    return value.addStation().AddTemperature(temperature);
                });
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("finish");
        return "";
    }
}
