package hu.ektf.iot.openbiomapsapp.repo.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import javax.inject.Inject;

// TODO Replace with AA shared preferences handling
public class StorageHelper {
    private static final String SHARED_PREFERENCES_NAME = "BIOMAPS_ANDROID_SHARED_PREFERENCES";

    private static final String KEY_SERVER_URL = "KEY_SERVER_URL";
    private static final String KEY_PROJECT_NAME = "KEY_PROJECT_NAME";
    private static final String KEY_EXPORT_PATH = "KEY_EXPORT_PATH";

    private static final String KEY_ACCESS_TOKEN = "KEY_ACCESS_TOKEN";
    private static final String KEY_REFRESH_TOKEN = "KEY_REFRESH_TOKEN";

    private static final String DEFAULT_EXPORT_PATH = Environment.getExternalStorageDirectory() + "/openbiomaps/";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Inject
    public StorageHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public String getServerUrl() {
        return sharedPreferences.getString(KEY_SERVER_URL, null);
    }

    public void setServerUrl(String newServerUrl) {
        editor.putString(KEY_SERVER_URL, newServerUrl);
        editor.commit();
    }

    public String getProjectName() {
        return sharedPreferences.getString(KEY_PROJECT_NAME, null);
    }

    public void setProjectName(String projectName) {
        editor.putString(KEY_PROJECT_NAME, projectName);
        editor.commit();
    }

    public String getExportPath() {
        return sharedPreferences.getString(KEY_EXPORT_PATH, DEFAULT_EXPORT_PATH);
    }

    public void setExportPath(String newPath) {
        editor.putString(KEY_EXPORT_PATH, newPath);
        editor.commit();
    }

    public String getAccessToken() {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
    }

    public boolean hasAccessToken() {
        return sharedPreferences.contains(KEY_ACCESS_TOKEN);
    }

    public void setAccessToken(String accessToken) {
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.commit();
    }

    public String getRefreshToken() {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, null);
    }

    public void setRefreshToken(String refreshToken) {
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.commit();
    }

    public void clearTokens() {
        editor.remove(KEY_ACCESS_TOKEN);
        editor.remove(KEY_REFRESH_TOKEN);
        editor.commit();
    }
}