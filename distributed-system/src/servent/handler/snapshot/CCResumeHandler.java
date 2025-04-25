package servent.handler.snapshot;

import app.AppConfig;
import app.snapshot_bitcake.CCBitcakeManager;
import app.snapshot_bitcake.SnapshotCollector;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;

public class CCResumeHandler implements MessageHandler {

    private final Message clientMessage;
    private final CCBitcakeManager bitcakeManager;

    public CCResumeHandler(Message clientMessage, SnapshotCollector snapshotCollector) {
        this.clientMessage = clientMessage;
        this.bitcakeManager = (CCBitcakeManager) snapshotCollector.getBitcakeManager();
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.SNAPSHOT_RESUME) {
            AppConfig.timestampedErrorPrint("Resume not valid");
            return;
        }

        int collectorId = Integer.parseInt(clientMessage.getMessageText());
        bitcakeManager.handleResume(collectorId);
    }
}
