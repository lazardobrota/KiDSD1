package servent.message.snapshot;

import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class CCSnapshotMessage extends BasicMessage {

    public CCSnapshotMessage(ServentInfo sender, ServentInfo receiver, int collectorId) {
        super(MessageType.SNAPSHOT_REQUEST, sender, receiver, String.valueOf(collectorId));
    }
}
