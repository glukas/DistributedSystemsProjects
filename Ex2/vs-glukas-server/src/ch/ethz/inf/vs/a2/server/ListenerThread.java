package ch.ethz.inf.vs.a2.server;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;

import ch.ethz.inf.vs.a2.server.ClientHandle.Status;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ListenerThread extends Thread implements SensorEventListener {

	//management fields
	private ServerService service;
	private SensorManager sensorManager;
	private ParsedRequest request;
	private ClientHandle<ParsedRequest> client;
	private ExecutorService threadPool;
	
	//return strings, default vibrate pattern
	private final String noSuchSensor;
	private final String emptyRequest;
	private final String wrongRequest;
	private final String vibrateDone;
	private final long[] defaultVibratePattern = new long[]{0, 100, 100, 100, 200, 100, 200};
	
	/**
	 * Create a new thread which handle given client
	 * @param service the service bounds to
	 * @param client the client which made a request
	 */
	public ListenerThread(ServerService service, ClientHandle<ParsedRequest> client, ExecutorService threadPool){
		this.threadPool = threadPool;
		this.service = service;
		this.client = client;
		this.request = client.request;
		this.sensorManager = (SensorManager) service.getSystemService(Context.SENSOR_SERVICE);

		this.noSuchSensor = service.getResources().getString(R.string.no_such_sensor);
		this.emptyRequest = service.getResources().getString(R.string.empty_request);
		this.wrongRequest = service.getResources().getString(R.string.wrong_request);
		this.vibrateDone = service.getResources().getString(R.string.vibrate);
	}
	
	@Override
	public void run(){

		//invalid request
		if (request == null){
			postResponse(wrongRequest, Status.NOT_FOUND);
			return;
		} else if (request.getSensorType().equals(SensorType.EMPTY)){
			postResponse(emptyRequest, Status.NOT_FOUND);
			return;
		} else if (request.getSensorType().getSensor() == null || request.getSensorType().equals(SensorType.UNSUPPORTED)){
			postResponse(noSuchSensor, Status.NOT_FOUND);
			return;
		}
		
		//valid request
		if (request.getSensorType().equals(SensorType.VIBRATOR)){
			handleValidVibrateRequest();
			return;
		} else {
			handleValidSensorRequest();
		}
	} 
	
	private void handleValidSensorRequest(){
		sensorManager.registerListener(this, request.getSensorType().getSensor(), SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	private void handleValidVibrateRequest(){
		if (request.getArgs() == null){
			vibrate();
		} else {
			vibrate(request.getArgs());
		}
		postResponse(vibrateDone, Status.OK);
	}
	
	private void postResponse(String s, Status status){
		Thread t = new PostResponseThread(client, s, status);
		threadPool.execute(t);
	}
	
	private void vibrate(){
		vibrate(defaultVibratePattern);
	}
	
	private void vibrate(long[] args){
		service.vibrate(args);
	}
	
	
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float[] valuesCopied = Arrays.copyOf(event.values, event.values.length);
		postResponse(parseValues(valuesCopied), Status.OK);
		sensorManager.unregisterListener(this);
	}
	
	public String parseValues(float[] values){
		String valuesStr = "";
		
		for (int i = 0; i < request.getSensorType().getNumberValues(); i++){
			valuesStr += String.format(" %.2f",values[i]);
			if ((i + 1) < request.getSensorType().getNumberValues()){
				valuesStr += ",";
			}
		}
		return String.format(service.getResources().getString(R.string.valid_sensor_request), request.getSensorType().getRequestName()) + valuesStr;
	}
}
