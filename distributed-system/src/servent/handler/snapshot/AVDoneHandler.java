package servent.handler.snapshot;

import app.AppConfig;
import app.CausalBroadcastShared;
import app.ServentInfo;
import app.snapshot_bitcake.AVBitcakeManager;
import app.snapshot_bitcake.SnapshotCollector;
import servent.handler.CausalMessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.PendingMessage;
import servent.message.snapshot.ACausalMessage;
import servent.message.snapshot.AVDoneCausalMessage;
import servent.message.util.MessageUtil;

import java.util.ArrayList;
import java.util.List;

public class AVDoneHandler implements CausalMessageHandler {

    private final Message clientMessage;
    private final AVBitcakeManager bitcakeManager;
    private final SnapshotCollector snapshotCollector;

    public AVDoneHandler(Message clientMessage, SnapshotCollector snapshotCollector) {
        this.clientMessage = clientMessage;
        this.bitcakeManager = (AVBitcakeManager) snapshotCollector.getBitcakeManager();
        this.snapshotCollector = snapshotCollector;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.AV_DONE) {
            AppConfig.timestampedErrorPrint("Done amount handler got: " + clientMessage);
            return;
        }

        CausalBroadcastShared.addPendingMessageAndCheck(new PendingMessage(false, clientMessage, this));
    }

    @Override
    public void continueExecution() {
        AVDoneCausalMessage doneMessage = (AVDoneCausalMessage) clientMessage;
        int collectorId = doneMessage.getRoute().getFirst().getId();
        if (AppConfig.myServentInfo.getId() == collectorId) {
            snapshotCollector.addCCSnapshotInfo(doneMessage.getSnapshotResult().getServantId(),
                    doneMessage.getSnapshotResult());
        } else {
            List<ServentInfo> updatedRoute = new ArrayList<>(doneMessage.getRoute());
            updatedRoute.removeLast();

            ACausalMessage avForwardMessage = new AVDoneCausalMessage(AppConfig.myServentInfo, updatedRoute.getLast(), updatedRoute, doneMessage.getMessageText(),
                    doneMessage.getSenderVectorClock(), doneMessage.getSnapshotResult());
            int neighborTcp = CausalBroadcastShared.incrementSendAndGet(avForwardMessage.getReceiverInfo().getId());
            avForwardMessage.setTpcNumber(neighborTcp);
            MessageUtil.sendMessage(avForwardMessage);
        }
    }
}
