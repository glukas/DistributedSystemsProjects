package ch.ethz.inf.vs.android.glukas.protocol;

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
	
	
	void onMessageReceived(String message, int userId, Lamport lamportClock, VectorClock vectorClock);

	
	void onDeregistrarionSucceeded();
	
	void onDeregistrationFailed();


	void onClientDeregistered(Integer clientId, String clientUsername);
	
	void onClientRegistered(Integer clientId, String clientUsername);
	
	
	void onInfoReceived(String message);
	
	void onUnknownCommand();
}
