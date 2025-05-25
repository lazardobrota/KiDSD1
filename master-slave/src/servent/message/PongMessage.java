package servent.message;

public class PongMessage extends BasicMessage{
    public PongMessage(int senderPort, int receiverPort) {
        super(MessageType.PONG, senderPort, receiverPort, senderPort + ":" + receiverPort);
    }

    public PongMessage(int senderPort, int receiverPort, String fromToPorts) {
        super(MessageType.PONG, senderPort, receiverPort, fromToPorts);
    }
}
