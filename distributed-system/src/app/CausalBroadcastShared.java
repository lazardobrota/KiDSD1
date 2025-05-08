package app;

import servent.message.Message;
import servent.message.MessageType;
import servent.message.PendingMessage;
import servent.message.snapshot.ACausalMessage;
import servent.message.util.MessageUtil;

import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CausalBroadcastShared {
    private final static Map<Integer, Integer> vectorClock = new ConcurrentHashMap<>();
    //    private final static List<Message> commitedCausalMessageList = new CopyOnWriteArrayList<>();
    private final static Queue<PendingMessage> pendingMessages = new ConcurrentLinkedQueue<>();
    private final static Object pendingMessagesLock = new Object();

    public static void initializeVectorClock(int serventCount) {
        for (int i = 0; i < serventCount; i++) {
            vectorClock.put(i, 0);
        }
    }

    public static void incrementClock(int serventId) {
        vectorClock.computeIfPresent(serventId, (key, oldValue) -> oldValue + 1);
        AppConfig.timestampedStandardPrint("Increment...");
        AppConfig.timestampedStandardPrint(vectorClock.toString());
    }

    //TODO Check if this works. Also myId should always be bigger or equal to otherVectorClock
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

    public static Queue<PendingMessage> getPendingMessages() {
        return pendingMessages;
    }

//    public static List<Message> getCommitedCausalMessages() {
//        List<Message> toReturn = new CopyOnWriteArrayList<>(commitedCausalMessageList);
//
//        return toReturn;
//    }

    public static void addPendingMessage(PendingMessage msg) {
        pendingMessages.add(msg);
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

    public static void checkPendingMessages() {
        boolean gotWork = true;
        boolean updateClock = false;
        PendingMessage pendingMessage = null;

        while (gotWork) {
            gotWork = false;
            updateClock = false;

            //Find valid pendingMessage
            synchronized (pendingMessagesLock) {
                Iterator<PendingMessage> iterator = pendingMessages.iterator();

                Map<Integer, Integer> myVectorClock = getVectorClock();
                while (iterator.hasNext()) {
                    pendingMessage = iterator.next();
                    ACausalMessage causalPendingMessage = pendingMessage.getMessage() != null ?
                            (ACausalMessage) pendingMessage.getMessage()
                            : (ACausalMessage) pendingMessage.getSendMessagesList().getFirst();

                    int senderId = causalPendingMessage.getOriginalSenderInfo().getId();

                    if (AppConfig.isWhite.get()) {

                        gotWork = true;
                        updateClock = true;
                        iterator.remove();
                        break;

                    } else if (!AppConfig.isWhite.get() && (causalPendingMessage.getMessageType() == MessageType.AV_TOKEN ||
                            causalPendingMessage.getMessageType() == MessageType.AV_DONE ||
                            causalPendingMessage.getMessageType() == MessageType.AV_TERMINATE)) { //Dont update clock

                        gotWork = true;
                        updateClock = false;
                        iterator.remove();
                        break;

                    } else if (!AppConfig.isWhite.get() && isMessageInPast(senderId, myVectorClock, causalPendingMessage.getSenderVectorClock())) {//Update clock

                        gotWork = true;
                        updateClock = true;
                        iterator.remove();
                        break;
                    }
                }

                //PendingMessage converting to Commited
//                if (gotWork) {
//                    if (pendingMessage.isSending())
//                        sendingMessage(pendingMessage);
//                    else
//                        receiveMessage(pendingMessage, updateClock);
//                }
            }

            //PendingMessage converting to Commited
            if (gotWork) {
                if (pendingMessage.isSending())
                    sendingMessage(pendingMessage);
                else
                    receiveMessage(pendingMessage, updateClock);
            }
        }
    }

    private static void sendingMessage(PendingMessage pendingMessage) {

        incrementClock(AppConfig.myServentInfo.getId());

        if (pendingMessage.getMessage() != null) {
            AppConfig.timestampedStandardPrint("Committing Send: " + pendingMessage.getMessage());

            ((ACausalMessage) pendingMessage.getMessage()).setSenderVectorClock(vectorClock);
            MessageUtil.sendMessage(pendingMessage.getMessage());
            return;
        }

        for (Message messageToSend : pendingMessage.getSendMessagesList()) {
            AppConfig.timestampedStandardPrint("Committing Send: " + messageToSend);

            ((ACausalMessage) messageToSend).setSenderVectorClock(vectorClock);
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

    private static void receiveMessage(PendingMessage pendingMessage, boolean updateClock) {
        ACausalMessage causalPendingMessage = (ACausalMessage) pendingMessage.getMessage();

        AppConfig.timestampedStandardPrint("Committing Receive: " + pendingMessage.getMessage());

        if (updateClock)
            updateVectorClock(AppConfig.myServentInfo.getId(), causalPendingMessage.getSenderVectorClock());

        pendingMessage.getCausalMessageHandler().continueExecution();
    }
}
