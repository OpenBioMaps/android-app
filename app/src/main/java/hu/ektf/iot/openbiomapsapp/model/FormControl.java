package hu.ektf.iot.openbiomapsapp.model;

import com.google.gson.annotations.SerializedName;

public class FormControl {

    public enum Type {
        @SerializedName("autocomplete")AUTOCOMPLETE,
        @SerializedName("boolen")BOOLEAN,
        @SerializedName("date")DATE,
        @SerializedName("datetime")DATE_TIME,
        @SerializedName("numeric")NUMERIC,
        @SerializedName("point")POINT,
        @SerializedName("text")TEXT,
        @SerializedName("time")TIME,
    }

    private String column;
    @SerializedName("short_name")
    private String shortName;
    private Type type;
    private Object value;

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
