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
    final int MESSAGE_READ = 9999;

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
        byte[] buffer = new byte[512];
        int begin = 0;
        int bytes;
        StringBuilder readMessage = new StringBuilder();
        while (true) {
            try {
                //inputStream.reset();
                bytes = inputStream.read(buffer);
                String readed = new String(buffer, 0, bytes);
                readMessage.append(readed);
                Log.d("WSDFKALSFKAD", readMessage.toString());
               /* if (readed.contains("\n")) {
                    Log.d("Alice","MADness");
                    this.commandProcessedHandler.obtainMessage(2, bytes, -1, readMessage.toString()).sendToTarget();
                    readMessage.setLength(0);
                }*/


               /* bytes += inputStream.read(buffer, bytes, buffer.length - bytes);
                String alice = new String(buffer);
                Log.d("ALICE", alice);
                for(int i = begin; i < bytes; i++) {
                    if(buffer[i] == "#".getBytes()[0]) {
                        this.commandProcessedHandler.obtainMessage(1, begin, i, buffer).sendToTarget();
                        begin = i + 1;
                        if(i == bytes - 1) {
                            bytes = 0;
                            begin = 0;
                        }
                    }

                }*/

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

