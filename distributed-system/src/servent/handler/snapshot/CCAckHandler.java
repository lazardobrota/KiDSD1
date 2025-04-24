package servent.handler.snapshot;

import app.AppConfig;
import app.snapshot_bitcake.SnapshotCollector;
import servent.handler.MessageHandler;
import servent.message.CCAckMessage;
import servent.message.Message;
import servent.message.MessageType;

public class CCAckHandler implements MessageHandler {

    private final Message clientMessage;
    private final SnapshotCollector snapshotCollector;

    public CCAckHandler(Message clientMessage, SnapshotCollector snapshotCollector) {
        this.clientMessage = clientMessage;
        this.snapshotCollector = snapshotCollector;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.ACK) {
            AppConfig.timestampedErrorPrint("Acknowledge amount handler got: " + clientMessage);
            return;
        }

        CCAckMessage ackMessage = (CCAckMessage)clientMessage;
        snapshotCollector.addCCSnapshotInfo(ackMessage.getOriginalSenderInfo().getId(), ackMessage.getCCSnapshotResult());
    }
}
