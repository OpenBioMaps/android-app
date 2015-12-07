package hu.ektf.iot.openbiomapsapp.database;

import android.database.sqlite.SQLiteDatabase;

import timber.log.Timber;


/**
 * This class defines the database table used to store Note type objects.
 */
public class NoteTable {
    public static final String TABLE_NAME = "note";

    public static final String _ID = "_ID";
    public static final String COMMENT = "COMMENT";
    public static final String SOUND_FILES = "SOUND_FILES";
    public static final String IMAGE_FILES = "IMAGE_FILES";
    public static final String DATE = "DATE";
    public static final String RESPONSE = "RESPONSE";
    public static final String LATITUDE = "LATITUDE";
    public static final String LONGITUDE = "LONGITUDE";
    public static final String STATE = "STATE";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME +
                    "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COMMENT + " TEXT, " +
                    LATITUDE + " TEXT NOT NULL, " +
                    LONGITUDE + " TEXT NOT NULL," +
                    SOUND_FILES + " TEXT, " +
                    IMAGE_FILES + " TEXT, " +
                    DATE + " TEXT NOT NULL, " +
                    STATE + " INTEGER NOT NULL, " +
                    RESPONSE + " INTEGER);";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Timber.v("Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

}
