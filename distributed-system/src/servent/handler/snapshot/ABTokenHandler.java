package servent.handler.snapshot;

import app.CausalBroadcastShared;
import app.snapshot_bitcake.ABBitcakeManager;
import app.snapshot_bitcake.SnapshotCollector;
import servent.handler.CausalMessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.PendingMessage;

public class ABTokenHandler implements CausalMessageHandler {
    private final Message clientMessage;
    private final ABBitcakeManager bitcakeManager;
    private final SnapshotCollector snapshotCollector;

    public ABTokenHandler(Message clientMessage, SnapshotCollector snapshotCollector) {
        this.clientMessage = clientMessage;
        this.bitcakeManager = (ABBitcakeManager) snapshotCollector.getBitcakeManager();
        this.snapshotCollector = snapshotCollector;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.AB_TOKEN)
            return;

        CausalBroadcastShared.addPendingMessageAndCheck(new PendingMessage(false, clientMessage, this));
    }

    @Override
    public void continueExecution() {
        bitcakeManager.handleSnapshot(clientMessage, snapshotCollector);
    }
}
