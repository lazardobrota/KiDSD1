package servent.handler;

import app.AppConfig;
import servent.message.CopyDataMessage;
import servent.message.Message;
import servent.message.MessageType;

public class CopyDataHandler implements MessageHandler{

    private Message clientMessage;

    public CopyDataHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.COPY_DATA) {
            AppConfig.timestampedErrorPrint("FOLLOW_FOUND got invalid type: " + clientMessage.getMessageType());
            return;
        }

        CopyDataMessage copyDataMessage = (CopyDataMessage) clientMessage;

        if (AppConfig.chordState.getPredecessor().getListenerPort() == clientMessage.getSenderPort())
            AppConfig.chordState.setAnticlockwiseCopyValueMap(copyDataMessage.getValueMap());
        else if (AppConfig.chordState.getNextNodePort() == clientMessage.getSenderPort())
            AppConfig.chordState.setClockwiseCopyValueMap(copyDataMessage.getValueMap());
        else
            AppConfig.timestampedErrorPrint("Who send their copy of data: " + clientMessage.getSenderPort());
    }

}
