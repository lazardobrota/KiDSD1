package servent.message;

public class KillMessage extends BasicMessage{
    public KillMessage(int senderPort, int receiverPort, int portOfNodeToKill) {
        super(MessageType.KILL, senderPort, receiverPort, String.valueOf(portOfNodeToKill));
    }
}
