package ch.ethz.inf.vs.android.glukas.protocol;

public interface SyntheticClock<T> extends Comparable<T> {
	
	/**
	 * @param lastDeliveredMessage
	 * @return Given the last message that was delivered, returns true if this message can be delivered next, false otherwise
	 */
	public boolean isDeliverable(T lastDeliveredMessage);
	
	/**
	 * @return the object holding the clock (usually 'this')
	 */
	T getClock();
	
}
