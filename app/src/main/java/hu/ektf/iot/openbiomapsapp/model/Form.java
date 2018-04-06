package hu.ektf.iot.openbiomapsapp.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "form")
public class Form {

    @PrimaryKey
    private int id;
    private String projectName;
    private String visibility;
    private List<FormControl> formControls;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public List<FormControl> getFormControls() {
        return formControls;
    }

    public void setFormControls(List<FormControl> formControls) {
        this.formControls = formControls;
    }
}
