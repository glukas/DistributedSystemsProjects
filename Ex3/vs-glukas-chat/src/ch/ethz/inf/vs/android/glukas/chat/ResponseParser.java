package ch.ethz.inf.vs.android.glukas.chat;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import ch.ethz.inf.vs.android.glukas.chat.Utils.ChatEventType;
import ch.ethz.inf.vs.android.glukas.chat.Utils.SyncType;

public class ResponseParser {

	private JSONObject responseJSON;
	private ChatServerResponseInterface delegate;

	public ResponseParser() {
	}

	// /
	// Requests
	// /
	public String getRegisterRequest(String username) {

		String registerString = "{\"cmd\": \"register\"" + ", \"text\": "
				+ username + "}";
		return registerString;
	}

	public String getsendMessageRequest(String message, int messageId) {

		String messageString = "{\"cmd\": \"message\"" + "\"text\": " + message
				+ ", \"messageId\": " + String.valueOf(messageId) + "}";
		return messageString;

	}

	public String getClientsRequest() {
		String getClientsString = "{\"cmd\": \"get_clients\"" + "}";
		return getClientsString;

	}

	public String getderegisterRequest() {
		String deregisterString = "{\"cmd\": \"deregister\"" + "}";
		return deregisterString;
	}

	// /
	// Responses
	// /

	public void setDelegate(ChatServerResponseInterface delegate) {
		this.delegate = delegate;
	}

	public void parseResponse(String response) {
		// TODO (Young)
		buildJSON(response);

		if (this.getCommand() == "message") {
			this.checkMessageType();
		} else if (this.getCommand() == "register") {

			if (this.getStatus() == "success") {
				delegate.onRegistrationSucceeded();

			} else if (this.getStatus() == "failure") {
				this.checkRegisterFailureReason();

			}

		} else if (this.getCommand() == "deregister") {
			if (this.getStatus() == "success") {
				delegate.onDeregistrarionSucceeded();
			}
			if (this.getStatus() == "failure") {
				delegate.onDeregistrationFailed();

			}

		} else if (this.getCommand() == "get_clients") {
			if (this.responseJSON.has("status")) {
				if (this.getStatus() == "failure") {

					// TODO Young : How do I know what reason?
					delegate.onGetClientMappingFailed(ChatFailureReason.timeout);
				}

			} else {
				Map<Integer, String> clientMap = this.getClients();
				delegate.onGetClientMapping(clientMap);
			}

		} else if (this.getCommand() == "info") {
			// TODO Young : ChatMessage
			ChatMessage infoMessage = new ChatMessage(ChatEventType.SOME_STATE,
					0, this.getText(), null, null, 0, null);
			delegate.onMessageReceived(infoMessage);
		} else if (this.getCommand() == "notification") {
			// TODO Young : ChatMessage
			ChatMessage notificationMessage = new ChatMessage(
					ChatEventType.SOME_STATE, 0, this.getText(), null, null, 0,
					null);
			delegate.onMessageReceived(notificationMessage);
		} else if (this.getCommand() == "unknown") {
			// TODO Not sure if we actually get the MessageId
			Integer messageId = Integer.valueOf(this.getMessageId());
			delegate.onMessageDeliveryFailed(ChatFailureReason.unknownCommand,
					messageId);

		}

	}

	private void checkRegisterFailureReason() {
		String reason = this.getText();
		if (reason.contains("username already in use")) {
			delegate.onRegistrationFailed(ChatFailureReason.usernameAlreadyInUse);
		} else if (reason == "Not registered") {
			delegate.onRegistrationFailed(ChatFailureReason.notRegistered);
		} else if (reason == "Already registered") {
			delegate.onRegistrationFailed(ChatFailureReason.alreadyRegistered);

		}
	}

	private void checkMessageFailureReason(Integer messageId) {
		// TODO Young : How do I get the failure reasons?
		delegate.onMessageDeliveryFailed(ChatFailureReason.timeout, messageId);

	}

	private void checkMessageType() {
		if (this.responseJSON.has("status")) {
			Integer messageId = Integer.valueOf(this.getMessageId());
			if (this.getStatus() == "success") {

				delegate.onMessageDeliverySucceeded(messageId);

			} else if (this.getStatus() == "failure") {
				checkMessageFailureReason(messageId);

			}
		} else {
			// TODO Young : ChatMessage
			ChatMessage message = new ChatMessage(ChatEventType.SOME_STATE, 0,
					this.getText(), null, null, 0, null);
			delegate.onMessageReceived(message);

		}

	}

	// This will set the JSONObject field to a new JSONObject we get out of the
	// responseString
	private void buildJSON(String responseString) {
		try {
			this.responseJSON = new JSONObject(responseString);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// Private getter methods
	private Map<Integer, String> getClients() {
		try {
			Map<Integer, String> clientMap = new HashMap<Integer, String>();
			String clientString = this.responseJSON.getString("clients");
			JSONObject clientJSON = new JSONObject(clientString);
			Iterator<?> keys = clientJSON.keys();
			while (keys.hasNext()) {
				String keyString = keys.next().toString();
				Integer keyInteger = Integer.valueOf(keyString);
				clientMap.put(keyInteger, clientJSON.getString(keyString));
			}

			return clientMap;

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	private String getText() {
		try {
			return this.responseJSON.getString("text");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Failure";
		}

	}

	private String getCommand() {
		try {
			return this.responseJSON.getString("cmd");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Failure";
		}

	}

	private String getStatus() {
		try {
			return this.responseJSON.getString("status");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Failure";

		}
	}

	private String getSender() {
		try {
			return this.responseJSON.getString("sender");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Failure";
		}

	}

	private String getLamport() {
		try {
			return this.responseJSON.getString("lamport");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Failure";
		}

	}

	private String getVector() {
		try {
			return this.responseJSON.getString("time_vector");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Failure";
		}

	}

	private String getMessageId() {
		try {
			return this.responseJSON.getString("messageId");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Failure";
		}

	}

}
