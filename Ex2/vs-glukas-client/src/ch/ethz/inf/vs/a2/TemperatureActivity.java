package ch.ethz.inf.vs.a2;

import ch.ethz.inf.vs.a2.sensor.Sensor;
import ch.ethz.inf.vs.a2.sensor.SensorFactory;
import ch.ethz.inf.vs.a2.sensor.SensorFactory.Type;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class TemperatureActivity extends Activity {

	public static final String SENSOR_TYPE_EXTRA = "ch.ethz.inf.vs.a2.senor_type";
	private Sensor sensor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_temperature);
		SensorFactory.Type type = Type.valueOf(this.getIntent().getStringExtra(SENSOR_TYPE_EXTRA));
		sensor = SensorFactory.getInstance(type);
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
}
