package ch.ethz.inf.vs.android.glukas.chat;

import java.util.SortedSet;

public class MessageSequencer<SyntheticClock> {

	SortedSet queue;
	ChatLogic chatLogic;
	SyntheticClock clock;
	boolean enableSend;
	
	public MessageSequencer(boolean enableSend, SyntheticClock clock, ChatLogic chatLogic){
		this.clock = clock;
		this.enableSend = enableSend;
		this.chatLogic = chatLogic;
	}
	
	
	
	
	
	
	
	public void MessageReceived(ChatMessage message){
			
		
		
		
		
	}
	
	
	
	
	
	
}
