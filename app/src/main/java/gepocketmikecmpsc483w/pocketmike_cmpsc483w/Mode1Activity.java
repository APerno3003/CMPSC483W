package gepocketmikecmpsc483w.pocketmike_cmpsc483w;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

import gepocketmikecmpsc483w.pocketmike_cmpsc483w.BluetoothConnection;

public class Mode1Activity extends AppCompatActivity implements View.OnClickListener {

    // Bluetooth
    private final static int REQUEST_ENABLE_BT = 1;
    private double currentValueOnScreen = 0.00;
    BluetoothConnection btConnection;

    Button Mode1BackButton;
    Button GetValueOnPocketMikeScreenButton; //1 mm to 250 mm (0.040 inch to 9.999 inch) vaild ranges for pocketMike
    Button ChangeUnitsButton;
    TextView MeasurementNumbersText;
    TextView unitsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mode1);
        Mode1BackButton = (Button) findViewById(R.id.Mode1BackButton);
        Mode1BackButton.setOnClickListener(this);

        GetValueOnPocketMikeScreenButton = (Button) findViewById(R.id.GetValueOnPocketMikeScreenButton);
        GetValueOnPocketMikeScreenButton.setOnClickListener(this);

        ChangeUnitsButton = (Button) findViewById(R.id.ChangeUnitsButton);
        ChangeUnitsButton.setOnClickListener(this);

        MeasurementNumbersText = (TextView) findViewById(R.id.MeasurementNumbersText);
        unitsText = (TextView) findViewById(R.id.unitsText);

        btConnection = new BluetoothConnection("HC-06");
        if (btConnection.getAdapter() != null) {

            // Check if bluetooth is enabled, if not ask user to enable it.
            if (!btConnection.getAdapter().isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                //startBluetooth();
            }

        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mode1, menu);
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

   /* private void enableBluetooth()
    {
        //check if device supports Bluetooth
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            return;
        }

        //if device supports Bluetooth
        //Check if Bluetooth is enabled
        //if it isn't enabled enable it
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        //Check if the Bluetooth device you want to access is already known to the phone
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            // Add the name and address to an array adapter to show in a ListView
            for (BluetoothDevice device : pairedDevices) {
                mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }

        //if the Bluetooth device isn't know try to discover it




    }*/


    private void Mode1BackButtonOnClick() {
        finish();
    }

    private void ChangeUnitsButtonOnClick() {
        if((unitsText.getText().toString()).equals("mm"))
        {
            unitsText.setText("in");
        }
        else
        {

            unitsText.setText("mm");
        }
    }

    private void GetValueOnPocketMikeScreenButtonOnClick() {

        if(((unitsText.getText().toString())).equals("mm")) {
            int upper = 250;
            int lower = 0;
            currentValueOnScreen = (int) (Math.random() * (upper - lower)) + lower;
            MeasurementNumbersText.setText("" + (int) currentValueOnScreen);
        }
        else
        {
            int upper = 9;
            int lower = 0;
            int integerValue = (int) (Math.random() * (upper - lower)) + lower;
            currentValueOnScreen = Math.random() + 0.04;
            currentValueOnScreen = currentValueOnScreen * 1000;
            currentValueOnScreen = (int) currentValueOnScreen;
            currentValueOnScreen = currentValueOnScreen / 1000;
            currentValueOnScreen = integerValue + currentValueOnScreen;
            MeasurementNumbersText.setText("" + currentValueOnScreen);

        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Mode1BackButton:
                Mode1BackButtonOnClick();
                break;
            case R.id.GetValueOnPocketMikeScreenButton:
                GetValueOnPocketMikeScreenButtonOnClick();
                break;
            case R.id.ChangeUnitsButton:
                ChangeUnitsButtonOnClick();
                break;
        }
    }




}
