package ethz.ch.vs.a1.glukas.sensors;

import java.util.ArrayList;
import java.util.HashMap;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class SensorActivity extends Activity implements SensorEventListener {
	private ListView listView;
	private SensorManager sensorManager;
	private SimpleAdapter simpleAdapter;
	float[] g = {0f,0f,0f};
	float alpha = 0.8f;
	
	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensor);	
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sensor, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			//Test if it really unregisters
			//sensorManager.unregisterListener(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	// One of the methodes of SensorEventListener we have to implement
	// If measured values change this method gets executed 
	@Override
	  public void onSensorChanged(SensorEvent event) {
		ArrayList<HashMap<String,String>> sensorData = new ArrayList<HashMap<String,String>>();
		int i = 0;
		HashMap<String,String> item;
		// Test for AntiThief Accelerator
		/*float[] values = null;
		values = event.values;
		// low-pass filter
				g[0] = (alpha) * g[0] +  (1-alpha) * values[0];
				g[1] = (alpha) * g[1] +  (1-alpha) * values[1];
				g[2] = (alpha) * g[2] +  (1-alpha) * values[2];
				
				// high-pass filter
				values[0] = values[0]- g[0];
				values[1] = values[1]- g[1];
				values[2] = values[2]- g[2];

				
				double[] average = {Math.sqrt(Math.pow(values[0] , 2) + Math.pow(values[0] , 2) + Math.pow(values[0] , 2))};*/
			
		// Display for names and units
		String[] names = {"1st value", "2nd value", "3rd value", "Other value", "Other value", "Other value"};
		switch (getIntent().getExtras().getInt(MainActivity.EXTRA_SENSOR_TYPE)){
		case Sensor.TYPE_ACCELEROMETER: {
		names[0] = "x in m/s\u00B2";
		names[1] = "y in m/s\u00B2";
		names[2] = "z in m/s\u00B2";
		}
		
		break;
		case Sensor.TYPE_AMBIENT_TEMPERATURE :{
		names[0] = "Temperature in Celsius";	
			
		} 
		break;
		case Sensor.TYPE_ROTATION_VECTOR:{
			names[0] = "x";
			names[1] = "y";
			names[2] = "z";
		} 
		break;
		case Sensor.TYPE_GRAVITY: {
			names[0] = "x in m/s\u00B2";
			names[1] = "y in m/s\u00B2";
			names[2] = "z in m/s\u00B2";
		}
		break;
		case Sensor.TYPE_GYROSCOPE: {
			names[0] = "x in rad/s";
			names[1] = "y in rad/s";
			names[2] = "z in rad/s";
		}
		break;
		case Sensor.TYPE_GYROSCOPE_UNCALIBRATED: {
			names[0] = "x_uncalib in rad/s";
			names[1] = "y_uncalib in rad/s";
			names[2] = "z_uncalib in rad/s";
			names[3] = "x_drift in rad/s";
			names[4] = "y_drift in rad/s";
			names[5] = "z_drift in rad/s";
			
		}
		break;
		case Sensor.TYPE_LIGHT:{ 
			names[0] = "Illumination in lx";
		} 
		break;
		case Sensor.TYPE_ORIENTATION: {
		names[0] = "x in degrees";
		names[1] = "y in degrees";
		names[2] = "z in degrees";
		}
		break;
		case Sensor.TYPE_PRESSURE: {
		names[0] = "Pressure in hPa";
		}
		break;
		case Sensor.TYPE_MAGNETIC_FIELD: {
		names[0] = "x in uT";
		names[1] = "y in uT";
		names[2] = "z in uT";
		}
		break;
		case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED: {
			names[0] = "x_uncalib in uT";
			names[1] = "y_uncalib in uT";
			names[2] = "z_uncalib in uT";
			names[3] = "x_bias in uT";
			names[4] = "y_bias in uT";
			names[5] = "z_bias in uT";
			
		}
		break;
		
		case Sensor.TYPE_PROXIMITY: {
		names[0] = "Distance in cm";
		}
		break;
		
		case Sensor.TYPE_RELATIVE_HUMIDITY: {
		names[0] = "Humidity in %";
		}
		break;
		
		case Sensor.TYPE_LINEAR_ACCELERATION: {
		names[0] = "x in m/s\u00B2";
		names[1] = "y in m/s\u00B2";
		names[2] = "z in m/s\u00B2";
		}
		break;
		case Sensor.TYPE_STEP_COUNTER: {
		names[0] = "Step count";

		}
		break;
		default: {
			
		}
		break;
		}
		
		
		
			for (float data : event.values) {
		//Log.v("Number of values", String.valueOf(event.values.length));
			item = new HashMap<String,String>();
			
			if (i < names.length)	
			item.put("item1",names[i]);
			else
			item.put("item1","Other value");	
			item.put("item2",String.valueOf(data));
			sensorData.add(item);
			i++;
		}
		//create adapter + fill list with it
		simpleAdapter = new SimpleAdapter(this,sensorData, R.layout.list_two,new String[] {"item1","item2" }, new int[] {R.id.text1, R.id.text2});
		
		listView = (ListView) findViewById(R.id.listView1);
		listView.setAdapter(simpleAdapter);

	  }
	// Second method we have to implement but for this task we can leave it blank
	@Override
	  public void onAccuracyChanged(Sensor sensor, int accuracy) {

	  }
	
	//Gets called everytime we return to this Activity 
	@Override
	  protected void onResume() {
	    super.onResume();
	    
	    // register this class as a listener for events
	    sensorManager.registerListener(this,
	    	sensorManager.getDefaultSensor(getIntent().getExtras().getInt(MainActivity.EXTRA_SENSOR_TYPE)),
	        SensorManager.SENSOR_DELAY_NORMAL);
	  }

	//Gets called everytime we leave this Activity
	  @Override
	  protected void onPause() {
	    // unregister listener
	    super.onPause();
	    // Important to unregister when we leave the Activity
	    sensorManager.unregisterListener(this);
	  }

}
