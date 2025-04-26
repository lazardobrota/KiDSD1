package servent.message.snapshot;

import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class CCResumeMessage extends BasicMessage {

    public CCResumeMessage(ServentInfo sender, ServentInfo receiver, int collectorId) {
        super(MessageType.SNAPSHOT_RESUME, sender, receiver, String.valueOf(collectorId));
    }
}
