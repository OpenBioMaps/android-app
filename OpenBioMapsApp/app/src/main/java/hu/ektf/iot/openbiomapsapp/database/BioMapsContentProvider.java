package hu.ektf.iot.openbiomapsapp.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by gerybravo on 2015.11.05..
 */
public class BioMapsContentProvider extends android.content.ContentProvider {
    private static final UriMatcher sURIMatcher;
    private static final int NOTES = 10;
    private static final int NOTE_ID = 20;
    private static final String BASE_PATH = "notes";

    public static final String AUTHORITY = "hu.ektf.iot.openbiomapsapp";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/measuredvalues";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/measuredvalue";
    public static final String QUERY_PARAMETER_LIMIT = "limit";

    static {
        sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, NOTES);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", NOTE_ID);
    }

    private DatabaseHelper database;

    @Override
    public boolean onCreate() {
        database = new DatabaseHelper(getContext());
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        checkColumns(projection);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(NoteTable.TABLE_NAME);
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case NOTES:
                break;
            case NOTE_ID:
                queryBuilder.appendWhere(NoteTable._ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        String limit = uri.getQueryParameter(QUERY_PARAMETER_LIMIT);
        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder, limit);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;

    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        long id = 0;
        switch (uriType) {
            case NOTES:
                id = sqlDB.insert(NoteTable.TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case NOTES:
                rowsDeleted = sqlDB.delete(NoteTable.TABLE_NAME, selection, selectionArgs);
                break;
            case NOTE_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(NoteTable.TABLE_NAME, NoteTable._ID + "=" + id, null);
                } else {
                    rowsDeleted = sqlDB.delete(NoteTable.TABLE_NAME, NoteTable._ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                new IllegalArgumentException("Unkown URI :" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // TODO Implement update
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    private void checkColumns(String[] projection) {
        String[] available = {NoteTable._ID, NoteTable.COMMENT,
                NoteTable.SOUND_FILES, NoteTable.IMAGE_FILES,
                NoteTable.DATE, NoteTable.RESPONSE, NoteTable.LATITUDE,
                NoteTable.LONGITUDE};

        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown column in projection");
            }
        }
    }
}
