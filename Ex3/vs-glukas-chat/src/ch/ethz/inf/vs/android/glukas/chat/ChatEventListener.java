package ch.ethz.inf.vs.android.glukas.chat;

import java.util.EventListener;
import java.util.Map;

import ch.ethz.inf.vs.android.glukas.chat.ChatEventSource.ChatEvent;

import android.os.Handler;

/**
 * This class is intercepting events triggered by the logic
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
	
	public enum FailureReason {
		notRegistered,
		alreadyRegistered,
		usernameAlreadyInUse,
		nethzNotRecognized,
		invalidLength,
		notOnEthSubnet,
		timeout,
		unkownCommand;
	};
	
	/*
	
	void onRegistrationSucceeded(int ownId, Lamport lamportClock, VectorClock, vectorClock);//TODO maybe separate onClientMapReceived
	
	void onRegistrationFailed(FailureReson reason);
	
	
	void onGetClientMapping(Map<Integer, String> clientIdToUsernameMap);
	
	void onGetClientMappingFailed(FailureReason reason);
	
	
	void onMessageDeliverySucceeded();//maybe provide handle to identify which message was delivered
	
	void onMessageDeliveryFailed(FailureReson reason);
	
	
	void onMessageReceived(ChatEvent message);

	
	void onDeregistrarionSucceeded();
	
	void onDeregistrationFailed();


	void onClientDeregistered(Integer clientId, String clientUsername);
	
	void onClientRegistered(Integer clientId, String clientUsername);*/
}
