package gepocketmikecmpsc483w.pocketmike_cmpsc483w;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.inputmethodservice.Keyboard;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Mode2Activity extends AppCompatActivity implements View.OnClickListener{

    Button Mode2BackButton;
    Button DeleteButton;
    Button ExportButton;

    //Database we are going to populate
    DBAdapter myDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode2);

        Mode2BackButton = (Button) findViewById(R.id.Mode2BackButton);
        DeleteButton = (Button) findViewById(R.id.DeleteButton);
        ExportButton = (Button) findViewById(R.id.ExportButton);

        //Checks to see if one of the buttons was pressed
        Mode2BackButton.setOnClickListener(this);
        DeleteButton.setOnClickListener(this);
        ExportButton.setOnClickListener(this);

        //Open the database to be able to display the data
        openDB();

        //Displays the current data for when the user wants to view the database
        displayData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mode2, menu);
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

    private void Mode2BackButtonOnClick() {
        myDb.close();
        finish();
    }

    //Deletes the entire database
    public void DeleteButtonOnClick(){

        createDiag();

    }

    private void createDiag() {
        AlertDialog.Builder alrtDiag = new AlertDialog.Builder(this);
        alrtDiag.setMessage("Are you sure you wan to delete? (This will delete all data)");
        alrtDiag.setCancelable(false);
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                switch(which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //This means the yes button was clicked
                        //Delete the database and update the display
                        myDb.deleteAll();
                        Toast.makeText(getApplicationContext(),"Data Deleted", Toast.LENGTH_SHORT)
                                .show();
                        displayData();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //The no button was clicked and we do not want to do anything
                        break;
                }
            }
        };


        //Creates the yes and no button on the button for the Alert Dialog
        alrtDiag.setPositiveButton("Yes", dialogClickListener).setNegativeButton("No",dialogClickListener).show();
    }

    //Will display the row number, the thickness, the unit of the thickness, the velocity, the velocity unit, the date and time
    //Latitude and longitude are stored but will be used to find the location of the site
    private void displayData(){
        Cursor cursor = myDb.getAllRows();
        String[] fromFieldNames = new String[] {DBAdapter.KEY_ROWID, DBAdapter.KEY_THICKNESS, DBAdapter.KEY_UNIT,
                DBAdapter.KEY_VELOCITY, DBAdapter.KEY_DATE, DBAdapter.KEY_TIME};
        int toViewIDs [] = new int[] {R.id.RowTextView, R.id.ThicknessTextView, R.id.UnitTextView, R.id.VelocityTextView,
                R.id.DateTextView, R.id.TimeTextView};
        //Allows us to be able to map the columns to for the cursor to the list view.
        SimpleCursorAdapter myCursorAdapter = new SimpleCursorAdapter(getBaseContext(), R.layout.item_layout, cursor, fromFieldNames, toViewIDs, 0);
        ListView myList = (ListView) findViewById(R.id.listView);
        myList.setAdapter(myCursorAdapter);
    }

    private void ExportButtonOnClick(){
        if(myDb.exportDB()){
            Toast.makeText(getApplicationContext(),
                    "Exported Data Successfully to /storage/emulated/0", Toast.LENGTH_SHORT)
                    .show();
        }
        else{
            Toast.makeText(getApplicationContext(),
                    "Could not export data", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    //Looks for what button was pressed
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Mode2BackButton:
                Mode2BackButtonOnClick();
                break;
            case R.id.DeleteButton:
                DeleteButtonOnClick();
                break;
            case R.id.ExportButton:
                ExportButtonOnClick();
                break;
        }
    }

    //Used to allow the database to open from our DBAdapter class
    private void openDB(){
        myDb = new DBAdapter(this); //Allows for interaction with the data base
        myDb.openRead();
    }
}
