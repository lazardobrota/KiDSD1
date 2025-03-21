package domain.utils;

import java.util.concurrent.atomic.AtomicBoolean;

public class ProgramUtils {

    public static AtomicBoolean running = new AtomicBoolean(true);
    public static AtomicBoolean inMemoryFilled = new AtomicBoolean(false);
}
