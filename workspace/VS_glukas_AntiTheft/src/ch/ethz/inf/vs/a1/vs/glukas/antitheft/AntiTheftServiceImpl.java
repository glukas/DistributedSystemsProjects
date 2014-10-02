package ch.ethz.inf.vs.a1.vs.glukas.antitheft;

import ch.ethz.inf.vs.a1.vs_glukas_antitheft.R;
import android.app.Notification;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class AntiTheftServiceImpl extends AbstractAntiTheftService {

	
	@Override
	public void onCreate() {
		super.onCreate();
		//start with the state of the progress bar at 0
		resetProgressBar();
	}
	
	@Override
	public void startAlarm() {
		MediaPlayer mp = MediaPlayer.create(this, R.raw.sound);
		mp.setVolume(1.0f, 1.0f);
		mp.start();
	}
	
	public void incrProgressBar(int toIncr){
		stateProgressBar += toIncr;
		notifBuilder.setProgress(getTimeout(), stateProgressBar, false);
		notif = notifBuilder.build();
		notif.flags = Notification.FLAG_ONGOING_EVENT;
		notifManager.notify(notifId, notif);
	}
	
	public void resetProgressBar(){
		stateProgressBar = 0;
		incrProgressBar(0);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
