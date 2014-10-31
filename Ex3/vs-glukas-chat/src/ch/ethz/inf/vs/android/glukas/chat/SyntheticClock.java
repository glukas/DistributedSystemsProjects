package ch.ethz.inf.vs.android.glukas.chat;

public class SyntheticClock<T extends Comparable<T>> implements SyntheticClockInterface<T> {
	
	private ChatDisplayMessageInterface chat;
	private boolean enableSend;
	private T clock;
	
	public SyntheticClock(boolean enableSend, ChatDisplayMessageInterface chat, T initClock){
		this.enableSend = enableSend;
		this.chat = chat;
		this.clock = initClock;
	}	
	
	@Override
	public void onMessageReceived(ChatMessage message){
		onDisplayMessage(message);
	}
	
	@Override
	public T getClock(){
		return clock;
	}
	
	private void onDisplayMessage(ChatMessage message){
		if (enableSend){
			chat.onDisplayMessage(message);
		}
	}
}
