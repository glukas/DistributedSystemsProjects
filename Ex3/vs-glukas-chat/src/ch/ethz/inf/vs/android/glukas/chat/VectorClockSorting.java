package ch.ethz.inf.vs.android.glukas.chat;

public class VectorClockSorting {
	
	private VectorClock vectorClock;
	private boolean enableSend;
	private ChatLogic chatLogic;
	
	
	public VectorClockSorting(boolean enableSend, VectorClock vecClock, ChatLogic chatLogic){
		this.vectorClock = vecClock;
		this.enableSend = enableSend;
		this.chatLogic = chatLogic;
	}
	
	public void onMessageReceived(ChatMessage message){
		if (enableSend){
			chatLogic.onDisplayMessage(message);
		}
	}
	
	public VectorClock getVectorClock() {
		return vectorClock;
	}
}
