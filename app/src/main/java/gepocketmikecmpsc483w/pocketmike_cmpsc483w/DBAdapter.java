package gepocketmikecmpsc483w.pocketmike_cmpsc483w;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.content.ContextWrapper;
import android.app.Fragment;

import com.opencsv.CSVWriter;
// opencsv library download: http://sourceforge.net/projects/opencsv/?source=typ_redirect
// opencsv version 3.6 as of 12/8/2014

import java.io.File;
import java.io.FileWriter;


/**
 *
 * Created by Brandon Manning on 12/2/15.
 */
public class DBAdapter {
    private static final String TAG = "DBAdapter"; // Used for logging database version

    //Field Names:
    public static final String KEY_ROWID = "_id";
    public static final String KEY_THICKNESS = "Thickness";
    public static final String KEY_UNIT = "Unit";
    public static final String KEY_VELOCITY = "Velocity";
    public static final String KEY_LATITUDE = "Latitude";
    public static final String KEY_LONGITUDE = "Longitude";
    public static final String KEY_DATE = "Date";
    public static final String KEY_TIME = "Time";

    //Contains an array of all the KEYS in the database.
    public static final String[] ALL_KEYS = new String[] {KEY_ROWID, KEY_THICKNESS, KEY_UNIT, KEY_VELOCITY,
            KEY_LATITUDE, KEY_LONGITUDE , KEY_DATE, KEY_TIME};

    //Column numbers for the database
    public static final int COL_ROWID = 0;
    public static final int COL_THICKNESS = 1;
    public static final int COL_UNIT = 2;
    public static final int COL_VELOCITY = 3;
    public static final int COL_LATITUDE = 4;
    public static final int COL_LONGITUDE = 5;
    public static final int COL_DATE = 6;
    public static final int COL_TIME = 7;

    //Database information:
    public static final String DATABASE_NAME = "dbPocketMike.sqlite";
    public static final String DATABASE_TABLE = "mainPocketMike";
    public static int DATABASE_VERSION = 2; //If any changes are made increment this value

    //SQL statement to create the database
    private static final String DATABASE_CREATE_SQL =
            "CREATE TABLE " + DATABASE_TABLE
                    + " (" + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_THICKNESS + " TEXT, "
                    + KEY_UNIT + " TEXT, "
                    + KEY_VELOCITY + " TEXT, "
                    + KEY_LATITUDE + " TEXT, "
                    + KEY_LONGITUDE + " TEXT, "
                    + KEY_DATE + " TEXT, "
                    + KEY_TIME + " TEXT"
                    + ");";

    private final Context context;
    private DatabaseHelper myDBHelper;
    private SQLiteDatabase db;

    private File databasePath;

    public DBAdapter(Context ctx){
        this.context = ctx;
        myDBHelper = new DatabaseHelper(context);

        databasePath = ctx.getDatabasePath(DATABASE_NAME);
    }

    //Opens the database connection
    public DBAdapter open(){
        db = myDBHelper.getWritableDatabase();
        return this;
    }

    //Closes the database connection
    public void close(){
        myDBHelper.close();
    }


    public DBAdapter openRead(){
        db = myDBHelper.getReadableDatabase();
        return this;
    }

    //Get the value of the database's path
    public File getDBpath(){ return databasePath; }

    //Add a new set of values to be inserted in the database
    public long insertRow(String thickness, String unit, String vel, String lat, String longitude, String date, String time){
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_THICKNESS, thickness);
        initialValues.put(KEY_UNIT, unit);
        initialValues.put(KEY_VELOCITY, vel);
        initialValues.put(KEY_LATITUDE, lat);
        initialValues.put(KEY_LONGITUDE, longitude);
        initialValues.put(KEY_DATE, date);
        initialValues.put(KEY_TIME, time);

        //Insert the data into the database
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    //Deletes a row from the database by the row id (PRIMARY KEY)
    public boolean deleteRow(long rowID){
        String where = KEY_ROWID + "=" + rowID;
        return db.delete(DATABASE_TABLE, where, null) != 0;
    }

    //Deletes all rows in the database
    public void deleteAll(){
        /*Cursor c = getAllRows();
        long rowId = c.getColumnIndexOrThrow(KEY_ROWID);
        if(c.moveToFirst()) {
            do {
                deleteRow(c.getLong((int) rowId));
            }
            while (c.moveToNext());
        }
        c.close();*/
        myDBHelper.onDelete(db);
    }

    //Return all rows in the database
    public Cursor getAllRows(){
        String where = null;
        Cursor c = db.query(true, DATABASE_TABLE, ALL_KEYS,
                where, null, null, null, null, null);
        if(c != null){
            c.moveToFirst();
        }
        return c;
    }

    //Get a specific row by the row ID
    public Cursor getRow(long rowId){
        String where = KEY_ROWID + "=" + rowId;
        Cursor c = db.query(true, DATABASE_TABLE, ALL_KEYS,
                where, null, null, null, null, null);
        if(c != null){
            c.moveToFirst();
        }
        return c;
    }

    //Change an existing row to equal to new data
    public boolean updateRow(long rowId, String thickness, String unit, String vel, String lat, String longitude, String date, String time ){
        String where = KEY_ROWID + "=" + rowId;
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_THICKNESS, thickness);
        newValues.put(KEY_UNIT, unit);
        newValues.put(KEY_VELOCITY, vel);
        newValues.put(KEY_LATITUDE, lat);
        newValues.put(KEY_LONGITUDE, longitude);
        newValues.put(KEY_DATE, date);
        newValues.put(KEY_TIME, time);

        //Update the row with the newvalues
        return db.update(DATABASE_TABLE, newValues, where, null) != 0;
    }

    public boolean exportDB(){
        File dbFile = getDBpath();
        DBAdapter dbAdapter = new DBAdapter(context);
        //File exportDirectory = new File(Environment.getExternalStorageDirectory(), "");
        File exportDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!exportDirectory.exists())
        {
            exportDirectory.mkdir();
        }

        File file = new File(exportDirectory, "PocketMIKE.csv");
        try
        {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            dbAdapter.openRead();
            Cursor cursorCSV = db.rawQuery("SELECT * FROM mainPocketMike", null);
            csvWrite.writeNext(cursorCSV.getColumnNames());
            while(cursorCSV.moveToNext())
            {
                // colums we are importing
                String listStr[] = {cursorCSV.getString(0),
                        cursorCSV.getString(1),
                        cursorCSV.getString(2),
                        cursorCSV.getString(3),
                        cursorCSV.getString(4),
                        cursorCSV.getString(5),
                        cursorCSV.getString(6),
                        cursorCSV.getString(7)};
                csvWrite.writeNext(listStr);
            }
            csvWrite.close();
            cursorCSV.close();
            dbAdapter.close();

        }
        catch (Exception sqlEx) {
            Log.e("Mode2Activity", sqlEx.getMessage(), sqlEx);
        }

        return(true);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase _db){
            _db.execSQL(DATABASE_CREATE_SQL);
        }

        public void onDelete(SQLiteDatabase _db){
            //Destroy the database
            _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);

            //Recreate new database
            onCreate(_db);
        }
        @Override
        public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion){
            Log.w(TAG, "Upgrading application's database from version " + oldVersion
                    + ", which will destroy all old data!!");

            //Destroy the old database
            _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);

            //Recreate new database
            onCreate(_db);
        }
    }
}