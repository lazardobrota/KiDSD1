package servent.message;

import java.util.Map;

public class CopyDataMessage extends BasicMessage {

    private final Map<Integer, String> valueMap;

    public CopyDataMessage(int senderPort, int receiverPort, Map<Integer, String> valueMap) {
        super(MessageType.COPY_DATA, senderPort, receiverPort);
        this.valueMap = valueMap;
    }

    public Map<Integer, String> getValueMap() {
        return valueMap;
    }
}
