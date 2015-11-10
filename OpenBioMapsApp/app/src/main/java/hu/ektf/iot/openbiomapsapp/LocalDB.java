package hu.ektf.iot.openbiomapsapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;

/**
 * Created by gerybravo on 2015.11.05..
 */
public class LocalDB extends android.content.ContentProvider {

    static final String PROVIDER_NAME = "hu.ektf.iot.openbiomapsapp";
    static final String URL = "content://"+PROVIDER_NAME+"/storage";
    static final Uri CONTENT_URI = Uri.parse(URL);
    private static Uri getContentUri(){return CONTENT_URI;}

    static final String _ID = "_ID";
    static final String COMMENT = "COMMENT";
    static final String GEOMETRY = "GEOMETRY";
    static final String SOUND_FILE = "SOUND_FILE";
    static final String IMAGE_FILE = "IMAGE_FILE";
    static final String DATE = "DATE";
    static final String RESPONSE = "RESPONSE";

    private static HashMap<String, String> STORAGE_PROJECTION_MAP;

    static final int RECORDS = 1;
    static final int RECORD_ID = 2;

    static final UriMatcher uriMatcher;
    static
    {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME,"storage",RECORDS);
        uriMatcher.addURI(PROVIDER_NAME,"storage/#",RECORD_ID);
    }

    private SQLiteDatabase db;
    static final String DATABASE_NAME = "iot.openbiomapsapp";
    static final String TABLE_NAME = "storage";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME +
                    "(_ID INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    "COMMENT TEXT, "+
                    "GEOMETRY TEXT NOT NULL, "+
                    "SOUND_FILE TEXT, "+
                    "IMAGE_FILE TEXT, "+
                    "DATE TEXT, " +
                    "RESPONSE INTEGER);";

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " +  TABLE_NAME);
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        db = dbHelper.getWritableDatabase();
        return (db == null)? false:true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        long rowID = db.insert(TABLE_NAME, "", contentValues);

        if (rowID > 0)
        {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public Cursor query(Uri uri, String[] projectiion, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_NAME);

        switch (uriMatcher.match(uri))
        {
            case RECORDS:
                qb.setProjectionMap(STORAGE_PROJECTION_MAP);
                break;
            case RECORD_ID:
                qb.appendWhere(_ID + "=" + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI "+ uri);
        }

        Cursor cursor = qb.query(db,projectiion,selection,selectionArgs,null,null,null);
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        //TODO
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)){
            case RECORDS:
                count = db.update(TABLE_NAME, contentValues, selection, selectionArgs);
                break;

            case RECORD_ID:
                count = db.update(TABLE_NAME, contentValues, _ID + " = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" +selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        //TODO
        return "";
    }
}
