package ch.ethz.inf.vs.android.glukas.chat;

/**
 * This class should be used for abstracting Lamport timestamps
 * @author hong-an
 *
 */
public class Lamport implements Comparable<Lamport> {
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
	}

	@Override
	/**
	 * This function compares two Lamport timestamps.
	 * @param toCompare The newly received Lamport timestamp
	 */
	public int compareTo(Lamport toCompare) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public boolean isConsecutive(Lamport toCompare){
		return false;
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
}
