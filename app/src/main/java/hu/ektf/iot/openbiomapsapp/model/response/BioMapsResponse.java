package hu.ektf.iot.openbiomapsapp.model.response;

import java.util.ArrayList;

/**
 * Created by szugyi on 2016.07.18..
 */
public class BioMapsResponse {
    ArrayList<String> error;
    ArrayList<String> result;

    public ArrayList<String> getError() {
        return error;
    }

    public void setError(ArrayList<String> error) {
        this.error = error;
    }
}
