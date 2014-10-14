package ch.ethz.inf.vs.a2.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ServerService extends Service{

	@Override
	public void onCreate() {
		Log.v(this.getPackageName(), "Server Service Created");
	}
	
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
