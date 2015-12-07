package hu.ektf.iot.openbiomapsapp;

import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import java.util.ArrayList;
import hu.ektf.iot.openbiomapsapp.adapter.MyUploadListCursorAdapter;
import hu.ektf.iot.openbiomapsapp.adapter.DividerItemDecoration;
import hu.ektf.iot.openbiomapsapp.database.BioMapsContentProvider;
import hu.ektf.iot.openbiomapsapp.helper.StorageHelper;
import hu.ektf.iot.openbiomapsapp.object.Note;

public class UploadActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private RecyclerView mRecyclerView;
    private MyUploadListCursorAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static final int URL_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerUploadList);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addItemDecoration(
                new DividerItemDecoration(this, R.drawable.divider));

        final ArrayList<Note> listObjects = new ArrayList<Note>();

        getSupportLoaderManager().initLoader(URL_LOADER, null, this);

        final StorageHelper sh = new StorageHelper(UploadActivity.this);

        mAdapter = new MyUploadListCursorAdapter(this, null);
/*
        mAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UploadActivity.this);

                alertDialogBuilder.setTitle("Exportálás");
                alertDialogBuilder.setMessage("Az exportálás helye:\n" + sh.getExportPath());

                alertDialogBuilder.setPositiveButton("Exportálás", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Note n = listObjects.get(position);
                        try {
                            ExportHelper.exportNote(n);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                alertDialogBuilder.setNegativeButton("Mégse", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
        */
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int loaderID, Bundle args) {
        return new android.support.v4.content.CursorLoader(
                this,   // Parent activity context
                BioMapsContentProvider.CONTENT_URI,        // Table to query
                null,     // Projection to return
                null,            // No selection clause
                null,            // No selection arguments
                null             // Default sort order
        );
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        mAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }
}
