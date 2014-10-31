package ch.ethz.inf.vs.android.glukas.chat;

public interface ChatDisplayMessageInterface {
	
	/**
	 * Display a message on all Listener of the chat
	 * @param message to display
	 */
	abstract void onDisplayMessage(ChatMessage message);
}
