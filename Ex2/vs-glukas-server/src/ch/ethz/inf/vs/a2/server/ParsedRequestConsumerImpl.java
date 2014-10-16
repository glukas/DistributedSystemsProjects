package ch.ethz.inf.vs.a2.server;

import java.io.IOException;
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
	
	private final long[] patternVibrate = new long[]{0, 100, 100, 100, 200, 100, 200};
	
	public ParsedRequestConsumerImpl(ServerService service){
		this.service = service;
		this.sensorManager = (SensorManager) service.getSystemService(Context.SENSOR_SERVICE);
	}
	
	
	@Override
	public synchronized void consume(ClientHandle<ParsedRequest> requestHandle) {
		
		client = requestHandle;
		request = requestHandle.request;
		
		try {
			
			//if the request is null (not valid or empty) post an empty reply
			//TODO differentiate between invalid and empty request (normally already done)
			if (request == null){
				requestHandle.postResponse("", Status.OK);
			}
		
			//differentiate the request if it's vibration / sensor read
			if (request.getSensorType().equals(SensorType.VIBRATOR)){
				if (request.getArgs() == null){
					vibrate();
				} else {
					vibrate(request.getArgs());
				}
				//vibrate launched, reply done
				requestHandle.postResponse("", Status.OK);
			} else {
				//register the listener for this particular sensor
				//TODO ensure existence of this sensor on the device
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


	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}


	@Override
	public void onSensorChanged(SensorEvent event) {
		//TODO get the values and parse them before post the reply
		try {
			client.postResponse("", Status.OK);
		} catch (IOException e) {
			e.printStackTrace();
		}
		sensorManager.unregisterListener(this);
	}

}
