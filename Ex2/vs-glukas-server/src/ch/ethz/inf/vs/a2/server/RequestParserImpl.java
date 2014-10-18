package ch.ethz.inf.vs.a2.server;

import android.content.Context;
import android.hardware.SensorManager;

/*
 * Parse the requests 
 * The requests format is :
 * 192.168.43.1:8081/DistributedSystemsProjects/SENSOR_NAME?ARG1.ARG2...
 * Where : 
 *   -SENSOR_NAME is a name listed in SensorType
 *   -ARGn , is an Integer. (Any number of arguments is allowed)
 */
public class RequestParserImpl implements RequestParser<ParsedRequest>{
	
	private final String path;
	private final String protocolName;
	private final String methodName;
	private final String separator;
	private final String separatorArgs;
	
	static SensorManager sensorManager;
	
	public RequestParserImpl(ServerService service){
		RequestParserImpl.sensorManager = (SensorManager) service.getSystemService(Context.SENSOR_SERVICE);
		
		//initialize strings related to request
		this.path = service.getResources().getString(R.string.app_path);
		this.protocolName = service.getResources().getString(R.string.protocol_name);
		this.methodName = service.getResources().getString(R.string.method_name);
		this.separator = service.getResources().getString(R.string.request_separator);
		this.separatorArgs = service.getResources().getString(R.string.request_args_separator);
	}
	
	@Override
	public ParsedRequest parse(String request) {
		
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
}
