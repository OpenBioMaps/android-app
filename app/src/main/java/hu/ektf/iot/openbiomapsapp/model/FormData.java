package hu.ektf.iot.openbiomapsapp.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import hu.ektf.iot.openbiomapsapp.util.DateUtil;

@Entity(tableName = "form_data")
public class FormData {

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

    @PrimaryKey(autoGenerate = true)
    private Integer id;
    private List<String> columns;
    private Date date;
    private List<String> files;
    private Integer formId;
    private String json;
    private State state;
    private String response;
    private String projectName;
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
        return date == null ? "-" : DateUtil.formatDateTime(date);
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

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
