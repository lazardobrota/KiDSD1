package servent.message.snapshot;

import app.ServentInfo;
import app.snapshot_bitcake.result.SnapshotResult;
import servent.message.MessageType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AVDoneCausalMessage extends ACausalMessage {

    private final SnapshotResult snapshotResult;

    public AVDoneCausalMessage(ServentInfo senderInfo, ServentInfo receiverInfo, List<ServentInfo> routes,
                               String messageText, Map<Integer, Integer> senderVectorClock, SnapshotResult snapshotResult) {
        super(MessageType.AV_DONE, senderInfo, receiverInfo, routes, messageText, senderVectorClock);

        this.snapshotResult = snapshotResult;
    }

    public SnapshotResult getSnapshotResult() {
        return snapshotResult;
    }

    @Override
    public ACausalMessage updateVectorClock(Map<Integer, Integer> updatedVectorClock) {
        return new AVDoneCausalMessage(getOriginalSenderInfo(), getReceiverInfo(), getRoute(), getMessageText(), new ConcurrentHashMap<>(updatedVectorClock), getSnapshotResult());
    }
}
