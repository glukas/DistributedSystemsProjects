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
	private volatile boolean noConnection;
	private final String address;
	private final int port;

	public AsyncNetwork(String address, int port) {
		this.address = address;
		this.port = port;
		setUDPCommunicator();
		setRequestHandling();
		setReceiveHandling();
	}
	
	private void setUDPCommunicator(){
		this.comm = new UDPCommunicator(address, port);
		noConnection = false;
	}
	
	private void setRequestHandling(){
		requestThread = new HandlerThread("requestThread");
		requestThread.start();
		requestHandler = new Handler(requestThread.getLooper());
	}
	
	private void setReceiveHandling(){
		receiveThread = new Thread() {
			@Override
			public void run() {
				try {
					while(alive){
						Log.v("", "Wait to receive");
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
	
	private void onSendFailure(){
		requestHandler.post(new Runnable() {
			@Override
			public void run() {
				delegate.OnReceive("No network connection : failed to deliver");
			}
		});
	}
	
	public void stopThreads() {
		this.alive=false;
		try {
			comm.finishConnection();
		} catch (IOException ex) {
			Log.e(this.getClass().toString(), ex.getLocalizedMessage());
		}
		requestThread.quit();
		noConnection = true;
	}
	
	public void setDelegate(AsyncNetworkDelegate delegate) {
		this.delegate = delegate;

	}

	public void sendMessage(String message) {
		final String msg = message;
		if (noConnection){
			setUDPCommunicator();
			setRequestHandling();
			setReceiveHandling();
			noConnection = false;
		}
		requestHandler.post(new Runnable() {
			@Override
			public void run() {
				try {
					comm.sendRequestString(msg);
				} catch (IOException e) {
					Log.e("Error delivery" +this.getClass().toString(), e.getLocalizedMessage());
					onSendFailure();
					noConnection = true;
				}
			}
		});
	}
}
