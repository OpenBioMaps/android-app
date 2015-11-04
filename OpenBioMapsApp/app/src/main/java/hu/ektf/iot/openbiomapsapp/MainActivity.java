package hu.ektf.iot.openbiomapsapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import hu.ektf.iot.openbiomapsapp.helper.GpsHandler;
import hu.ektf.iot.openbiomapsapp.helper.StorageHelper;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity {

    //Static stuffs
    final static int REQUESTCODE_RECORDING = 1;
    final static String END_POINT = "http://openbiomaps.org/pds";

    //Gps stuffs
    GpsHandler gpsHandler;
    Location currentLocation;

    //Views
    TextView tvPosition, tvVoiceRecords;
    Button tvButton, buttonShowMap, buttonAudioRecord, buttonGet;

    //retrofit
    IDownloader downloader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gpsHandler = new GpsHandler(MainActivity.this);

        RestAdapter retrofit = new RestAdapter.Builder().setEndpoint(END_POINT).build();

        downloader = retrofit.create(IDownloader.class);

        //Getting the views
        tvPosition = (TextView) findViewById(R.id.textViewPosition);
        tvVoiceRecords = (TextView) findViewById(R.id.textViewAudioRecords);
        tvButton = (Button) findViewById(R.id.buttonPosition);
        buttonShowMap = (Button) findViewById(R.id.buttonShowMap);
        buttonAudioRecord = (Button) findViewById(R.id.buttonAudioRecord);
        buttonGet = (Button) findViewById(R.id.buttonGet);

        tvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonShowMap.setEnabled(false);
                final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

                if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                    buildAlertMessageNoGps();
                    return;
                }
                currentLocation = gpsHandler.getLocation();
                //We dont reach this part if the gps is off
                if(currentLocation!=null) {
                    tvPosition.setText("szélesség: " + currentLocation.getLatitude() + "\nhosszúság: " + currentLocation.getLongitude());
                    buttonShowMap.setEnabled(true);
                } else {
                    tvPosition.setText(R.string.no_gps_data);
                }
            }
        });

        buttonShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uriString = "geo:"+currentLocation.getLatitude()+","+currentLocation.getLongitude();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriString));
                startActivity(intent);
            }
        });

        buttonAudioRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =
                        new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                if (isAvailable(MainActivity.this, intent)) {
                    startActivityForResult(intent,
                            REQUESTCODE_RECORDING);
                }
            }
        });

        buttonGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 downloader.getUploadedData(new Callback<Response>() {
                     @Override
                     public void success(Response s, Response response) {
                         Log.d("success",s.toString());
                         //Try to get response body
                         BufferedReader reader = null;
                         StringBuilder sb = new StringBuilder();
                         try {

                             reader = new BufferedReader(new InputStreamReader(s.getBody().in()));

                             String line;

                             try {
                                 while ((line = reader.readLine()) != null) {
                                     sb.append(line);
                                 }
                             } catch (IOException e) {
                                 e.printStackTrace();
                             }
                         } catch (IOException e) {
                             e.printStackTrace();
                         }


                         String result = sb.toString();
                         Log.d("result",result);
                     }

                     @Override
                     public void failure(RetrofitError error) {
                        Log.d("Error",error.toString());
                     }
                 });
            }
        });


    }

    @Override
    protected void onResume() {
        gpsHandler.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        gpsHandler.onPause();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUESTCODE_RECORDING) {
            if (resultCode == RESULT_OK) {
                Uri audioUri = intent.getData();
                String fileName = audioUri.getLastPathSegment() + ".3gp";
                Toast.makeText(getApplicationContext(), fileName + " hangfelvétel hozzáadva.", Toast.LENGTH_LONG).show();
                tvVoiceRecords.setText(tvVoiceRecords.getText() + fileName + " ");
                //TODO: save audio to local db
            }
            else {
                Toast.makeText(getApplicationContext(),R.string.error_audio_capture_text,Toast.LENGTH_LONG).show();
            }
        }
        else {
            super.onActivityResult(requestCode,
                    resultCode, intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_server_settings) {
            LayoutInflater li = LayoutInflater.from(MainActivity.this);
            View dialogView = li.inflate(R.layout.dialog_server_settings, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    MainActivity.this);
            alertDialogBuilder.setView(dialogView);

            final EditText etServerUrl = (EditText) dialogView
                    .findViewById(R.id.etServerUrl);

            final StorageHelper sh = new StorageHelper(MainActivity.this);
            etServerUrl.setText(sh.getServerUrl());
            etServerUrl.setSelection(etServerUrl.getText().length());

            alertDialogBuilder
                    .setCancelable(false)
                    .setTitle(R.string.settings_server_title)
                    .setPositiveButton(R.string.settings_save,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    sh.setServerUrl(etServerUrl.getText().toString());
                                }
                            })
                    .setNegativeButton(R.string.settings_cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_text_gps)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_text_yes, new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(R.string.dialog_text_no, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    //this method checks that the common intent is available or not
    public static boolean isAvailable(Context ctx, Intent intent) {
        final PackageManager mgr = ctx.getPackageManager();
        List<ResolveInfo> list =
                mgr.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
}
