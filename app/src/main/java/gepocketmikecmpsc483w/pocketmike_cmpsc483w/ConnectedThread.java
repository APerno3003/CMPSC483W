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
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;

public class ConnectedThread extends Thread {
    public BluetoothSocket socket;
    public Handler commandProcessedHandler;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    final int PocketMikeBufferSize = 24;
    private Double pocketMikeReturnNumber;
    private String currentCommand;
    private String currentDisplayUnits;

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
        Message msg;
        //int begin = 0;
        int bytes;
        String rf = "";
        while (true) try {
            bytes = inputStream.read(buffer);
            String readed = new String(buffer, 0, bytes);
            Log.d("PocketMike_CMPSC483W", readed);
            switch (getCurrentCommand()) {
                case "rd":
                    try {
                        pocketMikeReturnNumber = Double.parseDouble(readed.trim());
                        msg = this.commandProcessedHandler.obtainMessage(1, pocketMikeReturnNumber);
                        this.commandProcessedHandler.sendMessage(msg);
                    } catch (NumberFormatException nfe) {
                        Log.d("PocketMike_CMPSC483W", "The string extracted is not a double");
                    }
                    break;
                case "un":
                    try {
                        String units;
                        Integer unitsNumber = Integer.valueOf(readed.trim());
                        if (unitsNumber == 0) {
                            units = "mm";
                            msg = this.commandProcessedHandler.obtainMessage(1, units);
                            this.commandProcessedHandler.sendMessage(msg);

                        } else if (unitsNumber == 2) {
                            units = "inch";
                            msg = this.commandProcessedHandler.obtainMessage(1, units);
                            this.commandProcessedHandler.sendMessage(msg);
                        }
                    } catch (NumberFormatException nfe) {
                        Log.d("PocketMike_CMPSC483W", "The units couldn't be extracted try again");
                    }
                    break;
                case "ve":
                    try {
                        String hexVelocity = readed.trim();
                        String convertedVelocity;
                        Integer velocity;
                        //convert hex value to interger
                        velocity = Integer.parseInt(hexVelocity.trim(), 16);
                        //Log.d("PocketMike_CMPSC483W",velocity.toString());
                        //convert the velocity value to either m/s or in/us based on current units
                        if(getCurrentDisplayUnits().equals("mm"))
                        {
                            velocity = velocity/1000;
                            convertedVelocity = velocity.toString().trim() + " m/s";
                        }
                        else /*if(getCurrentDisplayUnits() == "inch")*/
                        {
                            Double tempVelocity = velocity.doubleValue();
                            tempVelocity = tempVelocity*(1.0/25.4)*(1.0/1000000.0);
                            tempVelocity = roundToFourDecimals(tempVelocity);
                            convertedVelocity = tempVelocity.toString().trim() + " in/us";
                        }

                        msg = this.commandProcessedHandler.obtainMessage(1, convertedVelocity);
                        this.commandProcessedHandler.sendMessage(msg);
                    } catch (NumberFormatException e) {
                        Log.d("PocketMike_CMPSC483W", "The value is not a number");
                    }
                    break;
                //bl 0 and bl 1 don't return anything so we can just leave them alone
                case "bl 0":
                    msg = this.commandProcessedHandler.obtainMessage(1, "Light was turned off");
                    this.commandProcessedHandler.sendMessage(msg);
                    Log.d("PocketMike_CMPSC483W", "Light was turned off");
                    break;
                case "bl 1":
                    msg = this.commandProcessedHandler.obtainMessage(1, "Light was turned on");
                    this.commandProcessedHandler.sendMessage(msg);
                    Log.d("PocketMike_CMPSC483W", "Light was turned on");
                    break;
                case "un 00":
                    msg = this.commandProcessedHandler.obtainMessage(1, "unit 00");
                    this.commandProcessedHandler.sendMessage(msg);
                    Log.d("PocketMike_CMPSC483W", "connectedThread units were changed 00");
                    break;
                case "un 01":
                    msg = this.commandProcessedHandler.obtainMessage(1, "unit 01");
                    this.commandProcessedHandler.sendMessage(msg);
                    Log.d("PocketMike_CMPSC483W", "connectedThread units were changed 01");
                    break;
                case "e0":
                    msg = this.commandProcessedHandler.obtainMessage(1, "Echo");
                    this.commandProcessedHandler.sendMessage(msg);
                    Log.d("PocketMike_CMPSC483W", "Echo turned off 1");
                    break;
                case "md 0":
                    msg = this.commandProcessedHandler.obtainMessage(1, "md");
                    this.commandProcessedHandler.sendMessage(msg);
                    Log.d("PocketMike_CMPSC483W", "Mode changed to 0");
                    break;
                case "velocityChanged":
                    msg = this.commandProcessedHandler.obtainMessage(1, "ve changed");
                    this.commandProcessedHandler.sendMessage(msg);
                    Log.d("PocketMike_CMPSC483W", "velcoityChanged");
                    break;
                case "rf":
                    try{
                        rf +=readed;
                        if(rf.trim().length() == 2) {
                            Integer couplingStatus = Integer.valueOf(readed.trim());
                            msg = this.commandProcessedHandler.obtainMessage(1, couplingStatus.toString().trim());
                            this.commandProcessedHandler.sendMessage(msg);
                            rf = "";
                        }
                    } catch (NumberFormatException nfe) {
                        Log.d("PocketMike_CMPSC483W", "The string extracted is not a vaild number");
                    }
                    Log.d("PocketMike_CMPSC483W", "Coupling status read");
                    break;
                default:
                    Log.d("PocketMike_CMPSC483W", "No Message read");

            }

        } catch (IOException e) {
            Log.d("PocketMike_CMPSC483W", "Failed to read PocketMike");
            break;
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


    //round the precision to one interger and 4 decimal points
    Double roundToFourDecimals(Double d) {
        DecimalFormat fourDForm = new DecimalFormat("#.####");
        return Double.valueOf(fourDForm.format(d));
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

    public String getCurrentDisplayUnits() {
        return currentDisplayUnits;
    }

    public void setCurrentDisplayUnits(String currentDisplayUnits) {
        this.currentDisplayUnits = currentDisplayUnits;
    }
}

