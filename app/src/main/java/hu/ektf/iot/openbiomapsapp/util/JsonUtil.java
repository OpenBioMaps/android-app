package hu.ektf.iot.openbiomapsapp.util;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public final class JsonUtil {

    private JsonUtil() {
        // Util class
    }

    public static List<String> arrayAsList(JSONArray array) {
        List<String> list = new ArrayList<>(array.length());
        for (int i = 0; i < array.length(); i++) {
            try {
                list.add(array.getString(i));
            } catch (JSONException e) {
                Timber.e(e, "Parsing JSON array failed");
            }
        }
        return list;
    }
}
