package app;

import java.util.ArrayList;
import java.util.List;

import app.snapshot_bitcake.NullSnapshotCollector;
import app.snapshot_bitcake.SnapshotCollector;
import app.snapshot_bitcake.SnapshotCollectorWorker;
import app.snapshot_bitcake.SnapshotType;
import cli.CLIParser;
import servent.SimpleServantListener;
import servent.message.CommitMessageListener;
import servent.message.util.FifoSendWorker;
import servent.message.util.MessageUtil;

/**
 * Describes the procedure for starting a single Servent
 *
 */
public class ServentMain {

	/**
	 * Command line arguments are:
	 * 0 - path to servent list file
	 * 1 - this servent's id
	 */
	public static void main(String[] args) {
		if (args.length != 2) {
			AppConfig.timestampedErrorPrint("Please provide servent list file and id of this servent.");
		}
		
		int serventId = -1;
		int portNumber = -1;
		
		String serventListFile = args[0];
		AppConfig.readConfig(serventListFile);
//		AppConfig.readConfig("snapshot-primer/servent_list.properties");

		try {
			serventId = Integer.parseInt(args[1]);
//			serventId = 0;
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
