package ethz.ch.vs.a1.glukas.sensors;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity implements OnItemClickListener {

	public static final String EXTRA_SENSOR_TYPE = "ch.ethz.VS.a1.glukas.sensor.extra_sensor_type";
	
	ListView listView;
	List<Sensor> sensors;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//get list of sensors
		SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
		//get the names of the sensors
		List<String> sensorNames = new ArrayList<String>();
		for (Sensor sensor : sensors) {
			sensorNames.add(sensor.getName());
		}
		//create ArrayAdapter & bind to list view
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_row, sensorNames);
		
		listView = (ListView)findViewById(R.id.listView1);
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		//here the new activity should is started
		Intent startSensorActivity = new Intent(this, SensorActivity.class);
		//here we need to set the information of the sensor that was selected
		Sensor sensor = sensors.get(position);
		startSensorActivity.putExtra(EXTRA_SENSOR_TYPE, sensor.getType());
		
		startActivity(startSensorActivity);
		
	}
}
