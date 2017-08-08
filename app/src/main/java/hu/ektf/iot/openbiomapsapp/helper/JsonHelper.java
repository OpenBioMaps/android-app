package hu.ektf.iot.openbiomapsapp.helper;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public final class JsonHelper {

    private JsonHelper() {

    }

    public static List<String> arrayAsList(JSONArray array) {
        List<String> list = new ArrayList<>(array.length());
        for (int i = 0; i < array.length(); i++) {
            try {
                list.add(array.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}
