package ch.ethz.inf.vs.a2.server;

import android.hardware.Sensor;

// TODO move in a better place (surely own class)
@SuppressWarnings("deprecation")
public enum SensorType {
	
	ACCELEROMETER("accelerometer",Sensor.TYPE_ACCELEROMETER, 3), 
	TEMPERATURE("temperature", Sensor.TYPE_TEMPERATURE, 1), 
	ROTATION("rotation", Sensor.TYPE_ROTATION_VECTOR, 3),
	ROTATION_GAME("rotationGame", Sensor.TYPE_GAME_ROTATION_VECTOR, 3),
	GRAVITY("gravity", Sensor.TYPE_GRAVITY, 3),
	GIROSCOPE("gyroscope", Sensor.TYPE_GYROSCOPE, 3),
	GIROSCOPE_UNCALIBRED("gyroscopeUncalibred", Sensor.TYPE_GYROSCOPE_UNCALIBRATED, 6),
	LIGHT("light", Sensor.TYPE_LIGHT, 1),
	ORIENTATION("orientation", Sensor.TYPE_ORIENTATION, 3),
	PRESSURE("pressure", Sensor.TYPE_PRESSURE, 1),
	MAGNETIC_FIELD("magnetic", Sensor.TYPE_MAGNETIC_FIELD, 3),
	MAGNETIC_FIELD_UNCALIBRED("magneticUncalibred", Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED, 6),
	PROXIMITY("proximity", Sensor.TYPE_PROXIMITY, 1),
	HUMDITY("humidity", Sensor.TYPE_RELATIVE_HUMIDITY, 1),
	LINEAR_ACCELERATION("linearAcc", Sensor.TYPE_LINEAR_ACCELERATION, 3),
	STEP_COUNTER("stepCounter", Sensor.TYPE_STEP_COUNTER, 1),
	STEP_DETECTOR("stepDetector", Sensor.TYPE_STEP_DETECTOR, 1),
	VIBRATOR("vibrator", -1, 0),
	UNSUPPORTED("unsupported", -1, 0),
	EMPTY("",-1, 0);
	
	private final String name;
	private final Sensor sensor;
	private final int numberValues;
	
	SensorType(String name, int sensorRef, int numberValues){
		this.name = name;
		this.sensor = RequestParserImpl.sensorManager.getDefaultSensor(sensorRef);
		this.numberValues = numberValues;
	}
	
	public String getRequestName(){
		return this.name;
	}
	
	public Sensor getSensor(){
		return sensor;
	}
	
	public int getNumberValues(){
		return numberValues;
	}
	
	public static SensorType getTypeFromName(String name){
		for (SensorType st : SensorType.values()){
			if (st.getRequestName().equals(name)){
				return st;
			}
		}
		return SensorType.UNSUPPORTED;
	}
}