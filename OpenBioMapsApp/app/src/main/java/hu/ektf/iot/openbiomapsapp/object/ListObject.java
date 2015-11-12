package hu.ektf.iot.openbiomapsapp.object;

import android.location.Location;

import java.util.ArrayList;

/**
 * Created by Pádi on 2015. 11. 10..
 */
public class ListObject {
    private String note;
    private Location location;
    private String date;
    private ArrayList<String> imagesList;
    private ArrayList<String> soundsList;
    private Integer status;
    Location l;



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
