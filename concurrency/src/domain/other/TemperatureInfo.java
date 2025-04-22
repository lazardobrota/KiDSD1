package domain.other;

public class TemperatureInfo {

    private final char letter;
    private long numberOfStationsWithSameLetter;
    private double sumOfTemperatures;

    public TemperatureInfo(char letter, long numberOfStationsWithSameLetter, double sumOfTemperatures) {
        this.letter = letter;
        this.numberOfStationsWithSameLetter = numberOfStationsWithSameLetter;
        this.sumOfTemperatures = sumOfTemperatures;
    }

    public TemperatureInfo addStation() {
        ++numberOfStationsWithSameLetter;
        return this;
    }

    public TemperatureInfo AddTemperature(double temperature) {
        sumOfTemperatures += temperature;
        return this;
    }

    public char getLetter() {
        return letter;
    }

    public long getNumberOfStationsWithSameLetter() {
        return numberOfStationsWithSameLetter;
    }

    public double getSumOfTemperatures() {
        return sumOfTemperatures;
    }

    @Override
    public String toString() {
        return letter + " : " + numberOfStationsWithSameLetter + " - " + sumOfTemperatures;
    }
}
