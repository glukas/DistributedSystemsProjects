package ch.ethz.inf.vs.a2;

import ch.ethz.inf.vs.a2.sensor.SensorFactory;
import ch.ethz.inf.vs.a2.sensor.SensorFactory.Type;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity implements OnClickListener {

	////
	//ACTIVITY
	////
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		this.findViewById(R.id.button1).setOnClickListener(this);
		this.findViewById(R.id.button2).setOnClickListener(this);
		this.findViewById(R.id.button3).setOnClickListener(this);
		this.findViewById(R.id.button4).setOnClickListener(this);
		this.findViewById(R.id.button5).setOnClickListener(this);
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
	
	////
	//On Click Listener
	////

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.button1 :
			startTemperatureActivityWithSensor(SensorFactory.Type.RAW_HTTP);
			break;
		case R.id.button2 :
			startTemperatureActivityWithSensor(SensorFactory.Type.HTML);
			break;
		case R.id.button3 :
			startTemperatureActivityWithSensor(SensorFactory.Type.JSON);
			break;
		case R.id.button4 :
			startTemperatureActivityWithSensor(SensorFactory.Type.XML);
			break;
		case R.id.button5 :
			startTemperatureActivityWithSensor(SensorFactory.Type.SOAP);
			break;
		default :
		throw new RuntimeException("unkown view clicked");
		}
	}

	private void startTemperatureActivityWithSensor(Type type) {
		Intent intent = new Intent(this, TemperatureActivity.class);
		intent.putExtra(TemperatureActivity.SENSOR_TYPE_EXTRA, type.name());
		startActivity(intent);
	}
}
