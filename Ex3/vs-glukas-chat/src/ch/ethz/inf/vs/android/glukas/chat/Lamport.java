package ch.ethz.inf.vs.android.glukas.chat;

/**
 * This class should be used for abstracting Lamport timestamps
 * @author hong-an
 *
 */
public class Lamport implements SyntheticClock<Lamport> {
	/**
	 * The timestamp
	 */
	private int value;

	/**
	 * Constructor
	 */
	public Lamport() {
		this.value = 0;
	}

	/**
	 * Setter for the timestamp
	 * @param initValue The value
	 */
	public Lamport(int initValue) {
		this.value = initValue;
	}

	/**
	 * This function updates our own timestamp
	 * to the newly received one
	 * @param toCompare The newly received Lamport timestamp
	 */
	public void update(Lamport toCompare) {
		// TODO Fill me
		this.value = toCompare.value;
	}

	@Override
	/**
	 * This function compares two Lamport timestamps.
	 * @param toCompare The newly received Lamport timestamp
	 */
	public int compareTo(Lamport toCompare) {
		// Lamport Integer Comparison
		if (this.value < toCompare.value) {
			return -1;
		}
		else if (this.value > toCompare.value){
			return 1;
		}
		else if (this.value == toCompare.value){
			return 0;
		}
		
		
		
		return 0;
	}

	@Override
	/**
	 * This function returns the String representation of the
	 * class
	 * @return String representation of Lamport timestamps
	 */
	public String toString() {
		// TODO Fill me
		return String.valueOf(this.value);
	}
	
	public int getTimestamp(){
		return this.value;
	}

	@Override
	public boolean isDeliverable(Lamport lastDeliveredMessage) {
		// TODO Auto-generated method stub
		return false;
	}
}
