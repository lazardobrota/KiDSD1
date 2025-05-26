package servent.handler;

import app.AppConfig;
import servent.message.Message;
import servent.message.MessageType;

public class ListFilesReceiveHandler implements MessageHandler{

    private Message clientMessage;

    public ListFilesReceiveHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.LIST_FILES_RECEIVE) {
            AppConfig.timestampedErrorPrint("LIST_FILES got invalid type: " + clientMessage.getMessageType());
            return;
        }

        AppConfig.timestampedStandardPrint("List: " + clientMessage.getMessageText());
    }
}
