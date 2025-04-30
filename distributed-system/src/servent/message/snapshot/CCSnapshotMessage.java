package servent.message.snapshot;

import app.AppConfig;
import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

import java.util.List;

public class CCSnapshotMessage extends BasicMessage {

    public CCSnapshotMessage(ServentInfo sender, ServentInfo receiver, List<ServentInfo> routeList, int collectorId) {
        super(MessageType.SNAPSHOT_REQUEST, sender, receiver, routeList, String.valueOf(collectorId));
    }
}
