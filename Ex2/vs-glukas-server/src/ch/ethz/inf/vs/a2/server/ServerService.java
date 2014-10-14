package ch.ethz.inf.vs.a2.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ServerService extends Service{

	ServerAcceptThread<Void> serverThread;
	
	@Override
	public void onCreate() {
		Log.v(this.getPackageName(), "Server Service Created");
		//TODO (Vincent) implement & use proper parser & consumer
		serverThread = new ServerAcceptThread<Void>(8081, null, null);
		//serverThread.start();
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

}
