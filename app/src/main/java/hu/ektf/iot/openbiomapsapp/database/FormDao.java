package hu.ektf.iot.openbiomapsapp.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import hu.ektf.iot.openbiomapsapp.model.Form;

@Dao
public interface FormDao {

    @Query("SELECT * FROM form")
    List<Form> getForms();

    @Query("SELECT * FROM form WHERE id=:id")
    Form getForm(int id);

    @Insert
    void insertAll(List<Form> forms);

    @Update
    void update(Form form);

    @Delete()
    void delete(Form form);

    @Query("DELETE FROM form")
    void deleteAll();
}
