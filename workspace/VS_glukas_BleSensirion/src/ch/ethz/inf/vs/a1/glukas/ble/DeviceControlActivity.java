package ch.ethz.inf.vs.a1.glukas.ble;

import ch.ethz.inf.vs.a1.glukas.ble.R.id;
import ch.ethz.inf.vs.a1.glukas.ble.R.layout;
import ch.ethz.inf.vs.a1.glukas.ble.R.menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DeviceControlActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_control);
		Intent intent = getIntent();
		TextView address = (TextView) findViewById(R.id.address);
		address.setText(intent.getExtras().getString(MainActivity.EXTRAS_DEVICE_ADDRESS));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.device_control, menu);
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
