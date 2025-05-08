package servent.message.snapshot;

import app.ServentInfo;
import app.snapshot_bitcake.result.ABSnapshotResult;
import servent.message.Message;
import servent.message.MessageType;

import java.util.List;
import java.util.Map;

public class ABFinishMessage extends ACausalMessage {

    private final ABSnapshotResult abSnapshotResult;


    public ABFinishMessage(ServentInfo senderInfo, ServentInfo receiverInfo, String messageText, Map<Integer, Integer> senderVectorClock, ABSnapshotResult snapshotResult) {
        super(MessageType.AB_FINISH, senderInfo, receiverInfo, messageText, senderVectorClock);

        abSnapshotResult = snapshotResult;
    }

    private ABFinishMessage(MessageType messageType, ServentInfo sender, ServentInfo receiver,
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
        return new ABFinishMessage(getMessageType(), getOriginalSenderInfo(), getReceiverInfo(),
                false, getRoute(), getMessageText(), getSenderVectorClock(), getMessageId(), getAbSnapshotResult());
    }
}
