package servent.message;

import app.ServentInfo;

public class CCResumeMessage extends BasicMessage{

    public CCResumeMessage(ServentInfo sender, ServentInfo receiver, int collectorId) {
        super(MessageType.SNAPSHOT_RESUME, sender, receiver, String.valueOf(collectorId));
    }
}
