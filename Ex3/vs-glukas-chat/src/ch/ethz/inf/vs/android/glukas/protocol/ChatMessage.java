package ch.ethz.inf.vs.android.glukas.protocol;

/**
 * This function represents a parsed JSON packet that contains
 * a message
 * @author hong-an
 *
 */
public class ChatMessage<T extends SyntheticClock<T>> implements SyntheticClock<ChatMessage<T>> {
	public final String text;
	public final Integer sender;
	public final long timestamp;
	public final SyntheticClock<T> clock;

	public ChatMessage(int sender, String text, SyntheticClock<T> clock, long timestamp) {
		this.sender = sender;
		this.text = text;
		this.clock = clock;
		this.timestamp = timestamp;
	}
	
	////
	//SYNTHETIC CLOCK
	////

	@Override
	public boolean isDeliverable(ChatMessage<T> lastMessageDelivered) {
		return this.clock.getClock().isDeliverable(lastMessageDelivered.clock.getClock());
	}
	
	@Override
	public int compareTo(ChatMessage<T> another) {
		int clockComparison = this.clock.getClock().compareTo(another.clock.getClock());
		if (clockComparison == 0) {
			return sender.compareTo(another.sender);
		} else {
			return clockComparison;
		}
	}

	@Override
	public ChatMessage<T> getClock() {
		return this;
	}

}
