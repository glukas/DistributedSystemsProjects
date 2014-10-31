package ch.ethz.inf.vs.android.glukas.protocol;

import java.util.Date;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.util.Log;
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
	private SyncType syncType;
	private ResponseParser parser = new ResponseParser(this);
	
	//messages
	private Deque<MessageRequest> outgoingMessages = new LinkedList<MessageRequest>();
	
	//sorting
	private MessageSequencerInterface<Lamport> lampSorter;
	private MessageSequencerInterface<VectorClock> vecClockSorter;
	
	/**
	 * Constructor
	 * @param vectorClockSync 
	 * @param context The calling activity
	 */
	public ChatLogic(SyncType syncType) {
		this.syncType = syncType;
	}
	
	public void close() {
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
		Log.d(this.getClass().toString(), "timed out " + this.outgoingMessages.peekFirst().message);
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
		String messageString = 	parser.getsendMessageRequest(message, lampSorter.getLastDeliveredMessage().clock.getClock(), vecClockSorter.getLastDeliveredMessage().clock.getClock());
		outgoingMessages.add(new MessageRequest(messageId, messageString, MessageRequestType.sendMessage));
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
		for (ChatEventListener l : eventListenerList) {
			l.onMessageReceived(message, userId);
		}
	}

	////
	//CHAT SERVER RESPONSE INTERFACE
	//(Called by the Parser component)
	////
	
	@Override
	public void onRegistrationSucceeded(int ownId, Lamport lamportClock, VectorClock vectorClock) {
		//TODO (Lukas) some housekeeping (keep track of ownId?)
		Log.i(this.getClass().toString(), "onRegistrationSucceeded");
		//remove the first message from the queue and check if it has the right type (it should!)
		if (!outgoingMessages.isEmpty() && outgoingMessages.pollFirst().type == MessageRequestType.register) {
			for (ChatEventListener l : eventListenerList) {
				l.onRegistrationSucceeded();
			}
			
			//create initial messages
			long timestamp = new Date().getTime();
			ChatMessage<Lamport> lamportMessage = new ChatMessage<Lamport>(-1, "", lamportClock, timestamp);
			ChatMessage<VectorClock> vectorClockMessage = new ChatMessage<VectorClock>(-1, "", vectorClock, timestamp);
			
			//initialize the sorting
			if (syncType.equals(SyncType.LAMPORT_SYNC)){
				lampSorter = new MessageSequencer<Lamport>(this, lamportMessage);
				vecClockSorter = new MessageSequencer<VectorClock>(null, vectorClockMessage);
			} else if (syncType.equals(SyncType.VECTOR_CLOCK_SYNC)){
				lampSorter = new MessageSequencer<Lamport>(null, lamportMessage);
				vecClockSorter = new MessageSequencer<VectorClock>(this, vectorClockMessage);
			}
			
		} else {
			inconsistentResponse();
		}

	}

	@Override
	public void onRegistrationFailed(ChatFailureReason reason) {
		Log.i(this.getClass().toString(), "onRegistrationFailed");
		//remove the first message from the queue and check if it has the right type (it should!)
		if (!outgoingMessages.isEmpty() && outgoingMessages.pollFirst().type == MessageRequestType.register) {
			for (ChatEventListener l : eventListenerList) {
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
			for (ChatEventListener l : eventListenerList) {
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
			for (ChatEventListener l : eventListenerList) {
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
			for (ChatEventListener l : eventListenerList) {
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
			for (ChatEventListener l : eventListenerList) {
				l.onMessageDeliveryFailed(reason, id);
			}
		} else {
			inconsistentResponse();	
		}
	}

	/*@Override
	public void onMessageReceived(ChatMessage message) {

	}*/

	@Override
	public void onDeregistrarionSucceeded() {
		Log.i(this.getClass().toString(), "onDeregistration Success");
		if (!outgoingMessages.isEmpty() && outgoingMessages.peekFirst().type == MessageRequestType.deregister) {
			for (ChatEventListener l : eventListenerList) {
				l.onDeregistrarionSucceeded();
			}
		} else {
			inconsistentResponse();	
		}
	}


	@Override
	public void onDeregistrationFailed() {
		Log.i(this.getClass().toString(), "onDeregistration Failure");
		if (!outgoingMessages.isEmpty() && outgoingMessages.peekFirst().type == MessageRequestType.deregister) {
			for (ChatEventListener l : eventListenerList) {
				l.onDeregistrationFailed();
			}
		} else {
			inconsistentResponse();
		}
	}

	@Override
	public void onClientDeregistered(Integer clientId, String clientUsername) {
		Log.i(this.getClass().toString(), "onClientDeregister");
		for (ChatEventListener l : eventListenerList) {
			l.onClientDeregistered(clientId, clientUsername);
		}
	}

	@Override
	public void onClientRegistered(Integer clientId, String clientUsername) {
		Log.i(this.getClass().toString(), "onClientRegister");
		for (ChatEventListener l : eventListenerList) {
			l.onClientRegistered(clientId, clientUsername);
		}
	}

	@Override
	public void onMessageReceived(String message, int userId, Lamport lamportClock, VectorClock vectorClock) {
		Log.i(this.getClass().toString(), "onMessageReceived");
		//enqueue the message on the sequencers
		long timestamp = new Date().getTime();
		lampSorter.onMessageReceived(new ChatMessage<Lamport>(userId, message, lamportClock, timestamp));
		vecClockSorter.onMessageReceived(new ChatMessage<VectorClock>(userId, message, vectorClock, timestamp));
	}
}
