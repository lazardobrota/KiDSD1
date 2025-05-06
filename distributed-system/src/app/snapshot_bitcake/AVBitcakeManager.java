package app.snapshot_bitcake;

import app.AppConfig;
import app.CausalBroadcastShared;
import app.ServentInfo;
import app.snapshot_bitcake.result.SnapshotResult;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.PendingMessage;
import servent.message.snapshot.AVDoneCausalMessage;
import servent.message.snapshot.AVTerminateCausalMessage;
import servent.message.snapshot.AVTokenCausalMessage;
import servent.message.util.MessageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class AVBitcakeManager implements BitcakeManager {

    private final AtomicInteger currentAmount = new AtomicInteger(1000);

    @Override
    public void takeSomeBitcakes(int amount) {
        currentAmount.getAndAdd(-amount);
    }

    @Override
    public void addSomeBitcakes(int amount) {
        currentAmount.getAndAdd(amount);
    }

    @Override
    public int getCurrentBitcakeAmount() {
        return currentAmount.get();
    }

    public int recordedBitcakeAmount = 0;

    private final Map<Integer, Boolean> closedChannels = new ConcurrentHashMap<>();
    //    private final Map<String, List<Integer>> allChannelTransactions = new ConcurrentHashMap<>();
    private final Object allChannelTransactionsLock = new Object();
    private List<ServentInfo> sendBackRoute = new ArrayList<>();

    /**
     * This is invoked when we are white and get a marker. Basically,
     * we or someone alse have started recording a snapshot.
     * This method does the following:
     * <ul>
     * <li>Makes us red</li>
     * <li>Records our bitcakes</li>
     * <li>Sets all channels to not closed</li>
     * <li>Sends markers to all neighbors</li>
     * </ul>
     *
     * @param collectorId - id of collector node, to be put into marker messages for others.
     */
    public void startSnapshotEvent(int collectorId) {
        synchronized (AppConfig.colorLock) {
            AppConfig.timestampedStandardPrint("Going red");
            AppConfig.isWhite.set(false);
            recordedBitcakeAmount = getCurrentBitcakeAmount();
            List<Message> messageList = new ArrayList<>();

            for (Integer neighbor : AppConfig.myServentInfo.getNeighbors()) {
                closedChannels.put(neighbor, false);
                Message avTokenCausalMessage = new AVTokenCausalMessage(AppConfig.myServentInfo, AppConfig.getInfoById(neighbor),
                        List.of(AppConfig.myServentInfo), String.valueOf(collectorId), CausalBroadcastShared.getVectorClock());

                messageList.add(avTokenCausalMessage);
            }

            CausalBroadcastShared.addPendingMessage(new PendingMessage(true, messageList, null));
            CausalBroadcastShared.checkPendingMessages();
        }
    }

    public void snapshotEvent(int collectorId, Message clientMessage) {
        synchronized (AppConfig.colorLock) {
            AppConfig.timestampedStandardPrint("Going red");
            AppConfig.isWhite.set(false);
            recordedBitcakeAmount = getCurrentBitcakeAmount();
            List<ServentInfo> updatedRoute = new ArrayList<>(clientMessage.getRoute());
            updatedRoute.add(AppConfig.myServentInfo);
            List<Message> messageList = new ArrayList<>();

            for (Integer neighbor : AppConfig.myServentInfo.getNeighbors()) {
                closedChannels.put(neighbor, false);
                Message avTokenCausalMessage = new AVTokenCausalMessage(AppConfig.myServentInfo, AppConfig.getInfoById(neighbor),
                        updatedRoute, String.valueOf(collectorId), CausalBroadcastShared.getVectorClock());

                messageList.add(avTokenCausalMessage);
            }

            CausalBroadcastShared.addPendingMessage(new PendingMessage(true, messageList, null));
            CausalBroadcastShared.checkPendingMessages();
        }
    }

    /**
     * This is invoked whenever we get a marker from another node. We do the following:
     * <ul>
     * <li>If we are white, we do markerEvent()</li>
     * <li>We mark the channel of the person that sent the marker as closed</li>
     * <li>If we are done, we report our snapshot result to the collector</li>
     * </ul>
     */
    public void handleSnapshot(Message clientMessage, SnapshotCollector snapshotCollector) {
        synchronized (AppConfig.colorLock) {
            int collectorId = Integer.parseInt(clientMessage.getMessageText());

            if (AppConfig.isWhite.get()) {
                snapshotEvent(collectorId, clientMessage);
                sendBackRoute = clientMessage.getRoute();
            }

            closedChannels.put(clientMessage.getOriginalSenderInfo().getId(), true);

            if (isDone()) {
                SnapshotResult snapshotResult = new SnapshotResult(
                        AppConfig.myServentInfo.getId(), recordedBitcakeAmount, CalculateChannelMessages(CausalBroadcastShared.getPendingMessages()));

                if (AppConfig.myServentInfo.getId() == collectorId) {
                    snapshotCollector.addCCSnapshotInfo(collectorId, snapshotResult);
                } else {
                    Message avDoneCausalMessage = new AVDoneCausalMessage(
                            AppConfig.myServentInfo, sendBackRoute.getLast(), sendBackRoute, String.valueOf(collectorId),
                            CausalBroadcastShared.getVectorClock(), snapshotResult);

                    CausalBroadcastShared.addPendingMessage(new PendingMessage(true, avDoneCausalMessage, null));
                    CausalBroadcastShared.checkPendingMessages();
                }

                recordedBitcakeAmount = 0;
//                allChannelTransactions.clear();
                sendBackRoute = new ArrayList<>();
            }
        }
    }

    //TODO When changing to white node we have white -> red which means in transaction items will be send to AllChannelTransaction
    // so some amount of bitcakes wont be saved since Transaction doesnt look into AllChannelTransaction i think?
    public void handleTerminate(int collectorId) {
        synchronized (AppConfig.colorLock) {

            if (AppConfig.isWhite.get())
                return;

            AppConfig.timestampedStandardPrint("Going white");
            AppConfig.isWhite.set(true);

            AppConfig.timestampedStandardPrint("Telling neighbors to terminate(resume)");
            List<Message> messageList = new ArrayList<>();

            for (Integer neighbor : AppConfig.myServentInfo.getNeighbors()) {
                AVTerminateCausalMessage avTerminateCausalMessage = new AVTerminateCausalMessage(AppConfig.myServentInfo, AppConfig.getInfoById(neighbor),
                        String.valueOf(collectorId), CausalBroadcastShared.getVectorClock());

                messageList.add(avTerminateCausalMessage);
            }

            CausalBroadcastShared.addPendingMessage(new PendingMessage(true, messageList, null));
            CausalBroadcastShared.checkPendingMessages();
        }
    }

    /**
     * Checks if we are done being red. This happens when all channels are closed.
     *
     * @return if snapshot is done or not or was never even in snapshot mode
     */
    private boolean isDone() {
        if (AppConfig.isWhite.get()) {
            return false;
        }
        AppConfig.timestampedStandardPrint(closedChannels.toString());
        for (Map.Entry<Integer, Boolean> closedChannel : closedChannels.entrySet()) {
            if (!closedChannel.getValue()) {
                return false;
            }
        }
        return true;
    }

    public Map<String, List<Integer>> CalculateChannelMessages(Queue<PendingMessage> pendingMessages) {
        Map<String, List<Integer>> allChannelTransactions = new ConcurrentHashMap<>();
        while (!pendingMessages.isEmpty()) {
            PendingMessage pendingMessage = pendingMessages.poll();

            if (pendingMessage.getMessage().getMessageType() == MessageType.TRANSACTION) {
                synchronized (allChannelTransactionsLock) {
                    String channelName = "channel " + AppConfig.myServentInfo.getId() + "<-" + pendingMessage.getMessage().getOriginalSenderInfo().getId();

                    List<Integer> channelMessages = allChannelTransactions.getOrDefault(channelName, new ArrayList<>());
                    channelMessages.add(Integer.parseInt(pendingMessage.getMessage().getMessageText()));
                    allChannelTransactions.put(channelName, channelMessages);
                }
            }
        }

        return allChannelTransactions;
    }
}
