package hu.ektf.iot.openbiomapsapp;

import android.app.Application;
import android.util.Log;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

/**
 * Created by szugyi on 20/11/15.
 */
public class BioMapsApplication extends Application {
    public final static String END_POINT = "http://openbiomaps.org/pds";
    private BioMapsService mapsService;

    @Override
    public void onCreate() {
        super.onCreate();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(END_POINT)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        mapsService = restAdapter.create(BioMapsService.class);
    }

    public BioMapsService getMapsService(){
        return mapsService;
    }

    public void testService(){
        Map<String, TypedFile> files = new HashMap<String, TypedFile>();
        TypedFile typedImage0 = new TypedFile("image/*", new File("/storage/external_SD/sample2.jpg"));
        files.put("m_file0[0]", typedImage0);
        TypedFile typedImage1 = new TypedFile("image/*", new File("/storage/external_SD/sample.jpg"));
        files.put("m_file0[1]", typedImage1);

        getMapsService().uploadNote("abc123", "PFS", "mapp", "Android test", "2015.11.20 15:11:00", "POINT(3.14 4.13)", files, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                Log.v("Upload test", "SUCCESS");
                Log.v("Upload test", s);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("Upload test", "ERROR");
                error.printStackTrace();
            }
        });
    }
}
