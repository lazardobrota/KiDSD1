package servent.message;

public class FollowFoundMessage extends BasicMessage{

    public FollowFoundMessage(int senderPort, int receiverPort, String messageText) {
        super(MessageType.FOLLOW_FOUND, senderPort, receiverPort, messageText);
    }
}
