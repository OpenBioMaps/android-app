package hu.ektf.iot.openbiomapsapp;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;

/**
 * Created by gerybravo on 2015.11.04..
 */
public interface BioMapsService {
    @GET("/service.php?service=PFS&upload_test")
    void getUploadedData(Callback<Response> callback);

    @Multipart
    @POST("/service.php")
    void uploadNote(@Part("hs") String hs, @Part("service") String service, @Part("upload") String upload, @Part("m_comment") String comment, @Part("m_datum") String date, @Part("m_geometry") String geometry, Callback<String> callback);
}
