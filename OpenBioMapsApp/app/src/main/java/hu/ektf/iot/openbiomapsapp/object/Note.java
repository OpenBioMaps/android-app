package hu.ektf.iot.openbiomapsapp.object;

import android.content.ContentValues;
import android.location.Location;

import org.parceler.Parcel;

import java.util.ArrayList;

import hu.ektf.iot.openbiomapsapp.database.NoteCreator;

/**
 * Created by Pádi on 2015. 11. 10..
 */
@Parcel
public class Note {
    private Integer id;
    private String comment;
    private Location location;
    private String date;
    private ArrayList<String> imagesList;
    private ArrayList<String> soundsList;
    private Integer response;
    private State state;

    public Note() {
        /* Required empty bean constructor form Parceler */
    }

    public Note(Integer id, String comment, Location location, String date, ArrayList<String> imagesList, ArrayList<String> soundsList, State state, Integer response) {
        setId(id);
        setComment(comment);
        setLocation(location);
        setDate(date);
        setImagesList(imagesList);
        setSoundsList(soundsList);
        setResponse(response);
        setState(state);
    }

    public Note(State state)
    {
        setState(state);
    }

    public ContentValues getContentValues(){
        return NoteCreator.getCVfromNote(this);
    }

    public String getLocationString() {
        return String.valueOf("(" + location.getLatitude()) + "," + String.valueOf(location.getLongitude() + ")");
    }

    public State getState(){return state;}

    public void setState(State value){state = value;}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getResponse() {
        return response;
    }

    public void setResponse(Integer response) {
        this.response = response;
    }
}
