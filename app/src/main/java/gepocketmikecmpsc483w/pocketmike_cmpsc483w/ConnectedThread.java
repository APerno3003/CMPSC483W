package gepocketmikecmpsc483w.pocketmike_cmpsc483w;

/**
 * Created by Anthony on 10/7/2015.
 */
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;

public class ConnectedThread extends Thread {
    public BluetoothSocket socket;
    public Handler commandProcessedHandler;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    public ConnectedThread(BluetoothSocket socket) {
        this.socket = socket;

        InputStream tmpInputStream = null;
        OutputStream tmpOutputStream = null;

        try {
            tmpInputStream = socket.getInputStream();
            tmpOutputStream = socket.getOutputStream();
        } catch (IOException e) {
            Log.d("PocketMike_CMPSC483W", "Failed to get input and output streams");
        }

        inputStream = tmpInputStream;
        outputStream = tmpOutputStream;
    }

   /* public void run() {
        Log.d("PocketMike_CMPSC483W", "Running ConnectedThread");

        while (true) {
            try {
                Reader reader = new InputStreamReader(inputStream, "US-ASCII");
                String currentCommand = "";
                int n;

                // Keep reading stream character by character until we see a new line. Once we receive
                // a new line character we know a full command has been sent. Pretty inefficient, but
                // it works for now. A better protocol is to just have the size of the buffer be sent
                // in the header of a buffer, and just read that many bytes. Requires you to keep track
                // of an offset though.
                while ((n = reader.read()) != -1) {
                    char currentChar = (char) n;

                    if (currentChar == '\n') {
                        // If an event handler is given, send the handler a SensorCommand object containing the current command
                        if (this.commandProcessedHandler != null) {
                            //Log.d("PocketMike_CMPSC483W", "Found handler, sending message");
                            if (currentCommand.charAt(0) == 'a') {
                                SensorCommand sensorCommand = new SensorCommand(currentCommand);

                                Message msg = this.commandProcessedHandler.obtainMessage(1, sensorCommand);
                                this.commandProcessedHandler.sendMessage(msg);
                            }
                        } else {
                            Log.d("PocketMike_CMPSC483W", "Could not find handler");
                        }
                        currentCommand = "";
                    } else {
                        currentCommand += currentChar;
                    }
                }
            } catch (IOException e) {
                Log.d("PocketMike_CMPSC483W", "Failed to read input stream");
                break;
            }
        }
    }*/

    public void setCommandProcessedHandler(Handler handler) {
        this.commandProcessedHandler = handler;
    }
}

