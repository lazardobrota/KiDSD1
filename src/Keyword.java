public enum Keyword {
    SCAN("scan"),
    STATUS("status"),
    MAP("map"),
    EXPORT_MAP("exportmap"),
    SHUTDOWN("shutdown"),
    START("start");

    private final String value;

    Keyword(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Keyword convertOrThrow(String givenCommand) throws Exception {
        for (Keyword k : Keyword.values()) {
            if (k.value.equals(givenCommand.toLowerCase()))
                return k;
        }

        throw new Exception("Keyword '" + givenCommand + "' is not recognised by the Program");
    }
}
