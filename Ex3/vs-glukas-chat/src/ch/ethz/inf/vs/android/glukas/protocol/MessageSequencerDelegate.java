package ch.ethz.inf.vs.android.glukas.protocol;


public interface MessageSequencerDelegate {
	
	/**
	 * Called when a message is ready to be displayed
	 * @param message to display
	 */
	abstract void onDisplayMessage(String message, int senderId);
}
