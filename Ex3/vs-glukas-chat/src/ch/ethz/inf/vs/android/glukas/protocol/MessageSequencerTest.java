package ch.ethz.inf.vs.android.glukas.protocol;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

public class MessageSequencerTest {

	private volatile boolean senderAlive = true;
	private volatile boolean receiverAlive = true;
	
	private static final long timestamp =  (new Date().getTime()%2048);
	private static final String testPrefix = "(unit-test " + timestamp + ") : ";
	
	private static final String senderId = "glukas"+timestamp;
	private static final String receiverId = "glukas"+(timestamp+1);
	
	private static final int numberOfTestMessages = 10;
	
	private void fail(String reason) {
		Log.e(this.getClass().toString(), reason);
		throw new RuntimeException("test failed : " + reason);
	}
	
	public void test() {
		
		final MessageSender senderListener = new MessageSender();
		final MessageReceiver receiverListener = new MessageReceiver();
		
		HandlerThread senderThread = new HandlerThread("messageSender1011");
		HandlerThread receiverThread = new HandlerThread("messageReceiver1012");
		
		senderThread.start();
		receiverThread.start();
		
		Handler receiverHandler = new Handler(receiverThread.getLooper());
		
		receiverHandler.post(new Runnable() {
			public void run() {
				receiverListener.logic = new ChatLogic(SyncType.LAMPORT_SYNC, null);
				receiverListener.logic.addChatEventListener(senderListener);
				receiverListener.logic.register(receiverId);
			}
		});
		
		Handler senderHandler = new Handler(senderThread.getLooper());
		
		senderHandler.post(new Runnable() {
			public void run() {
				senderListener.logic = new ChatLogic(SyncType.LAMPORT_SYNC, null);
				senderListener.logic.addChatEventListener(senderListener);
				senderListener.logic.register(senderId);
			}
		});
		
		while (senderAlive || receiverAlive) {	
		}
		
	}

	private class MessageSender implements ChatEventListener {
		
		ChatLogic logic;
		Map<Integer, String> clients;
		int numAcked = 0;
		
		@Override
		public void onRegistrationSucceeded() {
			logic.getClients();
		}

		@Override
		public void onRegistrationFailed(ChatFailureReason reason) {
			fail("registration failed");
		}

		@Override
		public void onGetClientMapping(Map<Integer, String> clientIdToUsernameMap) {
			clients = clientIdToUsernameMap;
			if (clients.containsValue(receiverId)) {
				sendTestMessages();
			}
		}
		
		private void sendTestMessages() {
			for (int i=0; i<numberOfTestMessages; i++) {
				logic.sendMessage(testPrefix + i, i);
			}
		}

		@Override
		public void onGetClientMappingFailed(ChatFailureReason reason) {
			fail(reason.getReasonString());
		}

		@Override
		public void onMessageDeliverySucceeded(int id) {
			numAcked++;
			if (numAcked == numberOfTestMessages) {
				logic.deregister();
			}
		}

		@Override
		public void onMessageDeliveryFailed(ChatFailureReason reason, int id) {
			fail(reason.getReasonString() + " message id : " + id);
		}

		@Override
		public void onMessageReceived(String message, int userId) {
		}

		@Override
		public void onDeregistrarionSucceeded() {
			senderAlive = false;
		}

		@Override
		public void onDeregistrationFailed() {
			fail("deregistration failed");
		}

		@Override
		public void onClientDeregistered(Integer clientId, String clientUsername) {
			
		}

		@Override
		public void onClientRegistered(Integer clientId, String clientUsername) {
			if (clientUsername.equals(receiverId)) {
				sendTestMessages();
			}
		}

		@Override
		public void onInfoReceived(String message) {
		}
	}
	
	private class MessageReceiver implements ChatEventListener {
		
		ChatLogic logic;
		int lastId = -1;
		
		@Override
		public void onRegistrationSucceeded() {
		}

		@Override
		public void onRegistrationFailed(ChatFailureReason reason) {
			fail("registration failed");
		}

		@Override
		public void onGetClientMapping(Map<Integer, String> clientIdToUsernameMap) {
		}

		@Override
		public void onGetClientMappingFailed(ChatFailureReason reason) {
			fail(reason.getReasonString());
		}

		@Override
		public void onMessageDeliverySucceeded(int id) {
		}

		@Override
		public void onMessageDeliveryFailed(ChatFailureReason reason, int id) {
		}

		@Override
		public void onMessageReceived(String message, int userId) {
			
			if (message.startsWith(testPrefix)) {
				String next = message.substring(testPrefix.length());
				int nextId = Integer.parseInt(next);
				if (nextId != lastId+1) fail(String.format("Messages out of order. Expected %d, but was %d", lastId+1, nextId));
				lastId = nextId;
				
				if (lastId >= numberOfTestMessages-1) {
					logic.deregister();
				}
			}

		}

		@Override
		public void onDeregistrarionSucceeded() {
			receiverAlive = false;
		}

		@Override
		public void onDeregistrationFailed() {
			fail("deregistration failed");
		}

		@Override
		public void onClientDeregistered(Integer clientId, String clientUsername) {
		}

		@Override
		public void onClientRegistered(Integer clientId, String clientUsername) {
		}

		@Override
		public void onInfoReceived(String message) {
		}
	}

}
