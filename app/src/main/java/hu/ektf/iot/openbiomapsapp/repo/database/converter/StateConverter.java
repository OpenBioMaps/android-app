package hu.ektf.iot.openbiomapsapp.repo.database.converter;

import android.arch.persistence.room.TypeConverter;

import hu.ektf.iot.openbiomapsapp.model.FormData.State;

public class StateConverter {

    @TypeConverter
    public static State fromInt(int value) {
        return State.getByValue(value);
    }

    @TypeConverter
    public static int stateToInt(State state) {
        return state == null ? null : state.getValue();
    }
}
