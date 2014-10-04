package ch.ethz.inf.vs.a1.vs.glukas.antitheft;

import ch.ethz.inf.vs.a1.vs_glukas_antitheft.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v4.app.NotificationCompat;

public abstract class AbstractAntiTheftService extends Service {

	protected AbstractMovementDetector listener;
	protected int notifId = 001;
	protected NotificationManager notifManager;
	protected NotificationCompat.Builder notifBuilder;
	protected Notification notif;
	protected int stateProgressBar = 0;
	protected SharedPreferences preferences;
	protected SensorManager sensorManager;
	
	@Override
	public void onCreate() {
		//Get the sensor manager
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		
		//Create the builder for the notification with all features
		notifBuilder =
			    new NotificationCompat.Builder(this)
			    .setSmallIcon(R.drawable.ic_launcher)
			    .setContentTitle("Anti Theft")
			    .setContentText("State");
		
		
		//create the notification with the flag "ongoing" (can't be deleted by user)
		notifManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		//Initialize movement detector
		listener = new MovementDetector();
		listener.setCallbackService(this);
		sensorManager.registerListener(listener,
		    	sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
		        SensorManager.SENSOR_DELAY_NORMAL);
		  
		//get a reference to the shared preferences
		preferences = this.getSharedPreferences(Settings.SETTINGS_FILENAME, 0);
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		//Destroy the notification and the listener
		sensorManager.unregisterListener(listener);
		notifManager.cancel(notifId);
	}

	/**
	 * Starts the alarm when a deliberate move is detected.
	 */
	public abstract void startAlarm();
	
	/**
	 * Increment the status of the progress bar in the notification
	 * The maximum (timeout) is the timeout specified in the setting (Settings.TIMEOUT_DEFAULT)
	 * @param toIncr, amount of time (in seconds) to add to the progress bar
	 */
	public abstract void incrProgressBar(int toIncr);
	
	/**
	 * Set the status of the progress bar in the notification
	 * The maximum (timeout) is the timeout specified in the setting (Settings.TIMEOUT_DEFAULT)
	 * @param toSet, amount of time (in seconds) to be set in the progress bar
	 */
	public abstract void setProgressBar(int toSet);
	
	/**
	 * Reset the progress bar to zero
	 */
	public abstract void resetProgressBar();
	
	protected int getTimeout() {
		return preferences.getInt(Settings.TIMEOUT_STR, Settings.TIMEOUT_DEFAULT);
	}
	
	protected int getSensitivity() {
		return preferences.getInt(Settings.SENSITIVITY_STR, Settings.SENSITIVITY_DEFAULT);
	}
}
