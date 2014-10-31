package ch.ethz.inf.vs.android.glukas.chat;

public interface SyntheticClock<T> extends Comparable<T> {
	
	public boolean isDeliverable(T lastDeliveredMessage);
	
}
