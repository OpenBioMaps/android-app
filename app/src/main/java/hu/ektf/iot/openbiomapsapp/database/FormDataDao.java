package hu.ektf.iot.openbiomapsapp.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import hu.ektf.iot.openbiomapsapp.model.FormData;

@Dao
public interface FormDataDao {

    @Query("SELECT * FROM form_data")
    List<FormData> getFormDataList();

    @Query("SELECT * FROM form_data WHERE state == :state ORDER BY date LIMIT 1")
    FormData getFormDataListByState(FormData.State state);

    @Insert
    void insert(FormData formData);

    @Delete()
    void delete(FormData form);
}
