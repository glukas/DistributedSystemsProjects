package ch.ethz.inf.vs.a2.server;

import android.app.Service;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.Vibrator;

public class ServerService extends Service{

	/*
	 * To access a resource, the following identifier format is used
	 * /DistributedSystemsProjects/SENSOR_NAME?ARG1.ARG2...
	 * Where : 
	 *   -SENSOR_NAME is a name listed in SensorType, for example 'accelerometer' or 'orientation' or 'vibrator'
	 *   -ARGn is an Integer. (Any number of arguments is allowed), for example it can be used to pass vibration patterns
	 */
	
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
