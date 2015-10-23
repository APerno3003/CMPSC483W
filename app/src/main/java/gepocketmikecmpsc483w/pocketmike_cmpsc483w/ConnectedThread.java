package gepocketmikecmpsc483w.pocketmike_cmpsc483w;

/**
 * Created by Anthony on 10/7/2015.
 */
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

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
    final int handlerState = 0;
    final int PocketMikeBufferSize = 24;

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

    public void run() {
        byte[] buffer = new byte[PocketMikeBufferSize];
        //int begin = 0;
        int bytes;
        StringBuilder readMessage = new StringBuilder();
        while (true) {
            try {
                bytes = inputStream.read(buffer);
                String readed = new String(buffer, 0, bytes);
                Log.d("PocketMike_CMPSC483W", readed.toString());
                //readMessage.append(readed);
                //Log.d("PocketMike_CMPSC483W", readMessage.toString());


            } catch (IOException e) {
                break;
            }
        }
    }
    public void write(byte[] bytes) {
        try {
            outputStream.write(bytes);
            outputStream.flush();
        } catch (IOException e) { }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) { }
    }

    public void setCommandProcessedHandler(Handler handler) {
        this.commandProcessedHandler = handler;
    }
}

