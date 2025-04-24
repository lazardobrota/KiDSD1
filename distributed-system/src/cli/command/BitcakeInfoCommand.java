package cli.command;

import app.snapshot_bitcake.SnapshotCollector;

public class BitcakeInfoCommand implements CLICommand {

	private final SnapshotCollector snapshotCollector;
	
	public BitcakeInfoCommand(SnapshotCollector snapshotCollector) {
		this.snapshotCollector = snapshotCollector;
	}
	
	@Override
	public String commandName() {
		return "bitcake_info";
	}

	@Override
	public void execute(String args) {
		snapshotCollector.startCollecting();

	}

}
