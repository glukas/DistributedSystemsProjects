package ch.ethz.inf.vs.android.glukas.chat;

import java.util.EventListener;
import java.util.Map;

public interface ChatServerRawResponseInterface extends EventListener {

	////
	//CALLBACKS FOR RESPONSES BY THE SERVER
	////
	
	void onRegistrationSucceeded(int ownId, Lamport lamportClock, VectorClock vectorClock);
	
	void onRegistrationFailed(ChatFailureReason reason);
	
	
	void onGetClientMapping(Map<Integer, String> clientIdToUsernameMap);
	
	void onGetClientMappingFailed(ChatFailureReason reason);
	
	
	void onMessageDeliverySucceeded();
	
	void onMessageDeliveryFailed(ChatFailureReason reason);
	
	
	void onMessageReceived(ChatMessage message);

	
	void onDeregistrarionSucceeded();
	
	void onDeregistrationFailed();


	void onClientDeregistered(Integer clientId, String clientUsername);
	
	void onClientRegistered(Integer clientId, String clientUsername);
	
}
