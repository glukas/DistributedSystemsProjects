package ch.ethz.inf.vs.android.glukas.capitalize;

import java.io.IOException;
import android.util.Log;


public class UDPCommunicatorTest {
	
	public static void testSend(){
		
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
		/*try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
		tSend.start();
	}

}
