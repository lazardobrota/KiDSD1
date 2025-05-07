package app.snapshot_bitcake;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import app.AppConfig;
import app.snapshot_bitcake.result.SnapshotResult;

/**
 * Main snapshot collector class. Has support for Naive, Chandy-Lamport
 * and Lai-Yang snapshot algorithms.
 *
 */
public class SnapshotCollectorWorker implements SnapshotCollector {

    private volatile boolean working = true;

    private final AtomicBoolean collecting = new AtomicBoolean(false);

    private final Map<String, Integer> collectedNaiveValues = new ConcurrentHashMap<>();
    private final Map<Integer, SnapshotResult> collectedCCValues = new ConcurrentHashMap<>();

    private final SnapshotType snapshotType;

    private BitcakeManager bitcakeManager;

    public SnapshotCollectorWorker(SnapshotType snapshotType) {
        this.snapshotType = snapshotType;

        switch (snapshotType) {
            case COORDINATED_CHECKPOINTING:
                bitcakeManager = new CCBitcakeManager();
                break;
            case ALAGAR_VENKATESAN:
                bitcakeManager = new AVBitcakeManager();
                break;
            case NONE:
                AppConfig.timestampedErrorPrint("Making snapshot collector without specifying type. Exiting...");
                System.exit(0);
        }
    }

    @Override
    public BitcakeManager getBitcakeManager() {
        return bitcakeManager;
    }

    @Override
    public void run() {
        while (working) {

            /*
             * Not collecting yet - just sleep until we start actual work, or finish
             */
            while (!collecting.get()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (!working) {
                    return;
                }
            }

            /*
             * Collecting is done in 3 or 4 stages:
             * 1. Send messages asking for values
             * 2. Wait for all the responses
             * 3. Print result
             * 4. Tell other to resume
             */

            //1 send asks
            switch (snapshotType) {
                case COORDINATED_CHECKPOINTING:
                    ((CCBitcakeManager) bitcakeManager).startSnapshotEvent(AppConfig.myServentInfo.getId());
                    break;
                case ALAGAR_VENKATESAN:
                    ((AVBitcakeManager) bitcakeManager).startSnapshotEvent(AppConfig.myServentInfo.getId());
                    break;
                case NONE:
                    //Shouldn't be able to come here. See constructor.
                    break;
            }

            //2 wait for responses or finish
            boolean waiting = true;
            while (waiting) {
                switch (snapshotType) {
                    case COORDINATED_CHECKPOINTING:
                    case ALAGAR_VENKATESAN:
                        if (collectedCCValues.size() == AppConfig.getServentCount()) {
                            waiting = false;
                        }
                        break;
                    case NONE:
                        //Shouldn't be able to come here. See constructor.
                        break;
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (!working) {
                    return;
                }
            }

            //print
            int sum;
            switch (snapshotType) {
                case NAIVE:
                    sum = 0;
                    for (Entry<String, Integer> itemAmount : collectedNaiveValues.entrySet()) {
                        sum += itemAmount.getValue();
                        AppConfig.timestampedStandardPrint(
                                "Info for " + itemAmount.getKey() + " = " + itemAmount.getValue() + " bitcake");
                    }

                    AppConfig.timestampedStandardPrint("System bitcake count: " + sum);

                    collectedNaiveValues.clear(); //reset for next invocation
                    break;
                case COORDINATED_CHECKPOINTING:
                case ALAGAR_VENKATESAN:
                    sum = 0;
                    for (Entry<Integer, SnapshotResult> nodeResult : collectedCCValues.entrySet()) {
                        sum += nodeResult.getValue().getRecordedBitcakeAmount();
                        AppConfig.timestampedStandardPrint(
                                "Recorded bitcake amount for " + nodeResult.getKey() + " = " + nodeResult.getValue().getRecordedBitcakeAmount());

                        if (nodeResult.getValue().getAllChannelMessages().isEmpty())
                            AppConfig.timestampedStandardPrint("No channel bitcake for " + nodeResult.getKey());
                        else {
                            for (Entry<String, List<Integer>> channelMessages : nodeResult.getValue().getAllChannelMessages().entrySet()) {
                                int channelSum = channelMessages.getValue().stream().reduce(0, Integer::sum);

                                AppConfig.timestampedStandardPrint("Channel bitcake for " + channelMessages.getKey() +
                                        ": " + channelMessages.getValue() + " with channel bitcake sum: " + channelSum);

                                sum += channelSum;
                            }
                        }
                    }

                    AppConfig.timestampedStandardPrint("System bitcake count: " + sum);
                    collectedCCValues.clear(); //reset for next invocation
                    break;
                case NONE:
                    //Shouldn't be able to come here. See constructor.
                    break;
            }

            switch (snapshotType) {
                case COORDINATED_CHECKPOINTING -> ((CCBitcakeManager) bitcakeManager).handleResume(AppConfig.myServentInfo.getId());
                case ALAGAR_VENKATESAN -> ((AVBitcakeManager) bitcakeManager).handleTerminate(AppConfig.myServentInfo.getId());
            }
            collecting.set(false);
        }
    }

    @Override
    public void addNaiveSnapshotInfo(String snapshotSubject, int amount) {
        collectedNaiveValues.put(snapshotSubject, amount);
    }

    @Override
    public void addCCSnapshotInfo(int id, SnapshotResult snapshotResult) {
        collectedCCValues.put(id, snapshotResult);
    }

    @Override
    public void startCollecting() {
        boolean oldValue = this.collecting.getAndSet(true);

        if (oldValue) {
            AppConfig.timestampedErrorPrint("Tried to start collecting before finished with previous.");
        }
    }

    @Override
    public void stop() {
        working = false;
    }

}
