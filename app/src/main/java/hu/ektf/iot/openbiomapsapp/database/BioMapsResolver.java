package hu.ektf.iot.openbiomapsapp.database;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.text.TextUtils;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import hu.ektf.iot.openbiomapsapp.model.FormData;
import hu.ektf.iot.openbiomapsapp.model.FormData.State;

/**
 * A wrapper class around the default ContentResolver from the Android system, which makes it possible
 * to use typed parameters and returns typed results for the different content related queries,
 * like get, insert, update and delete.
 */
public class BioMapsResolver {
    private ContentResolver cr;
    private Uri formDataURI = BioMapsContentProvider.CONTENT_URI;

    public BioMapsResolver(Context context) {
        cr = context.getContentResolver();
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

    public ArrayList<FormData> queryFormData(final String[] projection,
                                             final String selection, final String[] selectionArgs,
                                             final String sortOrder) throws RemoteException {
        Cursor result = cr.query(formDataURI, projection, selection, selectionArgs,
                sortOrder);

        ArrayList<FormData> formDatas = new ArrayList<>();

        formDatas.addAll(FormDataCreator.getFormDatasFromCursor(result));
        result.close();

        return formDatas;
    }

    public ArrayList<FormData> getAllFormData() throws RemoteException {
        return queryFormData(null, null, null, null);
    }

    public ArrayList<FormData> getFormDatasWithLimit(final String[] projection,
                                                     final String selection, final String[] selectionArgs,
                                                     final String sortOrder, final int limit) throws RemoteException {
        String l = String.valueOf(limit);
        Cursor result = cr.query(formDataURI.buildUpon().appendQueryParameter(BioMapsContentProvider.QUERY_PARAMETER_LIMIT, l).build(), projection, selection, selectionArgs,
                sortOrder);

        ArrayList<FormData> formDatas = new ArrayList<FormData>();

        formDatas.addAll(FormDataCreator.getFormDatasFromCursor(result));
        result.close();

        return formDatas;
    }

    public FormData getFormDataById(final long id)
            throws RemoteException {
        String[] selectionArgs = {String.valueOf(id)};
        ArrayList<FormData> results = queryFormData(null,
                FormDataTable._ID + "= ?", selectionArgs, null);

        if (results.size() > 0) {
            return results.get(0);
        } else {
            return null;
        }
    }

    public FormData getFormDataByStatus(final State state)
            throws RemoteException {
        String selection = FormDataTable.STATE + "= ?";
        String[] selectionArgs = {String.valueOf(state.getValue())};
        String orderBy = FormDataTable.DATE + " ASC";

        ArrayList<FormData> results = queryFormData(null, selection, selectionArgs, orderBy);

        if (results.size() > 0) {
            return results.get(0);
        } else {
            return null;
        }
    }

    public Uri insert(final FormData formData) throws RemoteException {
        ContentValues tempCV = formData.getContentValues();
        tempCV.remove(FormDataTable._ID);
        return cr.insert(formDataURI, tempCV);
    }

    public int bulkInsertFormData(final ArrayList<FormData> formDatas)
            throws RemoteException {
        ContentValues[] values = new ContentValues[formDatas.size()];
        int index = 0;
        for (FormData formData : formDatas) {
            ContentValues tempCV = formData.getContentValues();
            tempCV.remove(FormDataTable._ID);
            values[index] = tempCV;
            ++index;
        }
        return cr.bulkInsert(formDataURI, values);
    }

    public int deleteFormData(final String selection,
                              final String[] selectionArgs) throws RemoteException {
        return cr.delete(formDataURI, selection, selectionArgs);
    }

    public int deleteFormDataById(long id) throws RemoteException {
        String[] args = {String.valueOf(id)};
        return deleteFormData(FormDataTable._ID + " = ? ", args);
    }

    public int deleteFormDatasByIds(ArrayList<String> id_array) throws RemoteException {
        return deleteFormData(FormDataTable._ID + " IN " + "(" + TextUtils.join(", ", id_array) + ")", null);
    }

    public int updateFormData(final FormData formData, final String selection,
                              final String[] selectionArgs) throws RemoteException {
        return cr.update(formDataURI, formData.getContentValues(), selection, selectionArgs);
    }

    public int updateFormData(FormData formData) throws RemoteException {
        String selection = FormDataTable._ID + " = ?";
        String[] selectionArgs = {String.valueOf(formData.getId())};
        return updateFormData(formData, selection, selectionArgs);
    }

    public Object insertOrUpdateFormData(FormData formData) throws RemoteException {
        Object returnValue = null;

        if (formData.getId() == null) {
            Uri uri = insert(formData);
            int id = Integer.parseInt(uri.getLastPathSegment());
            formData.setId(id);
            returnValue = uri;
        } else {
            returnValue = updateFormData(formData);
        }
        return returnValue;
    }
}