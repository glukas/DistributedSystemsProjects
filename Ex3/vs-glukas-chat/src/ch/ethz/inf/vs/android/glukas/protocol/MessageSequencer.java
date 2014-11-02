package ch.ethz.inf.vs.android.glukas.protocol;

import java.util.Comparator;
import java.util.PriorityQueue;


import android.os.Handler;
import android.util.Pair;

class MessageSequencer<T extends SyntheticClock<T>> implements MessageSequencerInterface<T> {
	
	//call back to display
	private MessageSequencerDelegate chat;
	
	//sorting & timeout
	private SyntheticClock<T> clockOfLastDeliveredMessage;
	private static final long TIMEOUT_MILLIS = 20000;
	private Handler timeoutHandler = new Handler();
	private PriorityQueue<Pair<SyntheticClock<T>, ChatMessage>> messageQueue;
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
		Comparator<Pair<SyntheticClock<T>, ChatMessage>> comparator = new Comparator<Pair<SyntheticClock<T>, ChatMessage>> () {
			@Override
			public int compare(Pair<SyntheticClock<T>, ChatMessage> lhs, Pair<SyntheticClock<T>, ChatMessage> rhs) {
				return lhs.first.compareTo(rhs.first.getClock());
			}
		};
		this.messageQueue = new PriorityQueue<Pair<SyntheticClock<T>, ChatMessage>>(2, comparator);
	}	
	
	@Override
	public void onMessageReceived(SyntheticClock<T> clock, ChatMessage message){
		messageQueue.add(new Pair<SyntheticClock<T>, ChatMessage>(clock,message));
		
		if (messageQueue.peek().first.isDeliverable(clockOfLastDeliveredMessage.getClock())) {
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
		while(!messageQueue.isEmpty() && messageQueue.peek().first.isDeliverable(clockOfLastDeliveredMessage.getClock())) {
			popMessage();
		}
	}
	
	private void popMessage() {
		ChatMessage nextMessage = messageQueue.peek().second;
		SyntheticClock<T> nextClock = messageQueue.peek().first;
		messageQueue.poll();
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
	private void deliverMessage(ChatMessage message){
		if (chat != null){
			chat.onDisplayMessage(message);
		}
	}
}
