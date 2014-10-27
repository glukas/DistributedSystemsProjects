package ch.ethz.inf.vs.android.glukas.capitalize;

import java.util.ArrayList;
import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import ch.ethz.inf.vs.android.glukas.capitalize.MessageEventSource.ChatEvent;
import ch.ethz.inf.vs.android.glukas.capitalize.R;

/**
 * This function represents the main activity for launching the app.
 * 
 * @author hong-an
 *
 */
public class MainActivity extends ListActivity implements MessageEventListener {
	/**
	 * This object handles the logic for sending and receiving messages
	 * to / from the server
	 */
	 private MessageLogic logic;
	/**
	 * This lists contains the DisplayMessage to be used for the UI
	 */
	ArrayList<DisplayMessage> displayMessages;
	/**
	 * The adapter for displaying the DisplayMessage in a view
	 */
	DisplayMessageAdapter adapter;

	/**
	 * The view that contains the text the user wants to send
	 */
	TextView textInput;
	
	/**
	 * The logger to log messages
	 */
	Logger logger;

	////
	///ACTIVITY
	////
	
	@Override
	/**
	 * This function is called to create the app. Think about 
	 * what is necessary for the initialization.
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.activity_main);
		
		//hook up list view to adapter
		displayMessages = new ArrayList<DisplayMessage>();
		adapter = new DisplayMessageAdapter(this, displayMessages);
		setListAdapter(adapter);
		
		this.textInput = (TextView) findViewById(R.id.text);
		
		//register for messages
		logic = new MessageLogic();
		logic.addMessageEventListener(this);
		
		//Logger
		logger = new Logger(this);
	}
	
	/**
	 * This function handles pressing on the back button.
	 * This should be a clean exit. Think about what should be closed.
	 */
	@Override
	public void onBackPressed() {
		Log.v(this.getClass().toString(), "onBackPressed");
		logic.close();
		finish();
	}
	
	////
	//MAIN ACTIVITY
	////				
		
	public void sendMessage(View view) {
		if (view instanceof Button) {
			if (textInput.getText().length() > 0) {
				String message = textInput.getText().toString();
				displayMessage(textInput.getText().toString(), "glukas", true);
				logic.sendMessage(message);
			}
		}
	}
	
	private void displayMessage(String message, String username, boolean isMine) {
		DisplayMessage displayMessage = new DisplayMessage(message, username, isMine);
		logger.logReadyMsg(displayMessage, !displayMessage.isMine());
		displayMessages.add(displayMessage);
		adapter.notifyDataSetChanged();
		textInput.setText("");
		getListView().smoothScrollToPosition(adapter.getCount()-1);
	}

	////
	//MESSAGE EVENT LISTENER
	////
	
	@Override
	public Handler getCallbackHandler() {
		return callbackHandler;
	}
	
	@Override
	public void onReceiveChatEvent(ChatEvent message) {
		displayMessage(message.message , "server" , false);
	}
}

