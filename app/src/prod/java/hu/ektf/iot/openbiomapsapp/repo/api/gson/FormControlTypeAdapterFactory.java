package hu.ektf.iot.openbiomapsapp.repo.api.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import hu.ektf.iot.openbiomapsapp.model.FormControl;

public class FormControlTypeAdapterFactory extends CustomizedTypeAdapterFactory<FormControl> {

    public FormControlTypeAdapterFactory() {
        super(FormControl.class);
    }

    @Override
    protected void afterRead(JsonElement deserialized) {
        JsonObject desrializedObject = deserialized.getAsJsonObject();
        JsonElement typeElement = desrializedObject.get("type");
        desrializedObject.addProperty("typeName", typeElement.getAsString());
    }
}
