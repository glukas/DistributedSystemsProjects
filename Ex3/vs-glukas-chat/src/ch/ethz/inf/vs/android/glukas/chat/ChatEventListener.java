package ch.ethz.inf.vs.android.glukas.chat;

import android.os.Handler;

/**
 * This class is intercepting events triggered by the logic.
 * @author hong-an
 *
 */
public interface ChatEventListener extends ChatServerResponseInterface {

	/**
	 * Handler for the events stemming from the chat
	 * logic.
	 */
	final Handler callbackHandler = new Handler();
	
	/**
	 * This functions returns the handler.
	 * @return
	 */
	public Handler getCallbackHandler();
	

}
