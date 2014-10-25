package ch.ethz.inf.vs.android.glukas.capitalize;

import android.os.Handler;

public interface AsyncNetworkDelegate {

	Handler getCallbackHandler();
	
	public void OnReceive(String message);
	
	
	
	
	
}
