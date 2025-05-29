package servent.message;

public class AskListFilesErrorMessage extends BasicMessage {

    public AskListFilesErrorMessage(int senderPort, int receiverPort, String text) {
        super(MessageType.ASK_LIST_FILES_ERROR, senderPort, receiverPort, text);
    }
}
