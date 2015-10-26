package gepocketmikecmpsc483w.pocketmike_cmpsc483w;

/**
 * Created by Anthony on 10/7/2015.
 *
 */
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;

public class ConnectThread extends Thread {

    private BluetoothSocket socket;
    private BluetoothDevice device;
    private Handler handler;

    public ConnectedThread getConnectedThread() {
        return connectedThread;
    }

    public void setConnectedThread(ConnectedThread connectedThread) {
        this.connectedThread = connectedThread;
    }

    public ConnectedThread connectedThread;

    public ConnectThread(BluetoothDevice device) {
        this.device = device;

        try {
            this.socket = this.device.createRfcommSocketToServiceRecord(device.getUuids()[0].getUuid());
        } catch (IOException e) {
            Log.d("PocketMike_CMPSC483W", e.getMessage());
        }
    }

    public void run() {
        Log.d("PocketMike_CMPSC483W", "Starting Bluetooth connect thread");

        if (this.socket == null) {
            Log.d("PocketMike_CMPSC483W", "Could not find Bluetooth socket");
        } else {
            Log.d("PocketMike_CMPSC483W", "Successfully found Bluetooth socket");
        }

        try {
            this.socket.connect();

            //ConnectedThread connectedThread = new ConnectedThread(this.socket);
            connectedThread = new ConnectedThread(this.socket);
            connectedThread.setCommandProcessedHandler(this.handler);
            connectedThread.start();
        } catch (IOException e) {
            Log.d("PocketMike_CMPSC483W", "Failed to connect to Bluetooth socket");
        }
    }

    public void setCommandProcessedHandler(Handler handler) {
        this.handler = handler;
    }

}
