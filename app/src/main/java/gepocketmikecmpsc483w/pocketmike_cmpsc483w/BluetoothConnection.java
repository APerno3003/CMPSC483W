package gepocketmikecmpsc483w.pocketmike_cmpsc483w;

/**
 * Created by Anthony on 10/7/2015.
 *
 */
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
    private boolean isBluetoothRunning = false;
    private boolean isEchoOff = false;

    // Event handling
    private Handler commandProcessedHandler;
    public BluetoothConnection()
    {
        initAdapter();
        adapter.startDiscovery();

    }
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


    public void closeBluetoothConnection(){
        if(isBluetoothRunning) {
            connectThread.getConnectedThread().cancel();
        }
    }

    //allows the user to send commands to the pocketMike
    public void sendCommand(String commandString){
        Log.d("PocketMike_CMPSC483W", "BluetoothConnection sendCommand " + commandString);

        //String commandString = "rd\r"; //In order for the pocketMike to receive commands correctly the string must end with \r
        byte[] commandBytes = commandString.getBytes();
        connectThread.getConnectedThread().write(commandBytes);

    }

    public void turnOffEcho(){
        if(isBluetoothRunning && !isEchoOff) {
            setConnectedThreadCommand("e0");
            String sentMessage = "e0";
            getConnectThread().getConnectedThread().setCurrentCommand(sentMessage);
            sendCommand("e0\r");
        }

    }

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

    public void setIsBluetoothRunning(boolean isBluetooth)
    {
        isBluetoothRunning = isBluetooth;
    }

    //checks to see if bluetooth is currently running
    public boolean getIsBluetoothRunning()
    {
        if(connectThread == null || connectThread.getConnectedThread() == null) {
            setIsBluetoothRunning(false);
        }
        else
        {
            setIsBluetoothRunning(true);
        }

        return isBluetoothRunning;
    }


    public boolean getIsEchoOff() {
        return isEchoOff;
    }

    public void setIsEchoOff(boolean isEchoOff) {
        this.isEchoOff = isEchoOff;
    }

    public String getConnectedThreadCommand(){
        return this.getConnectThread().getConnectedThread().getCurrentCommand();
    }

    public void setConnectedThreadCommand(String currentCommand){
        this.getConnectThread().getConnectedThread().setCurrentCommand(currentCommand);
    }


}

