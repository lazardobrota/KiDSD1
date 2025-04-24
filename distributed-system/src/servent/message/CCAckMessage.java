package servent.message;

import app.ServentInfo;
import app.snapshot_bitcake.result.CCSnapshotResult;

public class CCAckMessage extends BasicMessage{

    private final CCSnapshotResult ccSnapshotResult;

    public CCAckMessage(ServentInfo sender, ServentInfo receiver, CCSnapshotResult clSnapshotResult) {
        super(MessageType.ACK, sender, receiver);

        this.ccSnapshotResult = clSnapshotResult;
    }

    public CCSnapshotResult getCCSnapshotResult() {
        return ccSnapshotResult;
    }
}
