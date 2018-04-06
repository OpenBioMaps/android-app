package hu.ektf.iot.openbiomapsapp.repo.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import hu.ektf.iot.openbiomapsapp.model.FormData;

@Dao
public interface FormDataDao {

    @Query("SELECT * FROM form_data ORDER BY date DESC")
    List<FormData> getFormDataList();

    @Query("SELECT * FROM form_data WHERE state == :state ORDER BY date LIMIT 1")
    FormData getFormDataListByState(FormData.State state);

    @Query("SELECT * FROM form_data WHERE id == :id LIMIT 1")
    FormData getFormData(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FormData formData);

    @Update
    void update(FormData formData);

    @Delete()
    void delete(FormData form);
}
