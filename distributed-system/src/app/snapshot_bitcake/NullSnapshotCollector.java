package app.snapshot_bitcake;

import app.snapshot_bitcake.result.CCSnapshotResult;

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
	public void addCCSnapshotInfo(int id, CCSnapshotResult ccSnapshotResult) {

	}


	@Override
	public void startCollecting() {}

}
