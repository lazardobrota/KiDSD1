package servent.message.snapshot;

import app.ServentInfo;
import servent.message.MessageType;

import java.util.List;
import java.util.Map;

public class AVTokenCausalMessage extends ACausalMessage {

    public AVTokenCausalMessage(ServentInfo senderInfo, ServentInfo receiverInfo, List<ServentInfo> routeList,
                                String messageText, Map<Integer, Integer> senderVectorClock) {
        super(MessageType.AV_TOKEN, senderInfo, receiverInfo, routeList, messageText, senderVectorClock);
    }
}
