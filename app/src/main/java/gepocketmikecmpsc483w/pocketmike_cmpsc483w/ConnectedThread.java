package gepocketmikecmpsc483w.pocketmike_cmpsc483w;

/**
 * Created by Anthony on 10/7/2015.
 *
 */
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ConnectedThread extends Thread {
    public BluetoothSocket socket;
    public Handler commandProcessedHandler;
    private final InputStream inputStream;
    private final OutputStream outputStream;
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

    //allows you to read what comes back from the pocketMike
    //this function is always running once the bluetooth starts up
    public void run() {
        byte[] buffer = new byte[PocketMikeBufferSize];
        //int begin = 0;
        int bytes;
        while (true) {
            try {
                bytes = inputStream.read(buffer);
                String readed = new String(buffer, 0, bytes);
                Log.d("PocketMike_CMPSC483W", readed);
                switch (getCurrentCommand())
                {
                    //rd is returned as 2 separte strings one says rd the other says the value it read
                    //because of this rd has to be handle in that way so the 1st time the code runs it throws away the rd string
                    //the second time it runs it converts the value that it receives to a double
                    case "rd":
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
                        break;
                    //un comes back as a string of 5 chars 'un XX' where XX represent numbers
                    //because of this you must get the last 2 characters to determine what un actually returned
                    case "un":
                        String units = readed.substring(3);
                        Integer someNumber = Integer.valueOf(units.trim());
                        if(someNumber == 0) {
                            units = "mm";
                            Message msg = this.commandProcessedHandler.obtainMessage(1, units);
                            this.commandProcessedHandler.sendMessage(msg);

                        } else if (someNumber == 2) {
                            units = "inch";
                            Message msg = this.commandProcessedHandler.obtainMessage(1, units);
                            this.commandProcessedHandler.sendMessage(msg);
                        }
                        break;
                    //bl 0 and bl 1 don't return anything so we can just leave them alone
                    case "bl 0":
                    case "bl 1":
                        Log.d("PocketMike_CMPSC483W", "Light was turn on/off");
                        break;
                    case "un 00":
                    case "un 01":
                        Log.d("PocketMike_CMPSC483W", "Obtained Units");
                        break;
                    default:
                        Log.d("PocketMike_CMPSC483W", "No Message read");

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

