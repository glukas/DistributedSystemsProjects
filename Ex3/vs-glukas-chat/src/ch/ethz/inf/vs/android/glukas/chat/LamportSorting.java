package ch.ethz.inf.vs.android.glukas.chat;

public class LamportSorting {
	
	private Lamport lamport;
	private boolean enableSend;
	private ChatLogic chatLogic;
	
	
	public LamportSorting(boolean enableSend, Lamport lamport, ChatLogic chatLogic){
		this.lamport = lamport;
		this.enableSend = enableSend;
		this.chatLogic = chatLogic;
	}
	
	public void onMessageReceived(ChatMessage message){
		if (enableSend){
			chatLogic.onDisplayMessage(message);
		}
	}
	
	public Lamport getLamport(){
		return lamport;
	}
}
