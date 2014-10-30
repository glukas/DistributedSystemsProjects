package ch.ethz.inf.vs.android.glukas.chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import ch.ethz.inf.vs.android.glukas.chat.DisplayMessage;
import ch.ethz.inf.vs.android.glukas.chat.DisplayMessageAdapter;
import ch.ethz.inf.vs.android.glukas.chat.R;
import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends ListActivity implements ChatEventListener {
	
	////
	//Members
	////
	
	//Views
	TextView textInput;
	
	//networking
	private ChatLogic chat;
	private final Handler callbackHandler = new Handler();
	
	//messages
	private volatile ArrayList<DisplayMessage> displayMessages;
	private DisplayMessageAdapter adapter;
	private Map<Integer, String> clientIdToUsernameMap;
	
	//user
	private String username;
	
	//Logger
	//TODO : use the logger to log messages
	//private Logger logger;
	
	////
	//Life cycle
	////
	
	@SuppressLint("UseSparseArrays")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        
        //set views
        this.textInput = (TextView) findViewById(R.id.text);
        
		//Retrieve and restore ChatLogic
		this.chat = ChatLogicFactory.getInstance();
		
		//Get info from Intent
		username = getIntent().getStringExtra(Utils.INTENT_ARG_USERNAME);
		
		//Register to chat
		chat.addChatEventListener(this);
		
		//Hook up list view to adapter
		displayMessages = new ArrayList<DisplayMessage>();
		adapter = new DisplayMessageAdapter(this, displayMessages);
		setListAdapter(adapter);
		
		//Logger
		//TODO : Use logger
		//logger = new Logger(username, this);
		
		//display greetings
		this.displayMessageSystem(new DisplayMessage(true, getResources().getString(R.string.enter_chat_greetings),
				getResources().getString(R.string.system)));
		
		//Ask for users already in the chat
		this.clientIdToUsernameMap = new HashMap<Integer, String>();
		chat.getClients();
	}
	
	@Override
	public void onDestroy(){
		chat.deregister();
	}
	
	public void onBackPressed() {
		chat.deregister();
		DialogFactory.createDialogNonErasable(getResources().getString(R.string.please_wait),
				getResources().getString(R.string.unLogin), this).show();
	}
	
	////
	//Send Messages
	////
	
	public synchronized void sendMessage(View view) {
		if (view instanceof Button) {
			if (textInput.getText().length() > 0) {
				String message = textInput.getText().toString();
				DisplayMessage messageToSend = new DisplayMessage(textInput.getText().toString(), username, true, true);
				displayMessageUser(messageToSend);
				chat.sendMessage(message, displayMessages.indexOf(messageToSend));
				Log.v("", "Id of the message is : "+adapter.getPosition(messageToSend) +" and size of the list : "+displayMessages.indexOf(messageToSend));
				textInput.setText("");
			}
		}
	}
	
	////
	//Display Messages
	////
	
	private void displayMessageUser(DisplayMessage message){
		displayMessages.add(message);
		adapter.notifyDataSetChanged();
		getListView().smoothScrollToPosition(adapter.getCount()-1);
	}
	
	private void setTryToSendToSend(int id){
		Log.v("", "Message correctly send");
		displayMessages.get(id).setSending(false);
	}
	
	private void setTryToSendToFailure(int id, ChatFailureReason reason){
		Log.v("", "Message NOT correctly send");
		displayMessages.get(id).setSending(false);
		displayMessages.get(id).setHasFailed(true);
		displayMessages.get(id).setReasonFailure(reason.getReasonString());
	}
	
	private void displayMessageSystem(DisplayMessage message){
		displayMessages.add(message);
		adapter.notifyDataSetChanged();
		getListView().smoothScrollToPosition(adapter.getCount()-1);
	}

	private String getUsernameById(int id){
		Log.v("","MAP : "+clientIdToUsernameMap.toString());
		String name = clientIdToUsernameMap.get(id);
		return name != null ? name : getResources().getString(R.string.unknown);
	}

	////
	//Chat event listener
	////
	
	@Override
	public Handler getCallbackHandler() {
		return this.callbackHandler;
	}

	@Override
	public void onGetClientMapping(Map<Integer, String> clientIdToUsernameMap) {
		Log.v("","Get client mapping : "+clientIdToUsernameMap.toString());
		this.clientIdToUsernameMap = clientIdToUsernameMap;
	}

	@Override
	public void onMessageDeliverySucceeded(int id) {
		setTryToSendToSend(id);
	}

	@Override
	public void onMessageDeliveryFailed(ChatFailureReason reason, int id) {
		setTryToSendToFailure(id, reason);
	}

	@Override
	public void onMessageReceived(ChatMessage message) {
		displayMessageUser(new DisplayMessage(message.getText(), getUsernameById(message.getSenderId()), false));
	}

	@Override
	public void onDeregistrarionSucceeded() {
		Intent intent = new Intent(this, RegisterActivity.class);
		startActivity(intent);
	}

	@Override
	public void onDeregistrationFailed() {
		DialogFactory.createDialogMessage(getResources().getString(R.string.deregistration_failed), 
				getResources().getString(R.string.error), this).show();
	}

	@Override
	public void onClientDeregistered(Integer clientId, String clientUsername) {
		if (this.clientIdToUsernameMap != null) {
			clientIdToUsernameMap.remove(clientId);
		}
		displayMessageSystem(new DisplayMessage(true, clientUsername+" "+getResources().getString(R.string.left),
				getResources().getString(R.string.system)));
	}

	@Override
	public void onClientRegistered(Integer clientId, String clientUsername) {
		if (this.clientIdToUsernameMap != null) {
			clientIdToUsernameMap.put(clientId, clientUsername);
		}
		displayMessageSystem(new DisplayMessage(true, clientUsername+" "+getResources().getString(R.string.joined),
				getResources().getString(R.string.system)));
	}
	
	
	////
	//Not used by this activity
	////
	
	@Override
	public void onRegistrationFailed(ChatFailureReason reason) {
	}

	@Override
	public void onRegistrationSucceeded() {
	}
	
	@Override
	public void onGetClientMappingFailed(ChatFailureReason reason) {
	}
}
