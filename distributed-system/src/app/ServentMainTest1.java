package app;

import app.snapshot_bitcake.NullSnapshotCollector;
import app.snapshot_bitcake.SnapshotCollector;
import app.snapshot_bitcake.SnapshotCollectorWorker;
import app.snapshot_bitcake.SnapshotType;
import cli.CLIParser;
import servent.SimpleServantListener;
import servent.message.CommitMessageListener;
import servent.message.util.FifoSendWorker;
import servent.message.util.MessageUtil;

import java.util.ArrayList;
import java.util.List;

public class ServentMainTest1 {

    public static void main(String[] args) {

        int serventId = -1;
        int portNumber = -1;

        String serventListFile = "snapshot-primer/servent_list.test.properties";
        AppConfig.readConfig(serventListFile);

        try {
            serventId = 1;
        } catch (NumberFormatException e) {
            AppConfig.timestampedErrorPrint("Second argument should be an int. Exiting...");
            System.exit(0);
        }

        if (serventId >= AppConfig.getServentCount()) {
            AppConfig.timestampedErrorPrint("Invalid servent id provided");
            System.exit(0);
        }

        AppConfig.myServentInfo = AppConfig.getInfoById(serventId);
        CausalBroadcastShared.initializeNeighborTcp();

        try {
            portNumber = AppConfig.myServentInfo.getListenerPort();

            if (portNumber < 1000 || portNumber > 2000) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            AppConfig.timestampedErrorPrint("Port number should be in range 1000-2000. Exiting...");
            System.exit(0);
        }

        MessageUtil.initializePendingMessages();

        AppConfig.timestampedStandardPrint("Starting servent " + AppConfig.myServentInfo);

        SnapshotCollector snapshotCollector;

        if (AppConfig.SNAPSHOT_TYPE == SnapshotType.NONE) {
            snapshotCollector = new NullSnapshotCollector();
        } else {
            snapshotCollector = new SnapshotCollectorWorker(AppConfig.SNAPSHOT_TYPE);
        }
        Thread snapshotCollectorThread = new Thread(snapshotCollector);
        snapshotCollectorThread.start();

        CommitMessageListener commitMessageListener = new CommitMessageListener();
        Thread commitMessageListenerThread = new Thread(commitMessageListener);
        commitMessageListenerThread.start();

        SimpleServantListener simpleListener = new SimpleServantListener(snapshotCollector);
        Thread listenerThread = new Thread(simpleListener);
        listenerThread.start();

        List<FifoSendWorker> senderWorkers = new ArrayList<>();
        if (AppConfig.IS_FIFO) {
            for (Integer neighbor : AppConfig.myServentInfo.getNeighbors()) {
                FifoSendWorker senderWorker = new FifoSendWorker(neighbor);

                Thread senderThread = new Thread(senderWorker);

                senderThread.start();

                senderWorkers.add(senderWorker);
            }

        }

        CLIParser cliParser = new CLIParser(simpleListener, commitMessageListener, senderWorkers, snapshotCollector);
        Thread cliThread = new Thread(cliParser);
        cliThread.start();

    }
}
