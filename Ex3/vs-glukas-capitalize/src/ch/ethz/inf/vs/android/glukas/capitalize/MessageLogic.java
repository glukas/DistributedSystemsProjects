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
public class MessageLogic extends MessageEventSource implements Serializable, AsyncNetworkDelegate{
	
	private static final long serialVersionUID = -459244179641490462L;

	private AsyncNetwork asyncNetwork;
	private Handler asyncNetworkCallbackHandler;

	/**
	 * Constructor
	 * @param context The calling activity
	 */
	public MessageLogic() {
		asyncNetworkCallbackHandler = new Handler();
		asyncNetwork = new AsyncNetwork(Utils.SERVER_ADDRESS,Utils.SERVER_PORT_CAPITALIZE, this);
	}
	
	public void sendMessage(String message) {
		asyncNetwork.sendMessage(message);
	}
	
	public void close() {
		asyncNetwork.close();
	}
	
	////
	//ASYNC NETWORK DELEGATE
	////
	
	@Override
	public Handler getCallbackHandler() {
		return asyncNetworkCallbackHandler;
	}

	@Override
	public void OnReceive(String message) {
		ChatEvent chatEvent = new ChatEvent(this, null, message, null);
		chatEvent.dispatchEvent();
	}
}


