package ch.ethz.inf.vs.android.glukas.protocol;

/**
 * This function represents a parsed JSON packet that contains
 * a message
 * @author hong-an
 *
 */
public class ChatMessage<T extends SyntheticClock<T>> implements Comparable<ChatMessage<T>> {
	public final String text;
	public final Integer sender;
	public final long timestamp;
	public final SyntheticClock<T> clock;
	private String clockType;

	public ChatMessage(int sender, String text, SyntheticClock<T> clock, long timestamp) {
		this.sender = sender;
		this.text = text;
		this.clock = clock;
		this.timestamp = timestamp;
		if (clock instanceof VectorClock){
			this.clockType = "time_vector";
		}
		else if (clock instanceof Lamport)
			this.clockType = "lamport";
		}
	
	
	
	public String toString() {
		return "{\"cmd\": \"message\"" + ", \""+ clockType +"\": "
				+ this.clock.toString() + ", \"text\": " + this.text
				+ ", \"sender\": " + this.sender + "}";

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

}
