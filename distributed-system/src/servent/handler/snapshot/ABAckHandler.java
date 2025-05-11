package servent.handler.snapshot;

import app.AppConfig;
import app.CausalBroadcastShared;
import app.ServentInfo;
import app.snapshot_bitcake.ABBitcakeManager;
import app.snapshot_bitcake.SnapshotCollector;
import servent.handler.CausalMessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.PendingMessage;
import servent.message.snapshot.ABAckMessage;
import servent.message.snapshot.ACausalMessage;
import servent.message.util.MessageUtil;

import java.util.ArrayList;
import java.util.List;

public class ABAckHandler implements CausalMessageHandler {

    private final Message clientMessage;
    private final ABBitcakeManager bitcakeManager;
    private final SnapshotCollector snapshotCollector;

    public ABAckHandler(Message clientMessage, SnapshotCollector snapshotCollector) {
        this.clientMessage = clientMessage;
        this.bitcakeManager = (ABBitcakeManager) snapshotCollector.getBitcakeManager();
        this.snapshotCollector = snapshotCollector;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.AB_ACK) {
            AppConfig.timestampedErrorPrint("Done amount handler got: " + clientMessage);
            return;
        }

        CausalBroadcastShared.addPendingMessageAndCheck(new PendingMessage(false, clientMessage, this));
    }

    @Override
    public void continueExecution() {
        ABAckMessage ackMessage = (ABAckMessage) clientMessage;
        int collectorId = ackMessage.getRoute().getFirst().getId();
        if (AppConfig.myServentInfo.getId() == collectorId) {
            snapshotCollector.addABSnapshotInfo(ackMessage.getAbSnapshotResult().getServentId(),
                    ackMessage.getAbSnapshotResult());
        } else {
            List<ServentInfo> updatedRoute = new ArrayList<>(ackMessage.getRoute());
            updatedRoute.removeLast();

            ACausalMessage abForwardMessage = new ABAckMessage(AppConfig.myServentInfo, updatedRoute.getLast(), updatedRoute, ackMessage.getMessageText(),
                    ackMessage.getSenderVectorClock(), ackMessage.getAbSnapshotResult());

            int neighborTcp = CausalBroadcastShared.incrementSendAndGet(abForwardMessage.getReceiverInfo().getId());
            abForwardMessage.setTpcNumber(neighborTcp);
            MessageUtil.sendMessage(abForwardMessage);
        }
    }
}