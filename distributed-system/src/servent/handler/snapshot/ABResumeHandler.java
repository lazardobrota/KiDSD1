package servent.handler.snapshot;

import app.AppConfig;
import app.CausalBroadcastShared;
import app.snapshot_bitcake.ABBitcakeManager;
import app.snapshot_bitcake.AVBitcakeManager;
import app.snapshot_bitcake.SnapshotCollector;
import servent.handler.CausalMessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.PendingMessage;

public class ABResumeHandler implements CausalMessageHandler {

    private final Message clientMessage;
    private final ABBitcakeManager bitcakeManager;

    public ABResumeHandler(Message clientMessage, SnapshotCollector snapshotCollector) {
        this.clientMessage = clientMessage;
        this.bitcakeManager = (ABBitcakeManager) snapshotCollector.getBitcakeManager();
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.AB_RESUME) {
            AppConfig.timestampedErrorPrint("Terminate not valid");
            return;
        }

        CausalBroadcastShared.addPendingMessageAndCheck(new PendingMessage(false, clientMessage, this));
    }

    @Override
    public void continueExecution() {
        int collectorId = Integer.parseInt(clientMessage.getMessageText());
        bitcakeManager.handleResume(collectorId);
    }
}
