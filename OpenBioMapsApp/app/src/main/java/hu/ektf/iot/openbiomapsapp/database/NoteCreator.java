package hu.ektf.iot.openbiomapsapp.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.location.Location;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import hu.ektf.iot.openbiomapsapp.object.Note;
import hu.ektf.iot.openbiomapsapp.object.Note.State;

/**
 * This is a helper class which is responsible for creating ContentValues from a Note type object,
 * and to create Note object from a cursor. By using these methods, the database handler classes do not
 * have to know anything about the structure of the Note object, and the way its values are serialised
 * and deserialised.
 */
public class NoteCreator {
    private static final String SEPARATOR = ",";

    public static ContentValues getCVfromNote(final Note note) {
        ContentValues contentValues = new ContentValues();

        if (note.getId() != null) contentValues.put(NoteTable._ID, note.getId());
        if (note.getLocation() != null) {
            contentValues.put(NoteTable.LATITUDE, note.getLocation().getLatitude());
            contentValues.put(NoteTable.LONGITUDE, note.getLocation().getLongitude());
        }
        contentValues.put(NoteTable.COMMENT, note.getComment());
        contentValues.put(NoteTable.DATE, note.getDate() != null ? note.getDate().getTime() : -1);
        contentValues.put(NoteTable.SOUND_FILES, TextUtils.join(SEPARATOR, note.getSoundsList()));
        contentValues.put(NoteTable.IMAGE_FILES, TextUtils.join(SEPARATOR, note.getImagesList()));
        contentValues.put(NoteTable.STATE, note.getState().getValue());
        contentValues.put(NoteTable.URL, note.getUrl());
        contentValues.put(NoteTable.RESPONSE, note.getResponse());

        return contentValues;
    }

    public static ArrayList<Note> getNotesFromCursor(
            Cursor cursor) {
        ArrayList<Note> mValue = new ArrayList<Note>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    mValue.add(getNoteFromCursor(cursor));
                } while (cursor.moveToNext() == true);
            }
        }
        return mValue;
    }

    public static Note getNoteFromCursor(Cursor cursor) {
        Location loc = null;
        if (cursor.getColumnIndex(NoteTable.LATITUDE) >= 0 && cursor.getColumnIndex(NoteTable.LONGITUDE) >= 0) {
            double latitude = cursor.getDouble(cursor.getColumnIndex(NoteTable.LATITUDE));
            double longitude = cursor.getDouble(cursor.getColumnIndex(NoteTable.LONGITUDE));
            loc = new Location(BioMapsContentProvider.AUTHORITY);
            loc.setLatitude(latitude);
            loc.setLongitude(longitude);
        }

        String images = cursor.getString(cursor.getColumnIndex(NoteTable.IMAGE_FILES));
        String sounds = cursor.getString(cursor.getColumnIndex(NoteTable.SOUND_FILES));

        Long timestamp = cursor.getLong(cursor.getColumnIndex(NoteTable.DATE));

        Note note = new Note();
        note.setId(cursor.getInt(cursor.getColumnIndex(NoteTable._ID)));
        note.setComment(cursor.getString(cursor.getColumnIndex(NoteTable.COMMENT)));
        note.setDate(timestamp > -1 ? new Date(timestamp) : null);
        note.setState(State.getByValue(cursor.getInt(cursor.getColumnIndex(NoteTable.STATE))));
        note.setUrl(cursor.getString(cursor.getColumnIndex(NoteTable.URL)));
        note.setResponse(cursor.getInt(cursor.getColumnIndex(NoteTable.RESPONSE)));
        note.setLocation(loc);
        if (!TextUtils.isEmpty(images))
            note.setImagesList(new ArrayList(Arrays.asList(images.split(SEPARATOR))));
        if (!TextUtils.isEmpty(sounds))
            note.setSoundsList(new ArrayList(Arrays.asList(sounds.split(SEPARATOR))));
        return note;
    }
}
