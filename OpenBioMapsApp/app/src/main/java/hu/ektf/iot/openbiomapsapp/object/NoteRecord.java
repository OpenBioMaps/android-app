package hu.ektf.iot.openbiomapsapp.object;

import android.content.ContentValues;
import android.location.Location;
import android.text.TextUtils;

import org.parceler.Parcel;

import java.util.ArrayList;

import hu.ektf.iot.openbiomapsapp.LocalDB;

/**
 * Created by Pádi on 2015. 11. 10..
 */
@Parcel
public class NoteRecord {
    private String comment;
    private Location location;
    private String date;
    private ArrayList<String> imagesList;
    private ArrayList<String> soundsList;
    private Integer response;

    public NoteRecord(String comment, Location location, String date, ArrayList<String> imagesList, ArrayList<String> soundsList, Integer response) {
        setComment(comment);
        setLocation(location);
        setDate(date);
        setImagesList(imagesList);
        setSoundsList(soundsList);
        setResponse(response);
    }

    public ContentValues getContentValues() {

        ContentValues contentValues = new ContentValues();

        contentValues.put(LocalDB.COMMENT, comment);
        contentValues.put(LocalDB.LATITUDE, location.getLatitude());
        contentValues.put(LocalDB.LONGITUDE, location.getLongitude());
        contentValues.put(LocalDB.SOUND_FILE, TextUtils.join(",", soundsList));
        contentValues.put(LocalDB.IMAGE_FILE, TextUtils.join(",", imagesList));
        contentValues.put(LocalDB.DATE, date);
        contentValues.put(LocalDB.RESPONSE, response);

        return contentValues;
    }

    public String getLocationString() {
        return String.valueOf("(" + location.getLatitude()) + "," + String.valueOf(location.getLongitude() + ")");
    }

    public Integer getResponse() {
        return response;
    }

    public void setResponse(Integer response) {
        this.response = response;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<String> getImagesList() {
        return imagesList;
    }

    public void setImagesList(ArrayList<String> imagesList) {
        this.imagesList = imagesList;
    }

    public ArrayList<String> getSoundsList() {
        return soundsList;
    }

    public void setSoundsList(ArrayList<String> soundsList) {
        this.soundsList = soundsList;
    }
}
