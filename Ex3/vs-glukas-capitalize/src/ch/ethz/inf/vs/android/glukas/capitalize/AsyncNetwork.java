package ch.ethz.inf.vs.android.glukas.capitalize;

import java.io.IOException;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

public class AsyncNetwork {

	private Handler requestHandler;

	private volatile boolean alive = true;
	HandlerThread requestThread;
	Thread receiveThread;
	UDPCommunicator comm;
	volatile AsyncNetworkDelegate delegate;

	public AsyncNetwork(String address, int port) {
		this.comm = new UDPCommunicator(address, port);
		
		/// Requests
		requestThread = new HandlerThread("requestThread");
		requestThread.start();
		requestHandler = new Handler(requestThread.getLooper());
		///
		
		
		///Replies
		receiveThread = new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(5000);
					while (alive) {
						Log.d("", "Trying to receiveReply");
						final String reply = comm.receiveReply();
						delegate.getCallbackHandler().post(new Runnable() {

							@Override
							public void run() {
								delegate.OnReceive(reply);
							}
						});
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		};
		receiveThread.start();
		///
		

	}

	public void setDelegate(AsyncNetworkDelegate delegate) {
		this.delegate = delegate;

	}

	public void sendMessage(String message) {

		final String msg = message;
		requestHandler.post(new Runnable() {

			@Override
			public void run() {
				Log.v("Thread:", Thread.currentThread().getName());
				try {

					comm.sendRequestString(msg);
					Log.v("Request:", "Sent");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		});

	}

}
