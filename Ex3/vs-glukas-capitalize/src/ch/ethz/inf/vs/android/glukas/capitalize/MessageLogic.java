package ch.ethz.inf.vs.android.glukas.capitalize;

import java.io.Serializable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;

@SuppressLint("UseSparseArrays")
/**
 * This class is handling the communication with the server
 * @author hong-an
 *
 */
public class MessageLogic extends MessageEventSource implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -459244179641490462L;

	/**
	 * 
	 */
	Context appContext;
	
	/**
	 * Use this handler for outgoing traffic, aka requests to the server.
	 */
	private Handler requestHandler;
	
	/**
	 * Use this handler for incoming traffic, aka responses from the server.
	 */
	private Handler receiveHandler;

	/**
	 * This object handles the UDP communication between the client and the chat server
	 */
	UDPCommunicator comm;
	
	/**
	 * This logger should always called when an incoming or outgoing message is ready to be
	 * displayed in the view.
	 */
	Logger log;

	/**
	 * Constructor
	 * @param context The calling activity
	 */
	public MessageLogic(Context context) {
		this.initLogger();
	}

	/**
	 * Initialization of the logger
	 */
	public void initLogger() {
		this.log = new Logger(appContext);
	}
}
