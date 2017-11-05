package hu.ektf.iot.openbiomapsapp.screen;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import hu.ektf.iot.openbiomapsapp.BioMapsApplication;
import hu.ektf.iot.openbiomapsapp.R;
import hu.ektf.iot.openbiomapsapp.view.recyclerview.DividerItemDecoration;
import hu.ektf.iot.openbiomapsapp.view.adapter.FormDataAdapter;
import hu.ektf.iot.openbiomapsapp.helper.ExportHelper;
import hu.ektf.iot.openbiomapsapp.helper.StorageHelper;
import hu.ektf.iot.openbiomapsapp.model.FormData;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class UploadActivity extends BaseActivity {

    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private FormDataAdapter adapter = new FormDataAdapter();
    private Button buttonExportAll;
    private TextView tvEmpty;

    private ExportAsyncTask exportTask;
    private StorageHelper sharedPrefStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        sharedPrefStorage = new StorageHelper(this);

        buttonExportAll = findViewById(R.id.buttonExport);
        buttonExportAll.setOnClickListener(view -> {
            ExportAsyncTask exportTask = new ExportAsyncTask();
            exportTask.execute();
        });

        tvEmpty = findViewById(R.id.textViewEmpty);

        recyclerView = findViewById(R.id.recyclerUploadList);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(
                new DividerItemDecoration(this, R.drawable.divider));

        adapter.setOnItemClickListener((adapterView, view, position, id) -> {
            FormData note = adapter.getItem(position);
            showDetailDialog(note);
        });

        adapter.setOnItemLongClickListener((parent, view, position, id) -> {
            FormData note = adapter.getItem(position);
            showContextMenu(note);
            return false;
        });

        recyclerView.setAdapter(adapter);

        loadList();
    }

    private void loadList() {
        repo.getSavedFormDataAsync()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dataList -> {
                    adapter.swapItems(dataList);

                    if (dataList.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                        buttonExportAll.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        tvEmpty.setVisibility(View.GONE);
                        buttonExportAll.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void showDetailDialog(final FormData note) {
        String prettyJSON = note.getResponse();
        try {
            int spacesToIndentEachLevel = 2;
            prettyJSON = new JSONObject(prettyJSON).toString(spacesToIndentEachLevel);
        } catch (Exception e) {
            Timber.e(e, "Format failed");
        }
        new AlertDialog.Builder(UploadActivity.this)
                .setTitle(getString(R.string.dialog_details_title))
                .setMessage(getString(R.string.dialog_details_message, note.getUrl(), prettyJSON))
                .create().show();
    }

    private void showContextMenu(final FormData note) {
        new AlertDialog.Builder(UploadActivity.this)
                .setItems(R.array.note_context, (dialog, which) -> {
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
                })
                .create().show();
    }

    private void showExportDialog(final FormData note) {
        new AlertDialog.Builder(UploadActivity.this)
                .setTitle(getString(R.string.dialog_export_title))
                .setMessage(getString(R.string.dialog_export_path, sharedPrefStorage.getExportPath()))
                .setPositiveButton(getString(R.string.dialog_export_text_yes), (arg0, arg1) -> {
                    ExportAsyncTask exportTask = new ExportAsyncTask();
                    exportTask.execute(note);
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .create().show();
    }

    private void showChangeUrlDialog(final FormData formData) {
        LayoutInflater li = LayoutInflater.from(UploadActivity.this);
        View dialogView = li.inflate(R.layout.dialog_server_settings, null);

        final EditText etServerUrl = dialogView
                .findViewById(R.id.etServerUrl);
        etServerUrl.setText(formData.getUrl());
        etServerUrl.setSelection(etServerUrl.getText().length());

        new AlertDialog.Builder(
                UploadActivity.this)
                .setView(dialogView)
                .setCancelable(false)
                .setTitle(R.string.dialog_change_url_title)
                .setPositiveButton(R.string.save,
                        (dialog, id) -> {
                            String newUrl = etServerUrl.getText().toString();
                            if (TextUtils.isEmpty(newUrl) || TextUtils.equals(formData.getUrl(), newUrl)) {
                                return;
                            }

                            formData.setState(FormData.State.CLOSED);
                            formData.setResponse("");
                            formData.setUrl(newUrl);

                            repo.saveData(formData)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe();
                            ((BioMapsApplication) getApplication()).requestSync();
                        })
                .setNegativeButton(R.string.cancel, null)
                .create().show();
    }

    private void showDeleteDialog(final FormData formData) {
        new AlertDialog.Builder(UploadActivity.this)
                .setTitle(R.string.dialog_delete_title)
                .setMessage(R.string.dialog_delete_message)
                .setPositiveButton(getString(R.string.yes), (arg0, arg1) -> {

                    repo.deleteData(formData)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe();
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .create().show();
    }


    private void retryUpload(final FormData formData) {
        Toast.makeText(UploadActivity.this, R.string.toast_uploading, Toast.LENGTH_LONG).show();
        formData.setState(FormData.State.CLOSED);
        formData.setResponse("");

        repo.saveData(formData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
        ((BioMapsApplication) getApplication()).requestSync();
    }

    // TODO Make it completable
    class ExportAsyncTask extends AsyncTask<FormData, Integer, String> {
        @Override
        protected String doInBackground(FormData... params) {
            try {
                List<FormData> notes = Arrays.asList(params);

                if (notes.isEmpty()) {
                    notes = repo.getSavedFormData();
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
            progressDialog.hide();

        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(UploadActivity.this);
            progressDialog.setTitle(getString(R.string.dialog_export_progress_title));
            progressDialog.setMessage(getString(R.string.dialog_export_progress_progress));
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setProgress(0);
            progressDialog.setMax(100);
            progressDialog.setCancelable(false);
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.dialog_export_progress_cancel), (dialog, which) -> {
                if (exportTask != null) {
                    exportTask.cancel(false);
                    exportTask = null;
                }

                dialog.dismiss();
            });
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setProgress(values[0]);
        }
    }
}
