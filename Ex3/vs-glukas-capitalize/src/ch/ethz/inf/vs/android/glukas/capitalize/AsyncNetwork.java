package ch.ethz.inf.vs.android.glukas.capitalize;

import java.io.IOException;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

public class AsyncNetwork {

	private Handler requestHandler;
	private Handler receiveHandler;
	private boolean alive = true;
	HandlerThread requestThread;
	HandlerThread receiveThread;
	UDPCommunicator comm;
	AsyncNetworkDelegate delegate;

	public AsyncNetwork(String address, int port) {
		this.comm = new UDPCommunicator(address, port);
		requestThread = new HandlerThread("requestThread");
		receiveThread = new HandlerThread("receiveThread");
		receiveThread.start();
		requestThread.start();
		requestHandler = new Handler(requestThread.getLooper());
		receiveHandler = new Handler(receiveThread.getLooper());

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
		receiveHandler.post(new Runnable() {
			@Override
			public void run() {
				while (true) {
					final String reply;
					try {
						Log.d("Thread:", Thread.currentThread().getName());

						// /
						// Thread gets stuck here!
						reply = comm.receiveReply();
						// /

						delegate.getCallbackHandler().post(new Runnable() {

							@Override
							public void run() {
								delegate.OnReceive(reply);

							}

						});
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}

		});

	}

}
