package hu.ektf.iot.openbiomapsapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import hu.ektf.iot.openbiomapsapp.adapter.UploadListAdapter;
import hu.ektf.iot.openbiomapsapp.adapter.DividerItemDecoration;
import hu.ektf.iot.openbiomapsapp.object.NoteRecord;

public class UploadActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerUploadList);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addItemDecoration(
                new DividerItemDecoration(this, R.drawable.divider));

        ArrayList<NoteRecord> listObjects = new ArrayList<NoteRecord>();
        NoteRecord lo = new NoteRecord("Két darab cinege a fán, jaj de szépek, teszt szöveg",null,"2015.11.21 - 11:16",null,null,0);
        listObjects.add(lo);

        NoteRecord lo2 = new NoteRecord("Két darab cinege a fán, jaj de szépek, teszt szöveg",null,"2015.11.21 - 11:16",null,null,0);
        listObjects.add(lo2);

        NoteRecord lo3 = new NoteRecord("Két darab cinege a fán, jaj de szépek, teszt szöveg",null,"2015.11.21 - 11:16",null,null,0);
        listObjects.add(lo3);

        NoteRecord lo4 = new NoteRecord("Két darab cinege a fán, jaj de szépek, teszt szöveg",null,"2015.11.21 - 11:16",null,null,0);
        listObjects.add(lo4);

        mAdapter = new UploadListAdapter(listObjects);
        mRecyclerView.setAdapter(mAdapter);
    }
}
