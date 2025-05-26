package servent.handler;

import app.AppConfig;
import app.ServentInfo;
import servent.message.*;
import servent.message.util.MessageUtil;

public class AskListFilesHandler implements MessageHandler{

    private Message clientMessage;

    public AskListFilesHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.ASK_LIST_FILES) {
            AppConfig.timestampedErrorPrint("ASK_LIST_FILES got invalid type: " + clientMessage.getMessageType());
            return;
        }

        int chordId = Integer.parseInt(clientMessage.getMessageText());

        if (AppConfig.myServentInfo.getChordId() == chordId)
            AppConfig.chordState.getUploadListOfPaths(clientMessage.getSenderPort(), false);
        else if (!AppConfig.chordState.isKeyMine(chordId)) {
            ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(chordId);
            MessageUtil.sendMessage(new AskListFilesMessage(clientMessage.getSenderPort(), nextNode.getListenerPort(), String.valueOf(chordId)));
        }
        else {
            AppConfig.timestampedErrorPrint("ASK_LIST_FILES Invalid port");
        }
    }
}
