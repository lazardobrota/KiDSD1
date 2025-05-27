package servent.handler;

import app.AppConfig;
import servent.message.Message;
import servent.message.MessageType;

public class FollowFoundHandler implements MessageHandler{

    private Message clientMessage;

    public FollowFoundHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.FOLLOW_FOUND) {
            AppConfig.timestampedErrorPrint("FOLLOW_FOUND got invalid type: " + clientMessage.getMessageType());
            return;
        }

        AppConfig.timestampedStandardPrint(clientMessage.getMessageText());
    }

}
