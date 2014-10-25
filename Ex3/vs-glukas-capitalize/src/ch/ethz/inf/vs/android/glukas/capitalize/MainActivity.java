package ch.ethz.inf.vs.android.glukas.capitalize;

import java.io.IOException;
import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
	
	Handler requestHandler;
	Handler receiveHandler;
	MessageEventSource eventsource;
	receiveThread rThread;
//	MessageLogic.requestThread rThread;
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
		//TODO crashes atm, so uncommented it
		eventsource = new MessageEventSource();
		eventsource.addMessageEventListener(this);
		this.logic = new MessageLogic(this);
		logic.eventsource = eventsource;
		
		
		requestHandler = new Handler(){
			@Override 
			public void handleMessage (Message msg){
			//	Log.v("Test","handled");
				try {
					
					logic.comm.sendRequestString(msg.getData().getString("message").toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}	
			
			
		};
		
		

		
		logic.setHandlers(requestHandler, receiveHandler);
		
		
	/*	Thread receiveThread = new Thread() {
			Handler receiveHandler = new Handler(){
				@Override
				public void handleMessage(Message msg){
					Log.v("Test", "receivehandled");
					MessageEventSource.ChatEvent chatEvent = eventsource.new ChatEvent(eventsource, null , msg.getData().getString("message").toString(), null);
					chatEvent.dispatchEvent();
					try {
						String reply = logic.comm.receiveReply();
						if (!reply.isEmpty())
						
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			
				}		
			};
			@Override
			public void run(){
				String s;
				
				Looper.prepare();
				try {
					s = logic.comm.receiveReply();
					Log.e("Test", "test");
					//logic.sendReply(s);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.v("Error", "bad");
					e.printStackTrace();
				}
				
				Log.v("Thread", "Okay");
				Looper.loop();
			
			
			}
			
			};	
	 */
		rThread = new receiveThread();
		rThread.start();
	//	receiveThread.start();
		//hook up list view to adapter
		displayMessages = new ArrayList<DisplayMessage>();
		displayMessages.add(new DisplayMessage("hello world", "glukas_static", true));
		displayMessages.add(new DisplayMessage("this is just set statically in onCreate", "server_static", false));
		adapter = new DisplayMessageAdapter(this, displayMessages);
		setListAdapter(adapter);
		
		this.textInput = (TextView) findViewById(R.id.text);
		
	//	UDPCommunicatorTest.testSendString();
		
	}
	
	/**
	 * This function handles pressing on the back button.
	 * This should be a clean exit. Think about what should be closed.
	 */
	@Override
	public void onBackPressed() {
		Log.v(this.getClass().toString(), "onBackPressed");
		// TODO Make sure to quit when the user presses on Back and to
		// quit the app cleanly.
		//TODO probably finish here
		finish();
	}
	
	////
	//MAIN ACTIVITY
	////

					
		
		
	public void sendMessage(View view) {
		if (view instanceof Button) {
			if (textInput.getText().length() > 0) {
				displayMessage(textInput.getText().toString(), "glukas_static", true);
				
				//TODO actually send message, using MessageLogic
				
				logic.setMessage(textInput.getText().toString());
				logic.sendMessage();

				rThread.receiveHandler.post(new Runnable() {

					@Override
					public void run() {
						String s;
						
						try {
							s = logic.receive();
							Log.e("Test", "receivehandled");
							MessageEventSource.ChatEvent chatEvent = eventsource.new ChatEvent(eventsource, null , s, null);
							chatEvent.dispatchEvent();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						
					}
					
					
				}
						);
				//logic.mess = textInput.getText().toString();
				//Thread rThread = new Thread (logic.new requestThread());
				//rThread.start();
				
				
				
				/*MessageEventSource.ChatEvent chatEvent = eventsource.new ChatEvent(eventsource, null , textInput.getText().toString(), null);
				chatEvent.dispatchEvent();*/
			}
		}
	}
	
	private void displayMessage(String message, String username, boolean isOwn) {
		displayMessages.add(new DisplayMessage(message, username, isOwn));
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
		displayMessage(message.message , "test" , true);
		// TODO Parse message using MessageLogic, then display it
	}
}

class receiveThread extends Thread {
	Handler receiveHandler;
	//MessageLogic logic;
	public receiveThread() {
		//this.logic  = logic;
		Log.v("init", "okay");
	}
	@Override
	public void run (){
		
		Log.v("test0", "okay");
		Looper.prepare();
		receiveHandler = new Handler();
		Looper.loop();	
		Log.v("afterlooper", "okay");
		while(true);
		
		
		
		
	}
}

