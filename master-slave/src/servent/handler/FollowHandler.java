package servent.handler;

import app.AppConfig;
import app.ServentInfo;
import servent.message.*;
import servent.message.util.MessageUtil;

public class FollowHandler implements MessageHandler {

    private Message clientMessage;

    public FollowHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.FOLLOW) {
            AppConfig.timestampedErrorPrint("FOLLOW got invalid type: " + clientMessage.getMessageType());
            return;
        }

        int chordId = Integer.parseInt(clientMessage.getMessageText());

        if (AppConfig.myServentInfo.getChordId() == chordId) {
            if (AppConfig.chordState.getPendingFollowRequests().contains(clientMessage.getSenderPort())) {
                MessageUtil.sendMessage(new FollowFoundMessage(clientMessage.getReceiverPort(), clientMessage.getSenderPort(), "Already pending node: " + clientMessage.getReceiverPort()));
            } else {
                AppConfig.chordState.getPendingFollowRequests().add(clientMessage.getSenderPort());
                MessageUtil.sendMessage(new FollowFoundMessage(clientMessage.getReceiverPort(), clientMessage.getSenderPort(), "Added Following Request for node: " + clientMessage.getReceiverPort()));
            }
        } else if (AppConfig.chordState.isKeyMine(chordId)) {
            MessageUtil.sendMessage(new FollowFoundMessage(clientMessage.getReceiverPort(), clientMessage.getSenderPort(), "Given port doesnt exist"));
        } else {
            ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(chordId);
            MessageUtil.sendMessage(new FollowMessage(clientMessage.getSenderPort(), nextNode.getListenerPort(), chordId));
        }
    }

}
