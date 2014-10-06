package ch.ethz.inf.vs.a1.vs.glukas.antitheft;

import ch.ethz.inf.vs.a1.vs_glukas_antitheft.R;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.IBinder;

public class AntiTheftServiceImpl extends AbstractAntiTheftService {

	public static final String broadcastMessage = "notificationClick";
	Receiver broadcastReceiver;
	AlarmThread armAlarm;
	protected volatile boolean alarmArmed = false;
	protected volatile boolean stopAlarm = false;
	protected NotificationWrapper notif;
	
	private class Receiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context c, Intent i) {
			finishAlarm();
			notif.setNotificationNoMove();
		}
	}
	
	private void finishAlarm(){
		stopAlarm = true;
		//wait for thread to finish
		try {
			if (armAlarm != null){
				armAlarm.join();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		stopAlarm = false;
		alarmArmed = false;
	}
	
	private void finishNotification(){
		notif.destroyNotification();
		notif = null;
	}
    
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		//Stop armed alarm
		finishAlarm();
		//unregister listener of sensor
		sensorManager.unregisterListener(listener);
		//destroy notification
		finishNotification();
		//unregister broadcast
		unregisterReceiver(broadcastReceiver);
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		//start with the state of the progress bar at 0
		sensorManager.registerListener(listener,
		    	sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
		        SensorManager.SENSOR_DELAY_NORMAL);
		
		//create a new notification
		notif = new NotificationWrapper(this);
		
		//create new broadcast and register it
        broadcastReceiver = new Receiver();
        IntentFilter iFilter = new IntentFilter();
        iFilter.addCategory(broadcastMessage);
        iFilter.addAction(broadcastMessage);
        registerReceiver(broadcastReceiver, iFilter);
	}
	
	@Override
	public void startAlarm() {
		if (alarmArmed){
			return;
		}
		alarmArmed = true;
		armAlarm = new AlarmThread();
		armAlarm.start();
	}
	
	public class AlarmThread extends Thread{

		@Override
		public void run(){
			notif.setNotificationInProgress(0);
			int timeout = getTimeout();
			int stepSleep = timeout / 10;
			int stepIncr = timeout / 100;
			for (int i = 0; i < 100; i++){
				try {
					Thread.sleep(stepSleep);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				notif.incrNotificationInProgress(stepIncr);
				//To not use stop() deprecated method, boolean used to abort thread prematurely
				if(stopAlarm){
					return;
				}
			}
			ringAlarm();
		}
	}
	
	protected void ringAlarm() {
		notif.setNotificationRinging();
		MediaPlayer mp = MediaPlayer.create(this, R.raw.sound);
		mp.setVolume(1.0f, 1.0f);
		mp.start();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
