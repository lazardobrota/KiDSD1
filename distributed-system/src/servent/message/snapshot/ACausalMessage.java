package servent.message.snapshot;

import app.CausalBroadcastShared;
import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;
import servent.message.TransactionMessage;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ACausalMessage extends BasicMessage {

    private Map<Integer, Integer> senderVectorClock;
    private int tpcNumber;

    public ACausalMessage(MessageType type, ServentInfo senderInfo, ServentInfo receiverInfo, List<ServentInfo> routes, String messageText,
                          Map<Integer, Integer> senderVectorClock) {
        super(type, senderInfo, receiverInfo, routes, messageText);

        this.senderVectorClock = senderVectorClock;
    }

    public ACausalMessage(MessageType type, ServentInfo senderInfo, ServentInfo receiverInfo, String messageText,
                          Map<Integer, Integer> senderVectorClock) {
        super(type, senderInfo, receiverInfo, messageText);

        this.senderVectorClock = senderVectorClock;
    }

    public ACausalMessage(MessageType type, ServentInfo senderInfo, ServentInfo receiverInfo, String messageText) {
        super(type, senderInfo, receiverInfo, messageText);

        this.senderVectorClock = new ConcurrentHashMap<>();
    }

    public ACausalMessage(MessageType messageType, ServentInfo sender, ServentInfo receiver, boolean white, List<ServentInfo> routeList, String messageText, int messageId, Map<Integer, Integer> senderVectorClock) {
        super(messageType, sender, receiver, white, routeList, messageText, messageId);

        this.senderVectorClock = senderVectorClock;
    }


    public Map<Integer, Integer> getSenderVectorClock() {
        return senderVectorClock;
    }

    public int getTpcNumber() {
        return tpcNumber;
    }

    public void setSenderVectorClock(Map<Integer, Integer> senderVectorClock) {
        this.senderVectorClock = senderVectorClock;
    }

    public void setTpcNumber(int tpcNumber) {
        this.tpcNumber = tpcNumber;
    }

    @Override
    public String toString() {
        return "[" + getOriginalSenderInfo().getId() + "|" + getMessageId() + "|" +
                getMessageText() + "|" + getMessageType() + "|" + getRoute().stream().map(ServentInfo::getId).toList() + "|" +
                senderVectorClock.toString() + "|" + "Send tpc num: " + tpcNumber + "|" +
                getReceiverInfo().getId() + "]";
    }
}
