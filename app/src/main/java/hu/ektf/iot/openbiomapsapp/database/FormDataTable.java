package hu.ektf.iot.openbiomapsapp.database;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import timber.log.Timber;

public class FormDataTable {
    public static final String TABLE_NAME = "form_data";

    public static final String _ID = BaseColumns._ID;
    public static final String FORM_ID = "FORM_ID";
    public static final String DATE = "DATE";
    public static final String FILES = "FILES";
    public static final String JSON = "JSON";
    public static final String RESPONSE = "RESPONSE";
    public static final String STATE = "STATE";
    public static final String URL = "URL";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME +
                    "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    FORM_ID + " INTEGER NOT NULL, " +
                    JSON + " TEXT NOT NULL, " +
                    STATE + " INTEGER NOT NULL, " +
                    FILES + " TEXT, " +
                    DATE + " TEXT, " +
                    URL + " TEXT, " +
                    RESPONSE + " TEXT);";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Timber.v("Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

}
