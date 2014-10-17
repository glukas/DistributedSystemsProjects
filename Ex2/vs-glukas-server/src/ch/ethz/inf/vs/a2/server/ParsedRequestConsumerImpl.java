package ch.ethz.inf.vs.a2.server;

import java.io.IOException;
import java.util.Arrays;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import ch.ethz.inf.vs.a2.server.ClientHandle.Status;
import ch.ethz.inf.vs.a2.server.RequestParserImpl.SensorType;

public class ParsedRequestConsumerImpl implements ParsedRequestConsumer<ParsedRequest>, SensorEventListener{
	
	private ServerService service;
	private SensorManager sensorManager;
	private ParsedRequest request;
	private ClientHandle<ParsedRequest> client;
	
	private final String noSuchSensor = "Sorry, no such sensor on that device";
	private final String invalidRequest = "Invalid request";
	private final String vibrateDone = "Vibrate... Done!";
	private final String requestEmpty = "This request is quite empty, isn't it?";
	private final long[] patternVibrate = new long[]{0, 100, 100, 100, 200, 100, 200};
	
	public ParsedRequestConsumerImpl(ServerService service){
		this.service = service;
		this.sensorManager = (SensorManager) service.getSystemService(Context.SENSOR_SERVICE);
	}
	
	
	@Override
	public synchronized void consume(ClientHandle<ParsedRequest> requestHandle) {
		
		client = requestHandle;
		request = requestHandle.request;
		
		//TODO define status with Lukas
		
		try {
			
			//if the request is null (not valid) post an empty reply
			if (request == null){
				requestHandle.postResponse(invalidRequest, Status.NOT_FOUND);
				return;
			} else if (request.getSensorType().equals(SensorType.EMPTY)){
				requestHandle.postResponse(requestEmpty, Status.NOT_FOUND);
				return;
			} else if (request.getSensorType().getSensor() == null || 
					request.getSensorType().equals(SensorType.UNSUPPORTED)){
				requestHandle.postResponse(noSuchSensor, Status.NOT_FOUND);
				return;
			} 
		
			//differentiate the request if it's vibration / sensor read
			if (request.getSensorType().equals(SensorType.VIBRATOR)){
				if (request.getArgs() == null){
					vibrate();
				} else {
					vibrate(request.getArgs());
				}
				//vibrate launched, reply done
				requestHandle.postResponse(vibrateDone, Status.OK);
			} else {
				//register the listener for this particular sensor
				sensorManager.registerListener(this, request.getSensorType().getSensor(), SensorManager.SENSOR_DELAY_NORMAL);
			}
		} catch (IOException e) {
			Log.e(this.getClass().toString(),e.getLocalizedMessage());
		}
	}
	
	private boolean vibrate(){
		return vibrate(patternVibrate);
	}
	
	private boolean vibrate(long[] pattern){
		service.vibrate(pattern);
		return true;
	}
	
	public String parseValues(float[] values){
		
		String valuesStr = "";
		
		for (int i = 0; i < request.getSensorType().getNumberValues(); i++){
			valuesStr += String.format("%.2f",values[i]);
			if ((i + 1) < request.getSensorType().getNumberValues()){
				valuesStr += ", ";
			}
		}
		
		String ret =  "The values for the sensor '"+request.getSensorType().getRequestName() +"'"+ 
				" are : "+valuesStr;
		return ret;
	}


	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}


	@Override
	public void onSensorChanged(SensorEvent event) {

		float[] valuesCopied = Arrays.copyOf(event.values, event.values.length);
		try {
			client.postResponse(parseValues(valuesCopied), Status.OK);
		} catch (IOException e) {
			e.printStackTrace();
		}
		sensorManager.unregisterListener(this);
	}

}
