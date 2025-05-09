package servent.message.snapshot;

import app.ServentInfo;
import servent.message.MessageType;

import java.util.Map;

public class ABResumeMessage extends ACausalMessage{
    public ABResumeMessage(ServentInfo senderInfo, ServentInfo receiverInfo, String messageText, Map<Integer, Integer> senderVectorClock) {
        super(MessageType.AB_RESUME, senderInfo, receiverInfo, messageText, senderVectorClock);
    }
}
