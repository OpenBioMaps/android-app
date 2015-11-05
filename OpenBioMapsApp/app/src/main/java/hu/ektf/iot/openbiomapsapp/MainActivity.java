package hu.ektf.iot.openbiomapsapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import hu.ektf.iot.openbiomapsapp.adapter.ImageListAdapter;
import hu.ektf.iot.openbiomapsapp.helper.GpsHandler;
import hu.ektf.iot.openbiomapsapp.helper.StorageHelper;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity {

    //Static stuffs

    final public static String END_POINT = "http://openbiomaps.org/pds";

    //REQ CODES
    final static int REQUESTCODE_RECORDING = 1;
    private static final int REQ_IMAGE_CHOOSER = 2;
    private static final int REQ_CAMERA = 3;

    //Gps stuffs
    GpsHandler gpsHandler;
    Location currentLocation;

    //Views
    EditText etNote;
    TextView tvPosition, tvVoiceRecords;
    Button tvButton, buttonShowMap, buttonAudioRecord, buttonGet, buttonCamera;
    private RecyclerView imageList;
    private ImageListAdapter adapter;
    private ArrayList<String> imagesList = new ArrayList<>();
    private String selectedImagePath;
    private File imageFile;

    //retrofit
    IDownloader downloader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            selectedImagePath = savedInstanceState.getString("selectedImagePath");
            ArrayList<String> siImagesList = savedInstanceState.getStringArrayList("imagesList");
            if (siImagesList != null) {
                imagesList.addAll(siImagesList);
            }
        }
        setContentView(R.layout.activity_main);
        gpsHandler = new GpsHandler(MainActivity.this);
        RestAdapter retrofit = new RestAdapter.Builder().setEndpoint(END_POINT).build();
        downloader = retrofit.create(IDownloader.class);

        //Getting the views
        etNote = (EditText) findViewById(R.id.etNote);
        tvPosition = (TextView) findViewById(R.id.textViewPosition);
        tvVoiceRecords = (TextView) findViewById(R.id.textViewAudioRecords);
        tvButton = (Button) findViewById(R.id.buttonPosition);
        buttonShowMap = (Button) findViewById(R.id.buttonShowMap);
        buttonAudioRecord = (Button) findViewById(R.id.buttonAudioRecord);
        buttonGet = (Button) findViewById(R.id.buttonGet);
        imageList = (RecyclerView) findViewById(R.id.imageList);
        buttonCamera = (Button) findViewById(R.id.buttonCamera);
		buttonLocal = (Button) findViewById(R.id.buttonLocal);
        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSDPresent()) {
                    showImageSourceDialog();
                }
            }
        });

        tvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonShowMap.setEnabled(false);
                final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    buildAlertMessageNoGps();
                    return;
                }
                currentLocation = gpsHandler.getLocation();
                //We dont reach this part if the gps is off
                if (currentLocation != null) {
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
                String uriString = "geo:" + currentLocation.getLatitude() + "," + currentLocation.getLongitude();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriString));
                startActivity(intent);
            }
        });

        int unitWidth = getListUnitWidth();
        adapter = new ImageListAdapter(imagesList);
        adapter.setImageSize(unitWidth);

        adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ImagePagerActivity.class);
                intent.putStringArrayListExtra(ImagePagerActivity.ARG_IMAGES, imagesList);
                intent.putExtra(ImagePagerActivity.ARG_POS, position);
                startActivity(intent);
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        imageList.setLayoutManager(layoutManager);
        setListHeight(unitWidth);
        imageList.setHasFixedSize(true);
        imageList.setAdapter(adapter);

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
                        Log.d("success", s.toString());
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
                        Log.d("result", result);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d("Error", error.toString());
                    }
                });
            }
        });

        buttonLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues contentValues = new ContentValues();

                contentValues.put(LocalDB.COMMENT,etNote.getText().toString());
                contentValues.put(LocalDB.GEOMETRY,etNote.getText().toString()+" geometry teszt");
                contentValues.put(LocalDB.SOUND_FILE,etNote.getText().toString()+" soundfile teszt");
                contentValues.put(LocalDB.IMAGE_FILE,etNote.getText().toString()+" imagefile teszt");
                Uri uri = getContentResolver().insert(LocalDB.CONTENT_URI,contentValues);

                Toast.makeText(getBaseContext(),
                        uri.toString(), Toast.LENGTH_LONG).show();
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("selectedImagePath", selectedImagePath);
        outState.putStringArrayList("imagesList", imagesList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQ_IMAGE_CHOOSER) {
            if (resultCode == RESULT_OK) {
                Uri selectedImageUri = intent.getData();
                selectedImagePath = selectedImageUri.getPath();
                String galleryPath = getPath(selectedImageUri);
                if (galleryPath != null) {
                    selectedImagePath = galleryPath;
                }
                imageFile = new File(selectedImagePath);
            }
        }

        if (requestCode == REQ_CAMERA) {
            if (resultCode == RESULT_OK) {
                imageFile = new File(selectedImagePath);
            } else {
                imageFile = null;
            }
        }

        if (imageFile != null) {
            String local = "file://" + imageFile.getPath();
            imagesList.add(local);
            adapter.notifyDataSetChanged();
            imageList.setVisibility(View.VISIBLE);
        }
        //TODO: need to fix RESULT_CODE_CANCELLED in case replaying record
        if (requestCode == REQUESTCODE_RECORDING) {
            if (resultCode == RESULT_OK) {
                Uri audioUri = intent.getData();
                String fileName = audioUri.getLastPathSegment() + ".3gp";
                Toast.makeText(getApplicationContext(), fileName + " hangfelvétel hozzáadva.", Toast.LENGTH_LONG).show();
                tvVoiceRecords.setText(tvVoiceRecords.getText() + fileName + " ");
                //TODO: save audio to local db
            } else {
                Toast.makeText(getApplicationContext(), R.string.error_audio_capture_text, Toast.LENGTH_LONG).show();
            }
        } else {
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

    private int getListUnitWidth() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        float unitDpWidth = (((dpWidth - 16) / 3) + 5);
        // TODO Do it more robust (using getDimension)
        int unitPixelWidth = (int) (unitDpWidth * displayMetrics.density);
        return unitPixelWidth;
    }

    private void setListHeight(int height) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageList.getLayoutParams();
        params.height = height;
        imageList.setLayoutParams(params);
    }

    /**
     * This function shows an Dialog helping the image uploading process.
     */
    private void showImageSourceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.title_image_source_dialog);
        builder.setNeutralButton(R.string.image_source_cancel, null);
        builder.setItems(R.array.image_source, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        dispatchChooseImageIntent();
                        break;
                    case 1:
                        dispatchTakePictureIntent();
                        break;
                    default:
                        break;
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void dispatchChooseImageIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.title_image_chooser)),
                REQ_IMAGE_CHOOSER);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQ_CAMERA);
            } else {
                Toast.makeText(this, R.string.no_file, Toast.LENGTH_LONG).show();
            }
        }
    }

    @SuppressLint("NewApi")
    private String getPath(Uri uri) {
        if (uri == null) {
            return null;
        }

        String[] projection = {MediaStore.Images.Media.DATA};

        Cursor cursor;
        if (Build.VERSION.SDK_INT > 19) {
            // Will return "image:x*"
            String wholeID = DocumentsContract.getDocumentId(uri);
            // Split at colon, use second item in the array
            String id = wholeID.contains(":") ? wholeID.split(":")[1] : wholeID;
            // where id is equal to
            String sel = MediaStore.Images.Media._ID + "=?";

            cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection, sel, new String[]{id}, null);
        } else {
            cursor = getContentResolver().query(uri, projection, null, null, null);
        }
        String path = null;

        try {
            int column_index = cursor
                    .getColumnIndex(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            path = cursor.getString(column_index).toString();
            cursor.close();
        } catch (NullPointerException e) {
            // Nothing to do, will return null
        }

        return path;
    }

    /**
     * This method creates an File object helping the image uploading process.
     *
     * @return The File object of the image.
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        selectedImagePath = image.getAbsolutePath();
        return image;
    }

    private static boolean isSDPresent() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }
}
