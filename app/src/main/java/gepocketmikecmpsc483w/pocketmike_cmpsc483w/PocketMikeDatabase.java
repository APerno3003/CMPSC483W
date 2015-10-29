package gepocketmikecmpsc483w.pocketmike_cmpsc483w;

/**
 * Created by Anthony on 10/29/2015.
 *
 */

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PocketMikeDatabase {

    // database version
    //private static final int databaseVersion = 1;
    // database name
    //private static final String databaseName = "PocketMikeDatabase";
    //private static final String tableName = "Measurements";
    //private static final String thickness = "Thickness";
    //private static final String unitType = "Unit Type";
    //private static final String location = "Location";

    //private static final String[] COLUMNS = { thickness, unitType};
    //String jaskjdhs = "SELECT " + columnName + " FROM " + tableName + " WHERE " + differnetColumnName +  " = 02";
    private static PocketMikeDatabase ourInstance;
    public static PocketMikeDatabase getInstance() {
        if (ourInstance == null) {
            ourInstance = new PocketMikeDatabase();
        }

        return ourInstance;
    }

    private PocketMikeDatabase() {

    }

}
//http://examples.javacodegeeks.com/android/core/database/android-database-example/
//http://developer.android.com/training/basics/data-storage/databases.html
//http://developer.android.com/reference/android/database/sqlite/package-summary.html
//http://developer.android.com/reference/android/database/package-summary.html
//http://www.vogella.com/tutorials/AndroidSQLite/article.html
//http://www.tutorialspoint.com/android/android_sqlite_database.htm
//https://www.youtube.com/watch?v=LZ8kJg4Pg4Y
//http://www.androidhive.info/2011/11/android-sqlite-database-tutorial/


