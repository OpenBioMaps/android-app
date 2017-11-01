package hu.ektf.iot.openbiomapsapp.upload;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import com.google.gson.Gson;

import hu.ektf.iot.openbiomapsapp.BioMapsApplication;
import hu.ektf.iot.openbiomapsapp.model.FormData;
import hu.ektf.iot.openbiomapsapp.model.FormData.State;
import hu.ektf.iot.openbiomapsapp.model.response.BioMapsResponse;
import hu.ektf.iot.openbiomapsapp.repo.ObmRepoImpl;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import timber.log.Timber;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private DynamicEndpoint endpoint;
    private ObmRepoImpl repo;
    private Gson gson;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        init(context);
    }

    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        init(context);
    }

    private void init(Context context) {
        BioMapsApplication bioMapsApplication = (BioMapsApplication) context.getApplicationContext();
        endpoint = bioMapsApplication.getDynamicEndpoint();
        repo = new ObmRepoImpl(context);
        gson = new Gson();
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        Timber.v("Wow! Such sync, so upload!");

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
            repo.saveData(formData).subscribe();

            endpoint.setUrl(formData.getUrl());
            // Map<String, TypedFile> fileMap = FileMapCreator.createFileMap(formData.getFiles());
            // TODO: Add files
            Response response = repo.uploadData(formData.getFormId(), gson.toJson(formData.getColumns()), formData.getJson());
            String respStr = new String(((TypedByteArray) response.getBody()).getBytes());
            formData.setResponse(respStr);
            formData.setState(State.UPLOADED);

            try {
                BioMapsResponse respObj = gson.fromJson(respStr, BioMapsResponse.class);
                if (!respObj.getError().isEmpty()) {
                    formData.setState(State.UPLOAD_ERROR);
                }
            } catch (Exception e) {
                formData.setState(State.UPLOAD_ERROR);
                Timber.e(e, "Upload response contained error");
            }

            repo.saveData(formData).subscribe();
            doSync();
        } catch (Exception ex) {
            ex.printStackTrace();

            if (formData != null) {
                try {
                    formData.setResponse("EXCEPTION: " + ex.toString());
                    formData.setState(State.UPLOAD_ERROR);
                    repo.saveData(formData).subscribe();
                    doSync();
                } catch (Exception e) {
                    ex.printStackTrace();
                }
            }

            isSyncing = false;
        }
    }
}
