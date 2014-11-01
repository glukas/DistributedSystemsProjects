package ch.ethz.inf.vs.android.glukas.chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import ch.ethz.inf.vs.android.glukas.chat.DisplayMessage;
import ch.ethz.inf.vs.android.glukas.chat.DisplayMessageAdapter;
import ch.ethz.inf.vs.android.glukas.chat.R;
import ch.ethz.inf.vs.android.glukas.protocol.ChatEventListener;
import ch.ethz.inf.vs.android.glukas.protocol.ChatFailureReason;
import ch.ethz.inf.vs.android.glukas.protocol.ChatLogic;
import ch.ethz.inf.vs.android.glukas.protocol.ChatLogicFactory;
import ch.ethz.inf.vs.android.glukas.protocol.Utils;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
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
	
	//messages
	private volatile ArrayList<DisplayMessage> displayMessages;
	private DisplayMessageAdapter adapter;
	private Map<Integer, String> clientIdToUsernameMap;
	
	//dialogs
	private Dialog logoutDialog;
	
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
		this.chat = ChatLogicFactory.getInstance(this);
		
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
		logoutDialog = DialogFactory.createDialogNonErasable(getResources().getString(R.string.please_wait),
				getResources().getString(R.string.unLogin), this);
		logoutDialog.show();
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
		displayMessages.get(id).setSending(false);
		adapter.notifyDataSetChanged();
	}
	
	private void setTryToSendToFailure(int id, ChatFailureReason reason){
		displayMessages.get(id).setSending(false);
		displayMessages.get(id).setHasFailed(true);
		displayMessages.get(id).setReasonFailure(reason.getReasonString());
		adapter.notifyDataSetChanged();
	}
	
	private void displayMessageSystem(DisplayMessage message){
		displayMessages.add(message);
		adapter.notifyDataSetChanged();
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
	public void onMessageReceived(String text, int userId) {
		displayMessageUser(new DisplayMessage(text, getUsernameById(userId), false));
	}

	@Override
	public void onDeregistrarionSucceeded() {
		chat.removeChatEventListener(this);
		Intent intent = new Intent(this, RegisterActivity.class);
		startActivity(intent);
	}

	@Override
	public void onDeregistrationFailed() {

		if (logoutDialog != null && logoutDialog.isShowing()) {
			logoutDialog.dismiss();
			DialogFactory.createDialogMessage(getResources().getString(R.string.deregistration_failed), 
					getResources().getString(R.string.error), this).show();
		}
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
	
	@Override
	public void onInfoReceived(String message) {
		displayMessageSystem(new DisplayMessage(true, message, getResources().getString(R.string.system)));
	}
	
	////
	//Not used by this activity
	////
	
	@Override
	public void onRegistrationFailed(ChatFailureReason reason) {
		if (reason.equals(ChatFailureReason.notRegistered)) {
			//the user tried to send a message, but he has been already kicked out by the server
			this.onDeregistrationFailed();
			chat.register(this.username);
		}
	}

	@Override
	public void onRegistrationSucceeded() {
	}
	
	@Override
	public void onGetClientMappingFailed(ChatFailureReason reason) {
	}
}
