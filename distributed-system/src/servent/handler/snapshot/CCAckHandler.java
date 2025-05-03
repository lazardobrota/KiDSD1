package servent.handler.snapshot;

import app.AppConfig;
import app.ServentInfo;
import app.snapshot_bitcake.CCBitcakeManager;
import app.snapshot_bitcake.SnapshotCollector;
import servent.handler.MessageHandler;
import servent.message.snapshot.CCAckMessage;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.util.MessageUtil;

import java.util.ArrayList;
import java.util.List;

public class CCAckHandler implements MessageHandler {

    private final Message clientMessage;
    private final CCBitcakeManager bitcakeManager;
    private final SnapshotCollector snapshotCollector;

    public CCAckHandler(Message clientMessage, SnapshotCollector snapshotCollector) {
        this.clientMessage = clientMessage;
        this.bitcakeManager = (CCBitcakeManager) snapshotCollector.getBitcakeManager();
        this.snapshotCollector = snapshotCollector;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.SNAPSHOT_ACK) {
            AppConfig.timestampedErrorPrint("Acknowledge amount handler got: " + clientMessage);
            return;
        }

        CCAckMessage ccTellMessage = (CCAckMessage) clientMessage;
        int collectorId = ccTellMessage.getRoute().getFirst().getId();
        if (AppConfig.myServentInfo.getId() == collectorId) {
            snapshotCollector.addCCSnapshotInfo(ccTellMessage.getCcSnapshotResult().getServantId(),
                    ccTellMessage.getCcSnapshotResult());
        } else {
            List<ServentInfo> updatedRoute = new ArrayList<>(ccTellMessage.getRoute());
            updatedRoute.removeLast();

            Message ccFowardMessage = new CCAckMessage(AppConfig.myServentInfo, updatedRoute.getLast(), collectorId, updatedRoute, ccTellMessage.getCcSnapshotResult());
            MessageUtil.sendMessage(ccFowardMessage);
        }

    }
}
