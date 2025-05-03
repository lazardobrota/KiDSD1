package servent.message.snapshot;

import app.ServentInfo;
import app.snapshot_bitcake.result.CCSnapshotResult;
import servent.message.BasicMessage;
import servent.message.MessageType;

import java.util.List;

public class CCAckMessage extends BasicMessage {
    private final CCSnapshotResult ccSnapshotResult;

    public CCAckMessage(ServentInfo sender, ServentInfo receiver, int collectorId, List<ServentInfo> routeList, CCSnapshotResult clSnapshotResult) {
        super(MessageType.SNAPSHOT_ACK, sender, receiver, routeList, String.valueOf(collectorId));

        this.ccSnapshotResult = clSnapshotResult;
    }

    public CCSnapshotResult getCcSnapshotResult() {
        return ccSnapshotResult;
    }
}
