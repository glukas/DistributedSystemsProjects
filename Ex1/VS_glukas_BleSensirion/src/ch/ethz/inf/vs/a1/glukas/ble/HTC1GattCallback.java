package ch.ethz.inf.vs.a1.glukas.ble;

import java.util.UUID;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class HTC1GattCallback extends BluetoothGattCallback {

	private static final String RHT_SERVICE_UUID = "0000AA20-0000-1000-8000-00805f9b34fb";
	private static final String RHT_CHARACTERISTIC_UUID = "0000AA21-0000-1000-8000-00805f9b34fb";

	private static final UUID serviceId = UUID.fromString(RHT_SERVICE_UUID);
	private static final UUID characteristicId = UUID.fromString(RHT_CHARACTERISTIC_UUID);
	
	private final Handler updateHandler;
	
	//if this flag is set to true, the callback will stop polling the device and close BluetoothGatt
	public boolean disabled = false;
	
	//the read update handler will receive a message when a new value is read from the device
	//the message will have the raw humidity reading as arg1, and the raw temperature reading as arg2
	public HTC1GattCallback(Handler readUpdateHandler) {
		this.updateHandler = readUpdateHandler;
	}
	
	@Override
	public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
		if (disabled) {
			gatt.close();
		} else if (newState == BluetoothProfile.STATE_CONNECTED) {
			gatt.discoverServices();
		}
	}
	
	@Override
	public void onServicesDiscovered (BluetoothGatt gatt, int status) {
		if (disabled) {
			gatt.close();

		} else if (status == BluetoothGatt.GATT_SUCCESS) {
			
			for (BluetoothGattService service : gatt.getServices()) {
				if (isServiceSupported(service.getUuid())) {

					BluetoothGattCharacteristic rht = new BluetoothGattCharacteristic(characteristicId,
							BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
							BluetoothGattCharacteristic.PERMISSION_READ);

					// add the characteristic to the discovered RH&T service
					service.addCharacteristic(rht);
					
					boolean success = gatt.readCharacteristic(rht);
					if (!success) {
						gatt.close();
						Log.e(RHT_SERVICE_UUID, "read failed");
					}
					
				}
			}

		} else {
			gatt.close();
			//TODO maybe show to the user?
			Log.e(RHT_SERVICE_UUID, "service discovery failed");
		}
		
	}
	
	@Override
	public void onCharacteristicRead (BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
		if (disabled) {
			gatt.close();
			
		} else if (status == BluetoothGatt.GATT_SUCCESS) {
			
			//get values
			int tmp = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0);
			int humid = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 2);
			
			//send message
			Message message = new Message();
			message.arg1 = humid;
			message.arg2 = tmp;
			updateHandler.sendMessage(message);

			gatt.readCharacteristic(characteristic);//poll for next value
		}   
	}

	private boolean isServiceSupported(UUID uuid) {
		return serviceId.equals(uuid);
	}
	
}
