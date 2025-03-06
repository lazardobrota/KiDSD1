import java.util.Arrays;
import java.util.function.Function;

public enum Command {

    SCAN_MIN         (Keyword.SCAN,     "--min",       "-m", Double::parseDouble),
    SCAN_MAX         (Keyword.SCAN,     "--max",       "-M", Double::parseDouble),
    SCAN_LETTER      (Keyword.SCAN,     "--letter",    "-l", s -> s),
    SCAN_OUTPUT      (Keyword.SCAN,     "--output",    "-o", s -> s),
    SCAN_JOB         (Keyword.SCAN,     "--job",       "-j", s -> s),
    STATUS_JOB       (Keyword.STATUS,   "--job",       "-j", s -> s),
    SHUTDOWN_SAVE_JOB(Keyword.SHUTDOWN, "--save-job",  "-s", s -> s),
    START_LOAD_JOB   (Keyword.START,    "--load-jobs", "-l", s -> s);


    private final Keyword keyword;
    private final String command;
    private final String shortCommand;
    private final Function<String, ?> parser;

    Command(Keyword keyword, String command, String shortCommand, Function<String, ?> parser) {
        this.keyword = keyword;
        this.command = command;
        this.shortCommand = shortCommand;
        this.parser = parser;
    }


    public static Command convertOrThrow(Keyword givenKeyword, String givenCommand) throws Exception {
        return Arrays.stream(Command.values())
                .filter(c -> c.getKeyword().equals(givenKeyword) && (c.command.equals(givenCommand) || c.shortCommand.equals(givenCommand)))
                .findFirst()
                .orElseThrow(() -> new Exception("Invalid command: " + givenCommand + " for keyword '" + givenKeyword.getValue() + "'"));
    }

    public Object parseArgument(String arg) throws Exception {
        try {
            return parser.apply(arg);
        } catch (Exception e) {
            throw new Exception("Invalid argument: " + arg + " for command '" + command + "'");
        }
    }

    public Keyword getKeyword() {
        return keyword;
    }

    public String getCommand() {
        return command;
    }

    public String getShortCommand() {
        return shortCommand;
    }

    public Function<String, ?> getParser() {
        return parser;
    }
}
