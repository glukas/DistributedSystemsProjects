package ch.ethz.inf.vs.android.glukas.protocol;


/**
 * The sequencer takes chat messages and calls back when it is time to display them
 *
 * @param <T> the type of clock used
 */
interface MessageSequencerInterface<T extends SyntheticClock<T>> {	
	
	/**
	 * Add a new message to the sequencer. The message may be delivered right away, or at a later point if the message is "from the future".
	 * The message clock is used to determine the ordering of the messages.
	 * @param message
	 */
	public abstract void onMessageReceived(SyntheticClock<T> clock, ChatMessage message);
	
	/**
	 * @return the delegate that handles the callbacks from this sequencer. Can be null, in which case no callbacks are made.
	 */
	public abstract MessageSequencerDelegate getDelegate();
}
