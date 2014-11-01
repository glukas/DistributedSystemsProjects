package ch.ethz.inf.vs.android.glukas.protocol;

import java.util.Date;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import ch.ethz.inf.vs.android.glukas.chat.Logger;
import ch.ethz.inf.vs.android.glukas.chat.MainActivity;
import ch.ethz.inf.vs.android.glukas.chat.network.AsyncNetwork;
import ch.ethz.inf.vs.android.glukas.chat.network.AsyncNetworkDelegate;
import ch.ethz.inf.vs.android.glukas.protocol.MessageRequest.MessageRequestType;

@SuppressLint("UseSparseArrays")
/**
 * This class handles all the logic for the communication
 * between the chat client and the server.
 * 
 * @author hong-an
 *
 */
public class ChatLogic extends ChatEventSource implements ChatClientRequestInterface, ChatServerRawResponseInterface, MessageSequencerDelegate, AsyncNetworkDelegate {
	
	
	
	//networking
	private static final long RECEIVE_TIMEOUT_MILLIS = 2000;
	private AsyncNetwork asyncNetwork = new AsyncNetwork(Utils.SERVER_ADDRESS, Utils.SERVER_PORT_CHAT_TEST, this);
	private Handler asyncNetworkCallbackHandler = new Handler();
	private boolean sending = false;
	private Runnable timeout = new Runnable() {
		@Override
		public void run() {
			onResponseTimedOut();
		}
	};

	//protocol
	private Context context;
	private Logger logger;
	private SyncType syncType;
	private ResponseParser parser = new ResponseParser(this);
	
	//messages
	private Deque<MessageRequest> outgoingMessages = new LinkedList<MessageRequest>();
	
	//sorting
	private MessageSequencerInterface<Lamport> lampSorter;
	private MessageSequencerInterface<VectorClock> vecClockSorter;
	private int ownId;
	private Lamport lamportClock;
	private VectorClock vectorClock;
	
	
	/**
	 * Constructor
	 * @param vectorClockSync 
	 * @param context The calling activity
	 */
	public ChatLogic(SyncType syncType, Context context) {
		this.context = context;
		this.syncType = syncType;
	}
	
	/**
	 * Removes all event listeners, and stops accepting messages.
	 * All pending messages are dropped.
	 * This is not the same as deregisteration. No message is sent to the server.
	 * If any of the ChatClientRequestInterface methods is used at a later point, the connection will resume automatically.
	 */
	public void pause() {
		removeAllListeners();
		outgoingMessages.clear();
		asyncNetwork.close();
	}
	
	private void asyncSendNext() {
		if (!sending && !outgoingMessages.isEmpty()) {
			sending = true;
			
			Log.d(this.getClass().toString(), "sending " + this.outgoingMessages.peekFirst().message);
			
			asyncNetwork.sendMessage(this.outgoingMessages.peekFirst().message);
			this.getCallbackHandler().postDelayed(timeout, RECEIVE_TIMEOUT_MILLIS);
		}
	}

	private void onResponseTimedOut() {
		this.sending = false;
		Log.v(this.getClass().toString(), "timed out " + this.outgoingMessages.peekFirst().message);
		dispatchFailure(ChatFailureReason.timeout);
		asyncSendNext();
	}
	
	private void dispatchFailure(ChatFailureReason reason) {
		switch(this.outgoingMessages.peekFirst().type) {
		case register : this.onRegistrationFailed(reason);
			break;
		case deregister: this.onDeregistrationFailed();
			break;
		case getClients: this.onGetClientMappingFailed(reason);
			break;
		case sendMessage: this.onMessageDeliveryFailed(reason);
			break;
		}
	}
	
	private void inconsistentResponse() {
		Log.e(this.getClass().toString(), "inconsistent request/response pair");
	}
	
	//add a message to the sequencers
	private void enqueForSequencing(String message, int userId, Lamport lamportClock, VectorClock vectorClock) {
		long timestamp = new Date().getTime();//TODO does this belong here?
		lampSorter.onMessageReceived(new ChatMessage<Lamport>(userId, message, lamportClock, timestamp));
		vecClockSorter.onMessageReceived(new ChatMessage<VectorClock>(userId, message, vectorClock, timestamp));
	}
	
	////
	//ASYNC NETWORK DELEGATE
	////
	
	@Override
	public Handler getCallbackHandler() {
		return asyncNetworkCallbackHandler;
	}

	@Override
	public void onReceive(String message) {
		this.getCallbackHandler().removeCallbacks(timeout);
		Log.d(this.getClass().toString(), "onReceive : " + message);
		this.sending = false;
		parser.parseResponse(message);
		asyncSendNext();
	}
	
	@Override
	public void onDeliveryFailed() {
		this.getCallbackHandler().removeCallbacks(timeout);
		this.sending = false;
		dispatchFailure(ChatFailureReason.noNetwork);
		asyncSendNext();
	}
	
	////
	//CHAT CLIENT REQUEST INTERFACE
	////
	
	@Override
	public void register(String username) {
		logger = new Logger(username , this.context);
		String registerString = parser.getRegisterRequest(username);
		Log.e(this.getClass().toString(), registerString);
		outgoingMessages.add(new MessageRequest(0, registerString, MessageRequestType.register));
		asyncSendNext();
	}

	@Override
	public void deregister() {
		//TODO : should deregister be immediate?
		String deregisterString = parser.getderegisterRequest();
		outgoingMessages.add(new MessageRequest(0, deregisterString, MessageRequestType.deregister));
		asyncSendNext();
	}

	@Override
	public void sendMessage(String message, int messageId) {
		lamportClock.tick();
		vectorClock.tick();
		String messageString = 	parser.getsendMessageRequest(message, lamportClock, vectorClock);
		outgoingMessages.add(new MessageRequest(messageId, messageString, MessageRequestType.sendMessage));
		enqueForSequencing(message, ownId, lamportClock, vectorClock);
		asyncSendNext();
	}

	@Override
	public void getClients() {
		String getClientsString = parser.getClientsRequest();
		outgoingMessages.add(new MessageRequest(0, getClientsString, MessageRequestType.getClients));
		asyncSendNext();
	}
	
	////
	//CALLBACK FOR THE SEQUENCER
	//Called when the message should be forwarded to the listeners (UI)
	////
	
	@Override
	public void onDisplayMessage(String message, int userId) {
		
		if (userId != ownId) {//Ignore messages that we sent out ourselves
			logger.logReadyMsg(message, true);
			for (ChatEventListener l : getEventListeners()) {
				l.onMessageReceived(message, userId);
			}
		}
		else {
			logger.logReadyMsg(message, false);
			
		}
	}

	////
	//CHAT SERVER RESPONSE INTERFACE
	//(Called by the Parser component)
	////
	
	@Override
	public void onRegistrationSucceeded(int ownId, Lamport lamportClock, VectorClock vectorClock) {
		Log.i(this.getClass().toString(), "onRegistrationSucceeded, ownId : " + ownId);
		//initialize clocks
		this.ownId = ownId;
		this.lamportClock = lamportClock;
		this.vectorClock = vectorClock;
		//remove the first message from the queue and check if it has the right type (it should!)
		if (!outgoingMessages.isEmpty() && outgoingMessages.pollFirst().type == MessageRequestType.register) {
			for (ChatEventListener l : getEventListeners()) {
				l.onRegistrationSucceeded();
			}
			
			//initialize the sorting
			if (syncType.equals(SyncType.LAMPORT_SYNC)){
				lampSorter = new MessageSequencer<Lamport>(this, lamportClock);
				vecClockSorter = new MessageSequencer<VectorClock>(null, vectorClock);
			} else if (syncType.equals(SyncType.VECTOR_CLOCK_SYNC)){
				lampSorter = new MessageSequencer<Lamport>(null, lamportClock);
				vecClockSorter = new MessageSequencer<VectorClock>(this, vectorClock);
			}
			
		} else {
			inconsistentResponse();
		}

	}

	@Override
	public void onRegistrationFailed(ChatFailureReason reason) {
		Log.i(this.getClass().toString(), "onRegistrationFailed");
		//here, we do not check if the first message has the right type, since the server responds
		//with the "register" command to any message if the user is not registered
		if (!outgoingMessages.isEmpty()) {
			//remove the first message from the queue
			 outgoingMessages.pollFirst();
			for (ChatEventListener l : getEventListeners()) {
				l.onRegistrationFailed(reason);
			}
		} else {
			inconsistentResponse();
		}
	}

	@Override
	public void onGetClientMapping(Map<Integer, String> clientIdToUsernameMap) {
		Log.i(this.getClass().toString(), "onGetClientMapping");
		if (!outgoingMessages.isEmpty() && outgoingMessages.pollFirst().type == MessageRequestType.getClients) {
			for (ChatEventListener l : getEventListeners()) {
				l.onGetClientMapping(clientIdToUsernameMap);
			}
		} else {
			inconsistentResponse();
		}
	}

	@Override
	public void onGetClientMappingFailed(ChatFailureReason reason) {
		Log.i(this.getClass().toString(), "onGetClientMapping failed");
		if (!outgoingMessages.isEmpty() && outgoingMessages.pollFirst().type == MessageRequestType.getClients) {
			for (ChatEventListener l : getEventListeners()) {
				l.onGetClientMappingFailed(reason);
			}
		} else {
			inconsistentResponse();	
		}
	}

	@Override
	public void onMessageDeliverySucceeded() {
		Log.i(this.getClass().toString(), "onMessageDelivery Success");
		if (!outgoingMessages.isEmpty() && outgoingMessages.peekFirst().type == MessageRequestType.sendMessage) {
			int id = outgoingMessages.pollFirst().id;
			for (ChatEventListener l : getEventListeners()) {
				l.onMessageDeliverySucceeded(id);
			}
		} else {
			inconsistentResponse();	
		}
	}

	@Override
	public void onMessageDeliveryFailed(ChatFailureReason reason) {
		Log.i(this.getClass().toString(), "onMessageDelivery Failed");
		if (!outgoingMessages.isEmpty() && outgoingMessages.peekFirst().type == MessageRequestType.sendMessage) {
			int id = outgoingMessages.pollFirst().id;
			for (ChatEventListener l : getEventListeners()) {
				l.onMessageDeliveryFailed(reason, id);
			}
		} else {
			inconsistentResponse();	
		}
	}

	@Override
	public void onDeregistrarionSucceeded() {
		Log.i(this.getClass().toString(), "onDeregistration Success");
		if (!outgoingMessages.isEmpty() && outgoingMessages.pollFirst().type == MessageRequestType.deregister) {
			for (ChatEventListener l : getEventListeners()) {
				l.onDeregistrarionSucceeded();
			}
		} else {
			inconsistentResponse();	
		}
	}

	@Override
	public void onDeregistrationFailed() {
		Log.i(this.getClass().toString(), "onDeregistration Failure");
		if (!outgoingMessages.isEmpty() && outgoingMessages.pollFirst().type == MessageRequestType.deregister) {
			for (ChatEventListener l : getEventListeners()) {
				l.onDeregistrationFailed();
			}
		} else {
			inconsistentResponse();
		}
	}

	@Override
	public void onClientDeregistered(Integer clientId, String clientUsername) {
		Log.i(this.getClass().toString(), "onClientDeregister");
		for (ChatEventListener l : getEventListeners()) {
			l.onClientDeregistered(clientId, clientUsername);
		}
	}

	@Override
	public void onClientRegistered(Integer clientId, String clientUsername) {
		Log.i(this.getClass().toString(), "onClientRegister");
		for (ChatEventListener l : getEventListeners()) {
			l.onClientRegistered(clientId, clientUsername);
		}
	}

	@Override
	public void onMessageReceived(String message, int userId, Lamport lamportClock, VectorClock vectorClock) {
		Log.i(this.getClass().toString(), "onMessageReceived");
		//enqueue the message on the sequencers
		this.lamportClock.update(lamportClock);
		this.vectorClock.update(vectorClock);
		enqueForSequencing(message, userId, lamportClock, vectorClock);
	}

	@Override
	public void onInfoReceived(String message) {
		Log.i(this.getClass().toString(), "onInfoReceived");
		for (ChatEventListener l : getEventListeners()) {
			l.onInfoReceived(message);
		}
	}
}
