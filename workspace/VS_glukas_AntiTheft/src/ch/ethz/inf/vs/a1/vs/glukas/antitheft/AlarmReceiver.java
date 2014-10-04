package ch.ethz.inf.vs.a1.vs.glukas.antitheft;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		System.err.println("Hello from broadcast");
	}
}  
	
