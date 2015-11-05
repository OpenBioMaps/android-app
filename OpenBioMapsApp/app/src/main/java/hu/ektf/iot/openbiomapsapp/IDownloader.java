package hu.ektf.iot.openbiomapsapp;

import com.google.gson.JsonElement;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;

/**
 * Created by gerybravo on 2015.11.04..
 */
public interface IDownloader {
    @GET("/service.php?service=PFS&upload_test")
    void getUploadedData(Callback<Response> callback);
}
