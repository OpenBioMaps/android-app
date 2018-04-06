package hu.ektf.iot.openbiomapsapp.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import com.google.gson.Gson;

import javax.inject.Inject;

import hu.ektf.iot.openbiomapsapp.BioMapsApplication;
import hu.ektf.iot.openbiomapsapp.repo.api.DynamicEndpoint;
import hu.ektf.iot.openbiomapsapp.repo.database.AppDatabase;
import hu.ektf.iot.openbiomapsapp.di.DaggerAppComponent;
import hu.ektf.iot.openbiomapsapp.model.FormData;
import hu.ektf.iot.openbiomapsapp.model.FormData.State;
import hu.ektf.iot.openbiomapsapp.model.response.BioMapsResponse;
import hu.ektf.iot.openbiomapsapp.model.response.Status;
import hu.ektf.iot.openbiomapsapp.repo.ObmRepoImpl;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import timber.log.Timber;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    @Inject
    DynamicEndpoint endpoint;

    @Inject
    AppDatabase database;

    @Inject
    ObmRepoImpl repo;

    @Inject
    Gson gson;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        init(context);
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        init(context);
    }

    private void init(Context context) {
        BioMapsApplication bioMapsApplication = (BioMapsApplication) context.getApplicationContext();
        // TODO Is there a more dagger 2 like way?
        DaggerAppComponent.builder()
                .application(bioMapsApplication)
                .build()
                .inject(this);
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        if (!isSyncing) {
            isSyncing = true;
            doSync();
        }
    }

    private boolean isSyncing = false;

    private void doSync() {
        FormData formData = null;

        try {
            formData = repo.getSavedFormDataByState(State.CLOSED);

            if (formData == null) {
                Timber.v("There was nothing to sync");
                isSyncing = false;
                return;
            }

            Timber.d("Upload started");
            formData.setState(State.UPLOADING);
            database.formDataDao().update(formData);

            // TODO: Add files
            // Map<String, TypedFile> fileMap = FileMapCreator.createFileMap(formData.getFiles());
            Response response = repo.uploadData(formData.getFormId(), gson.toJson(formData.getColumns()), formData.getJson());
            String respStr = new String(((TypedByteArray) response.getBody()).getBytes());
            formData.setResponse(respStr);
            formData.setState(State.UPLOADED);

            try {
                BioMapsResponse respObj = gson.fromJson(respStr, BioMapsResponse.class);
                if (respObj.getStatus() != Status.SUCCESS) {
                    formData.setState(State.UPLOAD_ERROR);
                }
            } catch (Exception ex) {
                formData.setState(State.UPLOAD_ERROR);
                Timber.e(ex, "Upload response contained error");
            }

            database.formDataDao().update(formData);
            doSync();
        } catch (Exception ex) {
            Timber.e(ex, "Sync failed");

            if (formData != null) {
                try {
                    formData.setResponse("EXCEPTION: " + ex.toString());
                    formData.setState(State.UPLOAD_ERROR);
                    database.formDataDao().update(formData);
                    doSync();
                } catch (Exception e) {
                    Timber.e(e, "Retry sync failed");
                }
            }

            isSyncing = false;
        }
    }
}
