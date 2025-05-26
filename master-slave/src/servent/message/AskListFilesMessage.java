package servent.message;

public class AskListFilesMessage extends BasicMessage {

    public AskListFilesMessage(int senderPort, int receiverPort, String text) {
        super(MessageType.ASK_LIST_FILES, senderPort, receiverPort, text);
    }
}
