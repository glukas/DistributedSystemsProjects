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
	private Set<ChatEventListener> eventListeners = new HashSet<ChatEventListener>();
	
	
	/**
	 * Adding listeners
	 * @param listener
	 */
	public void addChatEventListener(ChatEventListener listener) {
		eventListeners.add(listener);
	}
	
	/**
	 * Removing listener
	 * @param listener
	 */
	public void removeChatEventListener(ChatEventListener listener) {
		eventListeners.remove(listener);
	}
	
	protected void removeAllListeners() {
		this.eventListeners.clear();
	}
	
	/**
	 * Returns an fresh array of the current event listeners.
	 * It is safe to call removeChatEventListener while iterating over this array.
	 * When dispatching events to listeners, you do not want to iterate directly over the set of listeners,
	 * as it would produce concurrent modification exceptions in case they would call removeChatEventListener
	 * @return
	 */
	protected ChatEventListener[] getEventListeners() {
		return eventListeners.toArray(new ChatEventListener[eventListeners.size()]);
	}
}
