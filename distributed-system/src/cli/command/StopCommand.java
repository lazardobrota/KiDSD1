package cli.command;

import java.util.List;

import app.AppConfig;
import app.snapshot_bitcake.SnapshotCollector;
import cli.CLIParser;
import servent.SimpleServantListener;
import servent.message.CommitMessageListener;
import servent.message.util.FifoSendWorker;

public class StopCommand implements CLICommand {

	private final CLIParser parser;
	private final SimpleServantListener listener;
	private final CommitMessageListener commitMessageListener;
	private final SnapshotCollector snapshotCollector;
	private final List<FifoSendWorker> senderWorkers;
	
	public StopCommand(CLIParser parser, SimpleServantListener listener, CommitMessageListener commitMessageListener, List<FifoSendWorker> senderWorkers, SnapshotCollector snapshotCollector) {
		this.parser = parser;
		this.listener = listener;
		this.commitMessageListener = commitMessageListener;
		this.snapshotCollector = snapshotCollector;
		this.senderWorkers = senderWorkers;
	}
	
	@Override
	public String commandName() {
		return "stop";
	}

	@Override
	public void execute(String args) {
		AppConfig.timestampedStandardPrint("Stopping...");
		parser.stop();
		listener.stop();
		commitMessageListener.stop();
		for (FifoSendWorker senderWorker : senderWorkers) {
			senderWorker.stop();
		}
		snapshotCollector.stop();
	}

}
