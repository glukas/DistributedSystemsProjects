package ch.ethz.inf.vs.a2.server;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;

public class RequestParserImpl implements RequestParser<ParsedRequest>{
	
	private final String path = "/DistributedSystemsProjects/";
	private final String protocolName = "HTTP/1.1";
	private final String methodName = "GET";
	private final String separator = "\\?";
	private final String separatorArgs = "\\.";
	
	private static SensorManager sensorManager;
	
	public RequestParserImpl(ServerService service){
		RequestParserImpl.sensorManager = (SensorManager) service.getSystemService(Context.SENSOR_SERVICE);
	}
	
	@Override
	public synchronized ParsedRequest parse(String request) {
		
		//check if the request begins with good format
		if (!(request.startsWith(methodName + " " + path))){
			return null;
		}
		
		//extract body of request
		request = request.substring(1+methodName.length()+path.length());

		request = request.split(" "+protocolName).length > 0 ? request.split(" "+protocolName)[0] : null;
		if (request == null){
			return new ParsedRequest(SensorType.EMPTY, null);
		}
		String[] requestBody = request.split(separator);
		
		//determine type of sensor targeted
		SensorType sensorType = SensorType.getTypeFromName((requestBody[0]));
		if (sensorType.equals(SensorType.UNSUPPORTED)){
			return new ParsedRequest(SensorType.UNSUPPORTED, null);
		}
		
		//extract arguments if any
		String[] args = null;
		long[] argsParsed = null;
		if (requestBody.length > 1){
			args = requestBody[1].split(separatorArgs);
			argsParsed = parseArguments(sensorType, args);
		}
		
		//return a easy to use format of the request
		return new ParsedRequest(sensorType, argsParsed);
	}
	
	private long[] parseArguments(SensorType sensorT, String[] args){
		if (args == null){
			return null;
		}
		long[] argsParsed = new long[args.length];
		for (int i = 0; i < args.length; i++){
			try {
				argsParsed[i]  = Long.parseLong(args[i]);
			} catch(NumberFormatException e){
				return null;
			}
		}
		return argsParsed;
	}
	
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
			this.sensor = sensorManager.getDefaultSensor(sensorRef);
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
}
