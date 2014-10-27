package ch.ethz.inf.vs.android.glukas.capitalize;

import java.io.IOException;
import android.util.Log;


public class UDPCommunicatorTest {
	
	public static void testSendString(){
		
		final UDPCommunicator communicator = new UDPCommunicator("129.132.75.194", 4000);
		
		final int numMessages = 128;
		
		Thread tSend = new Thread(){
			@Override
			public void run(){
				try{
					for (int i=0; i<numMessages; i++) {
						communicator.sendRequestString("i want to be upper case " + i);
					}
				} catch (IOException ex){
					ex.printStackTrace();
				}
			}
		};
		
		
		Thread tReceive = new Thread(){
			@Override
			public void run(){
				
				try{
					//demonstrates that reply can arrive before receive is called.
					//even if the receive thread begins receiving after a message has arrived, it will receive it
					//packets are buffered on behalf of the application, so we can retrieve several packets that have arrived in the mean time
					//it seems like every second packet received contains "" (Why ?)
					Thread.sleep(5000);
					for (int i=0; i<2*numMessages; i++) {
						Log.d("", "RESPONSE : "+communicator.receiveReply());
					}
				} catch (IOException ex){
					ex.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		
		
		tReceive.start();
		tSend.start();
		
		
	}
}
