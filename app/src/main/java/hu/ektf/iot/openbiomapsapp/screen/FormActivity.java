package hu.ektf.iot.openbiomapsapp.screen;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

import hu.ektf.iot.openbiomapsapp.R;
import hu.ektf.iot.openbiomapsapp.util.JsonUtil;
import hu.ektf.iot.openbiomapsapp.model.FormControl;
import hu.ektf.iot.openbiomapsapp.model.FormData;
import hu.ektf.iot.openbiomapsapp.view.adapter.FormInputAdapter;
import hu.ektf.iot.openbiomapsapp.view.recyclerview.FormLinearLayoutManager;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class FormActivity extends BaseActivity {
    public static final String EXTRA_FORM_ID = "EXTRA_FORM_ID";

    private RecyclerView recyclerView;
    private FormInputAdapter adapter = new FormInputAdapter();

    private int formId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        formId = getIntent().getIntExtra(EXTRA_FORM_ID, -1);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        FormLinearLayoutManager layoutManager = new FormLinearLayoutManager(this);
        layoutManager.setListener(() -> {
            saveFormData();
        });

        recyclerView = findViewById(R.id.list);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            saveFormData();
        });
        loadForm();
    }

    private void loadForm() {
        repo.loadForm(formId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(controls -> adapter.setItems(controls), Timber::e);
    }

    private JSONObject getFormJson() {
        JSONObject object = new JSONObject();
        try {
            int childCount = recyclerView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = recyclerView.getChildAt(i);
                View input = child.findViewById(R.id.input);

                if (input == null) {
                    continue;
                }

                FormControl control = (FormControl) input.getTag(R.id.tag_form_control);

                if (control.getType() == FormControl.Type.BOOLEAN) {
                    object.put(control.getColumn(), ((CheckBox) input).isChecked());
                } else {
                    object.put(control.getColumn(), ((EditText) input).getText().toString());
                }
            }
        } catch (JSONException ex) {
            Timber.e(ex);
        }
        return object;
    }

    private void saveFormData() {
        JSONObject formJson = getFormJson();
        JSONArray columns = formJson.names();
        List<String> columnList = JsonUtil.arrayAsList(columns);

        FormData data = new FormData();
        data.setFormId(formId);
        data.setJson(formJson.toString());
        data.setColumns(columnList);
        data.setDate(new Date());
        data.setState(FormData.State.CLOSED);

        repo.saveData(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Toast.makeText(this, R.string.form_saved, Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(0);
                }, Timber::e);
    }
}
