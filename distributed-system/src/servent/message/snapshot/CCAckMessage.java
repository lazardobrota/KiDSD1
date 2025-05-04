package servent.message.snapshot;

import app.ServentInfo;
import app.snapshot_bitcake.result.SnapshotResult;
import servent.message.BasicMessage;
import servent.message.MessageType;

import java.util.List;

public class CCAckMessage extends BasicMessage {
    private final SnapshotResult snapshotResult;

    public CCAckMessage(ServentInfo sender, ServentInfo receiver, int collectorId, List<ServentInfo> routeList, SnapshotResult clSnapshotResult) {
        super(MessageType.SNAPSHOT_ACK, sender, receiver, routeList, String.valueOf(collectorId));

        this.snapshotResult = clSnapshotResult;
    }

    public SnapshotResult getCcSnapshotResult() {
        return snapshotResult;
    }
}
