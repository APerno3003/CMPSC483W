package gepocketmikecmpsc483w.pocketmike_cmpsc483w;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import java.text.DecimalFormat;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import gepocketmikecmpsc483w.pocketmike_cmpsc483w.BluetoothConnection;

public class Mode1Activity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // Bluetooth
    private final static int defaultLatitude = 360;
    private final static int defaultLongitude = 360;
    private final static int REQUEST_ENABLE_BT = 1;
    private double currentValueOnScreen = 0.00;
    private BluetoothConnection btConnection;

    private Button Mode1BackButton;
    private Button GetValueOnPocketMikeScreenButton; //1 mm to 250 mm (0.040 inch to 9.999 inch) vaild ranges for pocketMike
    private Button ChangeUnitsButton;
    private Button UpdateCurrentLocationButton;
    private TextView MeasurementNumbersText;
    private TextView unitsText;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private TextView valueOfLatitude;
    private TextView valueOfLongitude;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    public static final String TAG = Mode1Activity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode1);

        //Connect Buttons in layout to buttons in java file so we can listen to them
        Mode1BackButton = (Button) findViewById(R.id.Mode1BackButton);
        Mode1BackButton.setOnClickListener(this);

        GetValueOnPocketMikeScreenButton = (Button) findViewById(R.id.GetValueOnPocketMikeScreenButton);
        GetValueOnPocketMikeScreenButton.setOnClickListener(this);

        ChangeUnitsButton = (Button) findViewById(R.id.ChangeUnitsButton);
        ChangeUnitsButton.setOnClickListener(this);

        UpdateCurrentLocationButton = (Button) findViewById(R.id.UpdateCurrentLocationButton);
        UpdateCurrentLocationButton.setOnClickListener(this);

        //connect Text in layout to text in java file so we can edit them
        MeasurementNumbersText = (TextView) findViewById(R.id.MeasurementNumbersText);
        unitsText = (TextView) findViewById(R.id.unitsText);
        valueOfLatitude = (TextView) findViewById(R.id.valueOfLatitude);
        valueOfLongitude = (TextView) findViewById(R.id.valueOfLongitude);

        // First we need to check availability of play services
        if (checkPlayServices()) {
            // Building the GoogleApi client

            buildGoogleApiClient();

        }

        //Run bluetooth stuff
        btConnection = new BluetoothConnection("HC-06");
        if (btConnection.getAdapter() != null) {

            // Check if bluetooth is enabled, if not ask user to enable it.
            if (!btConnection.getAdapter().isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                //startBluetooth();
                Log.d("PocketMike_CMPSC483W", "Bluetooth is on");
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

    private void hideComments()
    {
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
    }

    //if pressed brings you back to the main activity
    private void Mode1BackButtonOnClick() {
        finish();
    }

    //Allows that user to change the current units the measurement is being taken in
    private void ChangeUnitsButtonOnClick() {
        if ((unitsText.getText().toString()).equals("mm")) {
            unitsText.setText("in");
            MeasurementNumbersText.setText("0.000");
        } else {

            unitsText.setText("mm");
            MeasurementNumbersText.setText("000");
        }
    }

    //right now it generates a random number that is supposed to be vaild pocketmike thicknessdata
    private void GetValueOnPocketMikeScreenButtonOnClick() {
        currentValueOnScreen = 0;
        if (((unitsText.getText().toString())).equals("mm")) {
            int upper = 250;
            int lower = 1;
            currentValueOnScreen = (int) (Math.random() * (upper - lower)) + lower;
            MeasurementNumbersText.setText(String.valueOf((int) currentValueOnScreen));
        } else {
            int upper = 9;
            int lower = 0;
            int integerValue = (int) (Math.random() * (upper - lower)) + lower;
            currentValueOnScreen = Math.random();
            if (currentValueOnScreen < 0.04 && integerValue == 0) {
                currentValueOnScreen = 0.04;
            }
            currentValueOnScreen = integerValue + currentValueOnScreen;
            MeasurementNumbersText.setText(String.valueOf(roundToFourDecimals(currentValueOnScreen)));
        }
    }


    //round the precision to one interger and 4 decimal points
    double roundToFourDecimals(double d) {
        DecimalFormat fourDForm = new DecimalFormat("#.####");
        return Double.valueOf(fourDForm.format(d));
    }

    //is the action listener for all the buttons and calls correct function based on case
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
            case R.id.UpdateCurrentLocationButton:
                UpdateCurrentLocationButtonOnClick();
                break;
        }
    }

    private void UpdateCurrentLocationButtonOnClick() {
        findAndDisplayLocation();
    }

    //Trys to find your currentLocation and if it finds it displays it
    private void findAndDisplayLocation()
    {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        Log.d("PocketMike_CMPSC483W", "Trying to Find Current Location");
        if (mLastLocation != null) {
            valueOfLatitude.setText(String.valueOf(mLastLocation.getLatitude()));
            valueOfLongitude.setText(String.valueOf(mLastLocation.getLongitude()));
            Log.d("PocketMike_CMPSC483W", "Current Location Found");
        }
        else
        {
            Toast.makeText(getApplicationContext(),
                    "Current Location data cannot be found. Latitude and longtiude will be set to the default value of 360.", Toast.LENGTH_LONG)
                    .show();
            valueOfLatitude.setText(String.valueOf(defaultLatitude));
            valueOfLongitude.setText(String.valueOf(defaultLongitude));
            Log.d("PocketMike_CMPSC483W", "Couldn't Find Current Location");
        }

    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        findAndDisplayLocation();

    }
    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


}

