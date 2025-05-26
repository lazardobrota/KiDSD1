package servent.message;

public class RemoveFileReceiveMessage extends BasicMessage {

    public RemoveFileReceiveMessage(int senderPort, int receiverPort, String keyAndValue) {
        super(MessageType.REMOVE_FILE_RECEIVE, senderPort, receiverPort, keyAndValue);
    }
}