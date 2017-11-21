package hu.ektf.iot.openbiomapsapp.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import hu.ektf.iot.openbiomapsapp.database.converter.DateConverter;
import hu.ektf.iot.openbiomapsapp.database.converter.FormControlListConverter;
import hu.ektf.iot.openbiomapsapp.database.converter.StateConverter;
import hu.ektf.iot.openbiomapsapp.database.converter.StringArrayConverter;
import hu.ektf.iot.openbiomapsapp.model.Form;
import hu.ektf.iot.openbiomapsapp.model.FormData;

@Database(entities = {Form.class, FormData.class}, version = 2)
@TypeConverters({DateConverter.class, FormControlListConverter.class,
                 StateConverter.class, StringArrayConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract FormDao formDao();

    public abstract FormDataDao formDataDao();
}
