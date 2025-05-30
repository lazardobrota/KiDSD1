package servent.handler;

import app.AppConfig;
import servent.PongListener;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.PongMessage;
import servent.message.util.MessageUtil;

public class PongHandler implements MessageHandler{

    private final Message clientMessage;
    private final PongListener pongListener;

    public PongHandler(Message clientMessage, PongListener pongListener) {
        this.clientMessage = clientMessage;
        this.pongListener = pongListener;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.PONG) {
            AppConfig.timestampedErrorPrint("PongHandler got invalid message type: " + clientMessage.getMessageType());
            return;
        }

        String[] fromToPortsStr = clientMessage.getMessageText().split(":");
        int[] fromToPorts = new int[] {Integer.parseInt(fromToPortsStr[0]), Integer.parseInt(fromToPortsStr[1])};

        if (fromToPorts[1] != AppConfig.myServentInfo.getListenerPort()) {
            AppConfig.timestampedStandardPrint("Pass Pong");
            MessageUtil.sendMessage(new PongMessage(clientMessage.getSenderPort(), fromToPorts[1], fromToPorts[0], fromToPorts[1]));
            return;
        }


        if (fromToPorts[0] == AppConfig.chordState.getNextNodePort()) {
            AppConfig.timestampedStandardPrint("Pong clockwise");
            pongListener.pongClockWiseArrived();
            return;
        }

        if (fromToPorts[0] == AppConfig.chordState.getPredecessor().getListenerPort()) {
            AppConfig.timestampedStandardPrint("Pong ANTIclockwise");
            pongListener.pongAntiClockWiseArrived();
            return;
        }

        AppConfig.timestampedErrorPrint("PongHandler got some really weird message");

//        boolean correctNode = false;
//
//        if (clientMessage.getSenderPort() == AppConfig.chordState.getNextNodePort()) {
//            AppConfig.timestampedStandardPrint("Pong for Next node: " + clientMessage);
//            pongListener.setClockwiseMachineAliveValue(2);
//            correctNode = true;
//        }
//
//        if (clientMessage.getSenderPort() == AppConfig.chordState.getPredecessor().getListenerPort()) {
//            AppConfig.timestampedStandardPrint("Pong for Predecessor node: " + clientMessage);
//            pongListener.setAnticlockwiseMachineAliveValue(2);
//            correctNode = true;
//        }
//
//        if (!correctNode) {
//            AppConfig.timestampedStandardPrint("Pass Pong: " + clientMessage);
//            String[] fromToPorts = clientMessage.getMessageText().split(":");
//            MessageUtil.sendMessage(new PongMessage(clientMessage.getSenderPort(), Integer.valueOf(fromToPorts[0])));
//        }
    }
}
