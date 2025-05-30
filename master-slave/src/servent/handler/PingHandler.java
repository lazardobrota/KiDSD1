package servent.handler;

import app.AppConfig;
import servent.message.*;
import servent.message.util.MessageUtil;

public class PingHandler implements MessageHandler {

    private Message clientMessage;

    public PingHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.PING) {
            AppConfig.timestampedErrorPrint("PingHandler got invalid message type: " + clientMessage.getMessageType());
            return;

        }

        String[] fromToPortsStr = clientMessage.getMessageText().split(":");
        int[] fromToPorts = new int[] {Integer.parseInt(fromToPortsStr[0]), Integer.parseInt(fromToPortsStr[1])};

        if (fromToPorts[1] == AppConfig.myServentInfo.getListenerPort()) {
            AppConfig.timestampedStandardPrint("Ping Valid");
            MessageUtil.sendMessage(new PongMessage(clientMessage.getReceiverPort(), clientMessage.getSenderPort(), fromToPorts[1], fromToPorts[0]));
        }
        else {
            AppConfig.timestampedStandardPrint("Pass Ping to final node: " + fromToPorts[1]);
            MessageUtil.sendMessage(new PingMessage(clientMessage.getSenderPort(), fromToPorts[1], clientMessage.getMessageText()));
        }
    }
}
