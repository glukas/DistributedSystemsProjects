package ch.ethz.inf.vs.a1.glukas.ble;

import java.util.UUID;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DeviceControlActivity extends Activity implements Handler.Callback {

	public static final String EXTRAS_DEVICE_ADDRESS = "ch.ethz.VS.a1.glukas.ble.extras_device_addr";
	private BluetoothAdapter bluetoothAdapter;
	private String deviceAddress;
	private BluetoothDevice device;
	private HTC1GattCallback callback;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_control);
		Intent intent = getIntent();
		TextView address = (TextView) findViewById(R.id.address);
		
		deviceAddress = intent.getExtras().getString(EXTRAS_DEVICE_ADDRESS);
		if (deviceAddress == null) {
			//this should not occur and is would represent bug in the code that started this activity
			throw new RuntimeException("No device address specified");
		}
		
		address.setText(deviceAddress);
		
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		bluetoothAdapter = bluetoothManager.getAdapter();
		device = bluetoothAdapter.getRemoteDevice(deviceAddress);
		
		if (device == null) {
			throw new RuntimeException("invalid device address " + deviceAddress);
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		// (re)connect
		//connecting can take a few seconds and is thus done asynchronously
		callback = new HTC1GattCallback(new Handler(this));
		device.connectGatt(this, false, callback);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		// disconnect / close on the next occasion
		if (callback != null) {
			callback.disabled = true;
		}
	}
	
	//message by the gatt callback to update the display
	@Override
	public boolean handleMessage(Message msg) {
		
		TextView humValue = (TextView) findViewById(R.id.textViewHumValue);
		int msbH = msg.arg1 / 100;
		int lsbH = msg.arg1 % 100;
		humValue.setText(String.valueOf(msbH)+","+String.valueOf(lsbH));
		
		TextView tmpValue = (TextView) findViewById(R.id.textViewTmpValue);
		int msbT = msg.arg2 / 100;
		int lsbT = msg.arg2 % 100;
		tmpValue.setText(String.valueOf(msbT)+","+String.valueOf(lsbT));
		
		return false;
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
