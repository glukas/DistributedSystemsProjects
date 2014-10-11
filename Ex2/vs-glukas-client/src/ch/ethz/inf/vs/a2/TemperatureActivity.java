package ch.ethz.inf.vs.a2;

import ch.ethz.inf.vs.a2.sensor.Sensor;
import ch.ethz.inf.vs.a2.sensor.SensorFactory;
import ch.ethz.inf.vs.a2.sensor.SensorFactory.Type;
import ch.ethz.inf.vs.a2.sensor.SensorListener;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class TemperatureActivity extends Activity implements SensorListener {

	public static final String HOST_TEMPERATURE = "vslab.inf.ethz.ch";
	public static final int PORT_TEMPERATURE = 8081;
	public static final String PATH_TEMPERATURE = "sunspots/Spot1";
	public static final String SENSOR_TYPE_EXTRA = "ch.ethz.inf.vs.a2.senor_type";
	private Sensor sensor;
	TextView text;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_temperature);
		String typeString = this.getIntent().getStringExtra(SENSOR_TYPE_EXTRA);
		SensorFactory.Type type = Type.valueOf(typeString);
		sensor = SensorFactory.getInstance(type);
		text = (TextView)this.findViewById(R.id.temperatureText);
		text.setText(R.string.temperatureLoadingText);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		sensor.registerListener(this);
		//TODO there could be an issue here.
		//	   if the user switches quickly between paused and stopped state, many requests will be generated
		sensor.getTemperature();
	}
	
	public void onStop() {
		super.onStop();
		sensor.unregisterListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.temperature, menu);
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
	
	////
	//SENSOR LISTENER
	////

	@Override
	public void onReceiveDouble(double value) {
		text.setText(String.format(this.getString(R.string.temperatureMainText), value));
	}

	@Override
	public void onReceiveString(String message) {
		//only occurs in error cases
		Log.e(this.getPackageName(), message);
	}
}
