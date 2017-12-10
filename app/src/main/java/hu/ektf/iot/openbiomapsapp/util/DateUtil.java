package hu.ektf.iot.openbiomapsapp.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class DateUtil {
    private static final SimpleDateFormat FULL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

    private DateUtil() {
        // Util class
    }

    public static String formatFullDate(Date date) {
        return FULL_DATE_FORMAT.format(date);
    }

    public static String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }

    public static String formatTime(Date date) {
        return TIME_FORMAT.format(date);
    }

    public static Date parseDate(String dateString) throws ParseException {
        return DATE_FORMAT.parse(dateString);
    }

    public static Date parseTime(String timeString) throws ParseException {
        return TIME_FORMAT.parse(timeString);
    }
}
