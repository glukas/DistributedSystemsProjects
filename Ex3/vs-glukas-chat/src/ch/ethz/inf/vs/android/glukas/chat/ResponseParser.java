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

	private ChatServerRawResponseInterface delegate;
	
	private final String msgNoStatus = "message with no status : ";
	private final String parseError = "Unable to parse : ";
	private final String parO = "{";
	private final String parC = "}";
	private final String col = ":";
	private final String quo = "\"";
	private final String com = ",";
	private final String spa = " ";
	
	private enum Cmd{
		CMD("cmd"), 
		CLIENTS("clients"),
		USER("user"),
		TEXT("text"),
		LAMPORT("lamport"),
		VECTOR("time_vector"),
		MESSAGEID("messageId"),
		STATUS("status"),
		INITLAMPORT("init_lamport"),
		INITVECCLOCK("init_time_vector"),
		INDEX("index"),
		SENDER("sender");
		
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
	
	public ResponseParser(ChatServerRawResponseInterface delegate) {
		this.delegate = delegate;
	}

	////
	//Requests
	////
	
	public String getRegisterRequest(String username) {
		return 	parO + getCmd(Cmd.CMD.getStr(),Arg.REGISTER.getStr()) + com + spa +
				getCmd(Cmd.USER.getStr(),username) + parC;
	}

	public String getsendMessageRequest(String msg, Lamport lamport, VectorClock vecClock) {
		return 	parO + getCmd(Cmd.CMD.getStr(), Arg.MESSAGE.getStr()) + com + spa +
				getCmd(Cmd.TEXT.getStr(), msg) + com + spa + 
				getCmdWQuote(Cmd.VECTOR.getStr(), vecClock.toString()) + com + spa +
				getCmd(Cmd.LAMPORT.getStr(), lamport.getTimestamp()) + parC;
	}

	public String getClientsRequest() {
		return getSingleCmd(Arg.GETCLIENTS.getStr());

	}

	public String getderegisterRequest() {
		return getSingleCmd(Arg.DEREGISTER.getStr());
	}
	
	private String getSingleCmd(String cmdArg){
		return parO + getCmd(Cmd.CMD.getStr(), cmdArg) + parC;
	}

	private String getCmd(String cmd, String arg){
		return quo + cmd + quo + col + spa + quo + arg + quo;
	}
	
	private String getCmdWQuote(String cmd, String arg){
		return quo + cmd + quo + col + spa + arg;
	}
	
	private String getCmd(String cmd, int arg){
		return quo + cmd + quo + col + spa + arg;
	}
	
	////
	//Responses
	////

	public void parseResponse(String response){
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
			Log.e(getClass().toString(),parseError+e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
	
	private void onGetClients(JSONObject jObject) throws JSONException {
		if (jObject.has(Cmd.STATUS.getStr()) && jObject.getString(Cmd.STATUS.getStr()).equals(Arg.FAILURE.getStr())) {
			delegate.onGetClientMappingFailed(ChatFailureReason.getReasonFromString(jObject.getString(Cmd.TEXT.getStr())));
		} else {
			Map<Integer, String> clientMap = getClients(jObject);
			delegate.onGetClientMapping(clientMap);
		} 
	}
	
	private void onRegister(JSONObject jObject) throws JSONException {
		String status = jObject.getString(Cmd.STATUS.getStr());
		if (status.equals(Arg.FAILURE.getStr())){
			delegate.onRegistrationFailed(ChatFailureReason.getReasonFromString(jObject.getString(Cmd.TEXT.getStr())));
		} else if (status.equals(Arg.SUCCESS.getStr())){
			delegate.onRegistrationSucceeded(jObject.getInt(Cmd.INDEX.getStr()), getLamport(jObject), 
					getVectorClock(jObject));
		} else {
			Log.e(getClass().toString(), msgNoStatus + jObject.toString());
		}
	}
	
	private void onNotification(JSONObject jObject) throws JSONException {
		ChatMessage notificationMessage = new ChatMessage(
				ChatEventType.SOME_STATE, 0, jObject.getString(Cmd.TEXT.getStr()), null, null, 0,
				null);
		delegate.onMessageReceived(notificationMessage);
	}
	
	private void onInfo(JSONObject jObject) throws JSONException {
		ChatMessage infoMessage = new ChatMessage(ChatEventType.SOME_STATE,
				0, jObject.getString(Cmd.TEXT.getStr()), null, null, 0, null);
		delegate.onMessageReceived(infoMessage);
	}
	
	private void onMessage(JSONObject jObject) throws JSONException {
		if (jObject.has(Cmd.STATUS.getStr())) {
			if (jObject.getString(Cmd.STATUS.getStr()).equals(Arg.SUCCESS.getStr())) {
				delegate.onMessageDeliverySucceeded();
			} else if (jObject.getString(Cmd.STATUS.getStr()).equals(Arg.FAILURE.getStr())) {
				delegate.onMessageDeliveryFailed(ChatFailureReason.getReasonFromString(jObject.getString(Cmd.TEXT.getStr())));
			}
		} else {
			//TODO : TIMESTAMP AND SYNC_TYPE
			ChatMessage notificationMessage = new ChatMessage(
					ChatEventType.SOME_STATE, 
					jObject.getInt(Cmd.SENDER.getStr()), 
					jObject.getString(Cmd.TEXT.getStr()), 
					getLamport(jObject), 
					getVectorClock(jObject), 
					0,
					null);
			delegate.onMessageReceived(notificationMessage);
		}
	}
	
	private void onDeregister(JSONObject jObject) throws JSONException {
		if (jObject.getString(Cmd.STATUS.getStr()).equals(Arg.SUCCESS.getStr())) {
			delegate.onDeregistrarionSucceeded();
		} else if (jObject.getString(Cmd.STATUS.getStr()).equals(Arg.FAILURE.getStr())) {
			delegate.onDeregistrationFailed();
		} else {
			Log.e(getClass().toString(), msgNoStatus);
		}
	}
	
	private void onUnknown(JSONObject jObject) throws JSONException {
		delegate.onMessageDeliveryFailed(ChatFailureReason.unknownCommand);
	}
	
	private Lamport getLamport(JSONObject jObject) throws JSONException {
		if (jObject.has(Cmd.INITLAMPORT.getStr())){
			return new Lamport(jObject.getInt(Cmd.INITLAMPORT.getStr()));
		} else {
			return new Lamport(jObject.getInt(Cmd.LAMPORT.getStr()));
		}
	}
	
	private VectorClock getVectorClock(JSONObject jObject) throws JSONException{
		//TODO : give own index
		return new VectorClock(getVectorClockMap(jObject));
	}
	
	private HashMap<Integer, Integer> getVectorClockMap(JSONObject jObject) throws JSONException{
		HashMap<Integer, Integer> vectorMap = new HashMap<Integer, Integer>();
		String vectorString = null;
		if (jObject.has(Cmd.INITVECCLOCK.getStr())){
			vectorString = jObject.getString(Cmd.INITVECCLOCK.getStr());
		} else {
			vectorString = jObject.getString(Cmd.VECTOR.getStr());
		}
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
	}
	
	private Map<Integer, String> getClients(JSONObject jObject) throws JSONException {
		Map<Integer, String> clientMap = new HashMap<Integer, String>();
		String clientString = jObject.getString(Cmd.CLIENTS.getStr());
		Log.v("", "Has client string : "+clientString);
		JSONObject clientJSON = new JSONObject(clientString);
		Iterator<?> keys = clientJSON.keys();
		while (keys.hasNext()) {
			String keyString = keys.next().toString();
			Integer keyInteger = Integer.valueOf(keyString);
			clientMap.put(keyInteger, clientJSON.getString(keyString));
		}
		Log.v("", "Return the map : "+clientMap.toString());
		return clientMap;
	}
}
