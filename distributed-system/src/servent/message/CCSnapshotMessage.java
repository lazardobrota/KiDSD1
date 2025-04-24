package servent.message;

import app.ServentInfo;

public class CCSnapshotMessage extends BasicMessage {

    public CCSnapshotMessage(ServentInfo sender, ServentInfo receiver, int collectorId) {
        super(MessageType.SNAPSHOT_REQUEST, sender, receiver, String.valueOf(collectorId));
    }
}
