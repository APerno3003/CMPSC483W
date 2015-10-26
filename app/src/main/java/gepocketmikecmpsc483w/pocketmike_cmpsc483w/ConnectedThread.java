package gepocketmikecmpsc483w.pocketmike_cmpsc483w;

/**
 * Created by Anthony on 10/7/2015.
 *
 */
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import java.util.Observable;
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
    private Double pocketMikeReturnNumber;
    private String currentCommand;

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
        //StringBuilder readMessage = new StringBuilder();
        while (true) {
            try {
                bytes = inputStream.read(buffer);
                String readed = new String(buffer, 0, bytes);
                //Log.d("PocketMike_CMPSC483W", readed);
                if (getCurrentCommand().equals("rd")) {
                    try {

                        pocketMikeReturnNumber = Double.parseDouble(readed);
                        //Log.d("PocketMike_CMPSC483W", pocketMikeReturnNumber.toString());
                        Message msg = this.commandProcessedHandler.obtainMessage(1, pocketMikeReturnNumber);
                        //Log.d("PocketMike_CMPSC483W", msg.toString());
                        this.commandProcessedHandler.sendMessage(msg);
                    }
                    catch(NumberFormatException nfe)
                    {
                        Log.d("PocketMike_CMPSC483W", "The string extracted is not a double");
                    }

                }
                else if(getCurrentCommand().equals("un"))
                {
                        String units;
                        String stringNumberThatRepresentsTheUnits = readed.substring(4);
                        Integer someNumber = Integer.valueOf(stringNumberThatRepresentsTheUnits.trim());
                        if(someNumber == 0) {
                            units = "mm";
                            Message msg = this.commandProcessedHandler.obtainMessage(1, units);
                            //Log.d("PocketMike_CMPSC483W", msg.toString());
                            this.commandProcessedHandler.sendMessage(msg);

                        } else if (someNumber == 2) {
                            units = "inch";
                            Message msg = this.commandProcessedHandler.obtainMessage(1, units);
                            //Log.d("PocketMike_CMPSC483W", msg.toString());
                            this.commandProcessedHandler.sendMessage(msg);
                        }

                }

            } catch (IOException e) {
                Log.d("PocketMike_CMPSC483W", "Failed to read PocketMike");
                break;
            }

        }
    }
    public void write(byte[] bytes) {
        try {
            outputStream.write(bytes);
            outputStream.flush();
        } catch (IOException e) {
            Log.d("PocketMike_CMPSC483W", "Failed to write");

        }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) {
            Log.d("PocketMike_CMPSC483W", "Failed to close socket");
             }
    }



    //////////////////////////////////
    /// GETS AND SETS
    /////////////////////////////////
    public void setCommandProcessedHandler(Handler handler) {
        this.commandProcessedHandler = handler;
    }


    public Double getPocketMikeReturnNumber() {
        return pocketMikeReturnNumber;
    }

    public void setPocketMikeReturnNumber(Double pocketMikeReturnNumber) {
        this.pocketMikeReturnNumber = pocketMikeReturnNumber;

    }


    public String getCurrentCommand() {
        return currentCommand;
    }

    public void setCurrentCommand(String currentCommand) {
        this.currentCommand = currentCommand;
    }


}

