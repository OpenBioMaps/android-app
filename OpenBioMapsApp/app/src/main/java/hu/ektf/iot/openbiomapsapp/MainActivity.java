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

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import hu.ektf.iot.openbiomapsapp.adapter.AudioListAdapter;
import hu.ektf.iot.openbiomapsapp.adapter.ImageListAdapter;
import hu.ektf.iot.openbiomapsapp.helper.GpsHandler;
import hu.ektf.iot.openbiomapsapp.helper.StorageHelper;
import retrofit.RestAdapter;

public class MainActivity extends AppCompatActivity {

    //Static stuffs
    final public static String END_POINT = "http://openbiomaps.org/pds";

    //REQ CODES
    final static int REQ_RECORDING = 1;
    private static final int REQ_IMAGE_CHOOSER = 2;
    private static final int REQ_CAMERA = 3;

    //Gps stuffs
    GpsHandler gpsHandler;
    Location currentLocation;

    //Views
    EditText etNote;
    TextView tvPosition;
    Button buttonPosition, buttonShowMap, buttonAudioRecord, buttonCamera, buttonSaveLocal, buttonReset;
    private RecyclerView imageRecycler, audioRecycler;
    private ImageListAdapter adapterImage;
    private AudioListAdapter adapterAudio;
    private ArrayList<String> imagesList = new ArrayList<>();
    private ArrayList<String> audiosList = new ArrayList<>();

    //retrofit
    private IDownloader downloader;

    //LocalDB management
    private String formattedPosition;
    private Integer currentRecordId;
    private String selectedImagePath;
    private String soundPath = "";
    private File imageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            selectedImagePath = savedInstanceState.getString("selectedImagePath");
            ArrayList<String> siImagesList = savedInstanceState.getStringArrayList("imagesList");
            ArrayList<String> siAudiosList = savedInstanceState.getStringArrayList("audiosList");
            if (siImagesList != null) {
                imagesList.addAll(siImagesList);
            }
            if (siAudiosList != null) {
                audiosList.addAll(siAudiosList);
            }
        }
        setContentView(R.layout.activity_main);
        gpsHandler = new GpsHandler(MainActivity.this);
        RestAdapter retrofit = new RestAdapter.Builder().setEndpoint(END_POINT).build();
        downloader = retrofit.create(IDownloader.class);

        //Getting the views
        etNote = (EditText) findViewById(R.id.etNote);
        tvPosition = (TextView) findViewById(R.id.textViewPosition);
        buttonPosition = (Button) findViewById(R.id.buttonPosition);
        buttonShowMap = (Button) findViewById(R.id.buttonShowMap);
        buttonAudioRecord = (Button) findViewById(R.id.buttonAudioRecord);
        imageRecycler = (RecyclerView) findViewById(R.id.imageRecycler);
        audioRecycler = (RecyclerView) findViewById(R.id.audioRecycler);
        buttonCamera = (Button) findViewById(R.id.buttonCamera);
        buttonSaveLocal = (Button) findViewById(R.id.buttonSaveLocal);
        buttonReset = (Button) findViewById(R.id.buttonReset);

        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSDPresent()) {
                    showImageSourceDialog();
                }
            }
        });

        if (imagesList.size() == 0) {
            imageRecycler.setVisibility(View.GONE);
        }
        if (audiosList.size() == 0) {
            audioRecycler.setVisibility(View.GONE);
        }

        buttonPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonShowMap.setEnabled(false);
                final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    buildAlertMessageNoGps();
                    return;
                }
                currentLocation = gpsHandler.getLocation();
                if (currentLocation != null) {
                    tvPosition.setText("szélesség: " + currentLocation.getLatitude() + "\nhosszúság: " + currentLocation.getLongitude());
                    formattedPosition = "szélesség: " + currentLocation.getLatitude() + " hosszúság: " + currentLocation.getLongitude();
                    buttonShowMap.setEnabled(true);
                    buttonSaveLocal.setEnabled(true);
                } else {
                    tvPosition.setText(R.string.no_gps_data);
                }

                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                SaveLocal(setContentValues(etNote.getText().toString(),tvPosition.getText().toString(),soundPath,selectedImagePath,dateFormat.format(date),0));
            }
        });

        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetFields();
                currentRecordId = null;
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
        adapterImage = new ImageListAdapter(imagesList);
        adapterAudio = new AudioListAdapter(audiosList);
        adapterImage.setImageSize(unitWidth);
        adapterAudio.setImageSize(unitWidth - 5);

        adapterImage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ImagePagerActivity.class);
                intent.putStringArrayListExtra(ImagePagerActivity.ARG_IMAGES, imagesList);
                intent.putExtra(ImagePagerActivity.ARG_POS, position);
                startActivity(intent);
            }
        });

        adapterAudio.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                String audiourl = "content://media/external/audio/media/" + audiosList.get(position);
                Uri myUri = Uri.parse(audiourl);
                intent.setDataAndType(myUri, "audio/*");
                startActivity(intent);
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        imageRecycler.setLayoutManager(layoutManager);
        RecyclerView.LayoutManager layoutAudio = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        audioRecycler.setLayoutManager(layoutAudio);
        setListHeight(unitWidth);
        imageRecycler.setHasFixedSize(true);
        audioRecycler.setHasFixedSize(true);
        imageRecycler.setAdapter(adapterImage);
        audioRecycler.setAdapter(adapterAudio);

        buttonAudioRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =
                        new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                if (isAvailable(MainActivity.this, intent)) {
                    startActivityForResult(intent,
                            REQ_RECORDING);
                }
            }
        });
        /*
        buttonSaveLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(LocalDB.COMMENT,etNote.getText().toString());
                contentValues.put(LocalDB.GEOMETRY,formattedPosition);
                contentValues.put(LocalDB.SOUND_FILE,soundPath);
                contentValues.put(LocalDB.IMAGE_FILE,selectedImagePath);
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                contentValues.put(LocalDB.DATE,dateFormat.format(date));
                contentValues.put(LocalDB.RESPONSE, "0"); //TODO save the proper response code
                Uri uri = getContentResolver().insert(LocalDB.CONTENT_URI,contentValues);

                Toast.makeText(getBaseContext(),
                        uri.toString(), Toast.LENGTH_LONG).show();
                Log.d("LocalDB record",etNote.getText().toString()
                        +" "+formattedPosition+" "+soundPath+" "+selectedImagePath+" "+dateFormat.format(date)+
                " "+"0");
            }
        });*/
    }

    private boolean SaveLocal(ContentValues contentValues) {
        currentRecordId = getCurrentRecordId();
        if(currentRecordId>0)
        {
            //TODO UPDATE
        }
        else
        {
            //TODO INSERT
        }
        return true;
    }

    private ContentValues setContentValues(String note, String position, String soundPath, String imagePath, String date, int response) {

        ContentValues contentValues = new ContentValues();

        contentValues.put(LocalDB.COMMENT,note);
        contentValues.put(LocalDB.GEOMETRY,position);
        contentValues.put(LocalDB.SOUND_FILE,soundPath);
        contentValues.put(LocalDB.IMAGE_FILE,imagePath);
        contentValues.put(LocalDB.DATE, date);
        contentValues.put(LocalDB.RESPONSE, response);

        return contentValues;
    }

    private int getCurrentRecordId() {
        String URL = "content://hu.ektf.iot.openbiomapsapp/storage";

        Uri storage = Uri.parse(URL);
        Cursor c = managedQuery(storage, null, null, null, "_ID");

        ArrayList<Integer> ids = new ArrayList<Integer>();

        if(c.moveToFirst())
        {
            do
            {
                ids.add(Integer.valueOf(c.getString(c.getColumnIndex(LocalDB._ID))));
            }while(c.moveToNext());

            Collections.sort(ids);
            return ids.get(ids.size()-1);
        }
        else
        {
            return -1;
        }
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
        outState.putStringArrayList("audiosList", audiosList);
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
            adapterImage.notifyDataSetChanged();
            imageRecycler.setVisibility(View.VISIBLE);
        }
        //TODO: need to fix RESULT_CODE_CANCELLED in case replaying record
        if (requestCode == REQ_RECORDING) {
            if (resultCode == RESULT_OK) {
                Uri audioUri = intent.getData();
                String fileName = audioUri.getLastPathSegment();
                soundPath = audioUri.getPath();
                Log.d("soundPath",soundPath);
                Toast.makeText(getApplicationContext(), fileName + " hangfelvétel hozzáadva.", Toast.LENGTH_LONG).show();
                audiosList.add(fileName);
                adapterAudio.notifyDataSetChanged();
                audioRecycler.setVisibility(View.VISIBLE);
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
        if (id == R.id.action_upload) {
            Intent intent = new Intent(MainActivity.this, UploadActivity.class);
            startActivity(intent);
        }
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
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageRecycler.getLayoutParams();
        params.height = height;
        imageRecycler.setLayoutParams(params);
        RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) audioRecycler.getLayoutParams();
        params2.height = height;
        audioRecycler.setLayoutParams(params2);
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

    private void resetFields() {
        etNote.setText("");
        imagesList.clear();
        audiosList.clear();
        adapterAudio.notifyDataSetChanged();
        adapterImage.notifyDataSetChanged();
        audioRecycler.setVisibility(View.GONE);
        imageRecycler.setVisibility(View.GONE);
        currentLocation = null;
        buttonSaveLocal.setEnabled(false);
    }
}
