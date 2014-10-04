package ch.ethz.inf.vs.a1.vs.glukas.antitheft;

import ch.ethz.inf.vs.a1.vs_glukas_antitheft.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class AntiTheftServiceImpl extends AbstractAntiTheftService {

	@Override
	public void onDestroy(){
		super.onDestroy();
		//Destroy the notification, the listener and the alarm
		alarmArmed = false;
		try {
			armAlarm.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		sensorManager.unregisterListener(listener);
		notifManager.cancel(notifId);
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		//start with the state of the progress bar at 0
		sensorManager.registerListener(listener,
		    	sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
		        SensorManager.SENSOR_DELAY_NORMAL);
		createNotification();
	}
	
	@Override
	public void startAlarm() {
		//multiple calls of startAlarm raise only one counting to zero
		if (alarmArmed){
			return;
		}
		alarmArmed = true;
		armAlarm.start();
	}
	
	protected void ringAlarm() {
		destroyProgressBar();
		setTextNotification(ringingStr);
		MediaPlayer mp = MediaPlayer.create(this, R.raw.sound);
		mp.setVolume(1.0f, 1.0f);
		mp.start();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	private Thread armAlarm = new Thread(){
		@Override
		public void run(){
			createProgressBar();
			int timeout = getTimeout();
			int stepSleep = timeout / 10;
			int stepIncr = timeout / 100;
			for (int i = 0; i < 100; i++){
				try {
					Thread.sleep(stepSleep);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				incrProgressBar(stepIncr);
				//To not use stop() deprecated method, boolean used to abort thread prematurely
				if(!alarmArmed){
					return;
				}
			}
			destroyProgressBar();
			ringAlarm();
		}
	};
	
	////
	//Manage Notification
	////
	
	protected final String titleStr = "Anti Theft";
	protected final String noMoveStr = "No movement detected";
	protected final String alarmArmedStr = "Alarm is armed. Tap to disable";
	protected final String ringingStr = "THIEEEF!!!";
	//be aware that the broadcast filter has to be the same in the manifest file
	public static final String broadcastFilter = "broadcastIntentFilter";
	private final int broadcastNumber = 1000;
	
	protected void createNotification(){
		//Create the builder for the notification with all features
				notifBuilder =
					    new NotificationCompat.Builder(this)
					    .setSmallIcon(R.drawable.ic_launcher)
					    .setContentTitle(titleStr)
					    .setContentText(noMoveStr);
					
		//create the notification with the flag "ongoing" (can't be deleted by user)
		notifManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		//define action on click
		Intent onClickIntent = new Intent();  
		onClickIntent.setAction(broadcastFilter);
		PendingIntent pendingIntentOnClick = PendingIntent.getBroadcast(this, broadcastNumber, onClickIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		notifBuilder.setContentIntent(pendingIntentOnClick);
		
		notif = notifBuilder.build();
		notif.flags = Notification.FLAG_ONGOING_EVENT;
		notifManager.notify(notifId, notif);
	}
	
	protected void setTextNotification(String s){
		notifBuilder.setContentText(s);
		notif = notifBuilder.build();
		notifManager.notify(notifId, notif);
	}
		
	protected void createProgressBar(){
		setTextNotification(alarmArmedStr);
		resetProgressBar();
	}
		
	protected void destroyProgressBar(){
		notifBuilder.setProgress(0, 0, false);
		notif = notifBuilder.build();
		notifManager.notify(notifId, notif);
	}
		
	protected void incrProgressBar(int toIncr){
		stateProgressBar += toIncr;
		notifBuilder.setProgress(getTimeout(), stateProgressBar, false);
		notif = notifBuilder.build();
		notif.flags = Notification.FLAG_ONGOING_EVENT;
		notifManager.notify(notifId, notif);
	}
		
	protected void resetProgressBar(){
		stateProgressBar = 0;
		incrProgressBar(0);
	}
		
	@Override
	protected void setProgressBar(int toSet) {
		stateProgressBar = toSet;
		incrProgressBar(0);	
	}
}
