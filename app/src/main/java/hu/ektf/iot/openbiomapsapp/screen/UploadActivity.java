package hu.ektf.iot.openbiomapsapp.screen;

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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import hu.ektf.iot.openbiomapsapp.BioMapsApplication;
import hu.ektf.iot.openbiomapsapp.R;
import hu.ektf.iot.openbiomapsapp.adapter.DividerItemDecoration;
import hu.ektf.iot.openbiomapsapp.adapter.FormDataCursorAdapter;
import hu.ektf.iot.openbiomapsapp.database.BioMapsContentProvider;
import hu.ektf.iot.openbiomapsapp.database.BioMapsResolver;
import hu.ektf.iot.openbiomapsapp.database.FormDataCreator;
import hu.ektf.iot.openbiomapsapp.database.FormDataTable;
import hu.ektf.iot.openbiomapsapp.helper.ExportHelper;
import hu.ektf.iot.openbiomapsapp.helper.StorageHelper;
import hu.ektf.iot.openbiomapsapp.model.FormData;
import timber.log.Timber;

public class UploadActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int NOTE_LOADER = 0;

    private ProgressDialog barProgressDialog;
    private RecyclerView recyclerView;
    private FormDataCursorAdapter adapter;
    private Button buttonExportAll;
    private TextView tvEmpty;

    private ExportAsyncTask exportTask;
    private StorageHelper sharedPrefStorage;
    private BioMapsResolver bioMapsResolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        sharedPrefStorage = new StorageHelper(this);
        bioMapsResolver = new BioMapsResolver(this);

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

        adapter = new FormDataCursorAdapter(this, null);
        adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {
                FormData note = getNoteByPosition(position);
                showDetailDialog(note);
            }
        });

        adapter.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                FormData note = getNoteByPosition(position);
                showContextMenu(note);
                return false;
            }
        });

        recyclerView.setAdapter(adapter);

        getSupportLoaderManager().initLoader(NOTE_LOADER, null, this);
    }

    private FormData getNoteByPosition(int position) {
        Cursor cursor = adapter.getCursor();
        cursor.moveToPosition(position);
        FormData note = FormDataCreator.getFormDataFromCursor(cursor);
        return note;
    }

    private void showDetailDialog(final FormData note) {
        String prettyJSON = note.getResponse();
        try{
            int spacesToIndentEachLevel = 2;
            prettyJSON = new JSONObject(prettyJSON).toString(spacesToIndentEachLevel);
        }
        catch (Exception e){
            Timber.e(e, "Format failed");
        }
        new AlertDialog.Builder(UploadActivity.this)
                .setTitle(getString(R.string.dialog_details_title))
                .setMessage(getString(R.string.dialog_details_message, note.getUrl(), prettyJSON))
                .create().show();
    }

    private void showContextMenu(final FormData note) {
        new AlertDialog.Builder(UploadActivity.this)
                .setItems(R.array.note_context, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                showExportDialog(note);
                                break;
                            case 1:
                                showChangeUrlDialog(note);
                                break;
                            case 2:
                                showDeleteDialog(note);
                                break;
                            case 3:
                                retryUpload(note);
                                break;
                        }
                    }
                })
                .create().show();
    }

    private void showExportDialog(final FormData note) {
        new AlertDialog.Builder(UploadActivity.this)
                .setTitle(getString(R.string.dialog_export_title))
                .setMessage(getString(R.string.dialog_export_path, sharedPrefStorage.getExportPath()))
                .setPositiveButton(getString(R.string.dialog_export_text_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        ExportAsyncTask exportTask = new ExportAsyncTask();
                        exportTask.execute(note);
                    }
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .create().show();
    }

    private void showChangeUrlDialog(final FormData note) {
        LayoutInflater li = LayoutInflater.from(UploadActivity.this);
        View dialogView = li.inflate(R.layout.dialog_server_settings, null);

        final EditText etServerUrl = (EditText) dialogView
                .findViewById(R.id.etServerUrl);
        etServerUrl.setText(note.getUrl());
        etServerUrl.setSelection(etServerUrl.getText().length());

        new AlertDialog.Builder(
                UploadActivity.this)
                .setView(dialogView)
                .setCancelable(false)
                .setTitle(R.string.dialog_change_url_title)
                .setPositiveButton(R.string.save,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String newUrl = etServerUrl.getText().toString();
                                if (TextUtils.isEmpty(newUrl) || TextUtils.equals(note.getUrl(), newUrl)) {
                                    return;
                                }

                                note.setState(FormData.State.CLOSED);
                                note.setResponse("");
                                note.setUrl(newUrl);

                                try {
                                    bioMapsResolver.updateFormData(note);
                                    ((BioMapsApplication) getApplication()).requestSync();
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                .setNegativeButton(R.string.cancel, null)
                .create().show();
    }

    private void showDeleteDialog(final FormData note) {
        new AlertDialog.Builder(UploadActivity.this)
                .setTitle(R.string.dialog_delete_title)
                .setMessage(R.string.dialog_delete_message)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        try {
                            bioMapsResolver.deleteFormDataById(note.getId());
                        } catch (Exception e) {
                            Timber.e(e, "Delete failed.");
                        }
                    }
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .create().show();
    }


    private void retryUpload(final FormData note) {
        Toast.makeText(UploadActivity.this, R.string.toast_uploading, Toast.LENGTH_LONG).show();
        note.setState(FormData.State.CLOSED);
        note.setResponse("");
        try {
            bioMapsResolver.updateFormData(note);
            ((BioMapsApplication) getApplication()).requestSync();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle args) {
        String selection = FormDataTable.STATE + " != ?";
        String[] selectionArgs = new String[]{String.valueOf(FormData.State.CREATED.getValue())};
        String order = FormDataTable.DATE + " DESC";
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

    class ExportAsyncTask extends AsyncTask<FormData, Integer, String> {
        @Override
        protected String doInBackground(FormData... params) {
            try {
                ArrayList<FormData> notes = new ArrayList<>(Arrays.asList(params));
                if (notes.isEmpty()) {
                    notes = bioMapsResolver.getAllFormData();
                }

                int count = notes.size();
                for (int i = 0; i < count; i++) {
                    if (isCancelled()) break;
                    if (notes.get(i).getState() == FormData.State.CREATED) continue;

                    ExportHelper.exportNote(notes.get(i));
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
            barProgressDialog.setTitle(getString(R.string.dialog_export_progress_title));
            barProgressDialog.setMessage(getString(R.string.dialog_export_progress_progress));
            barProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            barProgressDialog.setProgress(0);
            barProgressDialog.setMax(100);
            barProgressDialog.setCancelable(false);
            barProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.dialog_export_progress_cancel), new DialogInterface.OnClickListener() {
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
