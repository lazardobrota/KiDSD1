package servent.message;

public class RemoveFileMessage extends BasicMessage {

    public RemoveFileMessage(int senderPort, int receiverPort, String keyAndValue) {
        super(MessageType.REMOVE_FILE, senderPort, receiverPort, keyAndValue);
    }
}
