package hu.ektf.iot.openbiomapsapp.upload;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import com.google.gson.Gson;

import java.util.Map;

import hu.ektf.iot.openbiomapsapp.BioMapsApplication;
import hu.ektf.iot.openbiomapsapp.database.BioMapsResolver;
import hu.ektf.iot.openbiomapsapp.model.FormData;
import hu.ektf.iot.openbiomapsapp.model.FormData.State;
import hu.ektf.iot.openbiomapsapp.model.response.BioMapsResponse;
import hu.ektf.iot.openbiomapsapp.repo.ObmRepoImpl;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedFile;
import timber.log.Timber;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private BioMapsResolver bioMapsResolver;
    private BioMapsService mapsService;
    private DynamicEndpoint endpoint;
    private ObmRepoImpl repo;
    private Gson gson;

    /**
     * Set up the sync adapter
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        init(context);
    }

    /**
     * Set up the sync adapter. This form of the constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        init(context);
    }

    private void init(Context context) {
        bioMapsResolver = new BioMapsResolver(context.getContentResolver());

        BioMapsApplication bioMapsApplication = (BioMapsApplication) context.getApplicationContext();
        mapsService = bioMapsApplication.getMapsService();
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
            formData = bioMapsResolver.getFormDataByStatus(State.CLOSED);
            if (formData == null) {
                Timber.v("There was nothing to sync");
                isSyncing = false;
                return;
            }

            Timber.d("Upload started");
            formData.setState(State.UPLOADING);
            bioMapsResolver.updateFormData(formData);

            endpoint.setUrl(formData.getUrl());
            Map<String, TypedFile> fileMap = FileMapCreator.createFileMap(formData.getFiles());
            // TODO: Add files
            Response response = repo.putData(formData.getFormId(), gson.toJson(formData.getColumns()), formData.getJson());
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

            bioMapsResolver.updateFormData(formData);
            doSync();
        } catch (Exception ex) {
            ex.printStackTrace();

            if (formData != null) {
                try {
                    formData.setResponse("EXCEPTION: " + ex.toString());
                    formData.setState(State.UPLOAD_ERROR);
                    bioMapsResolver.updateFormData(formData);
                    doSync();
                } catch (Exception e) {
                    ex.printStackTrace();
                }
            }

            isSyncing = false;
        }
    }
}
