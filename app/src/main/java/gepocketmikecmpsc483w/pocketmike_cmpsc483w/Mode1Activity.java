package gepocketmikecmpsc483w.pocketmike_cmpsc483w;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Context;

import java.text.DecimalFormat;
import java.util.Calendar;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import gepocketmikecmpsc483w.pocketmike_cmpsc483w.BluetoothConnection;

public class Mode1Activity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final static int defaultLatitude = 360;
    private final static int defaultLongitude = 360;
    private final static int REQUEST_ENABLE_BT = 1;
    private double currentValueOnScreen = 0.00;
    private BluetoothConnection btConnection;
    private BluetoothAdapter btAdapter;

    private String pocketMikeName = "PMike-00";

    private Button Mode1BackButton;
    private Button GetValueOnPocketMikeScreenButton; //1 mm to 250 mm (0.040 inch to 9.999 inch) vaild ranges for pocketMike
    private Button ChangeUnitsButton;
    private Button UpdateCurrentLocationButton;
    private Button StoreDataButton;
    private TextView MeasurementNumbersText;
    private TextView unitsText;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private TextView valueOfLatitude;
    private TextView valueOfLongitude;
    private TextView setVelocityTextView;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private boolean isThreadFinished = true;

    //Database from the DBAdapter class that was created
    DBAdapter myDB;
    private String[] data = null; //Used to store data into the database.

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

        StoreDataButton = (Button) findViewById(R.id.StoreDataButton);
        openDB(); // Open the Database to be able to store the data
        StoreDataButton.setOnClickListener(this);

        //connect Text in layout to text in java file so we can edit them
        MeasurementNumbersText = (TextView) findViewById(R.id.MeasurementNumbersText);
        unitsText = (TextView) findViewById(R.id.unitsText);
        valueOfLatitude = (TextView) findViewById(R.id.valueOfLatitude);
        valueOfLongitude = (TextView) findViewById(R.id.valueOfLongitude);
        setVelocityTextView = (TextView) findViewById(R.id.setVelocityTextView);

        // First we need to check availability of play services
        Log.d("PocketMike_CMPSC483W", "Trying to connect to Google Play Store");
        if (checkPlayServices()) {
            // Building the GoogleApi client
            Log.d("PocketMike_CMPSC483W", "Successfully connected to Google Play Store");

            buildGoogleApiClient();

        }

        //Start up Bluetooth code
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


    //if bluetooth is on close the connection and then return to mainActivity
    private void Mode1BackButtonOnClick() {
        if(btConnection.getIsBluetoothRunning())
        {
            btConnection.closeBluetoothConnection();
        }
        myDB.close();
        finish();
    }

    //Allows that user to change the current units the measurement is being taken in
    private void ChangeUnitsButtonOnClick() {
        if (btConnection.getIsBluetoothRunning()) {
            if(btConnection.getIsEchoOff())
            {
                if(isThreadFinished) {
                    isThreadFinished = false;
                    if ((unitsText.getText().toString()).equals("mm")) {

                        btConnection.setConnectedThreadCommand("un 01");
                        btConnection.sendCommand("un 01\r");
                    } else {
                        btConnection.setConnectedThreadCommand("un 00");
                        btConnection.sendCommand("un 00\r");
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
                btConnection.turnOffEcho();
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(),
                    "Bluetooth is not currently running", Toast.LENGTH_SHORT)
                    .show();
        }
        //Places the units into the data array for it to be stored in the database
        if(data == null){
            data = new String[5];
            data[0] = String.valueOf(roundToFourDecimals(currentValueOnScreen));
            data[1] = unitsText.getText().toString();
        }
        else{
            data[0] = String.valueOf(roundToFourDecimals(currentValueOnScreen));
            data[1] = unitsText.getText().toString();
        }

    }

    //puts data into the database
    private void StoreDataButtonOnClick()
    {
        Calendar c = Calendar.getInstance();
        String date = (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.DAY_OF_MONTH) + "/"  + c.get(Calendar.YEAR);
        String time = c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND);

        data[0] = MeasurementNumbersText.getText().toString();
        data[1] = unitsText.getText().toString();
        data[2] = setVelocityTextView.getText().toString();

        //Places data into database.
        myDB.insertRow(data[0], data[1], data[2], data[3], data[4], date, time);

        Toast.makeText(getApplicationContext(),
                "Stored data successfully", Toast.LENGTH_SHORT)
                .show();
    }

    //Gets the value of what is currently on the pocketMike's screen
    private void GetValueOnPocketMikeScreenButtonOnClick() {

        Log.d("PocketMike_CMPSC483W", "Mode1Acitivity GetPocketButtonClick");
        if(btConnection.getIsBluetoothRunning())
        {
            if(btConnection.getIsEchoOff()) {
                if(isThreadFinished) {
                    isThreadFinished = false;
                    btConnection.setConnectedThreadCommand("md 0");
                    btConnection.sendCommand("md 0\r");
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
            //Places the units into the data array for it to be stored in the database
            if(data == null){
                data = new String[5];
                data[0] = String.valueOf(roundToFourDecimals(currentValueOnScreen));
                data[1] = unitsText.getText().toString();
            }
            else{
                data[0] = String.valueOf(roundToFourDecimals(currentValueOnScreen));
                data[1] = unitsText.getText().toString();
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(),
                    "Bluetooth is not currently running", Toast.LENGTH_SHORT)
                    .show();
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
            case R.id.StoreDataButton:
                StoreDataButtonOnClick();
                break;
        }
    }


    //starts the bluetooth and sets up handler for when you want to send and recevice messages
    public void startBluetooth() {

        btConnection.findDevice(); //for this line of code to work properly the phone and device must be synced atleast one time prior to using the app
        btConnection.setCommandProcessedHandler(new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (btConnection.getIsBluetoothRunning()) {

                    switch (btConnection.getConnectedThreadCommand()) {
                        case "md 0":
                            btConnection.setConnectedThreadCommand("rf");
                            btConnection.sendCommand("rf\r");
                            break;
                        case "rf":
                            if(msg.obj.toString().equals("6"))
                            {
                                btConnection.setConnectedThreadCommand("rd");
                                btConnection.sendCommand("rd\r");
                            }
                            else
                            {
                                isThreadFinished = true;
                                Toast.makeText(getApplicationContext(),
                                        "Please try again invalid coupling status", Toast.LENGTH_SHORT)
                                        .show();
                            }
                            break;
                        case "rd":
                            MeasurementNumbersText.setText(msg.obj.toString());
                            btConnection.setConnectedThreadCommand("un");
                            btConnection.sendCommand("un\r");
                            break;
                        case "un":
                            unitsText.setText(msg.obj.toString());
                            btConnection.getConnectThread().getConnectedThread().setCurrentDisplayUnits((unitsText.getText().toString()));
                            btConnection.setConnectedThreadCommand("ve");
                            btConnection.sendCommand("ve\r");
                            break;
                        case "ve":
                            setVelocityTextView.setText(msg.obj.toString());
                            isThreadFinished = true;
                            break;
                        case "un 00":
                            btConnection.setConnectedThreadCommand("rd");
                            btConnection.sendCommand("rd\r");
                            break;
                        case "un 01":
                            btConnection.setConnectedThreadCommand("rd");
                            btConnection.sendCommand("rd\r");
                            break;
                        case "e0":
                            btConnection.setIsEchoOff(true);
                            Log.d("PocketMike_CMPSC483W", "Echo turned off 2");
                            Toast.makeText(getApplicationContext(),
                                    "Please press the button again", Toast.LENGTH_SHORT)
                                    .show();
                            break;
                        default:
                            Log.d("PocketMike_CMPSC483W", "No command sent");
                            isThreadFinished = true;
                            btConnection.setConnectedThreadCommand("XX"); // NULL COMMAND
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

            //See if data was already initialized to be stored, if not set an array to hold the
            //data to be placed into the array for the location of the latitude and longitude.
            if(data == null){
                data = new String[5];
                data[3] = String.valueOf(mLastLocation.getLatitude());
                data[4] = String.valueOf(mLastLocation.getLongitude());
            }
            else{
                data[3] = String.valueOf(mLastLocation.getLatitude());
                data[4] = String.valueOf(mLastLocation.getLongitude());
            }

        }
        else
        {
            Toast.makeText(getApplicationContext(),
                    "Current Location data cannot be found. Latitude and longtiude will be set to the default value of 360.", Toast.LENGTH_SHORT)
                    .show();
            valueOfLatitude.setText(String.valueOf(defaultLatitude));
            valueOfLongitude.setText(String.valueOf(defaultLongitude));
            Log.d("PocketMike_CMPSC483W", "Couldn't Find Current Location");

            //Since the current location could not be found place the default latitude and longitude values
            //into the database
            if(data == null){
                data = new String[6];
                data[3] = String.valueOf(defaultLatitude);
                data[4] = String.valueOf(defaultLongitude);
            }
            else{
                data[3] = String.valueOf(defaultLatitude);
                data[4] = String.valueOf(defaultLongitude);
            }
        }

    }

    //builds google API client so we can use google maps api
    protected synchronized void buildGoogleApiClient() {
        Log.d("PocketMike_CMPSC483W", "Building Google API Client");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


    }

    //checks to see if google play services are running
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check if we're responding to enable bluetooth dialog
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                //btConnection.startBluetooth();
                startBluetooth();
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

    //Used to allow the database to open from our DBAdapter class
    private void openDB(){
        myDB = new DBAdapter(this);
        myDB.open();
    }

}

