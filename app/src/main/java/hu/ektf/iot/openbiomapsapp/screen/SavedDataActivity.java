package hu.ektf.iot.openbiomapsapp.screen;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.Toast;

import org.json.JSONObject;

import javax.inject.Inject;

import hu.ektf.iot.openbiomapsapp.R;
import hu.ektf.iot.openbiomapsapp.model.FormData;
import hu.ektf.iot.openbiomapsapp.repo.database.StorageHelper;
import hu.ektf.iot.openbiomapsapp.view.adapter.FormDataAdapter;
import hu.ektf.iot.openbiomapsapp.view.recyclerview.DividerItemDecoration;
import hu.ektf.iot.openbiomapsapp.view.recyclerview.ItemClickSupport;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class SavedDataActivity extends BaseActivity {

    @Inject
    StorageHelper storage;

    private RecyclerView recyclerView;
    private FormDataAdapter adapter = new FormDataAdapter();

    private String projectName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_data);

        projectName = storage.getProjectName();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, R.drawable.divider));
        recyclerView.setAdapter(adapter);

        ItemClickSupport.addTo(recyclerView)
                .setOnItemClickListener((recyclerView, position, view) -> {
                    FormData formData = adapter.getItemAtPosition(position);

                    if (!TextUtils.equals(projectName, formData.getProjectName())) {
                        Toast.makeText(SavedDataActivity.this,
                                R.string.error_another_project,
                                Toast.LENGTH_LONG)
                                .show();
                        return;
                    }

                    startRetry(formData);
                })
                .setOnItemLongClickListener((recyclerView, position, v) -> {
                    FormData formData = adapter.getItemAtPosition(position);
                    showDetailDialog(formData);
                    return true;
                });

        loadData();
    }

    @Override
    protected void onDestroy() {
        ItemClickSupport.removeFrom(recyclerView);
        super.onDestroy();
    }

    private void loadData() {
        repo.getSavedFormDataAsync()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(savedDataList -> adapter.swapItems(savedDataList), Timber::e);
    }

    private void startRetry(final FormData formData) {
        Intent intent = new Intent(SavedDataActivity.this, FormActivity.class);
        intent.putExtra(FormActivity.EXTRA_FORM_ID, formData.getFormId());
        intent.putExtra(FormActivity.EXTRA_FORM_DATA_ID, formData.getId());
        startActivity(intent);
    }

    private void showDetailDialog(final FormData formData) {
        String prettyJSON = formData.getResponse();
        try {
            int spacesToIndentEachLevel = 2;
            prettyJSON = new JSONObject(prettyJSON).toString(spacesToIndentEachLevel);
        } catch (Exception e) {
            Timber.e(e, "Format failed");
        }

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_details_title))
                .setMessage(getString(R.string.dialog_details_message, formData.getProjectName(), prettyJSON))
                .create().show();
    }
}
