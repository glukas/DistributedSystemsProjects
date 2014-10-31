package ch.ethz.inf.vs.android.glukas.chat;

import java.util.SortedSet;
import java.util.TreeSet;

import android.os.Handler;

public class DisplayLogic<T extends Comparable<T>> implements DisplayLogicInterface<T> {
	
	//call back to display
	private ChatDisplayMessageInterface chat;
	private boolean enableSend;
	
	//sorting & timeout
	private T clock;
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
	public DisplayLogic(boolean enableSend, ChatDisplayMessageInterface chat, T initClock){
		this.enableSend = enableSend;
		this.chat = chat;
		this.clock = initClock;
		this.sortedSet = new TreeSet<ChatMessage>();
	}	
	
	@Override
	public void onMessageReceived(ChatMessage message){
		//TODO : timeout and sorting
		onDisplayMessage(message);
	}
	
	@Override
	public T getClock(){
		return clock;
	}
	
	private void onTimedOut(){
		
	}
	
	private void onDisplayMessage(ChatMessage message){
		if (enableSend){
			chat.onDisplayMessage(message);
		}
	}
}
