package app.snapshot_bitcake.result;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SnapshotResult implements Serializable {

    private final int servantId;
    private final int recordedBitcakeAmount;
    private final Map<String, List<Integer>> allChannelMessages;

    public SnapshotResult(int servantId, int recordedBitcakeAmount, Map<String, List<Integer>> allChannelMessages) {
        this.servantId = servantId;
        this.recordedBitcakeAmount = recordedBitcakeAmount;
        this.allChannelMessages = new ConcurrentHashMap<>(allChannelMessages);
    }
    public int getServantId() {
        return servantId;
    }
    public int getRecordedBitcakeAmount() {
        return recordedBitcakeAmount;
    }
    public Map<String, List<Integer>> getAllChannelMessages() {
        return allChannelMessages;
    }
}
