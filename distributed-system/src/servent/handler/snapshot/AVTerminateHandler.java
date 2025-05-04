package servent.handler.snapshot;

import app.AppConfig;
import app.CausalBroadcastShared;
import app.snapshot_bitcake.AVBitcakeManager;
import app.snapshot_bitcake.SnapshotCollector;
import servent.handler.CausalMessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.PendingMessage;

public class AVTerminateHandler implements CausalMessageHandler {

    private final Message clientMessage;
    private final AVBitcakeManager bitcakeManager;

    public AVTerminateHandler(Message clientMessage, SnapshotCollector snapshotCollector) {
        this.clientMessage = clientMessage;
        this.bitcakeManager = (AVBitcakeManager) snapshotCollector.getBitcakeManager();
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.AV_TERMINATE) {
            AppConfig.timestampedErrorPrint("Terminate not valid");
            return;
        }

        CausalBroadcastShared.addPendingMessage(new PendingMessage(false, clientMessage, this));
        CausalBroadcastShared.checkPendingMessages();
    }

    @Override
    public void continueExecution() {
        int collectorId = Integer.parseInt(clientMessage.getMessageText());
        bitcakeManager.handleTerminate(collectorId);
    }
}
