package servent.message.snapshot;

import app.ServentInfo;
import servent.message.MessageType;

import java.util.List;
import java.util.Map;

public class AVTerminateCausalMessage extends ACausalMessage{
    public AVTerminateCausalMessage(ServentInfo senderInfo, ServentInfo receiverInfo,  String messageText, Map<Integer, Integer> senderVectorClock) {
        super(MessageType.AV_TERMINATE, senderInfo, receiverInfo, messageText, senderVectorClock);
    }
}
