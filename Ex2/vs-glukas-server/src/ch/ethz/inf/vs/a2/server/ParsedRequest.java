package ch.ethz.inf.vs.a2.server;


public class ParsedRequest {
	
	private final SensorType sensorType;
	private final long[] args;
	
	public ParsedRequest(SensorType sensorType, long[] args){
		this.sensorType = sensorType;
		this.args = args;
	}
	
	public SensorType getSensorType(){
		return sensorType;
	}
	
	public long[] getArgs(){
		return args;
	}

}
