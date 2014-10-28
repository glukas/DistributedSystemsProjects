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
public class ChatLogic extends ChatEventSource implements ChatClientRequestInterface, ChatServerRawResponseInterface, AsyncNetworkDelegate, Serializable {

	private transient AsyncNetwork asyncNetwork;
	private transient Handler asyncNetworkCallbackHandler;

	private transient SyncType syncType;
	
	private transient ResponseParser parser;
	
	/**
	 * Constructor
	 * @param vectorClockSync 
	 * @param context The calling activity
	 */
	public ChatLogic(SyncType syncType) {
		this.syncType = syncType;
		asyncNetworkCallbackHandler = new Handler();
		asyncNetwork = new AsyncNetwork(Utils.SERVER_ADDRESS,Utils.SERVER_PORT_CHAT, this);
		parser = new ResponseParser();//TODO
	}
	
	public void setSyncType(SyncType syncType) {
		this.syncType = syncType;
	}
	
	public void close() {
		asyncNetwork.close();
	}
	
	private void sendMessage(String message) {
		//TODO : make sure no more than 1 message is in the network at the same time (otherwise the ACKs will be useless)
		asyncNetwork.sendMessage(message);
	}
	
	////
	//SERIALIZATION
	////
	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
	}
	
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		asyncNetworkCallbackHandler = new Handler();//TODO can we be sure that this is called on the UI thread?
		asyncNetwork = new AsyncNetwork(Utils.SERVER_ADDRESS,Utils.SERVER_PORT_CHAT, this);
		parser = new ResponseParser();
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
		this.parser.parseResponse(message);
	}

	////
	//CHAT CLIENT REQUEST INTERFACE
	////
	
	@Override
	public void register(String username) {
		
		String registerString = parser.getRegisterRequest(username);
		this.sendMessage(registerString);
	}

	@Override
	public void deregister() {
	
		String deregisterString = parser.getderegisterRequest();
		this.sendMessage(deregisterString);
		
	}

	@Override
	public void sendMessage(String message, int messageId) {
		
		String messageString = 	parser.getsendMessageRequest(message, messageId);	
		this.sendMessage(messageString);
		
	}

	@Override
	public void getClients() {

		String getClientsString = parser.getClientsRequest();
		this.sendMessage(getClientsString);
	}

	////
	//CHAT SERVER RESPONSE INTERFACE
	//(Called by the Parser component)
	////
	
	//TODO (Lukas) enqueue message send requests, on received ack we can go on
	
	@Override
	public void onRegistrationSucceeded(int ownId, Lamport lamportClock, VectorClock vectorClock) {
		//TODO (Lukas) some housekeeping (keep track of ownId, lamportClock, vectorClock)
		for (ChatEventListener l : eventListenerList) {
			l.onRegistrationSucceeded();
		}
	}

	@Override
	public void onRegistrationFailed(ChatFailureReason reason) {
		for (ChatEventListener l : eventListenerList) {
			l.onRegistrationFailed(reason);
		}
	}

	@Override
	public void onGetClientMapping(Map<Integer, String> clientIdToUsernameMap) {
		for (ChatEventListener l : eventListenerList) {
			l.onGetClientMapping(clientIdToUsernameMap);
		}
	}

	@Override
	public void onGetClientMappingFailed(ChatFailureReason reason) {
		for (ChatEventListener l : eventListenerList) {
			l.onGetClientMappingFailed(reason);
		}
	}

	@Override
	public void onMessageDeliverySucceeded() {
		// TODO find message Id, notify listeners
		
	}

	@Override
	public void onMessageDeliveryFailed(ChatFailureReason reason) {
		// TODO find message Id, notify listeners
		
	}

	@Override
	public void onMessageReceived(ChatMessage message) {
		// TODO see if deliverable, if so deliver, otherwise enqueue
	}

	@Override
	public void onDeregistrarionSucceeded() {
		for (ChatEventListener l : eventListenerList) {
			l.onDeregistrarionSucceeded();
		}
	}

	@Override
	public void onDeregistrationFailed() {
		for (ChatEventListener l : eventListenerList) {
			l.onDeregistrationFailed();
		}
	}

	@Override
	public void onClientDeregistered(Integer clientId, String clientUsername) {
		for (ChatEventListener l : eventListenerList) {
			l.onClientDeregistered(clientId, clientUsername);
		}
	}

	@Override
	public void onClientRegistered(Integer clientId, String clientUsername) {
		for (ChatEventListener l : eventListenerList) {
			l.onClientRegistered(clientId, clientUsername);
		}
	}

}
