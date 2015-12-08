package hu.ektf.iot.openbiomapsapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;

import hu.ektf.iot.openbiomapsapp.adapter.DividerItemDecoration;
import hu.ektf.iot.openbiomapsapp.adapter.NoteCursorAdapter;
import hu.ektf.iot.openbiomapsapp.database.BioMapsContentProvider;
import hu.ektf.iot.openbiomapsapp.database.NoteCreator;
import hu.ektf.iot.openbiomapsapp.database.NoteTable;
import hu.ektf.iot.openbiomapsapp.helper.ExportHelper;
import hu.ektf.iot.openbiomapsapp.helper.StorageHelper;
import hu.ektf.iot.openbiomapsapp.object.Note;

public class UploadActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int NOTE_LOADER = 0;

    private RecyclerView recyclerView;
    private NoteCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        final StorageHelper sh = new StorageHelper(UploadActivity.this);

        // TODO Add emptyView for when the list is empty
        recyclerView = (RecyclerView) findViewById(R.id.recyclerUploadList);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(
                new DividerItemDecoration(this, R.drawable.divider));

        adapter = new NoteCursorAdapter(this, null);
        adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UploadActivity.this);

                // TODO Strings!
                alertDialogBuilder.setTitle("Exportálás");
                alertDialogBuilder.setMessage("Az exportálás helye:\n" + sh.getExportPath());

                alertDialogBuilder.setPositiveButton("Exportálás", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Note n = NoteCreator.getNoteFromCursor(adapter.getCursor());
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

        recyclerView.setAdapter(adapter);

        getSupportLoaderManager().initLoader(NOTE_LOADER, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle args) {
        String order = NoteTable.DATE + " DESC";
        return new CursorLoader(
                this,                                      // Activity context
                BioMapsContentProvider.CONTENT_URI,        // Table to query
                null,                                      // Projection to return
                null,                                      // No selection clause
                null,                                      // No selection arguments
                order                                      // Sort order
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.changeCursor(null);
    }
}
