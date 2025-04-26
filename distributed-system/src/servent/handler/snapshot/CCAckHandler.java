package servent.handler.snapshot;

import app.AppConfig;
import app.snapshot_bitcake.CCBitcakeManager;
import app.snapshot_bitcake.SnapshotCollector;
import servent.handler.MessageHandler;
import servent.message.snapshot.CCAckMessage;
import servent.message.Message;
import servent.message.MessageType;

public class CCAckHandler implements MessageHandler {

    private final Message clientMessage;
    private final CCBitcakeManager bitcakeManager;
    private final SnapshotCollector snapshotCollector;

    public CCAckHandler(Message clientMessage, SnapshotCollector snapshotCollector) {
        this.clientMessage = clientMessage;
        this.bitcakeManager = (CCBitcakeManager)snapshotCollector.getBitcakeManager();
        this.snapshotCollector = snapshotCollector;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.ACK) {
            AppConfig.timestampedErrorPrint("Acknowledge amount handler got: " + clientMessage);
            return;
        }

        CCAckMessage clTellMessage = (CCAckMessage)clientMessage;

        snapshotCollector.addCCSnapshotInfo(
                clTellMessage.getOriginalSenderInfo().getId(),
                clTellMessage.getCCSnapshotResult());
    }
}
