package servent.message.snapshot;

import app.ServentInfo;
import app.snapshot_bitcake.result.ABSnapshotResult;
import servent.message.Message;
import servent.message.MessageType;

import java.util.List;
import java.util.Map;

public class ABAckMessage extends ACausalMessage {

    private final ABSnapshotResult abSnapshotResult;


    public ABAckMessage(ServentInfo senderInfo, ServentInfo receiverInfo, List<ServentInfo> routes, String messageText, Map<Integer, Integer> senderVectorClock, ABSnapshotResult snapshotResult) {
        super(MessageType.AB_ACK, senderInfo, receiverInfo, routes, messageText, senderVectorClock);

        abSnapshotResult = snapshotResult;
    }

    private ABAckMessage(MessageType messageType, ServentInfo sender, ServentInfo receiver,
                         boolean white, List<ServentInfo> routeList, String messageText, Map<Integer, Integer> senderVectorClock, int messageId,
                         ABSnapshotResult snapshotResult) {
        super(messageType, sender, receiver, white, routeList, messageText, messageId, senderVectorClock);

        this.abSnapshotResult = snapshotResult;
    }

    public ABSnapshotResult getAbSnapshotResult() {
        return abSnapshotResult;
    }

    @Override
    public Message setRedColor() {
        return new ABAckMessage(getMessageType(), getOriginalSenderInfo(), getReceiverInfo(),
                false, getRoute(), getMessageText(), getSenderVectorClock(), getMessageId(), getAbSnapshotResult());
    }
}
