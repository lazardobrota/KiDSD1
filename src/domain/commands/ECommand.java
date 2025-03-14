package domain.commands;

public enum ECommand {
    SCAN("scan"),
    STATUS("status"),
    MAP("map"),
    EXPORT_MAP("exportmap"),
    SHUTDOWN("shutdown"),
    START("start");

    private final String value;

    ECommand(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ECommand convertOrThrow(String givenCommand) throws Exception {
        for (ECommand k : ECommand.values()) {
            if (k.value.equals(givenCommand.toLowerCase()))
                return k;
        }

        throw new Exception("Keyword '" + givenCommand + "' is not recognised by the Program");
    }
}
