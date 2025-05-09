package app;

/**
 * Describes a cancellable job. Our workers will normally implement
 * this one aside from {@link Runnable}.
 *
 */
public interface Cancellable {

	/**
	 * Stop executing this worker in a graceful way.
	 */
	void stop();
}
