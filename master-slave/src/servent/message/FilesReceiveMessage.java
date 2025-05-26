package servent.message;

public class FilesReceiveMessage extends BasicMessage {

    public FilesReceiveMessage(int senderPort, int receiverPort, String text) {
        super(MessageType.LIST_FILES_RECEIVE, senderPort, receiverPort, text);
    }
}
