package servent.handler;

import app.AppConfig;
import app.CausalBroadcastShared;
import app.snapshot_bitcake.ABBitcakeManager;
import app.snapshot_bitcake.AVBitcakeManager;
import app.snapshot_bitcake.BitcakeManager;
import app.snapshot_bitcake.SnapshotType;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.PendingMessage;

public class TransactionHandler implements CausalMessageHandler {

	private Message clientMessage;
	private BitcakeManager bitcakeManager;
	
	public TransactionHandler(Message clientMessage, BitcakeManager bitcakeManager) {
		this.clientMessage = clientMessage;
		this.bitcakeManager = bitcakeManager;
	}

	@Override
	public void run() {
		if (clientMessage.getMessageType() != MessageType.TRANSACTION)
			AppConfig.timestampedErrorPrint("Transaction handler got: " + clientMessage);

		if (AppConfig.SNAPSHOT_TYPE == SnapshotType.ALAGAR_VENKATESAN || AppConfig.SNAPSHOT_TYPE == SnapshotType.ACHARYA_BADRINATH) {
			CausalBroadcastShared.addPendingMessageAndCheck(new PendingMessage(false, clientMessage, this));
		}
		else
			continueExecution();
	}

	@Override
	public void continueExecution() {
		String amountString = clientMessage.getMessageText();

		int amountNumber = 0;
		try {
			amountNumber = Integer.parseInt(amountString);
		} catch (NumberFormatException e) {
			AppConfig.timestampedErrorPrint("Couldn't parse amount: " + amountString);
			return;
		}


		if (AppConfig.isWhite.get())
			bitcakeManager.addSomeBitcakes(amountNumber);
		else if (AppConfig.SNAPSHOT_TYPE == SnapshotType.ALAGAR_VENKATESAN)
			((AVBitcakeManager)bitcakeManager).addChannelMessage(clientMessage);
		else if (AppConfig.SNAPSHOT_TYPE == SnapshotType.ACHARYA_BADRINATH)
			bitcakeManager.addSomeBitcakes(amountNumber);

		synchronized (AppConfig.colorLock) {
			if (bitcakeManager instanceof ABBitcakeManager abBitcakeManager)
				abBitcakeManager.recordGetTransaction(clientMessage.getOriginalSenderInfo().getId(), amountNumber);
		}

		AppConfig.timestampedStandardPrint("AAAAAAAAAAA ADD: " + amountNumber + " ---- " + bitcakeManager.getCurrentBitcakeAmount());
	}
}
