package hu.ektf.iot.openbiomapsapp.model.response;

import com.google.gson.annotations.SerializedName;

public enum Status {
    @SerializedName("success")
    SUCCESS,
    @SerializedName("error")
    ERROR,
    @SerializedName("fail")
    FAIL
}
