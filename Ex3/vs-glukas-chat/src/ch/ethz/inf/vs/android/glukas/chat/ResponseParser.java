package ch.ethz.inf.vs.android.glukas.chat;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.util.Log;
import ch.ethz.inf.vs.android.glukas.chat.Utils.ChatEventType;

@SuppressLint("UseSparseArrays")
public class ResponseParser {

	private JSONObject responseJSON;
	private ChatServerRawResponseInterface delegate;
	private Lamport testlamport = new Lamport();
	private VectorClock testvectorClock = new VectorClock();
	
	private enum Cmd{
		CMD("cmd"), 
		CLIENTS("clients"),
		USER("user"),
		TEXT("text"),
		LAMPORT("lamport"),
		VECTOR("time_vector"),
		MESSAGEID("messageId"),
		STATUS("status");
		
		private Cmd(String s){
			name = s;
		}
		
		private final String name;
		
		public String getStr(){
			return name;
		}
		
	}
	
	private enum Arg{
		GETCLIENTS("get_clients"),
		REGISTER("register"),
		MESSAGE("message"),
		DEREGISTER("deregister"),
		INFO("info"),
		UNKNOWN("unknown"),
		NOTIFICATION("notification"),
		SUCCESS("success"),
		FAILURE("failure");
		
		private Arg(String s){
			name = s;
		}
		
		private final String name;
		
		public String getStr(){
			return name;
		}
	}
	
	public ResponseParser() {
	}

	////
	// Requests
	////
	
	public String getRegisterRequest(String username) {
		
		JSONObject request = new JSONObject();
		try {
			request.put(Cmd.CMD.getStr(), Arg.REGISTER.getStr());
			request.put(Cmd.USER.getStr(), username);
		} catch (JSONException e) {

		}
		Log.v("","Request : "+request.toString());
		return request.toString();
	}

	public String getsendMessageRequest(String msg, int messageId, Lamport lamport, VectorClock vecClock) {
		
		JSONObject request = new JSONObject();
		try {
			request.put(Cmd.CMD.getStr(), Arg.MESSAGE.getStr());
			request.put(Cmd.TEXT.getStr(), msg);
			request.put(Cmd.LAMPORT.getStr(), lamport.toString());
			request.put(Cmd.VECTOR.getStr(), vecClock.toString());
			request.put(Cmd.MESSAGEID.getStr(), String.valueOf(messageId));
		} catch (JSONException e) {

		}
		Log.v("","Request : "+request.toString());
		return request.toString();
	}

	public String getClientsRequest() {
		JSONObject request = new JSONObject();
		try {
			request.put(Cmd.CMD.getStr(), Arg.GETCLIENTS);
		} catch (JSONException e) {

		}
		Log.v("","Request : "+request.toString());
		return request.toString();
	}

	public String getderegisterRequest() {
		JSONObject request = new JSONObject();
		try {
			request.put(Cmd.CMD.getStr(), Arg.DEREGISTER);
		} catch (JSONException e) {

		}
		Log.v("","Request : "+request.toString());
		return request.toString();
	}

	////
	// Responses
	////

	public void setDelegate(ChatServerRawResponseInterface delegate) {
		this.delegate = delegate;
	}
	
	public void parseResponse2(String response){
		try{
			JSONObject jObject = new JSONObject(response);
			String argCmd = jObject.getString(Cmd.CMD.getStr());
			if (argCmd.equals(Arg.GETCLIENTS.getStr())){
				onGetClients(jObject);
			} else if (argCmd.equals(Arg.REGISTER.getStr())){
				onRegister(jObject);
			} else if (argCmd.equals(Arg.NOTIFICATION.getStr())){
				onNotification(jObject);
			} else if (argCmd.equals(Arg.INFO.getStr())){
				onInfo(jObject);
			} else if (argCmd.equals(Arg.MESSAGE.getStr())){
				onMessage(jObject);
			} else if (argCmd.equals(Arg.DEREGISTER.getStr())){
				onDeregister(jObject);
			} else if (argCmd.equals(Arg.UNKNOWN.getStr())){
				onUnknown(jObject);
			}
		} catch (JSONException e){
			Log.e("","Unable to parse : "+e.getLocalizedMessage());
		}
	}
	
	private void onGetClients(JSONObject jObject) throws JSONException {
		
	}
	
	private void onRegister(JSONObject jObject) throws JSONException {
		String status = jObject.getString(Cmd.STATUS.getStr());
		if (status.equals(Arg.FAILURE.getStr())){
			String reason = jObject.getString(Cmd.TEXT.getStr());
			delegate.onRegistrationFailed(ChatFailureReason.unknownCommand);
		} else if (status.equals(Arg.SUCCESS.getStr())){
			
		}
	}
	
	private void onNotification(JSONObject jObject) throws JSONException {
		
	}
	
	private void onInfo(JSONObject jObject) throws JSONException {
		
	}
	
	private void onMessage(JSONObject jObject) throws JSONException {
		
	}
	
	private void onDeregister(JSONObject jObject) throws JSONException {
		
	}
	
	private void onUnknown(JSONObject jObject) throws JSONException {
		
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
