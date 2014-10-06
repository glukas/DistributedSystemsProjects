package ch.ethz.inf.vs.a1.vs.glukas.antitheft;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public abstract class AbstractMovementDetector implements SensorEventListener {
	protected AbstractAntiTheftService antiTheftService;
	float[] g = {0f,0f,0f};
	float alpha = 0.8f;
	
	public void setCallbackService(AbstractAntiTheftService service) {
		antiTheftService = service;
	}
	
	int i = 0;
	
	/**
	 * Takes the sensor values, pass them to doAlarmLogic() to check if the alarm should be fired,
	 * then calls antiTheftService.startAlarm() if a deliberate movement is detected.
	 */
	@Override
	 public final void onSensorChanged(SensorEvent event) {
		float[] values = null;
		values = event.values;
		
		// Filtering out the gravitation in x,y and z
		// low-pass filter
		g[0] = (alpha) * g[0] +  (1-alpha) * values[0];
		g[1] = (alpha) * g[1] +  (1-alpha) * values[1];
		g[2] = (alpha) * g[2] +  (1-alpha) * values[2];
		
		// high-pass filter
		values[0] = values[0]- g[0];
		values[1] = values[1]- g[1];
		values[2] = values[2]- g[2];
		
		// Add code to populate the 'values' array with the sensor values
		boolean isAlarm = doAlarmLogic(values);
		if (isAlarm) {
			System.err.println("Call "+i+"the alarm");
			i++;
			((AbstractAntiTheftService)antiTheftService).startAlarm();
		}
	}
	
	/**
	 * Implements the sensor logic that is needed to trigger the alarm.
	 * @param values: the sensor values detected by the service.
	 * @return true if the service should start the alarm, false otherwise.
	 */
	protected abstract boolean doAlarmLogic(float[] values);
		
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// Do Nothing
	}
}
