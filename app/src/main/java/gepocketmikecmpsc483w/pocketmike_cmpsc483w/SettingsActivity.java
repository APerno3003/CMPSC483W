package gepocketmikecmpsc483w.pocketmike_cmpsc483w;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.bluetooth.BluetoothAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import gepocketmikecmpsc483w.pocketmike_cmpsc483w.BluetoothConnection;


public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{
    Button SettingsBackButton;
    Button onButton;
    Button offButton;
    Button setVelocityButton;
    EditText editVelocityText;
    Spinner unitsSpinner;
    private BluetoothConnection btConnection;
    private boolean isThreadFinished = true;
    private final static int REQUEST_ENABLE_BT = 1;
    private String pocketMikeName = "PMike-00";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        editVelocityText = (EditText) findViewById(R.id.editVelocityText);

        SettingsBackButton = (Button) findViewById(R.id.SettingsBackButton);
        SettingsBackButton.setOnClickListener(this);

        onButton = (Button) findViewById(R.id.onButton);
        onButton.setOnClickListener(this);

        offButton = (Button) findViewById(R.id.offButton);
        offButton.setOnClickListener(this);

        setVelocityButton = (Button) findViewById(R.id.setVelocityButton);
        setVelocityButton.setOnClickListener(this);

        unitsSpinner = (Spinner) findViewById(R.id.unitsSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.velocityUnits, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitsSpinner.setAdapter(adapter);


        //Run bluetooth stuff
        btConnection = new BluetoothConnection(pocketMikeName);
        Log.d("PocketMike_CMPSC483W", "Running Bluetooth stuff");
        // btConnection = new BluetoothConnection();
        if (btConnection.getAdapter() != null) {
            Log.d("PocketMike_CMPSC483W", "1");
            // Check if bluetooth is enabled, if not ask user to enable it.
            if (!btConnection.getAdapter().isEnabled()) {
                Log.d("PocketMike_CMPSC483W", "Start Bluetooth1");
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                //btConnection.startBluetooth();
                startBluetooth();
                Log.d("PocketMike_CMPSC483W", "Start Bluetooth2");

            }
            // btConnection.findDevice();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //if bluetooth is on close the connection and then return to mainActivity
    private void SettingsBackButtonOnClick() {
        if(btConnection.getIsBluetoothRunning())
        {
            btConnection.closeBluetoothConnection();
        }
        finish();
    }

    //sends the command to turn on the light on the pocketMike
    private void OnButtonOnClick() {
        if (btConnection.getIsBluetoothRunning()) {
            if (btConnection.getIsEchoOff()) {
                if(isThreadFinished) {
                    isThreadFinished = false;
                    String sentMessage = "bl 1\r";
                    btConnection.setConnectedThreadCommand("bl 1");
                    btConnection.sendCommand(sentMessage);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),
                            "Please wait until process has finished", Toast.LENGTH_SHORT)
                            .show();
                }
            } else {
                btConnection.turnOffEcho();

            }
        }
        else
        {
            Toast.makeText(getApplicationContext(),
                    "Bluetooth is not currently running", Toast.LENGTH_SHORT)
                    .show();

        }
    }

    //sends the command to turn off the light on the pocketMike
    private void OffButtonOnClick() {
        if (btConnection.getIsBluetoothRunning()) {
            if(btConnection.getIsEchoOff())
            {
                if(isThreadFinished) {
                    isThreadFinished = false;
                    String sentMessage = "bl 0\r";
                    btConnection.setConnectedThreadCommand("bl 0");
                    btConnection.sendCommand(sentMessage);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),
                            "Please wait until process has finished", Toast.LENGTH_SHORT)
                            .show();
                }
            }
            else
            {
                btConnection.turnOffEcho();

            }
        }
        else
        {
            Toast.makeText(getApplicationContext(),
                    "Bluetooth is not currently running", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void setVelocityButtonOnClick(){
        if(btConnection.getIsBluetoothRunning())
        {
            if(isThreadFinished) {
                isThreadFinished = false;

                //velocityValue can only be 8 characters in hex
                String velocityText = editVelocityText.getText().toString();
                Double velocityValue;
                try {
                    velocityValue = Double.parseDouble(velocityText.trim());
                    if (unitsSpinner.getSelectedItem().toString().equals("m/s")) {
                        if (0 <= velocityValue && velocityValue <= 9999) {
                            velocityValue = velocityValue * (1000);
                            velocityText = Integer.toHexString(velocityValue.intValue());
                            String sentMessage = "ve " + velocityText + "\r";
                            btConnection.setConnectedThreadCommand("velocityChanged");
                            btConnection.sendCommand(sentMessage);
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Please Enter a valid velocity value.", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    } else /*if(unitsSpinner.getSelectedItem().toString().equals("in/us"))*/ {
                        if (0 <= velocityValue && velocityValue < 1) {
                            velocityValue = (velocityValue * (1000000)) * (25.4 / 1);
                            velocityText = Integer.toHexString(velocityValue.intValue());
                            String sentMessage = "ve " + velocityText + "\r";
                            btConnection.setConnectedThreadCommand("velocityChanged");
                            btConnection.sendCommand(sentMessage);
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Please Enter a valid velocity value.", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }

                } catch (NumberFormatException e) {
                    Log.d("PocketMike_CMPSC483W", "The value is not a number");
                }
            }
            else
            {
                Toast.makeText(getApplicationContext(),
                        "Please wait until process has finished", Toast.LENGTH_SHORT)
                        .show();
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(),
                    "Bluetooth is not currently running", Toast.LENGTH_SHORT)
                    .show();

        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.SettingsBackButton:
                SettingsBackButtonOnClick();
                break;
            case R.id.onButton:
                OnButtonOnClick();
                break;
            case R.id.offButton:
                OffButtonOnClick();
                break;
            case R.id.setVelocityButton:
                setVelocityButtonOnClick();
                break;

        }
    }

    public void startBluetooth() {

        btConnection.findDevice(); //for this line of code to work properly the phone and device must be synced atleast one time prior to using the app
        btConnection.setCommandProcessedHandler(new Handler(Looper.getMainLooper()) {
            //When a message is recieved from connectedThread below is how to handle it
            @Override
            public void handleMessage(Message msg) {
                if (btConnection.getIsBluetoothRunning()) {
                    switch (btConnection.getConnectedThreadCommand()) {
                        case "bl 0":
                            isThreadFinished = true;
                            Log.d("PocketMike_CMPSC483W", "Turn off light command message returned");
                            break;
                        case "bl 1":
                            isThreadFinished = true;
                            Log.d("PocketMike_CMPSC483W", "Turn on light command message returned");
                            break;
                        case "velocityChanged":
                            isThreadFinished = true;
                            Log.d("PocketMike_CMPSC483W", "The velocity has been changed");
                            break;
                        case "e0":
                            btConnection.setIsEchoOff(true);
                            Log.d("PocketMike_CMPSC483W", "Echo turned off");
                            Toast.makeText(getApplicationContext(),
                                    "Please press the button again", Toast.LENGTH_SHORT)
                                    .show();
                            break;
                        default:
                            Log.d("PocketMike_CMPSC483W", "No command returned from settings menu");
                            btConnection.setConnectedThreadCommand("XX"); // NULL COMMAND
                            isThreadFinished = true;

                    }

                } else {
                    Toast.makeText(getApplicationContext(),
                            "Bluetooth is not currently running", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        btConnection.startReading();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check if we're responding to enable bluetooth dialog
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                startBluetooth();
            }
        }
    }

}
