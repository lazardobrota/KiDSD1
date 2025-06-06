package cli.command;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import app.AppConfig;
import app.CausalBroadcastShared;
import app.ServentInfo;
import app.snapshot_bitcake.BitcakeManager;
import app.snapshot_bitcake.SnapshotType;
import servent.message.Message;
import servent.message.PendingMessage;
import servent.message.TransactionMessage;
import servent.message.util.MessageUtil;

public class TransactionBurstCommand implements CLICommand {

//    private static final int TRANSACTION_COUNT = 5;
    private static final int TRANSACTION_COUNT = 20;
//    private static final int BURST_WORKERS = 10;
    private static final int BURST_WORKERS = 1;
    private static final int MAX_TRANSFER_AMOUNT = 10;

    private final BitcakeManager bitcakeManager;

    public TransactionBurstCommand(BitcakeManager bitcakeManager) {
        this.bitcakeManager = bitcakeManager;
    }

    private class TransactionBurstWorker implements Runnable {

        @Override
        public void run() {
            ThreadLocalRandom rand = ThreadLocalRandom.current();

            for (int i = 0; i < TRANSACTION_COUNT; i++) {
                List<Message> messages = new ArrayList<>();
                for (int neighbor : AppConfig.myServentInfo.getNeighbors()) {
                    ServentInfo neighborInfo = AppConfig.getInfoById(neighbor);

                    int amount = 1 + rand.nextInt(MAX_TRANSFER_AMOUNT);

                    /*
                     * The message itself will reduce our bitcake count as it is being sent.
                     * The sending might be delayed, so we want to make sure we do the
                     * reducing at the right time, not earlier.
                     */
                    Message transactionMessage = new TransactionMessage(
                            AppConfig.myServentInfo, neighborInfo, amount, bitcakeManager);

                    if (AppConfig.SNAPSHOT_TYPE == SnapshotType.ALAGAR_VENKATESAN || AppConfig.SNAPSHOT_TYPE == SnapshotType.ACHARYA_BADRINATH)
                        CausalBroadcastShared.addPendingMessageAndCheck(new PendingMessage(true, transactionMessage, null));
//                        messages.add(transactionMessage);
                    else
                        MessageUtil.sendMessage(transactionMessage);

                    try {
                        /**
                         * This sleep is here to artificially produce some white node -> red node messages
                         */
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

//                if (AppConfig.SNAPSHOT_TYPE == SnapshotType.ALAGAR_VENKATESAN || AppConfig.SNAPSHOT_TYPE == SnapshotType.ACHARYA_BADRINATH) {
//                    CausalBroadcastShared.addPendingMessageAndCheck(new PendingMessage(true, messages, null));
//                }
            }
        }
    }

    @Override
    public String commandName() {
        return "transaction_burst";
    }

    @Override
    public void execute(String args) {
        for (int i = 0; i < BURST_WORKERS; i++) {
            Thread t = new Thread(new TransactionBurstWorker());

            t.start();
        }
    }


}
