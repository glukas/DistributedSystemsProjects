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
		request = request.split(" "+protocolName)[0];
		String[] requestBody = request.split(separator);
		
		//determine type of sensor targeted
		SensorType sensorType = SensorType.getTypeFromName((requestBody[0]));
		if (sensorType.equals(SensorType.UNSUPPORTED)){
			return null;
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
		
		ACCELEROMETER("accelerometer",Sensor.TYPE_ACCELEROMETER), 
		TEMPERATURE("temperature", Sensor.TYPE_TEMPERATURE), 
		ROTATION("rotation", Sensor.TYPE_ROTATION_VECTOR),
		ROTATION_GAME("rotationGame", Sensor.TYPE_GAME_ROTATION_VECTOR),
		GRAVITY("gravity", Sensor.TYPE_GRAVITY),
		GIROSCOPE("gyroscope", Sensor.TYPE_GYROSCOPE),
		GIROSCOPE_UNCALIBRED("gyroscopeUncalibred", Sensor.TYPE_GYROSCOPE_UNCALIBRATED),
		LIGHT("light", Sensor.TYPE_LIGHT),
		ORIENTATION("orientation", Sensor.TYPE_ORIENTATION),
		PRESSURE("pressure", Sensor.TYPE_PRESSURE),
		MAGNETIC_FIELD("magnetic", Sensor.TYPE_MAGNETIC_FIELD),
		MAGNETIC_FIELD_UNCALIBRED("magneticUncalibred", Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED),
		PROXIMITY("proximity", Sensor.TYPE_PROXIMITY),
		HUMDITY("humidity", Sensor.TYPE_RELATIVE_HUMIDITY),
		LINEAR_ACCELERATION("linearAcc", Sensor.TYPE_LINEAR_ACCELERATION),
		STEP_COUNTER("stepCounter", Sensor.TYPE_STEP_COUNTER),
		STEP_DETECTOR("stepDetector", Sensor.TYPE_STEP_DETECTOR),
		VIBRATOR("vibrator", -1),
		UNSUPPORTED("unsupported", -1);
		
		private final String name;
		private final Sensor sensor;
		
		SensorType(String name, int sensorRef){
			this.name = name;
			this.sensor = sensorManager.getDefaultSensor(sensorRef);

		}
		
		public String getRequestName(){
			return this.name;
		}
		
		public Sensor getSensor(){
			return sensor;
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
