package hu.ektf.iot.openbiomapsapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import hu.ektf.iot.openbiomapsapp.adapter.UploadListAdapter;
import hu.ektf.iot.openbiomapsapp.helper.DividerItemDecoration;
import hu.ektf.iot.openbiomapsapp.object.ListObject;

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

        ArrayList<ListObject> listObjects = new ArrayList<ListObject>();
        ListObject lo = new ListObject();
        lo.setDate("2015.11.21 - 11:16");
        lo.setImagesList(null);
        lo.setNote("Két darab cinege a fán, jaj de szépek, teszt szöveg");
        lo.setStatus(1);
        lo.setSoundsList(null);
        lo.setLocation(null);
        listObjects.add(lo);

        ListObject lo2 = new ListObject();
        lo2.setDate("2015.11.15 - 15:33");
        lo2.setImagesList(null);
        lo2.setNote("Döglött madar lefotozoom oohh je");
        lo2.setStatus(0);
        lo2.setSoundsList(null);
        lo2.setLocation(null);
        listObjects.add(lo2);

        ListObject lo3 = new ListObject();
        lo3.setDate("2015.11.11 - 21:20");
        lo3.setImagesList(null);
        lo3.setNote("Ez itt egy leírás, note meg tesztszövegek meg mindenek");
        lo3.setStatus(1);
        lo3.setSoundsList(null);
        lo3.setLocation(null);
        listObjects.add(lo3);

        ListObject lo4 = new ListObject();
        lo4.setDate("2015.11.01 - 10:02");
        lo4.setImagesList(null);
        lo4.setNote("Ismét leírás vagyok nagyon rövid");
        lo4.setStatus(0);
        lo4.setSoundsList(null);
        lo4.setLocation(null);
        listObjects.add(lo4);

        mAdapter = new UploadListAdapter(listObjects);
        mRecyclerView.setAdapter(mAdapter);
    }
}
