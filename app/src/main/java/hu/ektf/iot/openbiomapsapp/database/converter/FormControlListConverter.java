package hu.ektf.iot.openbiomapsapp.database.converter;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import hu.ektf.iot.openbiomapsapp.model.FormControl;

public class FormControlListConverter {
    // TODO Inject?
    private static final Gson gson = new Gson();

    @TypeConverter
    public static List<FormControl> fromString(String value) {
        Type listType = new TypeToken<ArrayList<FormControl>>(){}.getType();
        return value == null ? null : gson.fromJson(value, listType);
    }

    @TypeConverter
    public static String listToString(List<FormControl> list) {
        return list == null ? null : gson.toJson(list);
    }
}
