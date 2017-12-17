package hu.ektf.iot.openbiomapsapp.screen;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.json.JSONObject;

import hu.ektf.iot.openbiomapsapp.R;
import hu.ektf.iot.openbiomapsapp.model.FormData;
import hu.ektf.iot.openbiomapsapp.view.adapter.FormDataAdapter;
import hu.ektf.iot.openbiomapsapp.view.recyclerview.DividerItemDecoration;
import hu.ektf.iot.openbiomapsapp.view.recyclerview.ItemClickSupport;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class SavedDataActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private FormDataAdapter adapter = new FormDataAdapter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_saved_data);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, R.drawable.divider));
        recyclerView.setAdapter(adapter);

        ItemClickSupport.addTo(recyclerView)
                .setOnItemClickListener((recyclerView, position, view) -> {
                    FormData note = adapter.getItemAtPosition(position);
                    showDetailDialog(note);
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

    private void showDetailDialog(final FormData note) {
        String prettyJSON = note.getResponse();
        try {
            int spacesToIndentEachLevel = 2;
            prettyJSON = new JSONObject(prettyJSON).toString(spacesToIndentEachLevel);
        } catch (Exception e) {
            Timber.e(e, "Format failed");
        }

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_details_title))
                .setMessage(getString(R.string.dialog_details_message, note.getUrl(), prettyJSON))
                .create().show();
    }
}
