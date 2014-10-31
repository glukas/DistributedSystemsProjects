package ch.ethz.inf.vs.android.glukas.protocol;

import java.util.SortedSet;
import java.util.TreeSet;


import android.os.Handler;

public class MessageSequencer<T extends SyntheticClock<T>> implements MessageSequencerInterface<T> {
	
	//call back to display
	private MessageSequencerDelegate chat;
	
	//sorting & timeout
	private ChatMessage<T> lastDelivered;
	private static final long TIMEOUT_MILLIS = 20000;
	private Handler timeoutHandler = new Handler();
	private SortedSet<ChatMessage<T>> messageQueue;
	private Runnable timeout = new Runnable() {
		@Override
		public void run() {
			onTimedOut();
		}
	};
	
	/**
	 * Create a new clock sorting object, which can call back chat
	 * @param chat where to send messages to be displayed, if this is null, no one will be notified, but the lastDelivered message is still kept current
	 * @param initialMessage
	 */
	public MessageSequencer(MessageSequencerDelegate chat, ChatMessage<T> initialMessage){
		this.chat = chat;
		this.lastDelivered = initialMessage;
		this.messageQueue = new TreeSet<ChatMessage<T>>();
	}	
	
	@Override
	public void onMessageReceived(ChatMessage<T> message){
		messageQueue.add(message);
		
		//if (messageQueue.first().isDeliverable(lastDelivered)) {
		//	timeoutHandler.removeCallbacks(timeout);
		//}
		//while(!messageQueue.isEmpty() && messageQueue.first().isDeliverable(lastDelivered)) {
		popMessage();
		//}
		//postTimeoutIfNonempty();
	}

	@Override
	public ChatMessage<T> getLastDeliveredMessage() {
		return lastDelivered;
	}

	@Override
	public MessageSequencerDelegate getDelegate() {
		return chat;
	}
	
	private void popMessage() {
		lastDelivered = messageQueue.first();
		messageQueue.remove(lastDelivered);
		deliverMessage(lastDelivered);
	}
	
	private void postTimeoutIfNonempty() {
		if (!messageQueue.isEmpty()) {
			timeoutHandler.postDelayed(timeout, TIMEOUT_MILLIS);
		}
	}
	
	private void onTimedOut(){
		popMessage();
		postTimeoutIfNonempty();
	}

	//asks the delegate to deliver the message
	private void deliverMessage(ChatMessage<T> message){
		if (chat != null){
			chat.onDisplayMessage(message.text, message.sender);
		}
	}
}
