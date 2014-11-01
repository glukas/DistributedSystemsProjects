package ch.ethz.inf.vs.android.glukas.protocol;

/**
 * This class should be used for abstracting Lamport timestamps
 * 
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
	 * 
	 * @param initValue
	 *            The value
	 */
	public Lamport(int initValue) {
		this.value = initValue;
	}

	/**
	 * This function updates our own timestamp to the newly received one
	 * 
	 * @param toCompare
	 *            The newly received Lamport timestamp
	 */
	public void update(Lamport toCompare) {
		// TODO Fill me
		if (this.value < toCompare.value)
		this.value = toCompare.value;
		else {
			//do nothing
			}	
	}

	// TODO does this belong in the parser?
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

	public int getTimestamp() {
		return this.value;
	}

	// //
	// SYNTHETIC CLOCK
	// //

	/**
	 * This function compares two Lamport timestamps.
	 * 
	 * @param toCompare
	 *            The newly received Lamport timestamp
	 */
	public int compareTo(Lamport toCompare) {
		// Lamport Integer Comparison
		if (this.value < toCompare.value) {
			return -1;
		} else if (this.value > toCompare.value) {
			return 1;
		} else if (this.value == toCompare.value) {
			return 0;
		}
		return 0;
	}

	@Override
	public boolean isDeliverable(Lamport lastDeliveredMessage) {
		Integer difference = this.value - lastDeliveredMessage.value;
		if (difference == 1) {
			return true;
		} else if (difference > 1) {
			return false;
		} else {
			// If its an earlier message just display directly
			return true;
		}


	}

	@Override
	public Lamport getClock() {
		return this;
	}

	@Override
	public void tick() {
		this.value++;
		// TODO (YOUNG) Auto-generated method stub
		
	}
}
