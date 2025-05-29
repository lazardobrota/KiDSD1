package servent.handler;

import app.AppConfig;
import app.ServentInfo;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.PutMessage;
import servent.message.util.MessageUtil;

public class PutHandler implements MessageHandler {

	private Message clientMessage;
	
	public PutHandler(Message clientMessage) {
		this.clientMessage = clientMessage;
	}
	
	@Override
	public void run() {
		if (clientMessage.getMessageType() == MessageType.PUT) {
			String[] splitText = clientMessage.getMessageText().split(":");
			if (splitText.length == 2) {
				int key = 0;
				String value;

				try {
					key = Integer.parseInt(splitText[0]);
					value = splitText[1];

					if (AppConfig.chordState.isKeyMine(key)) {
						AppConfig.timestampedStandardPrint("Saved image: " + value);
						AppConfig.chordState.getValueMap().put(key, value);
					} else {
						ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(key);
						PutMessage pm = new PutMessage(clientMessage.getSenderPort(), nextNode.getListenerPort(), key, value);
						MessageUtil.sendMessage(pm);
					}
				} catch (NumberFormatException e) {
					AppConfig.timestampedErrorPrint("Got put message with bad text: " + clientMessage.getMessageText());
				}
			} else {
				AppConfig.timestampedErrorPrint("Got put message with bad text: " + clientMessage.getMessageText());
			}


		} else {
			AppConfig.timestampedErrorPrint("Put handler got a message that is not PUT");
		}

	}

}
