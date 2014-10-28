package ch.ethz.inf.vs.android.glukas.chat;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import ch.ethz.inf.vs.android.glukas.chat.AsyncNetwork;
import ch.ethz.inf.vs.android.glukas.chat.Utils;
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
public class ChatLogic extends ChatEventSource implements ChatClientRequestInterface, ChatServerResponseInterface, AsyncNetworkDelegate, Serializable {

	private transient AsyncNetwork asyncNetwork;
	private transient Handler asyncNetworkCallbackHandler;

	private transient SyncType syncType;
	
	/**
	 * Constructor
	 * @param vectorClockSync 
	 * @param context The calling activity
	 */
	public ChatLogic(SyncType syncType) {
		this.syncType = syncType;
		asyncNetworkCallbackHandler = new Handler();
		asyncNetwork = new AsyncNetwork(Utils.SERVER_ADDRESS,Utils.SERVER_PORT_CHAT, this);
	}
	
	public void setSyncType(SyncType syncType) {
		this.syncType = syncType;
	}
	
	public void close() {
		asyncNetwork.close();
	}
	
	private void sendMessage(String message) {
		asyncNetwork.sendMessage(message);
	}
	
	////
	//SERIALIZATION
	////
	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
	}
	
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		asyncNetworkCallbackHandler = new Handler();
		asyncNetwork = new AsyncNetwork(Utils.SERVER_ADDRESS,Utils.SERVER_PORT_CHAT, this);
	}
	
	////
	//ASYNC NETWORK DELEGATE
	////
	
	@Override
	public Handler getCallbackHandler() {
		return asyncNetworkCallbackHandler;
	}

	@Override
	public void OnReceive(String message) {
		ChatEvent chatEvent = new ChatEvent(this, null, message, null);
		chatEvent.dispatchEvent();
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
	public void onRegistrationSucceeded(int ownId, Lamport lamportClock, VectorClock vectorClock) {
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
