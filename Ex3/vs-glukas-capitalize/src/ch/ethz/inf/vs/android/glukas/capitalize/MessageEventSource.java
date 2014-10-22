package ch.ethz.inf.vs.android.glukas.capitalize;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import org.json.JSONObject;

import ch.ethz.inf.vs.android.glukas.capitalize.Utils.MessageEventType;

/**
 * Wrapper class to trigger events (used with Message Logic)
 * to notify the UI for example
 * @author hong-an
 *
 */
public class MessageEventSource {
	protected List<MessageEventListener> eventListenerList = new ArrayList<MessageEventListener>();
	
	public void addMessageEventListener(MessageEventListener listener) {
		eventListenerList.add(listener);
	}
	
	public void removeMessageEventListener(MessageEventListener listener) {
		eventListenerList.remove(listener);
	}
	
	/**
	 * Class that defines the events used as triggers
	 * @author hong-an
	 *
	 */
	public class ChatEvent extends EventObject implements Serializable{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1930041885003089779L;
		public final String message;
		public JSONObject request;
		protected MessageEventType type;
		public MessageEventType getType() {
			return type;
		}

		/**
		 * Constructor
		 * @param source
		 * @param type
		 * @param message
		 * @param request
		 */
		public ChatEvent(Object source, MessageEventType type, String message, JSONObject request) {
			super(source);
			this.type = type;
			this.message = message;
			this.request = request;
		}
		
		/**
		 * Function used to notify that an event was triggered
		 */
		public void dispatchEvent() {
			// TODO Fill me with events to dispatch to the listener
		}
	}
	
	
}
