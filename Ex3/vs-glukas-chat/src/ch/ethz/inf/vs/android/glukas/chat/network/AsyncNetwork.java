package ch.ethz.inf.vs.android.glukas.chat.network;

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
	private final AsyncNetworkDelegate delegate;
	private volatile boolean noConnection = false;
	private final String address;
	private final int port;

	public AsyncNetwork(String address, int port, AsyncNetworkDelegate delegate) {
		this.address = address;
		this.port = port;
		this.delegate = delegate;
		open();
	}
	
	private void open() {
		Log.d(this.getClass().toString(), "open()");
		alive = true;
		noConnection = false;
		this.comm = new UDPCommunicator(address, port);
		setRequestHandling();
		setReceiveHandling();
	}
	
	private void setRequestHandling(){
		requestThread = new HandlerThread("requestThread");
		requestThread.start();
		requestHandler = new Handler(requestThread.getLooper());
		Log.v(this.getClass().toString(), "setRequestHandling");
	}
	
	private void setReceiveHandling(){
		receiveThread = new Thread() {
			@Override
			public void run() {
				try {
					while(alive){
						final String reply = comm.receiveReply();
						postResponseToDelegate(reply);
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
	
	private void postResponseToDelegate(final String reply) {
		delegate.getCallbackHandler().post(new Runnable() {
			@Override
			public void run() {
				delegate.onReceive(reply);
			}
		});
	}
	
	public void close() {
		Log.d(this.getClass().toString(), "close()");
		this.alive = false;
		try {
			comm.finishConnection();
		} catch (IOException ex) {
			Log.e(this.getClass().toString(), ex.getLocalizedMessage());
		}
		requestThread.quit();
		noConnection = true;
	}

	public void sendMessage(final String message) {
		if (noConnection){
			open();
		}
		requestHandler.post(new Runnable() {
			@Override
			public void run() {
				try {
					comm.sendRequestString(message);
				} catch (IOException e) {
					Log.e("Error delivering : " +this.getClass().toString(), e.getLocalizedMessage());
					close();
					delegate.getCallbackHandler().post(new Runnable() {
						@Override
						public void run() {
							delegate.onDeliveryFailed();
						}
					});
				}
			}
		});
	}
}
