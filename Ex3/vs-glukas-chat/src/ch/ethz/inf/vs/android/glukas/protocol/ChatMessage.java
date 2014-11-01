package ch.ethz.inf.vs.android.glukas.protocol;

/**
 * This function represents a parsed JSON packet that contains
 * a message
 * @author hong-an
 *
 */
public class ChatMessage {
	public final String text;
	public final Integer sender;
	public final long timestamp;
	public final Lamport lamportTime;
	public final VectorClock vectorTime;
 	
	public ChatMessage(int sender, String text, Lamport lamport, VectorClock vectorClock, long timestamp) {
		this.sender = sender;
		this.text = text;
		this.lamportTime = lamport;
		this.timestamp = timestamp;
		this.vectorTime = vectorClock;
	}
	
	/**
	 * String representation of the object. Used for debugging;
	 */
	public String toString() {
		return "{\"cmd\": \"message\"" + ", \"time_vector\": "
				+ this.vectorTime.toString() + ", \"lamport\": "
				+ this.lamportTime.toString() + ", \"text\": " + this.text
				+ ", \"sender\": " + this.sender + "}";
	}
	
}
