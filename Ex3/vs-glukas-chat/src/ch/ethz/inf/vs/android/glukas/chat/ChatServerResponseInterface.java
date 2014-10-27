package ch.ethz.inf.vs.android.glukas.chat;

import java.util.EventListener;
import java.util.Map;

public interface ChatServerResponseInterface extends EventListener {

	////
	//CALLBACKS FOR RESPONSES BY THE SERVER
	////
	
	void onRegistrationSucceeded(int ownId, Lamport lamportClock, VectorClock vectorClock);
	
	void onRegistrationFailed(ChatFailureReason reason);
	
	
	void onGetClientMapping(Map<Integer, String> clientIdToUsernameMap);
	
	void onGetClientMappingFailed(ChatFailureReason reason);
	
	
	void onMessageDeliverySucceeded(int id);
	
	void onMessageDeliveryFailed(ChatFailureReason reason, int id);
	
	
	void onMessageReceived(ChatMessage message);

	
	void onDeregistrarionSucceeded();
	
	void onDeregistrationFailed();


	void onClientDeregistered(Integer clientId, String clientUsername);
	
	void onClientRegistered(Integer clientId, String clientUsername);
	
}
