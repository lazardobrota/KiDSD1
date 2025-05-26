package servent.handler;

import app.AppConfig;
import app.ServentInfo;
import cli.ValueTypes;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.RemoveFileMessage;
import servent.message.RemoveFileReceiveMessage;
import servent.message.util.MessageUtil;

import java.util.Map;

public class RemoveFileReceiveHandler implements MessageHandler {

    private Message clientMessage;

    public RemoveFileReceiveHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.REMOVE_FILE_RECEIVE) {
            AppConfig.timestampedErrorPrint("REMOVE_FILE_RECEIVE handler got a message that is: " + clientMessage.getMessageType());
            return;
        }

        if (clientMessage.getMessageText().equals(ValueTypes.EMPTY.toString())) {
            AppConfig.timestampedStandardPrint("File doesn't exist");
        }
        else {
            String[] keyAndValue = clientMessage.getMessageText().split(":");
            AppConfig.timestampedStandardPrint("Removing: " + keyAndValue[1]);
        }

    }
}
