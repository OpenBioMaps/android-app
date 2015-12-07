package hu.ektf.iot.openbiomapsapp.database;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.text.TextUtils;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import hu.ektf.iot.openbiomapsapp.object.Note;
import hu.ektf.iot.openbiomapsapp.object.State;

/**
 * A wrapper class around the default ContentResolver from the Android system, which makes it possible
 * to use typed parameters and returns typed results for the different content related queries,
 * like get, insert, update and delete.
 */
public class BioMapsResolver {
    private ContentResolver cr;
    private Uri noteURI = BioMapsContentProvider.CONTENT_URI;

    public BioMapsResolver(Activity activity) {
        cr = activity.getContentResolver();
    }

    public BioMapsResolver(ContentResolver contentResolver) {
        cr = contentResolver;
    }

    public ContentProviderResult[] applyBatch(
            final ArrayList<ContentProviderOperation> operations)
            throws RemoteException, OperationApplicationException {
        return cr.applyBatch(BioMapsContentProvider.AUTHORITY, operations);
    }

    public String getType(Uri uri) throws RemoteException {
        return cr.getType(uri);
    }

    public ParcelFileDescriptor openFileDescriptor(final Uri uri,
                                                   final String mode) throws RemoteException, FileNotFoundException {
        return cr.openFileDescriptor(uri, mode);
    }

    public ArrayList<Note> queryNotes(final String[] projection,
                                      final String selection, final String[] selectionArgs,
                                      final String sortOrder) throws RemoteException {
        Cursor result = cr.query(noteURI, projection, selection, selectionArgs,
                sortOrder);

        ArrayList<Note> notes = new ArrayList<Note>();

        notes.addAll(NoteCreator.getNotesFromCursor(result));
        result.close();

        return notes;
    }

    public ArrayList<Note> getAllNote() throws RemoteException {
        return queryNotes(null, null, null, null);
    }

    public ArrayList<Note> getNotesWithLimit(final String[] projection,
                                             final String selection, final String[] selectionArgs,
                                             final String sortOrder, final int limit) throws RemoteException {
        String l = String.valueOf(limit);
        Cursor result = cr.query(noteURI.buildUpon().appendQueryParameter(BioMapsContentProvider.QUERY_PARAMETER_LIMIT, l).build(), projection, selection, selectionArgs,
                sortOrder);

        ArrayList<Note> notes = new ArrayList<Note>();

        notes.addAll(NoteCreator.getNotesFromCursor(result));
        result.close();

        return notes;
    }

    public Note getNoteById(final long id)
            throws RemoteException {
        String[] selectionArgs = {String.valueOf(id)};
        ArrayList<Note> results = queryNotes(null,
                NoteTable._ID + "= ?", selectionArgs, null);

        if (results.size() > 0) {
            return results.get(0);
        } else {
            return null;
        }
    }

    public Note getSyncableNote() throws RemoteException {
        // TODO select by Note.STATE
        String selection = NoteTable.RESPONSE + "= ?";
        String[] selectionArgs = {String.valueOf("")};
        String orderBy = NoteTable.DATE + " ASC";

        ArrayList<Note> results = queryNotes(null,
                selection, selectionArgs, orderBy);

        if (results.size() > 0) {
            return results.get(0);
        } else {
            return null;
        }
    }

    public Note getNoteByStatus(final State state)
            throws RemoteException {
        String[] selectionArgs = {String.valueOf(state)};
        ArrayList<Note> results = queryNotes(null,
                NoteTable.STATE + "= ?", selectionArgs, null);

        if (results.size() > 0) {
            return results.get(0);
        } else {
            return null;
        }
    }

    public Uri insert(final Note note) throws RemoteException {
        ContentValues tempCV = note.getContentValues();
        tempCV.remove(NoteTable._ID);
        return cr.insert(noteURI, tempCV);
    }

    public int bulkInsertNotes(final ArrayList<Note> notes)
            throws RemoteException {
        ContentValues[] values = new ContentValues[notes.size()];
        int index = 0;
        for (Note note : notes) {
            ContentValues tempCV = note.getContentValues();
            tempCV.remove(NoteTable._ID);
            values[index] = tempCV;
            ++index;
        }
        return cr.bulkInsert(noteURI, values);
    }

    public int deleteNote(final String selection,
                          final String[] selectionArgs) throws RemoteException {
        return cr.delete(noteURI, selection, selectionArgs);
    }

    public int deleteNotesById(long id) throws RemoteException {
        String[] args = {String.valueOf(id)};
        return deleteNote(NoteTable._ID + " = ? ", args);
    }

    public int deleteNoteByIds(ArrayList<String> id_array) throws RemoteException {
        return deleteNote(NoteTable._ID + " IN " + "(" + TextUtils.join(", ", id_array) + ")", null);
    }


    public int updateNote(final Note note, final String selection,
                          final String[] selectionArgs) throws RemoteException {
        return cr.update(noteURI, note.getContentValues(), selection, selectionArgs);
    }

    public int updateNote(Note note) throws RemoteException {
        String selection = "_id = ?";
        String[] selectionArgs = {String.valueOf(note.getId())};
        return updateNote(note, selection, selectionArgs);
    }
}