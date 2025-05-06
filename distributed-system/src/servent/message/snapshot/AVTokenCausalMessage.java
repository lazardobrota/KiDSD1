package servent.message.snapshot;

import app.ServentInfo;
import servent.message.MessageType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AVTokenCausalMessage extends ACausalMessage {

    public AVTokenCausalMessage(ServentInfo senderInfo, ServentInfo receiverInfo, List<ServentInfo> routeList,
                                String messageText, Map<Integer, Integer> senderVectorClock) {
        super(MessageType.AV_TOKEN, senderInfo, receiverInfo, routeList, messageText, senderVectorClock);
    }

    @Override
    public ACausalMessage updateVectorClock(Map<Integer, Integer> updatedVectorClock) {
        return new AVTokenCausalMessage(getOriginalSenderInfo(), getReceiverInfo(), getRoute(), getMessageText(), new ConcurrentHashMap<>(updatedVectorClock));
    }
}
