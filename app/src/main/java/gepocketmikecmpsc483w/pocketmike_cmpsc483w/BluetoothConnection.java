package gepocketmikecmpsc483w.pocketmike_cmpsc483w;

/**
 * Created by Anthony on 10/7/2015.
 *
 */
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

public class BluetoothConnection {

    private String deviceName;
    private String deviceAddress;
    private UUID deviceUuid;
    private BluetoothDevice device;
    private BluetoothAdapter adapter;
    private ConnectThread connectThread;


    // Event handling
    private Handler commandProcessedHandler;

    public BluetoothConnection(String deviceName) {
        this.deviceName = deviceName;
        initAdapter();
    }

    public boolean initAdapter() {
        adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            Log.d("PocketMike_CMPSC483W", "Bluetooth adapter not found");
            return false;
        } else {
            Log.d("PocketMike_CMPSC483W", "Bluetooth adapter found");
            return true;
        }
    }

    public void startReading() {

        //ConnectThread connectThread = new ConnectThread(this.device);
        connectThread = new ConnectThread(this.device);
        connectThread.setCommandProcessedHandler(this.commandProcessedHandler);
        connectThread.start();

    }

    public void findDevice() {
        Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {

                if (device.getName().equals(this.deviceName)) {
                    this.deviceAddress = device.getAddress();
                    this.device = device;

                    // Find UUID of device
                    for (ParcelUuid uuid : device.getUuids()) {
                        this.deviceUuid = uuid.getUuid();
                    }
                }
            }
        }
    }


    public void sendCommand(String commandString){
        Log.d("PocketMike_CMPSC483W", "BluetoothConnection sendCommand " + commandString);

        //String commandString = "rd\r";
        //String commandString = "bl 1\r"; //In order for the pocketMike to receive commands correctly the string must end with \r
        //String commandString = "un";
        //String commandString = "un\n";
        //String commandString = "un\r";
        //String commandString = "un\r\n";
        byte[] commandBytes = commandString.getBytes();
        connectThread.getConnectedThread().write(commandBytes);

    }

    /*public void startBluetooth() {
        findDevice();
        setCommandProcessedHandler(new Handler());
        startReading();
    }*/

    //////////////////////////////////
    /// GETS AND SETS
    /////////////////////////////////
    public BluetoothAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(BluetoothAdapter adapter) {
        this.adapter = adapter;
    }

    public Handler getCommandProcessedHandler() {
        return commandProcessedHandler;
    }

    public void setCommandProcessedHandler(Handler commandProcessedHandler) {
        this.commandProcessedHandler = commandProcessedHandler;
    }

    public ConnectThread getConnectThread() {
        return connectThread;
    }

    public void setConnectThread(ConnectThread connectThread) {
        this.connectThread = connectThread;
    }
}
