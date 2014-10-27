package ch.ethz.inf.vs.android.glukas.capitalize;

import java.io.IOException;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

public class AsyncNetwork {

	private Handler requestHandler;

	private volatile boolean alive = true;
	private HandlerThread requestThread;
	private Thread receiveThread;
	private UDPCommunicator comm;
	private AsyncNetworkDelegate delegate;

	public AsyncNetwork(String address, int port) {
		this.comm = new UDPCommunicator(address, port);
		
		//Requests
		requestThread = new HandlerThread("requestThread");
		requestThread.start();
		requestHandler = new Handler(requestThread.getLooper());		
		
		//Replies
		receiveThread = new Thread() {
			
			@Override
			public void run() {
				
				try {
					while(alive){
						Log.d("", "Trying to receiveReply");
						final String reply = comm.receiveReply();
						delegate.getCallbackHandler().post(new Runnable() {

							@Override
							public void run() {
								delegate.OnReceive(reply);
							}
						});
					}
				} catch (IOException e) {
					if (alive){
						Log.e(this.getClass().toString(), e.getLocalizedMessage());
					}
				}
			}
		};
		
		receiveThread.start();
	}

	
	
	public void stopThreads() {
		this.alive=false;
		try {
			comm.finishConnection();
		} catch (IOException ex) {
			Log.e(this.getClass().toString(), ex.getLocalizedMessage());
		}
		requestThread.quit();
	}
	
	public void setDelegate(AsyncNetworkDelegate delegate) {
		this.delegate = delegate;

	}

	public void sendMessage(String message) {
		final String msg = message;
		requestHandler.post(new Runnable() {
			@Override
			public void run() {
				try {
					comm.sendRequestString(msg);
				} catch (IOException e) {
					Log.e(this.getClass().toString(), e.getLocalizedMessage());
				}
			}
		});
	}
}
