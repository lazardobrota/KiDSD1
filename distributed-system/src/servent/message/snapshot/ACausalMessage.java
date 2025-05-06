package servent.message.snapshot;

import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;
import servent.message.TransactionMessage;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ACausalMessage extends BasicMessage {

    private final Map<Integer, Integer> senderVectorClock;

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


    public Map<Integer, Integer> getSenderVectorClock() {
        return senderVectorClock;
    }

    public abstract ACausalMessage updateVectorClock(Map<Integer, Integer> updatedVectorClock);

    @Override
    public String toString() {
        return "[" + getOriginalSenderInfo().getId() + "|" + getMessageId() + "|" +
                getMessageText() + "|" + getMessageType() + "|" + getRoute().stream().map(ServentInfo::getId).toList() + "|" +
                senderVectorClock.toString() + "|" +
                getReceiverInfo().getId() + "]";
    }
}
