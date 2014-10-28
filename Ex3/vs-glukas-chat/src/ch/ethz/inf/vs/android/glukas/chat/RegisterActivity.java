package ch.ethz.inf.vs.android.glukas.chat;

import java.util.Map;
import ch.ethz.inf.vs.android.glukas.chat.R;
import ch.ethz.inf.vs.android.glukas.chat.Utils.SyncType;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

/**
 * This activity is launched at the startup of the app.
 * This handles the registration to the server.
 * @author hong-an
 *
 */
public class RegisterActivity extends ListActivity implements ChatEventListener {

	////
	//Members
	////
	
	//Views
	private RadioButton lamportRadio;
	private EditText usernameEditText;
	private EditText numberUsername;
	
	//connectivity
	private ChatLogic chatLogic;
	
	//callback handler
	final Handler callbackHandler = new Handler();
	
	//user informations
	private SyncType syncType;
	private String username;
	
	////
	//Life cycle
	////
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
		.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.activity_register);
	}
	
	public void onBackPressed() {
		//react to back pressed
		chatLogic.deregister();
	}
	
	public void onClickLogin(View v) {
		//react to the click on login button
		if (!haveNetworkConnection()){
			onNoNetworkConnection();
			return;
		}
		tryLogin();
		DialogFactory.createDialogNonErasable(getResources().getString(R.string.please_wait),
				getResources().getString(R.string.login), this).show();
		
		//TODO DEBUG : (directly jump into MainActivity, normally wait the callBack from network)
		this.onRegistrationSucceeded();
	}
	
	////
	//Login
	////
	
	private void tryLogin() {
		//get all informations provided by the user and try to register to the chat
		lamportRadio = (RadioButton) findViewById(R.id.lamportRadio);
		usernameEditText = (EditText) findViewById(R.id.username);
		numberUsername = (EditText) findViewById(R.id.number);
		
		syncType = lamportRadio.isChecked() ? Utils.SyncType.LAMPORT_SYNC : Utils.SyncType.VECTOR_CLOCK_SYNC;
		
		if (syncType.equals(Utils.SyncType.LAMPORT_SYNC)){
			chatLogic = new ChatLogic(Utils.SyncType.LAMPORT_SYNC);
		} else {
			chatLogic = new ChatLogic(Utils.SyncType.VECTOR_CLOCK_SYNC);
		}
		username = usernameEditText.getText().toString() + numberUsername.getText().toString();
		chatLogic.register(username);
	}
	
	private boolean haveNetworkConnection() {
		//check if the device is able to send and receive data from the Internet
		ConnectivityManager connectivityManager = (ConnectivityManager) this
	               .getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager.getActiveNetworkInfo() != null){
			return connectivityManager.getActiveNetworkInfo().isAvailable() && 
					connectivityManager.getActiveNetworkInfo().isConnected();
		}	
		return false;
	}
	
	
	////
	//React to failures
	////
	private void onNoNetworkConnection() {
		//display dialog if device has no connection
		DialogFactory.createDialogMessage(getResources().getString(R.string.no_network_connectivity),
				getResources().getString(R.string.error), this).show();
	}
	
	private void onLoginError(String reasonError) {
		//display dialog if an error occurred while login
		DialogFactory.createDialogMessage(getResources().getString(R.string.login_failed),
				getResources().getString(R.string.error)+ " "+reasonError, this).show();
	}
	
	////
	//Networking
	////
	
	@Override
	public void onRegistrationSucceeded() {
		//call back from the network, server accepted connection
		Intent intent = new Intent(this, MainActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(Utils.INTENT_ARG_CHAT, chatLogic);
		intent.putExtras(bundle);
		//intent.putExtra(Utils.INTENT_ARG_OWNID, ownId);
		intent.putExtra(Utils.INTENT_ARG_SYNCTYPEID, syncType.getTypeId());
		intent.putExtra(Utils.INTENT_ARG_USERNAME, username);
		startActivity(intent);
	}

	@Override
	public void onRegistrationFailed(ChatFailureReason reason) {
		//call back from the network, server rejected connection
		onLoginError(reason.getReasonString());
	}
	
	@Override
	public Handler getCallbackHandler() {
		return callbackHandler;
	}
	
	////
	//Not used by this activity
	////
	
	@Override
	public void onClientRegistered(Integer clientId, String clientUsername) {
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
}
