package ch.ethz.inf.vs.android.glukas.capitalize;

import java.io.IOException;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

public class AsyncNetwork {

	private Handler requestHandler;

	volatile boolean alive = true;
	HandlerThread requestThread;
	Thread receiveThread;
	Thread reqTest;
	UDPCommunicator comm;
	AsyncNetworkDelegate delegate;

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
					Thread.sleep(1000);
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
				}catch (InterruptedException e) {
					//e.printStackTrace();
				} catch (IOException e) {
					
					//e.printStackTrace();
				}

			}

		};
		
		receiveThread.start();
		
	
		

	}

	
	
	public void stopThreads(){
		this.alive=false;
		receiveThread.interrupt();
		requestThread.quit();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.v("requestThread alive:",	String.valueOf(requestThread.isAlive()));
		Log.v("receiveThread alive:",	String.valueOf(receiveThread.isAlive()));
		
	}
	
	public void setDelegate(AsyncNetworkDelegate delegate) {
		this.delegate = delegate;

	}

	public void sendMessage(String message) {

		final String msg = message;
		Log.v("Message:", msg);
		requestHandler.post(new Runnable() {

			@Override
			public void run() {
				Log.v("Message:", "test");
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
