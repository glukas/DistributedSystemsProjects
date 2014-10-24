package ch.ethz.inf.vs.android.glukas.capitalize;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;


public class UDPCommunicatorTest {
	
	public static void testSendString(){
		
		final UDPCommunicator communicator = new UDPCommunicator("129.132.75.194", 4000);
		
		Thread tSend = new Thread(){
			@Override
			public void run(){
				try{
					communicator.sendRequestString("i want to be upper case");
				} catch (IOException ex){
					ex.printStackTrace();
				}
			}
		};
		
		
		Thread tReceive = new Thread(){
			@Override
			public void run(){
				try{
					Log.e("", "RESPONSE : "+communicator.receiveReply());
				} catch (IOException ex){
					ex.printStackTrace();
				}
			}
		};
		
		
		tReceive.start();
		//ensures tReceive is waiting for response before any response arrives.
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		tSend.start();
	}
}
