package servent.message.snapshot;

import app.ServentInfo;
import servent.message.MessageType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AVTerminateCausalMessage extends ACausalMessage{
    public AVTerminateCausalMessage(ServentInfo senderInfo, ServentInfo receiverInfo,  String messageText, Map<Integer, Integer> senderVectorClock) {
        super(MessageType.AV_TERMINATE, senderInfo, receiverInfo, messageText, senderVectorClock);
    }

    @Override
    public ACausalMessage updateVectorClock(Map<Integer, Integer> updatedVectorClock) {
        return new AVTerminateCausalMessage(getOriginalSenderInfo(), getReceiverInfo(), getMessageText(), new ConcurrentHashMap<>(updatedVectorClock));
    }
}
