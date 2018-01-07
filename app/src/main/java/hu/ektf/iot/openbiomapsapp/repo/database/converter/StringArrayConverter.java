package hu.ektf.iot.openbiomapsapp.repo.database.converter;

import android.arch.persistence.room.TypeConverter;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.List;

public class StringArrayConverter {
    private static final String SEPARATOR = ",";

    @TypeConverter
    public static List<String> fromString(String value) {
        return value == null ? null : Arrays.asList(value.split(SEPARATOR));
    }

    @TypeConverter
    public static String listToString(List<String> list) {
        return list == null ? null : TextUtils.join(SEPARATOR, list);
    }
}
