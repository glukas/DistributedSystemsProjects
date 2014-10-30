package ch.ethz.inf.vs.android.glukas.chat;

import java.util.SortedSet;
import java.util.TreeSet;

import android.os.Handler;

import ch.ethz.inf.vs.android.glukas.chat.Utils.ChatEventType;
import ch.ethz.inf.vs.android.glukas.chat.Utils.SyncType;

public class LamportSorting {
	
	private Lamport lamport;
	private boolean enableSend;
	private ChatLogic chatLogic;
	private ChatMessage smallestMessage;
	private ChatMessage initialMessage;
	private TreeSet<ChatMessage> queue;
	private Integer timeout_value = 5000;
	private Handler timeout_handler;
	private boolean timeout_running;
	private Runnable timeout = new Runnable(){

		@Override
		public void run() {
			
			displayQueue();
		}
		
	};
	
	
	public LamportSorting(boolean enableSend, Lamport lamport, ChatLogic chatLogic){
		this.lamport = lamport;
		this.enableSend = enableSend;
		this.chatLogic = chatLogic;
		this.initialMessage = new ChatMessage(ChatEventType.SOME_STATE, 0, null,lamport, null, 0,
				SyncType.LAMPORT_SYNC);
		this.smallestMessage = initialMessage;
		this.timeout_handler = new Handler();
		this.queue = new TreeSet(new LamportComparator());
	}
	
	public void onMessageReceived(ChatMessage message){
		queue.add(message);
		for (ChatMessage m : queue){
			if (m.isNextMessage(this.smallestMessage)){
				if (enableSend){
					this.setSmallestMessage(m);
					queue.pollFirst();
					chatLogic.onDisplayMessage(m);
					if(timeout_running){
					this.timeout_handler.removeCallbacks(this.timeout);
					this.timeout_running = false;
					}
				}
			}
			else {
				if (!timeout_running){
				this.timeout_handler.postDelayed(this.timeout, this.timeout_value);
				this.timeout_running = false;
				}
			}
		}
		
	}
	
	public void setSmallestMessage(ChatMessage message){
		this.smallestMessage = message;
		
	}
	
	public void displayQueue(){
		for (ChatMessage m : queue){
			chatLogic.onDisplayMessage(m);
		}
		this.setSmallestMessage(queue.last());
		queue.clear();
	}
	
	public Lamport getLamport(){
		return lamport;
	}
}
