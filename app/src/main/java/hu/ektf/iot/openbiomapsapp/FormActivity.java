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

import java.util.ArrayList;
import java.util.List;

import hu.ektf.iot.openbiomapsapp.adapter.FormAdapter;
import hu.ektf.iot.openbiomapsapp.object.FormControl;

public class FormActivity extends AppCompatActivity {

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
        List<FormControl> controls = new ArrayList<>();
        FormControl control = new FormControl();
        control.setType(FormControl.Type.TEXT);
        control.setShortName("Test Text");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.AUTOCOMPLETE);
        control.setShortName("Autocomplete control");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.BOOLEAN);
        control.setShortName("Checkity checkbox");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.DATE);
        control.setShortName("Here comes a date");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.NUMERIC);
        control.setShortName("Gimmi number");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.POINT);
        control.setShortName("Here comes a point");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.TEXT);
        control.setShortName("Test Text");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.AUTOCOMPLETE);
        control.setShortName("Autocomplete control");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.BOOLEAN);
        control.setShortName("Checkity checkbox");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.DATE);
        control.setShortName("Here comes a date");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.NUMERIC);
        control.setShortName("Gimmi number");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.POINT);
        control.setShortName("Here comes a point");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.TEXT);
        control.setShortName("Test Text");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.AUTOCOMPLETE);
        control.setShortName("Autocomplete control");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.BOOLEAN);
        control.setShortName("Checkity checkbox");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.DATE);
        control.setShortName("Here comes a date");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.NUMERIC);
        control.setShortName("Gimmi number");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.POINT);
        control.setShortName("Here comes a point");
        controls.add(control);

        adapter.setControls(controls);
    }

}
