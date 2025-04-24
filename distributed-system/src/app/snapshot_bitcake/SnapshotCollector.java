package app.snapshot_bitcake;

import app.Cancellable;
import app.snapshot_bitcake.result.CCSnapshotResult;

/**
 * Describes a snapshot collector. Made not-so-flexibly for readability.
 *
 *
 */
public interface SnapshotCollector extends Runnable, Cancellable {

	BitcakeManager getBitcakeManager();

	void addNaiveSnapshotInfo(String snapshotSubject, int amount);
	void addCCSnapshotInfo(int id, CCSnapshotResult ccSnapshotResult);

	void startCollecting();

}