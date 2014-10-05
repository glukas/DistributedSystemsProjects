package ethz.ch.vs.a1.glukas.sensors;

import java.util.ArrayList;
import java.util.HashMap;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
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
		int i = 1;
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
				for (double data : event.values) {
	
			item = new HashMap<String,String>();
			item.put("item1",String.valueOf(i));
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
