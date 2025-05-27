package servent.message;

public class FollowMessage extends BasicMessage{

    public FollowMessage(int senderPort, int receiverPort, int portOfNodeToFollow) {
        super(MessageType.FOLLOW, senderPort, receiverPort, String.valueOf(portOfNodeToFollow));
    }

    public FollowMessage(int senderPort, int receiverPort, String portOfNodeToFollow) {
        super(MessageType.FOLLOW, senderPort, receiverPort, portOfNodeToFollow);
    }
}
