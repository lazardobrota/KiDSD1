package servent.message;

public class FilesGetMessage extends BasicMessage {


    public FilesGetMessage(int senderPort, int receiverPort, String text) {
        super(MessageType.LIST_FILES, senderPort, receiverPort, text);
    }
}
