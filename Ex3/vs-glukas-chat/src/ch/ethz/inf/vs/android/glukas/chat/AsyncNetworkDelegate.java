package ch.ethz.inf.vs.android.glukas.chat;

import android.os.Handler;

public interface AsyncNetworkDelegate {

	Handler getCallbackHandler();
	
	public void OnReceive(String message);
	
}
