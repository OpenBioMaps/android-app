package hu.ektf.iot.openbiomapsapp.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import hu.ektf.iot.openbiomapsapp.model.Form;

@Dao
public interface FormDao {

    @Query("SELECT * FROM form")
    List<Form> getForms();

    @Insert
    void insertAll(List<Form> forms);

    @Delete()
    void delete(Form form);
}
