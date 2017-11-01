package hu.ektf.iot.openbiomapsapp.model.response;

import java.util.ArrayList;


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
