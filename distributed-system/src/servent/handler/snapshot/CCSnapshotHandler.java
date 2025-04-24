package servent.handler.snapshot;

import app.snapshot_bitcake.CCBitcakeManager;
import app.snapshot_bitcake.SnapshotCollector;
import servent.handler.MessageHandler;
import servent.message.Message;

public class CCSnapshotHandler implements MessageHandler {

    private final Message clientMessage;
    private final CCBitcakeManager bitcakeManager;
    private final SnapshotCollector snapshotCollector;

    public CCSnapshotHandler(Message clientMessage, SnapshotCollector snapshotCollector) {
        this.clientMessage = clientMessage;
        this.bitcakeManager = (CCBitcakeManager) snapshotCollector.getBitcakeManager();
        this.snapshotCollector = snapshotCollector;
    }

    @Override
    public void run() {
        bitcakeManager.handleSnapshot(clientMessage, snapshotCollector);
    }
}
