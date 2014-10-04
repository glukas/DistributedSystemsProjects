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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DeviceControlActivity extends Activity {

	public static final String EXTRAS_DEVICE_ADDRESS = "ch.ethz.VS.a1.glukas.ble.extras_device_addr";
	private BluetoothAdapter bluetoothAdapter;
	private String deviceAddress;
	private BluetoothDevice device;
	private GattCallback callback;
	
	private static String RHT_SERVICE_UUID = "0000AA20-0000-1000-8000-00805f9b34fb";
	private static String RHT_CHARACTERISTIC_UUID = "0000AA21-0000-1000-8000-00805f9b34fb";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_control);
		Intent intent = getIntent();
		TextView address = (TextView) findViewById(R.id.address);
		
		deviceAddress = intent.getExtras().getString(EXTRAS_DEVICE_ADDRESS);
		if (deviceAddress == null) {
			throw new RuntimeException("no device address specified");//TODO : nicer error handling
		}
		
		address.setText(deviceAddress);
		
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		bluetoothAdapter = bluetoothManager.getAdapter();
		device = bluetoothAdapter.getRemoteDevice(deviceAddress);
		
		if (device == null) {
			throw new RuntimeException("invalid device address " + deviceAddress);
		}
		//connecting can take a few seconds and is thus done asynchronously
		callback = new GattCallback();
		device.connectGatt(this, false, callback);
	}
	
	@Override
	public void onRestart() {
		super.onRestart();
		// reconnect
		callback = new GattCallback();
		device.connectGatt(this, false, callback);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		// disconnect / close on the next occasion
		callback.disabled = true;
	}
	
	class GattCallback extends BluetoothGattCallback {
	
		private UUID serviceId = UUID.fromString(RHT_SERVICE_UUID);
		private UUID characteristicId = UUID.fromString(RHT_CHARACTERISTIC_UUID);
		
		public boolean disabled = false;
		
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				gatt.discoverServices();
			}
		}
		
		@Override
		public void onServicesDiscovered (BluetoothGatt gatt, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				
				if (disabled) {
					gatt.close();
					return;
				}
				
				for (BluetoothGattService service : gatt.getServices()) {
					if (isServiceSupported(service.getUuid())) {

						BluetoothGattCharacteristic rht = new BluetoothGattCharacteristic(characteristicId,
								BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
								BluetoothGattCharacteristic.PERMISSION_READ);

						// add the characteristic to the discovered RH&T service
						service.addCharacteristic(rht);
						
						boolean success = gatt.readCharacteristic(rht);
						if (!success) {
							Log.e(RHT_SERVICE_UUID, "read failed");
						}
						/*success = gatt.setCharacteristicNotification(rht, true);
						
						if (!success) {
							Log.e(RHT_SERVICE_UUID, "notify failed");
						}*/
						
					}
				}

			} else {
				//TODO maybe retry?
				Log.e(RHT_SERVICE_UUID, "service discovery failed");

			}
			
		}
		
		@Override
		public void onCharacteristicChanged (BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

			//TODO (Lukas) proper formatting
			/*byte[] value = characteristic.getValue();
			String result = "";
			for (byte b : value) {
				result += String.format("%02X", b);
			}
			//characteristic.getFloatValue(formatType, offset)
			Log.v("READ_TEST CHANGED", result);*/
			//gatt.readCharacteristic(characteristic);
			
		}
		
		@Override
		public void onCharacteristicRead (BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			if (disabled) {
				gatt.close();
				
			} else if (status == BluetoothGatt.GATT_SUCCESS) {
				
				//TODO (Lukas) proper formatting
				// I have absolutely no idea how to parse the values. the only thing I found was this datasheet
				//http://www.sensirion.com/fileadmin/user_upload/customers/sensirion/Dokumente/Humidity/Sensirion_Humidity_SHTC1_Datasheet_V3.pdf
				
				byte[] value = characteristic.getValue();
				String result = "";
				for (byte b : value) {
				//	result += String.format("%02X", b);
				}
				for (int i=0; i<4; i++) {
					result = result += String.format("%d ",characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, i));
				}
				
				//result += "\n " + Float.intBitsToFloat(Integer.decode("0x"+result.substring(0, 4)));
				int rawHumid = (characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0)<<8)+characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 1);
				double humid = 100.0*((float)rawHumid)/((double)(1<<16));
				//double humid = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, 2);
				//float humid = (float)rawHumid;
				result += String.format("\n%f ", humid);
				
				int rawT = (characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 2)<<8)+characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 3);
				double t = 100.0*((double)rawT)/((double)(1<<16));
				result += String.format("\n%f ", t);
				
				
				Log.v("READ_TEST READ", result);
				
				gatt.readCharacteristic(characteristic);//polling
			}
		}

		private boolean isServiceSupported(UUID uuid) {
			return serviceId.equals(uuid);
		}
		
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
