package ch.ethz.inf.vs.android.glukas.chat;

import java.util.ArrayList;
import java.util.Map;

import ch.ethz.inf.vs.android.glukas.chat.ChatEventSource.ChatEvent;
import ch.ethz.inf.vs.android.glukas.chat.R;
import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends ListActivity implements ChatEventListener {
	private ChatLogic chat;
	ArrayList<DisplayMessage> displayMessages;
	DisplayMessageAdapter adapter;
	EditText text;
	String sender;
	String ownUsername;
	String ownNethz;
	String ownUsernameNumber;
	
	Button loginButton;
	EditText nethzText;
	EditText numberText;
	
	final Handler callbackHandler = new Handler();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

		//Retrieve ChatLogic object from ConnectionActivity
		this.chat = (ChatLogic) getIntent().getSerializableExtra("ChatLogic");
	}
	
	public void onBackPressed() {
		// TODO Make sure to deregister when the user presses on Back and to quit the app cleanly.
	}


	////
	//CHAT EVENT LISTENER
	////
	
	@Override
	public Handler getCallbackHandler() {
		return this.callbackHandler;
	}


	@Override
	public void onRegistrationSucceeded(int ownId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRegistrationFailed(FailureReason reason) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetClientMapping(Map<Integer, String> clientIdToUsernameMap) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetClientMappingFailed(FailureReason reason) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMessageDeliverySucceeded(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMessageDeliveryFailed(FailureReason reason, int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMessageReceived(ChatEvent message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDeregistrarionSucceeded() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDeregistrationFailed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClientDeregistered(Integer clientId, String clientUsername) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClientRegistered(Integer clientId, String clientUsername) {
		// TODO Auto-generated method stub
		
	}
}
