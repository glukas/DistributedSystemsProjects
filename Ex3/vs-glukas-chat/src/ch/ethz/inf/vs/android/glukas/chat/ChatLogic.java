package ch.ethz.inf.vs.android.glukas.chat;

import java.io.Serializable;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import ch.ethz.inf.vs.android.glukas.chat.Utils.ChatEventType;
import ch.ethz.inf.vs.android.glukas.chat.Utils.SyncType;

@SuppressLint("UseSparseArrays")
/**
 * This class handles all the logic for the communication
 * between the chat client and the server.
 * 
 * @author hong-an
 *
 */
public class ChatLogic extends ChatEventSource implements ChatClientRequestInterface, ChatServerResponseInterface {

	/**
	 * Context of the activity
	 */
	Context appContext;
	
	/**
	 * This object handles the UDP communication between the client and the chat
	 * server
	 */
	UDPCommunicator comm;
	
	/**
	 * This object should be used to log 
	 * deliverable messages.
	 */
	Logger log;

	/**
	 * This function should initialize the logger as
	 * soon as the username is registered.
	 * @param username
	 */
	public void initLogger(String username) {
		this.log = new Logger(username, appContext);
	}

	/**
	 * Constructor
	 * @param context Context of the Activity
	 * @param sync Indicates whether Lamport timestamps or Vector clocks should be used
	 */
	public ChatLogic(Context context, SyncType sync) {

	}

	
	//TODO MOVE INTO SOME PARSER CLASS
	/**
	 * This function should parse incoming JSON packets and trigger
	 * the necessary events.
	 * @param jsonMap Incoming JSON packet
	 * @return The type of event that took place.
	 * @throws JSONException
	 */
	public ChatEventType parseJSON(JSONObject jsonMap) throws JSONException {
		// TODO Fill me
		return ChatEventType.SOME_STATE;
	}

	////
	//CHAT CLIENT REQUEST INTERFACE
	////
	
	@Override
	public void register(String username) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deregister() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendMessage(String message, int messageId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getClients() {
		// TODO Auto-generated method stub
		
	}

	////
	//CHAT SERVER RESPONSE INTERFACE
	//(Called by the Parser component)
	////
	
	@Override
	public void onRegistrationSucceeded(int ownId, Lamport lamportClock,
			VectorClock vectorClock) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRegistrationFailed(ChatFailureReason reason) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetClientMapping(Map<Integer, String> clientIdToUsernameMap) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetClientMappingFailed(ChatFailureReason reason) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMessageDeliverySucceeded(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMessageDeliveryFailed(ChatFailureReason reason, int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMessageReceived(ChatMessage message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDeregistrarionSucceeded() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDeregistrationFailed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClientDeregistered(Integer clientId, String clientUsername) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClientRegistered(Integer clientId, String clientUsername) {
		// TODO Auto-generated method stub
		
	}

}
