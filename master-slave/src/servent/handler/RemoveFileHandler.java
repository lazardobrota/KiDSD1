package servent.handler;

import app.AppConfig;
import app.ServentInfo;
import cli.ValueTypes;
import servent.message.*;
import servent.message.util.MessageUtil;

import java.util.Map;

public class RemoveFileHandler implements MessageHandler {

    private Message clientMessage;

    public RemoveFileHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.REMOVE_FILE) {
            AppConfig.timestampedErrorPrint("REMOVE_FILE handler got a message that is not REMOVE_FILE");
            return;
        }

        try {
            int key = Integer.parseInt(clientMessage.getMessageText().split(":")[0]);
            if (AppConfig.chordState.isKeyMine(key)) {
                Map<Integer, String> valueMap = AppConfig.chordState.getValueMap();
                String value = ValueTypes.EMPTY.toString();

                if (valueMap.containsKey(key)) {
                    value = valueMap.remove(key);
                }

                RemoveFileReceiveMessage message = new RemoveFileReceiveMessage(AppConfig.myServentInfo.getListenerPort(), clientMessage.getSenderPort(),
                        key + ":" + value);
                MessageUtil.sendMessage(message);
            } else {
                ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(key);
                RemoveFileMessage message = new RemoveFileMessage(clientMessage.getSenderPort(), nextNode.getListenerPort(), clientMessage.getMessageText());
                MessageUtil.sendMessage(message);
            }
        } catch (NumberFormatException e) {
            AppConfig.timestampedErrorPrint("Got ask get with bad text: " + clientMessage.getMessageText());
        }

    }
}
