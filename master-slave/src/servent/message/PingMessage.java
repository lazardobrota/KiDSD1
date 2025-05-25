package servent.message;

public class PingMessage extends BasicMessage{
    public PingMessage(int senderPort, int receiverPort) {
        super(MessageType.PING, senderPort, receiverPort, senderPort + ":" + receiverPort);
    }

    public PingMessage(int senderPort, int receiverPort, String fromToPorts) {
        super(MessageType.PING, senderPort, receiverPort, fromToPorts);
    }
}
