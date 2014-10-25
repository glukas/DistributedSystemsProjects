package ch.ethz.inf.vs.android.glukas.capitalize;

import java.io.IOException;
import java.io.Serializable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

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
	String mess;
	MessageEventSource eventsource;
	/**
	 * 
	 */
	Context appContext;
	

	public void setMessage (String msg){
		this.mess = msg;
	}
	
	public void sendMessage() {
		
		Message message = Message.obtain();
        Bundle b = new Bundle();
        b.putString("message", this.mess);
        message.setData(b);
        Log.v("TEST", "Request Okay");
	 	requestHandler.sendMessage(message);
		
	}
	
	public void sendReply(String reply) {
		Message message = Message.obtain();
        Bundle b = new Bundle();
        b.putString("message", reply);
        message.setData(b);
        Log.v("TEST", "Reply Okay");
	 	requestHandler.sendMessage(message);
		
		
	}
	public void setHandlers(Handler request, Handler receive){
		
		this.requestHandler = request;
		this.receiveHandler = receive;
	}
	public String receive() throws IOException{
		
		return comm.receiveReply();
	}
	/**
	 * Use this handler for outgoing traffic, aka requests to the server.
	 */
	private Handler requestHandler;
		
	
	/**
	 * Use this handler for incoming traffic, aka responses from the server.
	 */


	private Handler receiveHandler;
	
	/*class requestThread implements Runnable{
		 @Override
		 public void run(){
			 	Message message = requestHandler.obtainMessage();
                Bundle b = new Bundle();
                b.putString("message", mess);
                message.setData(b);
               // if (message.getData().get("message").toString().isEmpty())
                	Log.v("TEST", "Okay");
			 	requestHandler.sendMessage(message);
			 	
			 }
		 }
	*/
	
	



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
}
