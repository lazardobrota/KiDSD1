package servent.handler.snapshot;

import app.CausalBroadcastShared;
import app.snapshot_bitcake.AVBitcakeManager;
import app.snapshot_bitcake.SnapshotCollector;
import servent.handler.CausalMessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.PendingMessage;

public class AVTokenHandler implements CausalMessageHandler {
    private final Message clientMessage;
    private final AVBitcakeManager bitcakeManager;
    private final SnapshotCollector snapshotCollector;

    public AVTokenHandler(Message clientMessage, SnapshotCollector snapshotCollector) {
        this.clientMessage = clientMessage;
        this.bitcakeManager = (AVBitcakeManager) snapshotCollector.getBitcakeManager();
        this.snapshotCollector = snapshotCollector;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.AV_TOKEN)
            return;

        CausalBroadcastShared.addPendingMessage(new PendingMessage(false, clientMessage, this));
        CausalBroadcastShared.checkPendingMessages();
    }

    @Override
    public void continueExecution() {
        bitcakeManager.handleSnapshot(clientMessage, snapshotCollector);
    }
}
