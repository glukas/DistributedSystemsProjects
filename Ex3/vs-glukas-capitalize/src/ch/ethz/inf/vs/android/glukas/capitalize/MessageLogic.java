package ch.ethz.inf.vs.android.glukas.capitalize;

import java.io.IOException;
import java.io.Serializable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

@SuppressLint("UseSparseArrays")
/**
 * This class is handling the communication with the server
 * @author hong-an
 *
 */
public class MessageLogic extends MessageEventSource implements Serializable, AsyncNetworkDelegate{
	/**
	 * 
	 */
	private static final long serialVersionUID = -459244179641490462L;
	String mess;
	
	/**
	 * 
	 */
	Context appContext;
	private Handler requestHandler;
	private Handler receiveHandler;
	
	
	public void setMessage(String message){
		this.mess = message;
	}
	
	
	
	@Override
	public void OnReceive(String message) {
		// TODO Auto-generated method stub
		
	}


	/**
	 * This object handles the UDP communication between the client and the chat server
	 */
	UDPCommunicator comm = new UDPCommunicator("129.132.75.194", 4000);
	
	/**
	 * This logger should always called when an incoming or outgoing message is ready to be
	 * displayed in the view.
	 */
	//Logger log;

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
		//this.log = new Logger(appContext);
	}

	@Override
	public Handler getCallbackHandler() {
		// TODO Auto-generated method stub
		return null;
	}


}


