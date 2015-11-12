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
    private String sentMessage;
    private final static int REQUEST_ENABLE_BT = 1;

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
        btConnection = new BluetoothConnection("PMike-00");
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
            sentMessage = "bl 1\r";
            btConnection.getConnectThread().getConnectedThread().setCurrentCommand(sentMessage);
            btConnection.sendCommand(sentMessage);
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
            sentMessage = "bl 0\r";
            btConnection.getConnectThread().getConnectedThread().setCurrentCommand(sentMessage);
            btConnection.sendCommand(sentMessage);
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
            //velocityValue can only be 8 characters in hex
            String velocityText = editVelocityText.getText().toString();
            Double velocityValue;
            try {
            velocityValue = Double.parseDouble(velocityText.trim());
            if(unitsSpinner.getSelectedItem().toString().equals("m/s"))
            {
                if(0 <= velocityValue && velocityValue <= 9999)
                {
                    velocityValue = velocityValue*(1000);
                    velocityText = Integer.toHexString(velocityValue.intValue());
                    sentMessage = "ve "+ velocityText + "\r";
                    btConnection.getConnectThread().getConnectedThread().setCurrentCommand(sentMessage);
                    btConnection.sendCommand(sentMessage);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),
                            "Please Enter a valid velocity value.", Toast.LENGTH_SHORT)
                            .show();
                }

            }
            else /*if(unitsSpinner.getSelectedItem().toString().equals("in/us"))*/
            {
                if(0 <= velocityValue && velocityValue < 1)
                {
                    velocityValue = (velocityValue*(1000000))*(25.4/1);
                    velocityText = Integer.toHexString(velocityValue.intValue());
                    sentMessage = "ve "+ velocityText + "\r";
                    btConnection.getConnectThread().getConnectedThread().setCurrentCommand(sentMessage);
                    btConnection.sendCommand(sentMessage);
                }
                else {
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

            btConnection.findDevice();
            btConnection.setCommandProcessedHandler(new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    if (btConnection.getIsBluetoothRunning()) {
                        switch (sentMessage) {
                            //case bl 0 and bl 1 currently wont get called as I don't send
                            //a message in conncetedThread
                            case "bl 0":
                                Log.d("PocketMike_CMPSC483W", "Turn off light command message returned");
                                break;
                            case "bl 1":
                                Log.d("PocketMike_CMPSC483W", "Turn on light command message returned");
                                break;
                            default:
                                Log.d("PocketMike_CMPSC483W", "No command sent from settings menu");
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
