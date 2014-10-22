package ch.ethz.inf.vs.android.glukas.capitalize;

import java.util.EventListener;

import android.os.Handler;

/**
 * This class provides a wrapper for events to be triggered
 * by the message logic.
 * @author hong-an
 *
 */

public interface MessageEventListener extends EventListener {
	/**
	 * Handler for the events stemming from the message
	 * logic.
	 */
	final Handler callbackHandler = new Handler();
	
	/**
	 * Function that returns the callback handler
	 * @return
	 */
	public Handler getCallbackHandler();
	
	// TODO Add all necessary event triggers
}
