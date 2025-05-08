//package app.snapshot_bitcake;
//
//import app.AppConfig;
//import app.CausalBroadcastShared;
//import app.snapshot_bitcake.result.ABSnapshotResult;
//import servent.message.Message;
//import servent.message.snapshot.ABFinishMessage;
//import servent.message.util.MessageUtil;
//
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.function.BiFunction;
//
//public class ABBitcakeManager implements BitcakeManager{
//
//    private final AtomicInteger currentAmount = new AtomicInteger(1000);
//
//    public void takeSomeBitcakes(int amount) {
//        currentAmount.getAndAdd(-amount);
//    }
//
//    public void addSomeBitcakes(int amount) {
//        currentAmount.getAndAdd(amount);
//    }
//
//    public int getCurrentBitcakeAmount() {
//        return currentAmount.get();
//    }
//
//    private Map<Integer, Integer> giveHistory = new ConcurrentHashMap<>();
//    private Map<Integer, Integer> getHistory = new ConcurrentHashMap<>();
//
//    public ABBitcakeManager() {
//        for(Integer neighbor : AppConfig.myServentInfo.getNeighbors()) {
//            giveHistory.put(neighbor, 0);
//            getHistory.put(neighbor, 0);
//        }
//    }
//
//    /*
//     * This value is protected by AppConfig.colorLock.
//     * Access it only if you have the blessing.
//     */
//    public int recordedAmount = 0;
//
//    public void markerEvent(int collectorId, SnapshotCollector snapshotCollector) {
//        synchronized (AppConfig.colorLock) {
//            AppConfig.isWhite.set(false);
//            recordedAmount = getCurrentBitcakeAmount();
//
//            ABSnapshotResult snapshotResult = new ABSnapshotResult(
//                    AppConfig.myServentInfo.getId(), recordedAmount, giveHistory, getHistory);
//
//            if (collectorId == AppConfig.myServentInfo.getId()) {
//                snapshotCollector.addABSnapshotInfo(
//                        AppConfig.myServentInfo.getId(),
//                        snapshotResult);
//            } else {
//
//                Message tellMessage = new ABFinishMessage(
//                        AppConfig.myServentInfo, AppConfig.getInfoById(collectorId), String.valueOf(collectorId), CausalBroadcastShared.getVectorClock(), snapshotResult);
//
//                MessageUtil.sendMessage(tellMessage);
//            }
//
//            for (Integer neighbor : AppConfig.myServentInfo.getNeighbors()) {
//                Message clMarker = new AbTokenMessage(AppConfig.myServentInfo, AppConfig.getInfoById(neighbor), collectorId);
//                MessageUtil.sendMessage(clMarker);
//                try {
//                    /**
//                     * This sleep is here to artificially produce some white node -> red node messages
//                     */
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    private class MapValueUpdater implements BiFunction<Integer, Integer, Integer> {
//
//        private int valueToAdd;
//
//        public MapValueUpdater(int valueToAdd) {
//            this.valueToAdd = valueToAdd;
//        }
//
//        @Override
//        public Integer apply(Integer key, Integer oldValue) {
//            return oldValue + valueToAdd;
//        }
//    }
//
//    public void recordGiveTransaction(int neighbor, int amount) {
//        giveHistory.compute(neighbor, new MapValueUpdater(amount));
//    }
//
//    public void recordGetTransaction(int neighbor, int amount) {
//        getHistory.compute(neighbor, new MapValueUpdater(amount));
//    }
//}
