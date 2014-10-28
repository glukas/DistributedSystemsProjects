package ch.ethz.inf.vs.android.glukas.chat;

import java.util.ArrayList;
import java.util.Map;
import ch.ethz.inf.vs.android.glukas.chat.DisplayMessage;
import ch.ethz.inf.vs.android.glukas.chat.DisplayMessageAdapter;
import ch.ethz.inf.vs.android.glukas.chat.R;
import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
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
		this.displayMessageSystem(true, getResources().getString(R.string.enter_chat_greetings),
				getResources().getString(R.string.system));
		
		//TODO : ask for users already in the chat
		
	}
	
	public void onBackPressed() {
		chat.deregister();
	}
	
	private void displayMessageUser(String text, String username, boolean isMine){
		DisplayMessage displayMessage = new DisplayMessage(text, username, isMine);
		displayMessages.add(displayMessage);
		adapter.notifyDataSetChanged();
		textInput.setText("");
		getListView().smoothScrollToPosition(adapter.getCount()-1);
		
	}
	
	private void displayMessageSystem(boolean isStatus, String text, String username){
		DisplayMessage displayMessage = new DisplayMessage(isStatus, text, username);
		displayMessages.add(displayMessage);
		adapter.notifyDataSetChanged();
		textInput.setText("");
		getListView().smoothScrollToPosition(adapter.getCount()-1);
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
	}

	@Override
	public void onGetClientMappingFailed(ChatFailureReason reason) {
	}

	@Override
	public void onMessageDeliverySucceeded(int id) {
	}

	@Override
	public void onMessageDeliveryFailed(ChatFailureReason reason, int id) {
	}

	@Override
	public void onMessageReceived(ChatMessage message) {
	}

	@Override
	public void onDeregistrarionSucceeded() {
	}

	@Override
	public void onDeregistrationFailed() {
	}

	@Override
	public void onClientDeregistered(Integer clientId, String clientUsername) {
	}

	@Override
	public void onClientRegistered(Integer clientId, String clientUsername) {
	}
	
	
	////
	//Not used by this activity
	////
	
	@Override
	public void onRegistrationFailed(ChatFailureReason reason) {
	}

	@Override
	public void onRegistrationSucceeded(int ownId, Lamport lamportClock, VectorClock vectorClock) {
	}
}
