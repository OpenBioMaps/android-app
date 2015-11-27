package hu.ektf.iot.openbiomapsapp;

import android.app.Application;

import java.util.ArrayList;
import java.util.Map;

import hu.ektf.iot.openbiomapsapp.upload.BioMapsServiceInterface;
import hu.ektf.iot.openbiomapsapp.upload.FileMapCreator;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import timber.log.Timber;

/**
 * Created by szugyi on 20/11/15.
 */
public class BioMapsApplication extends Application {
    public final static String END_POINT = "http://openbiomaps.org/pds";
    private BioMapsServiceInterface mapsService;

    private void setupRetrofit(){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(END_POINT)
                .setLogLevel(getRetrofitLogLevel())
                .build();
        mapsService = restAdapter.create(BioMapsServiceInterface.class);
    }

    protected RestAdapter.LogLevel getRetrofitLogLevel() {
        return RestAdapter.LogLevel.NONE;
    }

    protected void setupLogging(){
        // Placeholder for debug application
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setupRetrofit();
        setupLogging();
    }

    public BioMapsServiceInterface getMapsService(){
        return mapsService;
    }

    public void testService(){
        ArrayList<String> files = new ArrayList<String>();
        files.add("/storage/external_SD/sample.jpg");
        files.add("/storage/external_SD/sample2.jpg");
        Map<String, TypedFile> fileMap = FileMapCreator.createFileMap(files);

        getMapsService().uploadNote("abc123", "PFS", "mapp", "Android test", "2015.11.20 15:11:00", "POINT(3.14 4.13)", fileMap, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                Timber.i("Upload test", "SUCCESS");
                Timber.i("Upload test", s);
            }

            @Override
            public void failure(RetrofitError error) {
                Timber.e("Upload test", "ERROR");
                error.printStackTrace();
            }
        });
    }
}
