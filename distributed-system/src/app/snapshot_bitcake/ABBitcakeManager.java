package app.snapshot_bitcake;

import app.AppConfig;
import app.CausalBroadcastShared;
import app.ServentInfo;
import app.snapshot_bitcake.result.ABSnapshotResult;
import servent.message.Message;
import servent.message.PendingMessage;
import servent.message.snapshot.ABAckMessage;
import servent.message.snapshot.ABResumeMessage;
import servent.message.snapshot.ABTokenMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;

public class ABBitcakeManager implements BitcakeManager {

    private final AtomicInteger currentAmount = new AtomicInteger(1000);

    public void takeSomeBitcakes(int amount) {
        currentAmount.getAndAdd(-amount);
    }

    public void addSomeBitcakes(int amount) {
        currentAmount.getAndAdd(amount);
    }

    public int getCurrentBitcakeAmount() {
        return currentAmount.get();
    }

    private Map<Integer, Integer> giveHistory = new ConcurrentHashMap<>();
    private Map<Integer, Integer> getHistory = new ConcurrentHashMap<>();

    public ABBitcakeManager() {
        for (Integer neighbor : AppConfig.myServentInfo.getNeighbors()) {
            giveHistory.put(neighbor, 0);
            getHistory.put(neighbor, 0);
        }
    }

    /*
     * This value is protected by AppConfig.colorLock.
     * Access it only if you have the blessing.
     */
    public int recordedAmount = 0;

    public void startSnapshotEvent(int collectorId, SnapshotCollector snapshotCollector) {
        snapshotEvent(collectorId, List.of(),snapshotCollector);
    }

    public void handleSnapshot(Message clientMessage, SnapshotCollector snapshotCollector) {
        snapshotEvent(Integer.parseInt(clientMessage.getMessageText()), clientMessage.getRoute(),snapshotCollector);
    }

    public void snapshotEvent(int collectorId, List<ServentInfo> currRoute, SnapshotCollector snapshotCollector) {
        synchronized (AppConfig.colorLock) {

            if (!AppConfig.isWhite.get())
                return;

            AppConfig.isWhite.set(false);
            recordedAmount = getCurrentBitcakeAmount();

            ABSnapshotResult snapshotResult = new ABSnapshotResult(
                    AppConfig.myServentInfo.getId(), recordedAmount, giveHistory, getHistory);

            if (collectorId == AppConfig.myServentInfo.getId()) {

                snapshotCollector.addABSnapshotInfo(
                        AppConfig.myServentInfo.getId(),
                        snapshotResult);
            } else {
                Message finishMessage = new ABAckMessage(
                        AppConfig.myServentInfo, currRoute.getLast(), new ArrayList<>(currRoute), String.valueOf(AppConfig.myServentInfo.getId()), CausalBroadcastShared.getVectorClock(), snapshotResult);

                CausalBroadcastShared.addPendingMessageAndCheck(new PendingMessage(true, finishMessage, null));
            }

            List<Message> messages = new ArrayList<>();
            List<ServentInfo> updatedRoute = new ArrayList<>(currRoute);
            updatedRoute.add(AppConfig.myServentInfo);

            for (Integer neighbor : AppConfig.myServentInfo.getNeighbors()) {
                messages.add(new ABTokenMessage(AppConfig.myServentInfo, AppConfig.getInfoById(neighbor), updatedRoute, String.valueOf(collectorId), CausalBroadcastShared.getVectorClock()));
            }

            CausalBroadcastShared.addPendingMessageAndCheck(new PendingMessage(true, messages, null));
        }
    }

    public void handleResume(int collectorId) {
        synchronized (AppConfig.colorLock) {

            if (AppConfig.isWhite.get())
                return;

            AppConfig.timestampedStandardPrint("Going white");
            AppConfig.isWhite.set(true);

            AppConfig.timestampedStandardPrint("Telling neighbors to resume");
            List<Message> messageList = new ArrayList<>();

            for (Integer neighbor : AppConfig.myServentInfo.getNeighbors()) {
                ABResumeMessage abResumeMessage = new ABResumeMessage(AppConfig.myServentInfo, AppConfig.getInfoById(neighbor),
                        String.valueOf(collectorId), CausalBroadcastShared.getVectorClock());

                messageList.add(abResumeMessage);
            }

            CausalBroadcastShared.addPendingMessageAndCheck(new PendingMessage(true, messageList, null));
        }
    }

    private class MapValueUpdater implements BiFunction<Integer, Integer, Integer> {

        private int valueToAdd;

        public MapValueUpdater(int valueToAdd) {
            this.valueToAdd = valueToAdd;
        }

        @Override
        public Integer apply(Integer key, Integer oldValue) {
            return oldValue + valueToAdd;
        }
    }

    public void recordGiveTransaction(int neighbor, int amount) {
        giveHistory.compute(neighbor, new MapValueUpdater(amount));
    }

    public void recordGetTransaction(int neighbor, int amount) {
        getHistory.compute(neighbor, new MapValueUpdater(amount));
    }
}
