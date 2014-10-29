package ch.ethz.inf.vs.android.glukas.chat;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;
import ch.ethz.inf.vs.android.glukas.chat.Utils.ChatEventType;

public class ResponseParser {

	private JSONObject responseJSON;
	private ChatServerRawResponseInterface delegate;
	private Lamport testlamport = new Lamport();
	private VectorClock testvectorClock = new VectorClock();
	public ResponseParser() {
	}

	////
	// Requests
	////
	
	public String getRegisterRequest(String username) {

		String registerString = "{\"cmd\": \"register\"" + ", \"user\": "
				+ "\"" + username + "\"" + "}";
		return registerString;
	}

	public String getsendMessageRequest(String message, int messageId) {
		if (this.testlamport == null) {
			Log.v("test", "Yes lamport is null!");
		}
		if (this.testvectorClock == null){
			Log.v("test", "Yes vectorclock is null!");
		}
	
		String messageString = "{\"cmd\": \"message\"" + ", \"text\": "
		+ "\""	+ message + "\"" 
		+ ", \"lamport\": "   + this.testlamport.toString() 
		+ ", \"time_vector\": " + this.testvectorClock.toString() 
		//+ ", \"messageId\": " + "\"" + String.valueOf(messageId) + "\"" 
		+ "}";
		Log.v("", messageString);
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

	public void setDelegate(ChatServerRawResponseInterface delegate) {
		this.delegate = delegate;
	}

	public void parseResponse(String response) {
		// TODO (Young)
		this.buildJSON(response);
		try {
			Log.v("After Build", this.responseJSON.getString("init_time_vector"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.v("Command", this.getCommand());
		Log.v("Success", this.getStatus());
		if (this.getCommand().equals( "message")) {
			this.checkMessageType();
		} else if (this.getCommand().equals("register")) {
			Log.v("Register", "okay");
			if (this.getStatus().equals( "success")) {
				Log.v("Status", "okay");
				delegate.onRegistrationSucceeded(this.getIndex(),
						this.getLamport(), this.getVectorClock());// TODO
																	// (Young)

			} else if (this.getStatus().equals("failure")) {
				this.checkRegisterFailureReason();

			}

		} else if (this.getCommand().equals( "deregister")) {
			if (this.getStatus().equals( "success")) {
				delegate.onDeregistrarionSucceeded();
			}
			if (this.getStatus().equals( "failure")) {
				delegate.onDeregistrationFailed();

			}

		} else if (this.getCommand().equals( "get_clients")) {
			if (this.responseJSON.has("status")) {
				if (this.getStatus().equals( "failure")) {

					// TODO Young : How do I know what reason?
					delegate.onGetClientMappingFailed(ChatFailureReason.timeout);
				}

			} else {
				Map<Integer, String> clientMap = this.getClients();
				delegate.onGetClientMapping(clientMap);
			}

		} else if (this.getCommand().equals( "info")) {
			// TODO Young : ChatMessage
			ChatMessage infoMessage = new ChatMessage(ChatEventType.SOME_STATE,
					0, this.getText(), null, null, 0, null);
			delegate.onMessageReceived(infoMessage);
		} else if (this.getCommand().equals( "notification")) {
			// TODO Young : ChatMessage
			ChatMessage notificationMessage = new ChatMessage(
					ChatEventType.SOME_STATE, 0, this.getText(), null, null, 0,
					null);
			delegate.onMessageReceived(notificationMessage);
		} else if (this.getCommand().equals("unknown")) {
			delegate.onMessageDeliveryFailed(ChatFailureReason.unknownCommand);

		}

	}

	private void checkRegisterFailureReason() {
		String reason = this.getText();
		if (reason.contains("username already in use")) {
			delegate.onRegistrationFailed(ChatFailureReason.usernameAlreadyInUse);
		} else if (reason.equals( "Not registered")) {
			delegate.onRegistrationFailed(ChatFailureReason.notRegistered);
		} else if (reason.equals( "Already registered")) {
			delegate.onRegistrationFailed(ChatFailureReason.alreadyRegistered);

		}
	}

	private void checkMessageFailureReason() {
		// TODO Young : How do I get the failure reasons?
		delegate.onMessageDeliveryFailed(ChatFailureReason.timeout);

	}

	private void checkMessageType() {
		if (this.responseJSON.has("status")) {

			if (this.getStatus().equals("success")) {

				delegate.onMessageDeliverySucceeded();

			} else if (this.getStatus().equals( "failure")) {
				checkMessageFailureReason();

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

	private HashMap<Integer, Integer> getVectorClockMap() {

		try {
			HashMap<Integer, Integer> vectorMap = new HashMap<Integer, Integer>();
			String vectorString = this.responseJSON.getString("init_time_vector");
			
			JSONObject clientJSON = new JSONObject(vectorString);
			Iterator<?> keys = clientJSON.keys();
			while (keys.hasNext()) {
				String keyString = keys.next().toString();
				Integer keyInteger = Integer.valueOf(keyString);
				Integer valueInteger = Integer.valueOf(clientJSON
						.getString(keyString));
				vectorMap.put(keyInteger, valueInteger);
			}
			return vectorMap;

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.v("JSONException", "VectorMap");
			return null;
		}

	}

	private VectorClock getVectorClock() {
		Log.v("getVectorClock", "got called");
		VectorClock vectorclock = new VectorClock(this.getVectorClockMap(),
				this.getIndex());
		this.testvectorClock = vectorclock;
		return vectorclock;
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

	private Integer getIndex() {

		Integer index;
		try {
			index = this.responseJSON.getInt("index");
			return index;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
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

	private Lamport getLamport() {
		Log.v("getLamport", "got called");
		try {
			
			Lamport lamport = new Lamport(this.responseJSON.getInt("init_lamport"));
			
			this.testlamport = lamport;
			Log.v("getLamport",this.testlamport.toString());
			return lamport;

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.v("","RAISED");
			e.printStackTrace();
			return null;
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
