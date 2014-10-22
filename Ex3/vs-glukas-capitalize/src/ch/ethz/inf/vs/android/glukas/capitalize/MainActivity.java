package ch.ethz.inf.vs.android.glukas.capitalize;

import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.widget.Button;
import android.widget.EditText;
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

	// TODO Add some more views to control the sending of the messages

	
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
		this.logic = new MessageLogic(this);
	}

	@Override
	public Handler getCallbackHandler() {
		return callbackHandler;
	}

	/**
	 * This function handles pressing on the back button.
	 * This should be a clean exit. Think about what should be closed.
	 */
	public void onBackPressed() {
		// TODO Make sure to quit when the user presses on Back and to
		// quit the app cleanly.
	}
}
