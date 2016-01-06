package hu.ektf.iot.openbiomapsapp.upload;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import java.util.Map;

import hu.ektf.iot.openbiomapsapp.BioMapsApplication;
import hu.ektf.iot.openbiomapsapp.database.BioMapsResolver;
import hu.ektf.iot.openbiomapsapp.object.Note;
import hu.ektf.iot.openbiomapsapp.object.Note.State;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedFile;
import timber.log.Timber;

/**
 * Created by szugyi on 27/11/15.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private BioMapsResolver bioMapsResolver;
    private BioMapsServiceInterface mapsService;
    private DynamicEndpoint endpoint;

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
        Note noteToSync = null;
        try {
            noteToSync = bioMapsResolver.getNoteByStatus(Note.State.CLOSED);
            if (noteToSync == null) {
                Timber.v("There was nothing to sync");
                isSyncing = false;
                return;
            }

            Timber.d("Upload started");
            noteToSync.setState(State.UPLOADING);
            bioMapsResolver.updateNote(noteToSync);

            endpoint.setUrl(noteToSync.getUrl());
            Map<String, TypedFile> fileMap = FileMapCreator.createFileMap(noteToSync.getImagesList(), noteToSync.getSoundsList());
            Response response = mapsService.uploadNote(noteToSync.getComment(), noteToSync.getDateString(), noteToSync.getGeometryString(), fileMap);
            String respStr = new String(((TypedByteArray) response.getBody()).getBytes());

            noteToSync.setResponse(respStr);
            noteToSync.setState(State.UPLOADED);
            bioMapsResolver.updateNote(noteToSync);

            doSync();
        } catch (Exception ex) {
            ex.printStackTrace();

            if (noteToSync != null) {
                try {
                    noteToSync.setResponse("EXCEPTION: " + ex.toString());
                    noteToSync.setState(State.UPLOAD_ERROR);
                    bioMapsResolver.updateNote(noteToSync);
                } catch (Exception e) {
                    ex.printStackTrace();
                }
            }

            isSyncing = false;
        }
    }
}
