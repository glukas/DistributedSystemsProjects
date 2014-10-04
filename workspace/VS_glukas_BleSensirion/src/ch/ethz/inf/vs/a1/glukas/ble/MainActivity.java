package ch.ethz.inf.vs.a1.glukas.ble;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnItemClickListener {

	private List<BluetoothDevice> devices;
	private ArrayAdapter<String> adapternames;
	private List<String> devicenames;
	private BluetoothAdapter mBluetoothAdapter;
	private ListView DeviceList;
	private static final int REQUEST_ENABLE_BT = 1;
	private boolean mScanning;
	private Handler mHandler;
	private Runnable countdown;
	// Stops scanning after 10 seconds.
	private static final long SCAN_TIME = 10000;
	
	private static String DEVICE_NAME_PREFIX = "SHTC1";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mHandler = new Handler();
		devicenames = new ArrayList<String>();
		devices = new ArrayList<BluetoothDevice>();

		// Checks if Phone has BLe : Yes -> do nothing , No -> Ask User to
		// toggle BLe
		if (!getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT)
					.show();
			finish();
		}
		// Gets the Bluetooth Adapter
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();

		// Checks if also the normal Bluetooth is supported
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, R.string.error_bluetooth_not_supported,
					Toast.LENGTH_SHORT).show();
			finish();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		// Displaying menu differently depending on mScanning
		if (!mScanning) {
			menu.findItem(R.id.menu_stop).setVisible(false);
			menu.findItem(R.id.menu_scan).setVisible(true);
			menu.findItem(R.id.menu_refresh).setActionView(null);
		} else {
			menu.findItem(R.id.menu_stop).setVisible(true);
			menu.findItem(R.id.menu_scan).setVisible(false);
			menu.findItem(R.id.menu_refresh).setActionView(R.layout.progress);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.menu_scan) {
			devices.clear();
			devicenames.clear();
			devicenames = new ArrayList<String>();
			devices = new ArrayList<BluetoothDevice>();
			adapternames = new ArrayAdapter<String>(MainActivity.this,
					R.layout.list_row, devicenames);
			DeviceList = (ListView) findViewById(R.id.listView1);
			DeviceList.setAdapter(adapternames);
			scanLeDevice(true);
		} else if (id == R.id.menu_stop) {
			scanLeDevice(false);
		}
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Ensures Bluetooth is enabled on the device. If Bluetooth is not
		// currently enabled,
		// fire an intent to display a dialog asking the user to grant
		// permission to enable it.
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}

		// Initializes a new List which will contain device names
		devicenames = new ArrayList<String>();
		devices = new ArrayList<BluetoothDevice>();
		scanLeDevice(true);
	}

	@Override
	protected void onPause() {
		super.onPause();
		scanLeDevice(false);
		devices.clear();
		devicenames.clear();
		devicenames = new ArrayList<String>();
		devices = new ArrayList<BluetoothDevice>();
		adapternames = new ArrayAdapter<String>(MainActivity.this,
		R.layout.list_row, devicenames);
		DeviceList = (ListView) findViewById(R.id.listView1);
		DeviceList.setAdapter(adapternames);
	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		BluetoothDevice device = devices.get(position);
		final Intent intent = new Intent(this, DeviceControlActivity.class);
		intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
		startActivity(intent);
	}

	// Scanning method
	private void scanLeDevice(final boolean enable) {
		if (enable) {

			// Stops scanning after a pre-defined scan period.
			mHandler.postDelayed(countdown = new Runnable() {
				@Override
				public void run() {
					mScanning = false;
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
					invalidateOptionsMenu();
				}
			}, SCAN_TIME);

			mScanning = true;
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			mHandler.removeCallbacks(countdown);
			mScanning = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
		invalidateOptionsMenu();
	}

	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, int rssi,
				byte[] scanRecord) {
			runOnUiThread(new Runnable() {

				private String formatDevice(BluetoothDevice device) {
					return String.format("%s (%s)", device.getName(), device.getAddress());
				}
				
				private boolean isDeviceSupported(BluetoothDevice device) {
					//this seems enough for our purposes
					return device.getName().startsWith(DEVICE_NAME_PREFIX);
				}

				@Override
				public void run() {
					if (!devicenames.contains(formatDevice(device)) && isDeviceSupported(device)) {
						devices.add(device);
						devicenames.add(formatDevice(device));
						adapternames = new ArrayAdapter<String>(MainActivity.this, R.layout.list_row, devicenames);
						DeviceList = (ListView) findViewById(R.id.listView1);
						DeviceList.setAdapter(adapternames);
						DeviceList.setOnItemClickListener(MainActivity.this);
					}

				}
			});
		}
	};

}
