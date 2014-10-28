package ch.ethz.inf.vs.android.glukas.chat;

import java.util.ArrayList;
import java.util.Map;
import ch.ethz.inf.vs.android.glukas.chat.DisplayMessage;
import ch.ethz.inf.vs.android.glukas.chat.DisplayMessageAdapter;
import ch.ethz.inf.vs.android.glukas.chat.R;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
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
	private ArrayList<DisplayMessage> displayMessages;
	private DisplayMessageAdapter adapter;
	private Map<Integer, String> clientIdToUsernameMap;
	
	//user
	private String username;
	
	//Logger
	private Logger logger;
	
	////
	//Life cycle
	////
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        
        //set views
        this.textInput = (TextView) findViewById(R.id.text);
        
		//Retrieve and restore ChatLogic
		this.chat = (ChatLogic) getIntent().getSerializableExtra(Utils.INTENT_ARG_CHAT);
		chat.setSyncType(Utils.SyncType.getSyncTypeById(getIntent().getIntExtra(Utils.INTENT_ARG_SYNCTYPEID, 1)));
		
		//Get info from Intent
		username = getIntent().getStringExtra(Utils.INTENT_ARG_USERNAME);
		
		//Register to chat
		chat.addChatEventListener(this);
		
		//Hook up list view to adapter
		displayMessages = new ArrayList<DisplayMessage>();
		adapter = new DisplayMessageAdapter(this, displayMessages);
		setListAdapter(adapter);
		
		//Logger
		logger = new Logger(username, this);
		
		//display greetings
		this.displayMessageSystem(new DisplayMessage(true, getResources().getString(R.string.enter_chat_greetings),
				getResources().getString(R.string.system)));
		
		//Ask for users already in the chat
		this.clientIdToUsernameMap = null;
		chat.getClients();
		
	}
	
	public void onBackPressed() {
		chat.deregister();
		
		//TODO : DEBUG , normally wait for call from network
		this.onDeregistrarionSucceeded();
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
				chat.sendMessage(message, displayMessages.size() - 1);
			}
		}
	}
	
	////
	//Display Messages
	////
	
	private void displayMessageUser(DisplayMessage message){
		displayMessages.add(message);
		adapter.notifyDataSetChanged();
		textInput.setText("");
		getListView().smoothScrollToPosition(adapter.getCount()-1);
	}
	
	private void setTryToSendToSend(int id){
		displayMessages.get(id).setSending(false);
	}
	
	private void setTryToSendToFailure(int id, ChatFailureReason reason){
		displayMessages.get(id).setSending(false);
		displayMessages.get(id).setHasFailed(true);
		displayMessages.get(id).setReasonFailure(reason.getReasonString());
	}
	
	private void displayMessageSystem(DisplayMessage message){
		displayMessages.add(message);
		adapter.notifyDataSetChanged();
		textInput.setText("");
		getListView().smoothScrollToPosition(adapter.getCount()-1);
	}

	private String getUsernameById(int id){
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
