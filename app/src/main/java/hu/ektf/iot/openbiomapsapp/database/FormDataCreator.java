package hu.ektf.iot.openbiomapsapp.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import hu.ektf.iot.openbiomapsapp.helper.JsonHelper;
import hu.ektf.iot.openbiomapsapp.model.FormData;
import hu.ektf.iot.openbiomapsapp.model.FormData.State;
import timber.log.Timber;

/**
 * This is a helper class which is responsible for creating ContentValues from a FormData type object,
 * and to create FormData object from a cursor. By using these methods, the database handler classes do not
 * have to know anything about the structure of the FormData object, and the way its values are serialised
 * and deserialised.
 */
public class FormDataCreator {
    private static final String SEPARATOR = ",";

    public static ContentValues getCVfromFormData(final FormData formData) {
        ContentValues contentValues = new ContentValues();

        if (formData.getId() != null) contentValues.put(FormDataTable._ID, formData.getId());
        contentValues.put(FormDataTable.FORM_ID, formData.getFormId());
        contentValues.put(FormDataTable.DATE, formData.getDate() != null ? formData.getDate().getTime() : -1);
        contentValues.put(FormDataTable.JSON, formData.getJson());
        contentValues.put(FormDataTable.FILES, TextUtils.join(SEPARATOR, formData.getFiles()));
        contentValues.put(FormDataTable.STATE, formData.getState().getValue());
        contentValues.put(FormDataTable.URL, formData.getUrl());
        contentValues.put(FormDataTable.RESPONSE, formData.getResponse());

        return contentValues;
    }

    public static ArrayList<FormData> getFormDatasFromCursor(
            Cursor cursor) {
        ArrayList<FormData> mValue = new ArrayList<FormData>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    mValue.add(getFormDataFromCursor(cursor));
                } while (cursor.moveToNext() == true);
            }
        }
        return mValue;
    }

    public static FormData getFormDataFromCursor(Cursor cursor) {
        String files = cursor.getString(cursor.getColumnIndex(FormDataTable.FILES));
        Long timestamp = cursor.getLong(cursor.getColumnIndex(FormDataTable.DATE));

        FormData formData = new FormData();
        formData.setId(cursor.getInt(cursor.getColumnIndex(FormDataTable._ID)));
        formData.setFormId(cursor.getInt(cursor.getColumnIndex(FormDataTable.FORM_ID)));
        formData.setDate(timestamp > -1 ? new Date(timestamp) : null);
        formData.setJson(cursor.getString(cursor.getColumnIndex(FormDataTable.JSON)));
        formData.setState(State.getByValue(cursor.getInt(cursor.getColumnIndex(FormDataTable.STATE))));
        formData.setUrl(cursor.getString(cursor.getColumnIndex(FormDataTable.URL)));
        formData.setResponse(cursor.getString(cursor.getColumnIndex(FormDataTable.RESPONSE)));
        if (!TextUtils.isEmpty(files)) {
            formData.setFiles(new ArrayList(Arrays.asList(files.split(SEPARATOR))));
        }

        try {
            JSONObject formJson = new JSONObject(formData.getJson());
            JSONArray columns = formJson.names();
            formData.setColumns(new ArrayList(JsonHelper.arrayAsList(columns)));
        } catch (JSONException e) {
            Timber.e(e);
        }

        return formData;
    }
}
