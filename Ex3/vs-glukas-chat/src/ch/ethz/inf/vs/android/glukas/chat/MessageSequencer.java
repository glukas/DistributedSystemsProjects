package ch.ethz.inf.vs.android.glukas.chat;

import java.util.SortedSet;
import java.util.TreeSet;

import android.os.Handler;

public class MessageSequencer implements MessageSequencerInterface {
	
	//call back to display
	private ChatDisplayMessageInterface chat;
	private boolean enableSend;
	
	//sorting & timeout
	private ChatMessage lastDelivered;
	private static final long TIMEOUT_MILLIS = 2000;
	private Handler callBackHandler = new Handler();
	private SortedSet<ChatMessage> sortedSet;
	private Runnable timeout = new Runnable() {
		@Override
		public void run() {
			onTimedOut();
		}
	};
	
	/**
	 * Create a new clock sorting object, which can call back chat
	 * @param enableSend if this clock is used to sort
	 * @param chat where to send messages to be displayed
	 * @param initClock the initial clock
	 */
	public MessageSequencer(boolean enableSend, ChatDisplayMessageInterface chat, ChatMessage initialMessage){
		this.enableSend = enableSend;
		this.chat = chat;
		this.lastDelivered = initialMessage;
		this.sortedSet = new TreeSet<ChatMessage>();
	}	
	
	@Override
	public void onMessageReceived(ChatMessage message){
		//TODO : timeout and sorting
		onDisplayMessage(message);
	}
	
	private void onTimedOut(){
		
	}
	
	private void onDisplayMessage(ChatMessage message){
		if (enableSend){
			chat.onDisplayMessage(message);
		}
	}

	@Override
	public ChatMessage getLastDeliveredMessage() {
		// TODO Auto-generated method stub
		return null;
	}
}
