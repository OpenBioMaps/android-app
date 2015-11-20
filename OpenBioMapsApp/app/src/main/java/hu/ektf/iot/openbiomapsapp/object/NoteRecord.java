package hu.ektf.iot.openbiomapsapp.object;

import android.content.ContentValues;
import android.location.Location;

import org.parceler.Parcel;

import java.util.ArrayList;

import hu.ektf.iot.openbiomapsapp.LocalDB;

/**
 * Created by Pádi on 2015. 11. 10..
 */
@Parcel
public class NoteRecord {
    private String note;
    private Location location;
    private String date;
    private ArrayList<String> imagesList;
    private ArrayList<String> soundsList;
    private Integer status;
    Location l;

    public NoteRecord() {
        /* Required empty bean constructor form Parceler */
    }

    public NoteRecord(String note, Location location, String date, ArrayList<String> imagesList, ArrayList<String> soundsList, Integer status) {
        setNote(note);
        setLocation(location);
        setDate(date);
        setImagesList(imagesList);
        setSoundsList(soundsList);
        setStatus(status);
    }

    public ContentValues getContentValues() {

        ContentValues contentValues = new ContentValues();

        contentValues.put(LocalDB.COMMENT,note);
        contentValues.put(LocalDB.GEOMETRY,location.toString());
        contentValues.put(LocalDB.SOUND_FILE,"soundfile path");
        contentValues.put(LocalDB.IMAGE_FILE,"imgfile path");
        contentValues.put(LocalDB.DATE, date);
        contentValues.put(LocalDB.RESPONSE, "");

        return contentValues;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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
