package hu.ektf.iot.openbiomapsapp;

import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

import hu.ektf.iot.openbiomapsapp.adapter.UploadListAdapter;
import hu.ektf.iot.openbiomapsapp.adapter.DividerItemDecoration;
import hu.ektf.iot.openbiomapsapp.database.NoteTable;
import hu.ektf.iot.openbiomapsapp.object.Note;

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

        ArrayList<Note> listObjects = new ArrayList<Note>();

        // TODO Use BioMapsResolver
        String URL = "content://hu.ektf.iot.openbiomapsapp/storage";

        Uri storage = Uri.parse(URL);
        Cursor c = managedQuery(storage,null,null,null,"_ID");
        if (c.moveToFirst()) {
            do{

                String id = c.getString(c.getColumnIndex(NoteTable._ID));
                String latitude = c.getString(c.getColumnIndex(NoteTable.LATITUDE));
                String longitude = c.getString(c.getColumnIndex(NoteTable.LONGITUDE));
                String comment = c.getString(c.getColumnIndex(NoteTable.COMMENT));
                String sound_file = c.getString(c.getColumnIndex(NoteTable.SOUND_FILES));
                String image_file = c.getString(c.getColumnIndex(NoteTable.IMAGE_FILES));
                String response = c.getString(c.getColumnIndex(NoteTable.RESPONSE));
                String date = c.getString(c.getColumnIndex(NoteTable.DATE));

                        Log.d("In storage, ID: ", id
                                        + ", COMMENT: " + comment
                                        + ", LATITUDE: " + latitude
                                        + ", LONGITUDE: " + longitude
                                        + ", SOUND_FILE" + sound_file
                                        + ", IMAGE_FILE" + image_file
                                        + ", RESPONSE" + response
                                        + ", DATE" + date
                        );
                Location locfromdb = new Location(LocationManager.PASSIVE_PROVIDER);
                locfromdb.setLatitude(Double.valueOf(latitude));
                locfromdb.setLongitude(Double.valueOf(longitude));

                ArrayList<String> soundsfromdb = new ArrayList<String>(Arrays.asList(sound_file.split(",")));
                ArrayList<String> imagesfromdb = new ArrayList<String>(Arrays.asList(image_file.split(",")));
                Note nr = new Note(null, comment, locfromdb, date, soundsfromdb, imagesfromdb, Integer.valueOf(response));
                listObjects.add(nr);

            } while (c.moveToNext());
        }

        mAdapter = new UploadListAdapter(listObjects);
        mRecyclerView.setAdapter(mAdapter);
    }
}
