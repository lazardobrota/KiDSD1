package app;

import servent.message.Message;
import servent.message.MessageType;
import servent.message.PendingMessage;
import servent.message.snapshot.ACausalMessage;
import servent.message.util.MessageUtil;

import java.util.*;
import java.util.concurrent.*;

public class CausalBroadcastShared {
    private final static Map<Integer, Integer> vectorClock = new ConcurrentHashMap<>();
    private final static Map<Integer, int[]> neighborTcp = new ConcurrentHashMap<>();
    private final static BlockingQueue<PendingMessage> commitedCausalMessageQueue = new LinkedBlockingQueue<>();
    private final static Queue<PendingMessage> pendingSendMessages = new ConcurrentLinkedQueue<>();
    private final static Queue<PendingMessage> pendingReceiveMessages = new ConcurrentLinkedQueue<>();
    private final static Object pendingMessagesLock = new Object();
    private final static Set<MessageType> messageTypesToIgnoreSnapshot = new HashSet<>(Set.of(
            MessageType.AV_TOKEN, MessageType.AV_DONE, MessageType.AV_TERMINATE,
            MessageType.AB_TOKEN, MessageType.AB_ACK, MessageType.AB_RESUME
    ));

    public static void initializeVectorClocks(int serventCount) {
        for (int i = 0; i < serventCount; i++) {
            vectorClock.put(i, 0);
        }
    }

    public static void initializeNeighborTcp() {
        for (int serventId : AppConfig.myServentInfo.getNeighbors())
            neighborTcp.put(serventId, new int[]{0, 0});
    }

    public static void incrementClock(int serventId) {
        vectorClock.computeIfPresent(serventId, (key, oldValue) -> oldValue + 1);
        AppConfig.timestampedStandardPrint("Increment... " + vectorClock);
    }

    public static int incrementSendAndGet(int neighborId) {
        incrementClock(AppConfig.myServentInfo.getId());
        neighborTcp.compute(neighborId, (id, sendAndReceive) -> {
            if (sendAndReceive == null) return new int[]{1, 0};
            sendAndReceive[0]++;
            return sendAndReceive;
        });
        AppConfig.timestampedStandardPrint("Increment send... " + AppConfig.myServentInfo.getId() + "->" + neighborId + " " + Arrays.toString(neighborTcp.get(neighborId)));

        return getNeighborTpcSend(neighborId);
    }

    public static int incrementReceiveAndGet(int neighborId) {
        neighborTcp.compute(neighborId, (id, sendAndReceive) -> {
            if (sendAndReceive == null) return new int[]{0, 1};
            sendAndReceive[1]++;
            return sendAndReceive;
        });
        AppConfig.timestampedStandardPrint("Increment receive... " + neighborId + "->" + AppConfig.myServentInfo.getId() + " " + Arrays.toString(neighborTcp.get(neighborId)));

        return getNeighborTpcReceive(neighborId);
    }

    public static int getNeighborTpcSend(int neighborId) {
        return neighborTcp.getOrDefault(neighborId, new int[]{0, 0})[0];
    }

    public static int getNeighborTpcReceive(int neighborId) {
        return neighborTcp.getOrDefault(neighborId, new int[]{0, 0})[1];
    }

    public static BlockingQueue<PendingMessage> GetCommitedCausalMessageQueue() {
        return commitedCausalMessageQueue;
    }

    private static void updateVectorClock(int myId, Map<Integer, Integer> otherVectorClock) {
        if (vectorClock.size() != otherVectorClock.size()) {
            throw new IllegalArgumentException("Clocks are not same size how why");
        }

        for (int i = 0; i < vectorClock.size(); i++) {
//            if (i != myId)
            vectorClock.put(i, Math.max(vectorClock.get(i), otherVectorClock.get(i)));
        }

        AppConfig.timestampedStandardPrint("Updating Vector Clock...");
        AppConfig.timestampedStandardPrint(vectorClock.toString());

//        incrementClock(myId);
    }

    public static Map<Integer, Integer> getVectorClock() {
        return vectorClock;
    }

    public static Map<Integer, int[]> getNeighborTcp() {
        return neighborTcp;
    }

    public static Queue<PendingMessage> getPendingReceiveMessages() {
        return pendingReceiveMessages;
    }

//    public static List<Message> getCommitedCausalMessages() {
//        List<Message> toReturn = new CopyOnWriteArrayList<>(commitedCausalMessageList);
//
//        return toReturn;
//    }

    public static void addPendingMessageAndCheck(PendingMessage pendingMessage) {


        if (pendingMessage.isSending())
            pendingSendMessages.add(pendingMessage);
        else {
            pendingReceiveMessages.add(pendingMessage);
            AppConfig.timestampedStandardPrint("Curr for: " + pendingMessage.getMessage() + " - " + Arrays.toString(neighborTcp.getOrDefault(pendingMessage.getMessage().getOriginalSenderInfo().getId(), new int[]{0, 0})));
        }

        checkPendingMessages();
    }

//    public static void sendMessage(Message newMessage) {
//        AppConfig.timestampedStandardPrint("Committing " + newMessage);
//        MessageUtil.sendMessage(newMessage);
//        incrementClock(newMessage.getOriginalSenderInfo().getId());
//
//        checkPendingMessages();
//    }


//    public static void commitCausalMessage(Message newMessage) {
//        AppConfig.timestampedStandardPrint("Committing " + newMessage);
//        commitedCausalMessageList.add(newMessage);
//        incrementClock(newMessage.getOriginalSenderInfo().getId());
//
//        checkPendingMessages();
//    }

    private static boolean isMessageInPast(int senderId, Map<Integer, Integer> myClock, Map<Integer, Integer> messageClock) {
        if (myClock.size() != messageClock.size()) {
            throw new IllegalArgumentException("Clocks are not same size how why");
        }

        return messageClock.get(senderId) <= myClock.get(senderId);
    }

    private static boolean isMessageReady(int senderId, int senderTcpNumber) {
        // <= valid also
        return senderTcpNumber == getNeighborTpcReceive(senderId) + 1;
    }

//    public static void checkPendingMessages() {
//        boolean gotWork = true;
//        PendingMessage pendingMessage = null;
//
//        while (gotWork) {
//            gotWork = false;
//
//            //Find valid pendingMessage
//            synchronized (pendingMessagesLock) {
//                int i = 0;
//                while (!gotWork && i < 2) {
////                    Iterator<PendingMessage> iterator = !pendingSendMessages.isEmpty() ? pendingSendMessages.iterator() : pendingReceiveMessages.iterator();
//                    Iterator<PendingMessage> iterator = i == 0 ? pendingSendMessages.iterator() : pendingReceiveMessages.iterator();
//                    i++;
//
//                    while (iterator.hasNext()) {
//                        pendingMessage = iterator.next();
//                        ACausalMessage causalPendingMessage = pendingMessage.getMessage() != null ?
//                                (ACausalMessage) pendingMessage.getMessage()
//                                : (ACausalMessage) pendingMessage.getSendMessagesList().getFirst();
//
//                        int senderId = causalPendingMessage.getOriginalSenderInfo().getId();
//
//                        if (AppConfig.isWhite.get()) {
//
//                            gotWork = true;
//                            pendingMessage.setUpdateVectorClock(true);
//                            commitedCausalMessageQueue.add(pendingMessage);
//                            iterator.remove();
//                            break;
//
//                        } else if (!AppConfig.isWhite.get() && pendingMessage.isSending() && messageTypesToIgnoreSnapshot.contains(causalPendingMessage.getMessageType())) { //Dont update clock
//
//                            gotWork = true;
//                            pendingMessage.setUpdateVectorClock(false);
//                            commitedCausalMessageQueue.add(pendingMessage);
//                            iterator.remove();
//                            break;
//
//                        } else if (!AppConfig.isWhite.get() && !pendingMessage.isSending() && isMessageReady(senderId, causalPendingMessage.getTpcNumber())) {//Update clock
//
//                            gotWork = true;
//                            pendingMessage.setUpdateVectorClock(!messageTypesToIgnoreSnapshot.contains(causalPendingMessage.getMessageType()));
//                            commitedCausalMessageQueue.add(pendingMessage);
//                            iterator.remove();
//                            break;
//                        }
//                    }
//                }
//
//
//                //PendingMessage converting to Commited
////                if (gotWork) {
////                    if (pendingMessage.isSending())
////                        sendingMessage(pendingMessage);
////                    else
////                        receiveMessage(pendingMessage, updateClock);
////                }
//            }
//
//            //PendingMessage converting to Commited

    /// /            if (gotWork) {
    /// /                if (pendingMessage.isSending())
    /// /                    sendingMessage(pendingMessage);
    /// /                else
    /// /                    receiveMessage(pendingMessage, updateClock);
    /// /            }
//        }
//    }
    public static void checkPendingMessages() {
        boolean gotWork = true;

        while (gotWork) {
            gotWork = false;

            synchronized (pendingMessagesLock) {
                gotWork = checkSendMessages();

                if (!gotWork)
                    gotWork = checkReceiveMessage();
            }
        }
    }

    private static boolean checkSendMessages() {
        boolean gotWork = false;
        PendingMessage pendingMessage = null;

        //Find valid pendingMessage
        Iterator<PendingMessage> iterator = pendingSendMessages.iterator();
        while (iterator.hasNext()) {
            pendingMessage = iterator.next();
            ACausalMessage causalPendingMessage = pendingMessage.getMessage() != null ?
                    (ACausalMessage) pendingMessage.getMessage()
                    : (ACausalMessage) pendingMessage.getSendMessagesList().getFirst();

            int senderId = causalPendingMessage.getOriginalSenderInfo().getId();

            if (AppConfig.isWhite.get()) {
                gotWork = true;
                commitedCausalMessageQueue.add(pendingMessage);
                iterator.remove();
                break;
            } else if (!AppConfig.isWhite.get() && messageTypesToIgnoreSnapshot.contains(causalPendingMessage.getMessageType()))
            {
                gotWork = true;
                commitedCausalMessageQueue.add(pendingMessage);
                iterator.remove();
                break;
            }
        }

        return gotWork;
    }

    private static boolean checkReceiveMessage() {
        boolean gotWork = false;
        PendingMessage pendingMessage = null;

        Iterator<PendingMessage> iterator = pendingReceiveMessages.iterator();
        while (iterator.hasNext()) {
            pendingMessage = iterator.next();
            ACausalMessage causalPendingMessage = pendingMessage.getMessage() != null ?
                    (ACausalMessage) pendingMessage.getMessage()
                    : (ACausalMessage) pendingMessage.getSendMessagesList().getFirst();

            int senderId = causalPendingMessage.getOriginalSenderInfo().getId();

            if (isMessageReady(senderId, causalPendingMessage.getTpcNumber())) {
                gotWork = true;
                pendingMessage.setUpdateVectorClock(true);
                incrementReceiveAndGet(pendingMessage.getMessage().getOriginalSenderInfo().getId());
                commitedCausalMessageQueue.add(pendingMessage);
                iterator.remove();
                break;
            }
        }

        return gotWork;
    }

    public static void sendingMessage(PendingMessage pendingMessage) {

        if (pendingMessage.getMessage() != null) {
            int neighborTcp = incrementSendAndGet(pendingMessage.getMessage().getReceiverInfo().getId());
            ((ACausalMessage) pendingMessage.getMessage()).setSenderVectorClock(vectorClock);
            ((ACausalMessage) pendingMessage.getMessage()).setTpcNumber(neighborTcp);

            AppConfig.timestampedStandardPrint("Committing Send: " + pendingMessage.getMessage());
            if (!AppConfig.IS_FIFO)
                pendingMessage.getMessage().sendEffect();
            MessageUtil.sendMessage(pendingMessage.getMessage());
            return;
        }

        for (Message messageToSend : pendingMessage.getSendMessagesList()) {
            int neighborTcp = incrementSendAndGet(messageToSend.getReceiverInfo().getId());
            ((ACausalMessage) messageToSend).setSenderVectorClock(vectorClock);
            ((ACausalMessage) messageToSend).setTpcNumber(neighborTcp);

            AppConfig.timestampedStandardPrint("Committing Send: " + messageToSend);
            if (!AppConfig.IS_FIFO)
                messageToSend.sendEffect();
            MessageUtil.sendMessage(messageToSend);

            try {
                /**
                 * This sleep is here to artificially produce some white node -> red node messages
                 */
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void receiveMessage(PendingMessage pendingMessage, boolean updateClock) {
        ACausalMessage causalPendingMessage = (ACausalMessage) pendingMessage.getMessage();

        AppConfig.timestampedStandardPrint("Committing Receive: " + pendingMessage.getMessage());

        if (updateClock)
            updateVectorClock(AppConfig.myServentInfo.getId(), causalPendingMessage.getSenderVectorClock());

        pendingMessage.getCausalMessageHandler().continueExecution();
    }
}
