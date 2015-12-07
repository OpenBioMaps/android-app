package hu.ektf.iot.openbiomapsapp.object;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gerybravo on 2015.11.26..
 */
public enum State {
    CREATED(0), CLOSED(1), UPLOADING(2), UPLOADED(3), UPLOAD_ERROR(4);

    private static final Map<Integer,State> lookup
            = new HashMap<Integer,State>();

    static {
        for(State s : EnumSet.allOf(State.class))
            lookup.put(s.getValue(),s);
    }

    private final int value;
    private State(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }

    public static State getByValue(int value)
    {
        return lookup.get(value);
    }
}
