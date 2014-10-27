package ch.ethz.inf.vs.android.glukas.chat;

import java.util.EventListener;
import java.util.Map;

import ch.ethz.inf.vs.android.glukas.chat.ChatEventSource.ChatEvent;

import android.os.Handler;

/**
 * This class is intercepting events triggered by the logic.
 * @author hong-an
 *
 */
public interface ChatEventListener extends EventListener {

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
	
	
	////
	//CALLBACKS FOR RESPONSES BY THE SERVER
	////
	
	void onRegistrationSucceeded(int ownId);
	
	void onRegistrationFailed(ChatFailureReason reason);
	
	
	void onGetClientMapping(Map<Integer, String> clientIdToUsernameMap);
	
	void onGetClientMappingFailed(ChatFailureReason reason);
	
	
	void onMessageDeliverySucceeded(int id);
	
	void onMessageDeliveryFailed(ChatFailureReason reason, int id);
	
	
	void onMessageReceived(ChatEvent message);

	
	void onDeregistrarionSucceeded();
	
	void onDeregistrationFailed();


	void onClientDeregistered(Integer clientId, String clientUsername);
	
	void onClientRegistered(Integer clientId, String clientUsername);
}
