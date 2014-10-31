package ch.ethz.inf.vs.android.glukas.protocol;

import java.util.HashSet;
import java.util.Set;

/**
 * This class provides a wrapper for events to be triggered
 * by the chat logic.
 * @author hong-an
 *
 */
public class ChatEventSource {
	/**
	 * Listeners of events
	 */
	protected Set<ChatEventListener> eventListenerList = new HashSet<ChatEventListener>();
	
	/**
	 * Adding listeners
	 * @param listener
	 */
	public void addChatEventListener(ChatEventListener listener) {
		eventListenerList.add(listener);
	}
	
	/**
	 * Removing listener
	 * @param listener
	 */
	public void removeChatEventListener(ChatEventListener listener) {
		eventListenerList.remove(listener);
	}
	
}
