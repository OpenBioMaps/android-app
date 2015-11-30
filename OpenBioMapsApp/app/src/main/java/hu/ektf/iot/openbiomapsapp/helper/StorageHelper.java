package hu.ektf.iot.openbiomapsapp.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

/**
 * Created by Csaba on 2015. 10. 28..
 */
public class StorageHelper {
    private static final String SHARED_PREFERENCES_NAME = "BIOMAPS_ANDROID_SHARED_PREFERENCES";

    private static final String KEY_SERVER_URL= "KEY_SERVER_URL";
    private static final String KEY_EXPORT_PATH = "KEY_EXPORT_PATH";
    private static final String DEFAULT_SERVER_URL = "http://defaultserver.hu";
    private static final String DEFAULT_EXPORT_PATH = Environment.getExternalStorageDirectory() + "/openbiomaps/";

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public StorageHelper(Context context) {
        this.context = context;
        sharedPreferences = getSharedPref(context);
        editor = sharedPreferences.edit();
    }

    private SharedPreferences getSharedPref(Context context) {
        return context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public String getServerUrl() {
        return sharedPreferences.getString(KEY_SERVER_URL, DEFAULT_SERVER_URL);
    }

    public void setServerUrl(String newServerUrl) {
        editor.putString(KEY_SERVER_URL, newServerUrl);
        editor.commit();
    }

    public String getExportPath() {
        return sharedPreferences.getString(KEY_EXPORT_PATH, DEFAULT_EXPORT_PATH);
    }

    public void setExportPath(String newPath) {
        editor.putString(KEY_EXPORT_PATH, newPath);
        editor.commit();
    }
}