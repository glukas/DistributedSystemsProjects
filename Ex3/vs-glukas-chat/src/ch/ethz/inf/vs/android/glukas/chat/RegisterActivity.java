package ch.ethz.inf.vs.android.glukas.chat;

import java.util.Map;
import ch.ethz.inf.vs.android.glukas.chat.R;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
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

	//Views
	private RadioButton lamportRadio;
	private EditText username;
	private EditText numberUsername;
	
	//connectivity
	private ChatLogic chatLogic;
	
	//callback handler
	final Handler callbackHandler = new Handler();

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
		.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		setContentView(R.layout.activity_register);
		
	}
	
	public void onClickLogin(View v) {
		Log.v("", "on click login");
		
		
		if (!haveNetworkConnection()){
			onNoNetworkConnection();
			return;
		}
		tryLogin();
		createDialogMessage(getResources().getString(R.string.please_wait),
				getResources().getString(R.string.login), false);
	}
	
	private void tryLogin() {
		Log.v("", "Try log in");
		lamportRadio = (RadioButton) findViewById(R.id.lamportRadio);
		username = (EditText) findViewById(R.id.username);
		numberUsername = (EditText) findViewById(R.id.number);
		
		if (lamportRadio.isChecked()){
			chatLogic = new ChatLogic(this, Utils.SyncType.LAMPORT_SYNC);
		} else {
			chatLogic = new ChatLogic(this, Utils.SyncType.VECTOR_CLOCK_SYNC);
		}
		chatLogic.register(username.getText().toString() + numberUsername.getText().toString());
	}
	
	private void onNoNetworkConnection() {
		Log.v("", "No Network Connection");
		createDialogMessage(getResources().getString(R.string.no_network_connectivity),
				getResources().getString(R.string.error), true);
	}
	
	private void onLoginError(String reasonError) {
		Log.v("", "Login error : "+reasonError);
		createDialogMessage(getResources().getString(R.string.login_failed),
				getResources().getString(R.string.error)+ " "+reasonError, true);
	}
	
	private void createDialogMessage(String text, String title, boolean isErasable){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setMessage(text)
		       .setTitle(title);
		
		if (isErasable){
			builder.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		           }
		       });
		}
		AlertDialog dialog = builder.create();
		dialog.show(); 
	}

	/**
	 * Check if a connectivity interface exists, if the device is connected to and
	 * if it's possible to pass and receive data
	 */
	private boolean haveNetworkConnection() {
		ConnectivityManager connectivityManager = (ConnectivityManager) this
	               .getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager.getActiveNetworkInfo() != null){
			return connectivityManager.getActiveNetworkInfo().isAvailable() && 
					connectivityManager.getActiveNetworkInfo().isConnected();
		}	
		return false;
	}

	/**
	 * This function should be called when the back button is pressed. 
	 * Make sure everything is closed / quit properly.
	 */
	public void onBackPressed() {
		chatLogic.deregister();
	}

	@Override
	public void onRegistrationSucceeded(int ownId, Lamport lamportClock,
			VectorClock vectorClock) {
		//TODO : start MainActivity
	}

	@Override
	public void onRegistrationFailed(ChatFailureReason reason) {
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
