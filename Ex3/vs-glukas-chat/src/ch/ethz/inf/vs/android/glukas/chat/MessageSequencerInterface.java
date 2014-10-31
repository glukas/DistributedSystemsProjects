package ch.ethz.inf.vs.android.glukas.chat;

/**
 * A clock to decide when to display messages and in which order
 *
 * @param <T> the type of clock used
 */
public interface MessageSequencerInterface {	
	
	/**
	 * Give the message which just arrived to the sorting
	 * @param message
	 */
	public abstract void onMessageReceived(ChatMessage message);
	
	/**
	 * Get the actual clock
	 * @return the current clock
	 */
	public abstract ChatMessage getLastDeliveredMessage();
}
