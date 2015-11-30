package hu.ektf.iot.openbiomapsapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.Arrays;

import hu.ektf.iot.openbiomapsapp.adapter.UploadListAdapter;
import hu.ektf.iot.openbiomapsapp.adapter.DividerItemDecoration;
import hu.ektf.iot.openbiomapsapp.database.BioMapsContentProvider;
import hu.ektf.iot.openbiomapsapp.database.NoteTable;
import hu.ektf.iot.openbiomapsapp.helper.ExportHelper;
import hu.ektf.iot.openbiomapsapp.helper.StorageHelper;
import hu.ektf.iot.openbiomapsapp.object.Note;

public class UploadActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private UploadListAdapter mAdapter;
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

        final ArrayList<Note> listObjects = new ArrayList<Note>();

        // TODO Use BioMapsResolver
        Cursor c = managedQuery(BioMapsContentProvider.CONTENT_URI,null,null,null,"_ID");
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
        final StorageHelper sh = new StorageHelper(UploadActivity.this);

        mAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UploadActivity.this);

                alertDialogBuilder.setTitle("Exportálás");
                alertDialogBuilder.setMessage("Az exportálás helye:\n" + sh.getExportPath());

                alertDialogBuilder.setPositiveButton("Exportálás", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Note n = listObjects.get(position);
                        try {
                            ExportHelper.exportNote(n);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                alertDialogBuilder.setNegativeButton("Mégse", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }
}
