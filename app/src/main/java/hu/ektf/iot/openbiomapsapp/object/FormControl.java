package hu.ektf.iot.openbiomapsapp.object;

import com.google.gson.annotations.SerializedName;

public class FormControl {

    public enum Type {
        @SerializedName("autocomplete") AUTOCOMPLETE,
        @SerializedName("boolen") BOOLEAN,
        @SerializedName("date") DATE,
        @SerializedName("numeric") NUMERIC,
        @SerializedName("point") POINT,
        @SerializedName("text") TEXT,
    }

    private String column;
    @SerializedName("short_name")
    private String shortName;
    private Type type;

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
}