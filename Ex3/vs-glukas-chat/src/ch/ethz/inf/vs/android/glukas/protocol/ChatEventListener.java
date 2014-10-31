package ch.ethz.inf.vs.android.glukas.protocol;

import java.util.Map;

/**
 * This class is intercepting events triggered by the logic.
 * @author hong-an
 *
 */
public interface ChatEventListener {

	////
	//CALLBACKS FOR RESPONSES BY THE SERVER
	////
	
	void onRegistrationSucceeded();
	
	void onRegistrationFailed(ChatFailureReason reason);
	
	
	void onGetClientMapping(Map<Integer, String> clientIdToUsernameMap);
	
	void onGetClientMappingFailed(ChatFailureReason reason);
	
	
	void onMessageDeliverySucceeded(int id);
	
	void onMessageDeliveryFailed(ChatFailureReason reason, int id);
	
	
	void onMessageReceived(String message, int userId);

	
	void onDeregistrarionSucceeded();
	
	void onDeregistrationFailed();


	void onClientDeregistered(Integer clientId, String clientUsername);
	
	void onClientRegistered(Integer clientId, String clientUsername);
	
	
	void onInfoReceived(String message);
}
