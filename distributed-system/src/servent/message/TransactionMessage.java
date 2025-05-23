package servent.message;

import app.AppConfig;
import app.ServentInfo;
import app.snapshot_bitcake.ABBitcakeManager;
import app.snapshot_bitcake.BitcakeManager;
import servent.message.snapshot.ACausalMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a bitcake transaction. We are sending some bitcakes to another node.
 *
 *
 */
public class TransactionMessage extends ACausalMessage {

	private static final long serialVersionUID = -333251402058492901L;

	private final transient BitcakeManager bitcakeManager;
	
	public TransactionMessage(ServentInfo sender, ServentInfo receiver, int amount, BitcakeManager bitcakeManager) {
		super(MessageType.TRANSACTION, sender, receiver, String.valueOf(amount));
		this.bitcakeManager = bitcakeManager;
	}

	public TransactionMessage(ServentInfo sender, ServentInfo receiver, int amount, BitcakeManager bitcakeManager, Map<Integer, Integer> senderVectorClock) {
		super(MessageType.TRANSACTION, sender, receiver, String.valueOf(amount), senderVectorClock);
		this.bitcakeManager = bitcakeManager;
	}

	public TransactionMessage(ServentInfo sender, ServentInfo receiver, String amount, BitcakeManager bitcakeManager, Map<Integer, Integer> senderVectorClock) {
		super(MessageType.TRANSACTION, sender, receiver, amount, senderVectorClock);
		this.bitcakeManager = bitcakeManager;
	}
	
	/**
	 * We want to take away our amount exactly as we are sending, so our snapshots don't mess up.
	 * This method is invoked by the sender just before sending, and with a lock that guarantees
	 * that we are white when we are doing this in Chandy-Lamport.
	 */
	@Override
	public void sendEffect() {
		int amount = Integer.parseInt(getMessageText());
		bitcakeManager.takeSomeBitcakes(amount);

		if (bitcakeManager instanceof ABBitcakeManager abBitcakeManager) {

			abBitcakeManager.recordGiveTransaction(getReceiverInfo().getId(), amount);
		}

		AppConfig.timestampedStandardPrint("BBBBBBBBBBBBBBB REMOVE: " + amount + " ---- " + bitcakeManager.getCurrentBitcakeAmount());
	}
}
