package app.snapshot_bitcake;

import app.snapshot_bitcake.result.SnapshotResult;

/**
 * This class is used if the user hasn't specified a snapshot type in config.
 */
public class NullSnapshotCollector implements SnapshotCollector {

	@Override
	public void run() {}

	@Override
	public void stop() {}

	@Override
	public BitcakeManager getBitcakeManager() {
		return null;
	}

	@Override
	public void addNaiveSnapshotInfo(String snapshotSubject, int amount) {}

	@Override
	public void addCCSnapshotInfo(int id, SnapshotResult snapshotResult) {

	}


	@Override
	public void startCollecting() {}

}
