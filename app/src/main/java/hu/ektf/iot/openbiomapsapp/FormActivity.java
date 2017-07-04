package hu.ektf.iot.openbiomapsapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.List;

import hu.ektf.iot.openbiomapsapp.adapter.FormAdapter;
import hu.ektf.iot.openbiomapsapp.object.FormControl;
import hu.ektf.iot.openbiomapsapp.repo.ObmRepository;
import hu.ektf.iot.openbiomapsapp.repo.ObmRepositoryImpl;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class FormActivity extends AppCompatActivity {

    private ObmRepository repo = new ObmRepositoryImpl();

    private RecyclerView recyclerView;
    private FormAdapter adapter = new FormAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
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
                Snackbar.make(view, "Save form", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        loadForm();
    }

    private void loadForm() {
        repo.loadForm(0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<FormControl>>() {
                    @Override
                    public void call(List<FormControl> controls) {
                        adapter.setControls(controls);
                    }
                });
    }
}
