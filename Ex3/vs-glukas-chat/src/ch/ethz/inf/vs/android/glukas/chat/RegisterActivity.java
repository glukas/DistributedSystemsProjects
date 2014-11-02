package ch.ethz.inf.vs.android.glukas.chat;

import java.util.Map;
import ch.ethz.inf.vs.android.glukas.chat.R;
import ch.ethz.inf.vs.android.glukas.protocol.ChatEventListener;
import ch.ethz.inf.vs.android.glukas.protocol.ChatFailureReason;
import ch.ethz.inf.vs.android.glukas.protocol.ChatLogic;
import ch.ethz.inf.vs.android.glukas.protocol.ChatLogicFactory;
import ch.ethz.inf.vs.android.glukas.protocol.MessageSequencerTest;
import ch.ethz.inf.vs.android.glukas.protocol.Utils;
import ch.ethz.inf.vs.android.glukas.protocol.SyncType;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
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
	private RadioButton vectorClockRadio;
	private EditText usernameEditText;
	private EditText numberUsername;
	private CheckBox checkBox;
	
	//connectivity
	private ChatLogic chatLogic;
	
	//callback handler
	final Handler callbackHandler = new Handler();
	
	//user informations
	private SyncType syncType;
	private String username;
	private SharedPreferences preferences;
	
	//dialog message
	private volatile boolean isLogging = false;
	private AlertDialog logMessage;

	private MessageSequencerTest test = new MessageSequencerTest();
	
	////
	//Life cycle
	////
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
		.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.activity_register);
		
		//views
		usernameEditText = (EditText) findViewById(R.id.username);
		numberUsername = (EditText) findViewById(R.id.number);
		checkBox = (CheckBox) findViewById(R.id.remember);
		lamportRadio = (RadioButton) findViewById(R.id.lamportRadio);
		vectorClockRadio = (RadioButton) findViewById(R.id.vectorRadio);
		
		//preferences
		preferences = this.getSharedPreferences(Settings.SETTINGS_CHAT, MODE_PRIVATE);
		restoreUserPreferences();
		
		test.test();
	}
	
	public void onBackPressed() {
		//react to back pressed
		if (isLogging){
			isLogging = false;
		} else if (chatLogic != null){
			chatLogic.deregister();
		}
		finish();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	public void onClickLogin(View v) {
		//react to the click on login button
		if (!haveNetworkConnection()){
			onNoNetworkConnection();
			return;
		}
		setRemember();
		isLogging = true;
		tryLogin();
		//display to the user that the application tries to log
		logMessage = DialogFactory.createDialogNonErasable(getResources().getString(R.string.please_wait),
				getResources().getString(R.string.login), this);
		logMessage.show();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		if (chatLogic != null) chatLogic.removeChatEventListener(this);
	}
	
	////
	//Login
	////
	
	private void tryLogin() {
		//get all informations provided by the user and try to register to the chat
		syncType = lamportRadio.isChecked() ? SyncType.LAMPORT_SYNC : SyncType.VECTOR_CLOCK_SYNC;
		if (syncType.equals(SyncType.LAMPORT_SYNC)){
			ChatLogicFactory.setSyncType(SyncType.LAMPORT_SYNC);
		} else {
			ChatLogicFactory.setSyncType(SyncType.VECTOR_CLOCK_SYNC);
		}

		username = usernameEditText.getText().toString() + numberUsername.getText().toString();
		chatLogic = ChatLogicFactory.getInstance(this);
		chatLogic.addChatEventListener(this);
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
	//User preferences saving
	////
	
	private void restoreUserPreferences(){
		//reload into the views user preferences
		if (preferences.getBoolean(Settings.REMEMBER_ME_STR, false)){
			usernameEditText.setText(preferences.getString(Settings.USERNAME, ""));
			numberUsername.setText(preferences.getString(Settings.NUMBER_USERNAME, ""));
			checkBox.setChecked(true);
			if (preferences.getBoolean(Settings.SYNC_TYPE, true)){
				lamportRadio.setChecked(false);
				vectorClockRadio.setChecked(true);
			} else {
				vectorClockRadio.setChecked(false);
				lamportRadio.setChecked(true);
			}
		}
	}
	
	private void onRememberMe(String usernameArg, String numberUsernameArg, boolean syncTypeArg){
		//store user preferences
		Editor preferencesEditor = preferences.edit();
		preferencesEditor.putString(Settings.USERNAME, usernameArg);
		preferencesEditor.putString(Settings.NUMBER_USERNAME, numberUsernameArg);
		preferencesEditor.putBoolean(Settings.REMEMBER_ME_STR, true);
		if (syncTypeArg) {
			preferencesEditor.putBoolean(Settings.SYNC_TYPE, true);
		} else {
			preferencesEditor.putBoolean(Settings.SYNC_TYPE, false);
		}
		preferencesEditor.apply();
	}
	
	private void onNotRememberMe(){
		//erase all user preferences
		Editor preferencesEditor = preferences.edit();
		preferencesEditor.putString(Settings.USERNAME, "");
		preferencesEditor.putString(Settings.NUMBER_USERNAME, "");
		preferencesEditor.putBoolean(Settings.REMEMBER_ME_STR, false);
		preferencesEditor.putBoolean(Settings.SYNC_TYPE, false);
		preferencesEditor.apply();
	}
	
	public void setRemember(){
		//decide if and what to remember
		if (checkBox.isChecked()){
			String arg1 = usernameEditText.getText().toString();
			String arg2 = numberUsername.getText().toString();
			if (lamportRadio.isChecked()){
				onRememberMe(arg1, arg2, false);
			} else {
				onRememberMe(arg1, arg2, true);
			}
		} else {
			onNotRememberMe();
		}
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
		logMessage.dismiss();
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
		intent.putExtra(Utils.INTENT_ARG_USERNAME, username);
		startActivity(intent);
	}

	@Override
	public void onRegistrationFailed(ChatFailureReason reason) {
		//call back from the network, server rejected connection
		Log.e(this.getClass().toString(), reason.getReasonString());
		onLoginError(reason.getReasonString());
		this.chatLogic.removeChatEventListener(this);
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
	public void onMessageReceived(String text, int userId) {
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
	public void onInfoReceived(String message) {
	}
}
