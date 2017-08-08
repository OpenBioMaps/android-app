package hu.ektf.iot.openbiomapsapp.screen;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

import hu.ektf.iot.openbiomapsapp.BioMapsApplication;
import hu.ektf.iot.openbiomapsapp.R;
import hu.ektf.iot.openbiomapsapp.adapter.FormAdapter;
import hu.ektf.iot.openbiomapsapp.database.BioMapsResolver;
import hu.ektf.iot.openbiomapsapp.model.FormControl;
import hu.ektf.iot.openbiomapsapp.model.FormData;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class FormActivity extends BaseActivity {

    public static final String EXTRA_FORM_ID = "EXTRA_FORM_ID";

    private RecyclerView recyclerView;
    private FormAdapter adapter = new FormAdapter();

    private BioMapsResolver bioMapsResolver;
    private int formId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        bioMapsResolver = new BioMapsResolver(this);
        formId = getIntent().getIntExtra(EXTRA_FORM_ID, -1);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveFormData();

                ((BioMapsApplication) getApplication()).requestSync();
            }
        });
        loadForm();
    }

    private void loadForm() {
        repo.loadForm(formId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<FormControl>>() {
                    @Override
                    public void call(List<FormControl> controls) {
                        adapter.setControls(controls);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Timber.e(throwable);
                    }
                });
    }

    private JSONObject getFormJson() {
        JSONObject object = new JSONObject();
        try {
            int childCount = recyclerView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = recyclerView.getChildAt(i);
                View input = child.findViewById(R.id.input);
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
        try {
            JSONObject formJson = getFormJson();

            FormData data = new FormData();
            data.setFormId(formId);
            data.setJson(formJson.toString());
            data.setDate(new Date());
            data.setState(FormData.State.CLOSED);
            Object o = bioMapsResolver.insertOrUpdateFormData(data);
            Timber.i("InsertOrUpdate result: %s", o.toString());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}