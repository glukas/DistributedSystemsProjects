package ch.ethz.inf.vs.android.glukas.protocol;

import java.util.SortedSet;
import java.util.TreeSet;


import android.os.Handler;

class MessageSequencer<T extends SyntheticClock<T>> implements MessageSequencerInterface<T> {
	
	//call back to display
	private MessageSequencerDelegate chat;
	
	//sorting & timeout
	private SyntheticClock<T> clockOfLastDeliveredMessage;
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
	public MessageSequencer(MessageSequencerDelegate chat, SyntheticClock<T> initialClock){
		this.chat = chat;
		clockOfLastDeliveredMessage = initialClock;
		this.messageQueue = new TreeSet<ChatMessage<T>>();
	}	
	
	@Override
	public void onMessageReceived(ChatMessage<T> message){
		messageQueue.add(message);
		
		if (messageQueue.first().clock.isDeliverable(clockOfLastDeliveredMessage.getClock())) {
			timeoutHandler.removeCallbacks(timeout);
		}
		popAllDeliverables();
		postTimeoutIfNonempty();
		
	}
	
	@Override
	public MessageSequencerDelegate getDelegate() {
		return chat;
	}
	
	private void popAllDeliverables() {
		while(!messageQueue.isEmpty() && messageQueue.first().clock.isDeliverable(clockOfLastDeliveredMessage.getClock())) {
			popMessage();
		}
	}
	
	private void popMessage() {
		ChatMessage<T> nextMessage = messageQueue.first();
		SyntheticClock<T> nextClock = nextMessage.clock;
		messageQueue.remove(nextMessage);
		clockOfLastDeliveredMessage.update(nextClock.getClock());
		deliverMessage(nextMessage);
	}
	
	private void postTimeoutIfNonempty() {
		if (!messageQueue.isEmpty()) {
			timeoutHandler.postDelayed(timeout, TIMEOUT_MILLIS);
		}
	}
	
	private void onTimedOut(){
		if (!messageQueue.isEmpty()) popMessage();
		popAllDeliverables();
		postTimeoutIfNonempty();
	}

	//asks the delegate to deliver the message
	private void deliverMessage(ChatMessage<T> message){
		if (chat != null){
			chat.onDisplayMessage(message, message.sender);
		}
	}
}
