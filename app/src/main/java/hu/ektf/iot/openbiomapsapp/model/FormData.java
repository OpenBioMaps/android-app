package hu.ektf.iot.openbiomapsapp.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import hu.ektf.iot.openbiomapsapp.helper.DateUtil;

@Entity(tableName = "form_data")
public class FormData implements Parcelable {

    public enum State {
        CREATED(0), CLOSED(1), UPLOADING(2), UPLOADED(3), UPLOAD_ERROR(4);

        private static final SparseArray<State> LOOKUP = new SparseArray<>();

        static {
            for (State s : EnumSet.allOf(State.class))
                LOOKUP.put(s.getValue(), s);
        }

        private final int value;

        State(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static State getByValue(int value) {
            return LOOKUP.get(value);
        }
    }

    private static final String ID = "id";
    private static final String COLUMNS = "columns";
    private static final String DATE = "date";
    private static final String FILES = "files";
    private static final String FORM_ID = "formId";
    private static final String JSON = "json";
    private static final String RESPONSE = "response";
    private static final String STATE = "state";
    private static final String URL = "url";

    @PrimaryKey(autoGenerate = true)
    private Integer id;
    private List<String> columns;
    private Date date;
    private List<String> files;
    private Integer formId;
    private String json;
    private State state;
    private String response;
    private String url;

    public FormData() {
        files = new ArrayList<>();
        state = State.CREATED;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public String getDateString() {
        return date == null ? "-" : DateUtil.formatFullDate(date);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    public Integer getFormId() {
        return formId;
    }

    public void setFormId(int formId) {
        this.formId = formId;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
        if (getColumns() != null) bundle.putStringArrayList(COLUMNS, new ArrayList<>(getColumns()));
        if (getFiles() != null) bundle.putStringArrayList(FILES, new ArrayList<>(getFiles()));
        if (getFormId() != null) bundle.putInt(FORM_ID, getFormId());
        if (getJson() != null) bundle.putString(JSON, getJson());
        if (getState() != null) bundle.putSerializable(STATE, state);
        if (getResponse() != null) bundle.putString(RESPONSE, getResponse());
        if (getUrl() != null) bundle.putString(URL, getUrl());

        dest.writeBundle(bundle);
    }

    private FormData(Parcel in) {
        Bundle bundle = in.readBundle();

        long dateTime = bundle.getLong(DATE);
        setDate(dateTime == -1 ? null : new Date(dateTime));
        if (bundle.containsKey(ID)) setId(bundle.getInt(ID));
        if (bundle.containsKey(COLUMNS)) setColumns(bundle.getStringArrayList(COLUMNS));
        if (bundle.containsKey(FILES)) setFiles(bundle.getStringArrayList(FILES));
        if (bundle.containsKey(FORM_ID)) setFormId(bundle.getInt(FORM_ID));
        if (bundle.containsKey(JSON)) setJson(bundle.getString(JSON));
        if (bundle.containsKey(STATE)) setState((State) bundle.getSerializable(STATE));
        if (bundle.containsKey(RESPONSE)) setResponse(bundle.getString(RESPONSE));
        if (bundle.containsKey(URL)) setUrl(bundle.getString(URL));
    }

    public static final Creator<FormData> CREATOR = new Creator<FormData>() {
        public FormData createFromParcel(Parcel in) {
            return new FormData(in);
        }

        public FormData[] newArray(int size) {
            return new FormData[size];
        }
    };
}
