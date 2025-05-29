package servent.handler;

import app.AppConfig;
import app.ServentInfo;
import servent.message.AskListFilesMessage;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.util.MessageUtil;

public class AskListFilesErrorHandler implements MessageHandler{

    private Message clientMessage;

    public AskListFilesErrorHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.ASK_LIST_FILES_ERROR) {
            AppConfig.timestampedErrorPrint("ASK_LIST_FILES_ERROR got invalid type: " + clientMessage.getMessageType());
            return;
        }

        AppConfig.timestampedStandardPrint(clientMessage.getMessageText());
    }
}
