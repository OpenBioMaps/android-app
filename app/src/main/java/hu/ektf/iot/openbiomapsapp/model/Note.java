package hu.ektf.iot.openbiomapsapp.model;

import android.content.ContentValues;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import hu.ektf.iot.openbiomapsapp.R;
import hu.ektf.iot.openbiomapsapp.database.NoteCreator;
import hu.ektf.iot.openbiomapsapp.helper.GeometryConverter;

/**
 * Created by Pádi on 2015. 11. 10..
 */
public class Note implements Parcelable {

    public enum State {
        CREATED(0), CLOSED(1), UPLOADING(2), UPLOADED(3), UPLOAD_ERROR(4);

        private static final Map<Integer, State> lookup
                = new HashMap<Integer, State>();

        static {
            for (State s : EnumSet.allOf(State.class))
                lookup.put(s.getValue(), s);
        }

        private final int value;

        State(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static State getByValue(int value) {
            return lookup.get(value);
        }
    }

    private static final String ID = "id";
    private static final String COMMENT = "comment";
    private static final String LOCATION = "location";
    private static final String DATE = "date";
    private static final String IMAGES = "images";
    private static final String SOUNDS = "sounds";
    private static final String STATE = "state";
    private static final String RESPONSE = "response";
    private static final String URL = "url";

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Integer id;
    private String comment;
    private Location location;
    private Date date;
    private ArrayList<String> imagesList;
    private ArrayList<String> soundsList;
    private State state;
    private String response;
    private String url;

    public Note() {
        imagesList = new ArrayList<>();
        soundsList = new ArrayList<>();
        state = State.CREATED;
    }

    public ContentValues getContentValues() {
        return NoteCreator.getCVfromNote(this);
    }

    public String getLocationString() {
        return String.valueOf("(" + location.getLatitude()) + "," + String.valueOf(location.getLongitude() + ")");
    }

    public String getLocationString(Context ctx) {
        return ctx.getString(R.string.tv_location, location.getLatitude(), location.getLongitude());
    }

    public String getGeometryString() throws Exception {
        return GeometryConverter.LocationToString(location);
    }

    public String getDateString() {
        return date == null ? DATE_FORMAT.toPattern() : DATE_FORMAT.format(date);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
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

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    // Parcelable implementation
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();
        bundle.putLong(DATE, getDate() == null ? -1 : getDate().getTime());
        if (getId() != null) bundle.putInt(ID, getId());
        if (getLocation() != null) bundle.putParcelable(LOCATION, location);
        if (getComment() != null) bundle.putString(COMMENT, getComment());
        if (getImagesList() != null) bundle.putStringArrayList(IMAGES, getImagesList());
        if (getSoundsList() != null) bundle.putStringArrayList(SOUNDS, getSoundsList());
        if (getState() != null) bundle.putSerializable(STATE, state);
        if (getResponse() != null) bundle.putString(RESPONSE, getResponse());
        if (getUrl() != null) bundle.putString(URL, getUrl());

        dest.writeBundle(bundle);
    }

    private Note(Parcel in) {
        Bundle bundle = in.readBundle();

        long dateTime = bundle.getLong(DATE);
        setDate(dateTime == -1 ? null : new Date(dateTime));
        if (bundle.containsKey(ID)) setId(bundle.getInt(ID));
        if (bundle.containsKey(LOCATION)) setLocation((Location) bundle.getParcelable(LOCATION));
        if (bundle.containsKey(COMMENT)) setComment(bundle.getString(COMMENT));
        if (bundle.containsKey(IMAGES)) setImagesList(bundle.getStringArrayList(IMAGES));
        if (bundle.containsKey(SOUNDS)) setSoundsList(bundle.getStringArrayList(SOUNDS));
        if (bundle.containsKey(STATE)) setState((State) bundle.getSerializable(STATE));
        if (bundle.containsKey(RESPONSE)) setResponse(bundle.getString(RESPONSE));
        if (bundle.containsKey(URL)) setUrl(bundle.getString(URL));
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        public Note[] newArray(int size) {
            return new Note[size];
        }
    };
}
