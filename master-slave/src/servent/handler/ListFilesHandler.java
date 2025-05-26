package servent.handler;

import app.AppConfig;
import app.ServentInfo;
import servent.message.FilesGetMessage;
import servent.message.FilesReceiveMessage;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.util.MessageUtil;

public class ListFilesHandler implements MessageHandler{

    private Message clientMessage;

    public ListFilesHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.LIST_FILES) {
            AppConfig.timestampedErrorPrint("LIST_FILES got invalid type: " + clientMessage.getMessageType());
            return;
        }

        int hash = Integer.parseInt(clientMessage.getMessageText());

        if (AppConfig.chordState.isKeyMine(hash)) {
            if (AppConfig.chordState.getValueMap().containsKey(hash))
                MessageUtil.sendMessage(new FilesReceiveMessage(clientMessage.getReceiverPort(), clientMessage.getSenderPort(), hash + ":" + AppConfig.chordState.getValueMap().get(hash)));
            else
                MessageUtil.sendMessage(new FilesReceiveMessage(clientMessage.getReceiverPort(), clientMessage.getSenderPort(), String.valueOf(hash)));
        }
        else {
            ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(hash);
            FilesGetMessage message = new FilesGetMessage(clientMessage.getSenderPort(), nextNode.getListenerPort(), String.valueOf(hash));
            MessageUtil.sendMessage(message);
        }
    }
}
