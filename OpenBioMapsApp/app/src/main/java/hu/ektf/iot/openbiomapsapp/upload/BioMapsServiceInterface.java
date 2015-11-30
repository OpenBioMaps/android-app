package hu.ektf.iot.openbiomapsapp.upload;

import java.util.Map;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.PartMap;
import retrofit.mime.TypedFile;

/**
 * Created by gerybravo on 2015.11.04..
 */
public interface BioMapsServiceInterface {
    String PARAM_FILE_ARRAY_FORMAT = "m_file0[%d]";

    @GET("/service.php?service=PFS&upload_test")
    void getUploadedData(Callback<Response> callback);

    @Multipart
    @POST("/service.php")
    void uploadNote(@Part("hs") String hs, @Part("service") String service, @Part("upload") String upload, @Part("m_comment") String comment, @Part("m_datum") String date, @Part("m_geometry") String geometry, @PartMap Map<String, TypedFile> files, Callback<String> callback);
}
