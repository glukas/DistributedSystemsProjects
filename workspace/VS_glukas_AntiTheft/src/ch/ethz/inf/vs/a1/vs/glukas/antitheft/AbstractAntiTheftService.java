package ch.ethz.inf.vs.a1.vs.glukas.antitheft;

import android.app.Service;
import android.content.SharedPreferences;
import android.hardware.SensorManager;


public abstract class AbstractAntiTheftService extends Service {

	protected AbstractMovementDetector listener;
	protected SharedPreferences preferences;
	protected SensorManager sensorManager;
	
	@Override
	public void onCreate() {
		//Get the sensor manager
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		
		//Initialize movement detector
		listener = new MovementDetector();
		listener.setCallbackService(this);
		  
		//get a reference to the shared preferences
		preferences = this.getSharedPreferences(Settings.SETTINGS_FILENAME, 0);
	}

	/**
	 * Starts the alarm when a deliberate move is detected.
	 */
	public abstract void startAlarm();
	
	protected int getTimeout() {
		return preferences.getInt(Settings.TIMEOUT_STR, Settings.TIMEOUT_DEFAULT);
	}
	
	protected int getSensitivity() {
		return preferences.getInt(Settings.SENSITIVITY_STR, Settings.SENSITIVITY_DEFAULT);
	}
}
