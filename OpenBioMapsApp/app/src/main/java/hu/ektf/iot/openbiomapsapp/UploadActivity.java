package hu.ektf.iot.openbiomapsapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import hu.ektf.iot.openbiomapsapp.adapter.DividerItemDecoration;
import hu.ektf.iot.openbiomapsapp.adapter.NoteCursorAdapter;
import hu.ektf.iot.openbiomapsapp.database.BioMapsContentProvider;
import hu.ektf.iot.openbiomapsapp.database.BioMapsResolver;
import hu.ektf.iot.openbiomapsapp.database.NoteCreator;
import hu.ektf.iot.openbiomapsapp.database.NoteTable;
import hu.ektf.iot.openbiomapsapp.helper.ExportHelper;
import hu.ektf.iot.openbiomapsapp.helper.StorageHelper;
import hu.ektf.iot.openbiomapsapp.object.Note;

public class UploadActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int NOTE_LOADER = 0;

    private ProgressDialog barProgressDialog;
    private RecyclerView recyclerView;
    private NoteCursorAdapter adapter;
    private Button buttonExportAll;
    private TextView tvEmpty;

    private ExportAsyncTask exportTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        final StorageHelper sh = new StorageHelper(UploadActivity.this);

        buttonExportAll = (Button) findViewById(R.id.buttonExport);
        buttonExportAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExportAsyncTask exportTask = new ExportAsyncTask();
                exportTask.execute();
            }
        });

        tvEmpty = (TextView) findViewById(R.id.textViewEmpty);

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

                alertDialogBuilder.setTitle(getString(R.string.dialog_export_title));
                alertDialogBuilder.setMessage(getString(R.string.dialog_export_path, sh.getExportPath()));

                alertDialogBuilder.setPositiveButton(getString(R.string.dialog_export_text_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Cursor cursor = adapter.getCursor();
                        cursor.moveToPosition(position);
                        Note note = NoteCreator.getNoteFromCursor(cursor);
                        try {
                            ExportHelper.exportNote(note);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                alertDialogBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        adapter.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = adapter.getCursor();
                cursor.moveToPosition(position);
                Note note = NoteCreator.getNoteFromCursor(cursor);

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UploadActivity.this);
                alertDialogBuilder.setTitle(getString(R.string.dialog_details_title));
                alertDialogBuilder.setMessage(getString(R.string.dialog_details_message, note.getUrl(), note.getResponse()));
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return false;
            }
        });

        recyclerView.setAdapter(adapter);

        getSupportLoaderManager().initLoader(NOTE_LOADER, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle args) {
        String selection = NoteTable.STATE + " != ?";
        String[] selectionArgs = new String[]{String.valueOf(Note.State.CREATED.getValue())};
        String order = NoteTable.DATE + " DESC";
        return new CursorLoader(
                this,                                      // Activity context
                BioMapsContentProvider.CONTENT_URI,        // Table to query
                null,                                      // Projection to return
                selection,                                 // No selection clause
                selectionArgs,                             // No selection arguments
                order                                      // Sort order
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.changeCursor(data);
        if (adapter.getItemCount() == 0) {
            tvEmpty.setVisibility(View.VISIBLE);
           buttonExportAll.setVisibility(View.GONE);
           recyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.changeCursor(null);
    }

    class ExportAsyncTask extends AsyncTask<Void, Integer, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
                ArrayList<Note> allNote = new BioMapsResolver(UploadActivity.this).getAllNote();
                int count = allNote.size();
                for (int i = 0; i < count; i++) {
                    if (isCancelled()) break;
                    if (allNote.get(i).getState() == Note.State.CREATED) break;

                    ExportHelper.exportNote(allNote.get(i));
                    publishProgress((int) ((i / (float) count) * 100));
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "Task Completed.";
        }

        @Override
        protected void onPostExecute(String result) {
            barProgressDialog.hide();

        }

        @Override
        protected void onPreExecute() {
            barProgressDialog = new ProgressDialog(UploadActivity.this);
            barProgressDialog.setTitle(getString(R.string.export_progressbar_title));
            barProgressDialog.setMessage(getString(R.string.export_progressbar_progress));
            barProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            barProgressDialog.setProgress(0);
            barProgressDialog.setMax(100);
            barProgressDialog.setCancelable(false);
            barProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.export_progressbar_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if (exportTask != null) {
                        exportTask.cancel(false);
                        exportTask = null;
                    }
                }
            });
            barProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            barProgressDialog.setProgress(values[0]);
        }
    }
}
