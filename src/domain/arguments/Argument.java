package domain.arguments;

import java.util.function.Function;

public enum Argument {
    SCAN_MIN         ("--min",       "-m", Double::parseDouble),
    SCAN_MAX         ("--max",       "-M", Double::parseDouble),
    SCAN_LETTER      ("--letter",    "-l", s -> s),
    SCAN_OUTPUT      ("--output",    "-o", s -> s.endsWith(".txt") || s.endsWith(".csv") ? s : s + ".txt"),
    SCAN_JOB         ("--job",       "-j", s -> s),
    STATUS_JOB       ("--job",       "-j", s -> s),
    SHUTDOWN_SAVE_JOB("--save-job",  "-s", s -> s),
    START_LOAD_JOB   ("--load-jobs", "-l", s -> s);

    private final String longArgument;
    private final String shortArgument;
    private final Function<String, ?> parser;

    Argument(String longArgument, String shortArgument, Function<String, ?> parser) {
        this.longArgument = longArgument;
        this.shortArgument = shortArgument;
        this.parser = parser;
    }

    public Object parseOrThrow(String value, String command) throws Exception {
        try {
            if (value == null || value.startsWith("--"))
                throw new Exception();

            return parser.apply(value);
        } catch (Exception e) {
            throw new Exception("Invalid value '%s' for argument '%s' for command '%s'".formatted(value, longArgument, command));
        }
    }

    public Object parseOrDefault(String value, Object object) throws Exception {
        try {
            return value != null ? parser.apply(value) : object;
        } catch (Exception e) {
            return object;
        }
    }

    public String getLongArgument() {
        return longArgument;
    }

    public String getShortArgument() {
        return shortArgument;
    }

    public Function<String, ?> getParser() {
        return parser;
    }
}
