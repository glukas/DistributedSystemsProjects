package ch.ethz.inf.vs.android.glukas.protocol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import org.json.JSONObject;

import ch.ethz.inf.vs.android.glukas.protocol.Utils.ChatEventType;

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
	protected List<ChatEventListener> eventListenerList = new ArrayList<ChatEventListener>();
	
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
