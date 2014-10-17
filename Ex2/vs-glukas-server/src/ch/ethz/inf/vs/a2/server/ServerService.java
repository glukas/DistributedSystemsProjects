package ch.ethz.inf.vs.a2.server;

import android.app.Service;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.Vibrator;

public class ServerService extends Service{

	ServerAcceptThread<ParsedRequest> serverThread;
	SensorManager sManager;
	
	@Override
	public void onCreate() {
		serverThread = new ServerAcceptThread<ParsedRequest>(8081, 
													Factory.getConsumer(this), 
													Factory.getParser(this));
		serverThread.start();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		serverThread.terminateGracefully();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	public boolean vibrate(long[] pattern){
		Vibrator vib = (Vibrator) getSystemService(VIBRATOR_SERVICE); 
		vib.vibrate(pattern, -1);
		return true;
	}

}
