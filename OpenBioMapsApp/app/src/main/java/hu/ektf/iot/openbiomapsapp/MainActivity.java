package hu.ektf.iot.openbiomapsapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import hu.ektf.iot.openbiomapsapp.adapter.AudioListAdapter;
import hu.ektf.iot.openbiomapsapp.adapter.ImageListAdapter;
import hu.ektf.iot.openbiomapsapp.database.BioMapsResolver;
import hu.ektf.iot.openbiomapsapp.helper.FileHelper;
import hu.ektf.iot.openbiomapsapp.helper.GpsHelper;
import hu.ektf.iot.openbiomapsapp.helper.StorageHelper;
import hu.ektf.iot.openbiomapsapp.object.Note;
import hu.ektf.iot.openbiomapsapp.object.Note.State;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {
    // REQ CODES
    private static final int REQ_RECORDING = 1;
    private static final int REQ_IMAGE_CHOOSER = 2;
    private static final int REQ_CAMERA = 3;

    // State key constants
    private static final String SELECTED_IMAGE_PATH = "selectedImagePath";
    private static final String NOTE = "note";

    // Views
    private EditText etNote;
    private TextView tvPosition;
    private TextView tvDate;
    private Button buttonPosition, buttonShowMap, buttonAudioRecord, buttonCamera, buttonSave;
    private ProgressBar progressGps;
    private RecyclerView imageRecycler, audioRecycler;
    private ImageListAdapter adapterImage;
    private AudioListAdapter adapterAudio;

    // Gps stuffs
    private GpsHelper gpsHelper;
    private Location currentLocation;
    private long gpsRefreshRate = 10000;

    // Persistence
    private BioMapsResolver bioMapsResolver;
    private StorageHelper sharedPrefStorage;

    // File
    private FileHelper fileHelper;
    private Note note;
    private String selectedImagePath;
    private File imageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bioMapsResolver = new BioMapsResolver(this);
        sharedPrefStorage = new StorageHelper(this);
        gpsHelper = new GpsHelper(this);
        fileHelper = new FileHelper(getApplicationContext());

        if (savedInstanceState != null) {
            selectedImagePath = savedInstanceState.getString(SELECTED_IMAGE_PATH);

            if (savedInstanceState.containsKey(NOTE)) {
                note = savedInstanceState.getParcelable(NOTE);
            }
        } else {
            createNote();
        }

        //Getting the views
        etNote = (EditText) findViewById(R.id.etNote);
        tvPosition = (TextView) findViewById(R.id.textViewPosition);
        tvDate = (TextView) findViewById(R.id.textViewDate);
        buttonPosition = (Button) findViewById(R.id.buttonPosition);
        buttonShowMap = (Button) findViewById(R.id.buttonShowMap);
        buttonAudioRecord = (Button) findViewById(R.id.buttonAudioRecord);
        imageRecycler = (RecyclerView) findViewById(R.id.imageRecycler);
        audioRecycler = (RecyclerView) findViewById(R.id.audioRecycler);
        buttonCamera = (Button) findViewById(R.id.buttonCamera);
        buttonSave = (Button) findViewById(R.id.buttonReset);
        progressGps = (ProgressBar) findViewById(R.id.progressGps);

        int unitWidth = getListItemWidth();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        imageRecycler.setLayoutManager(layoutManager);
        RecyclerView.LayoutManager layoutAudio = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        audioRecycler.setLayoutManager(layoutAudio);
        setListHeight(unitWidth);

        createAdapters();

        buttonPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                // If GPS is off, show a dialog
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    showNoGpsDialog();
                    return;
                }
                // If GPS is on
                currentLocation = GpsHelper.getLocation();
                if (currentLocation != null && System.currentTimeMillis() - currentLocation.getTime() <= gpsRefreshRate) {
                    note.setLocation(currentLocation);
                    note.setDate(new Date());
                    note.setComment(etNote.getText().toString());
                    saveNote();
                    updateUI();

                } else {
                    progressGps.setVisibility(View.VISIBLE);
                    tvPosition.setText(R.string.waiting_for_gps);

                    gpsHelper.setExternalListener(new ExternalLocationListener());
                }
            }
        });

        buttonShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uriString = "geo:" + note.getLocation().getLatitude() + "," + note.getLocation().getLongitude();
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
                            REQ_RECORDING);
                } else {
                    // TODO Some devices might not have an Audio recorder
                    // we should create our own for this situation
                }
            }
        });

        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fileHelper.isSDPresent()) {
                    showImageSourceDialog();
                }
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = sharedPrefStorage.getServerUrl();
                if (TextUtils.isEmpty(url)) {
                    showServerUrlDialog();
                    return;
                }

                if (note.getLocation() == null) {
                    Toast.makeText(MainActivity.this, R.string.error_no_location, Toast.LENGTH_LONG).show();
                    return;
                }

                note.setComment(etNote.getText().toString());
                note.setUrl(url);
                note.setState(State.CLOSED);
                saveNote();
                ((BioMapsApplication) getApplication()).requestSync();

                note = new Note();
                createAdapters();
                updateUI();
            }
        });

        // Quick note click handling
        QuickNoteClickListener quickListener = new QuickNoteClickListener();
        findViewById(R.id.button_quick_a).setOnClickListener(quickListener);
        findViewById(R.id.button_quick_b).setOnClickListener(quickListener);
        findViewById(R.id.button_quick_c).setOnClickListener(quickListener);
        findViewById(R.id.button_quick_d).setOnClickListener(quickListener);

        adapterImage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ImagePagerActivity.class);
                intent.putStringArrayListExtra(ImagePagerActivity.ARG_IMAGES, note.getImagesList());
                intent.putExtra(ImagePagerActivity.ARG_POS, position);
                startActivity(intent);
            }
        });

        adapterAudio.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String audioUrl = note.getSoundsList().get(position);
                Uri myUri = Uri.fromFile(new File(audioUrl));

                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(myUri, "audio/*");
                startActivity(intent);

            }
        });

        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gpsHelper.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gpsHelper.onPause();

        note.setComment(etNote.getText().toString());
        saveNote();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SELECTED_IMAGE_PATH, selectedImagePath);
        outState.putParcelable(NOTE, note);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQ_IMAGE_CHOOSER) {
            if (resultCode == RESULT_OK) {
                Uri selectedImageUri = intent.getData();
                selectedImagePath = selectedImageUri.getPath();
                String galleryPath = FileHelper.getPath(this, selectedImageUri);
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
            String local = imageFile.getPath();
            note.getImagesList().add(local);
            adapterImage.notifyDataSetChanged();

            note.setComment(etNote.getText().toString());
            saveNote();
            updateUI();
        }

        if (requestCode == REQ_RECORDING) {
            if (resultCode == RESULT_OK) {
                Uri audioUri = intent.getData();
                String fileName = FileHelper.getPath(MainActivity.this, intent.getData());
                note.getSoundsList().add(fileName);
                adapterAudio.notifyDataSetChanged();

                note.setComment(etNote.getText().toString());
                saveNote();
                updateUI();
            }
        } else {
            super.onActivityResult(requestCode,
                    resultCode, intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_upload: {
                Intent intent = new Intent(MainActivity.this, UploadActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.action_server_settings: {
                showServerUrlDialog();
                return true;
            }
            case R.id.action_app_settings: {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    private void showServerUrlDialog() {
        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        View dialogView = li.inflate(R.layout.dialog_server_settings, null);

        final EditText etServerUrl = (EditText) dialogView
                .findViewById(R.id.etServerUrl);
        etServerUrl.setText(sharedPrefStorage.getServerUrl());
        etServerUrl.setSelection(etServerUrl.getText().length());

        new AlertDialog.Builder(MainActivity.this)
                .setView(dialogView)
                .setCancelable(false)
                .setTitle(R.string.dialog_set_server_url_title)
                .setPositiveButton(R.string.save,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                sharedPrefStorage.setServerUrl(etServerUrl.getText().toString());
                            }
                        })
                .setNegativeButton(R.string.cancel, null)
                .create().show();
    }

    private void createNote() {
        try {
            note = bioMapsResolver.getNoteByStatus(State.CREATED);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if (note == null) {
            note = new Note();
        }
    }

    private void saveNote() {
        try {
            Object o = bioMapsResolver.insertOrUpdateNote(note);
            Timber.i("InsertOrUpdate result: %s", o.toString());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void updateUI() {
        if (!TextUtils.equals(etNote.getText(), note.getComment())) {
            etNote.setText(note.getComment());
        }

        tvDate.setText(note.getDateString());

        if (note.getLocation() != null) {
            tvPosition.setText(note.getLocationString(this));
            buttonShowMap.setEnabled(true);
        } else {
            tvPosition.setText(R.string.tv_position);
            buttonShowMap.setEnabled(false);
        }

        imageRecycler.setVisibility(note.getImagesList().size() == 0 ? View.GONE : View.VISIBLE);
        audioRecycler.setVisibility(note.getSoundsList().size() == 0 ? View.GONE : View.VISIBLE);

    }

    private int getListItemWidth() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        float unitDpWidth = ((dpWidth - 32) / 3);
        // TODO Do it more robust (using getDimension)
        int unitPixelWidth = (int) (unitDpWidth * displayMetrics.density);
        return unitPixelWidth;
    }

    private void createAdapters() {
        int unitWidth = getListItemWidth();
        adapterImage = new ImageListAdapter(note.getImagesList());
        adapterAudio = new AudioListAdapter(note.getSoundsList());
        adapterImage.setImageSize(unitWidth);
        adapterAudio.setImageSize(unitWidth - 5);
        imageRecycler.setHasFixedSize(true);
        audioRecycler.setHasFixedSize(true);
        imageRecycler.setAdapter(adapterImage);
        audioRecycler.setAdapter(adapterAudio);
    }

    private void setListHeight(int height) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageRecycler.getLayoutParams();
        params.height = height;
        imageRecycler.setLayoutParams(params);
        RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) audioRecycler.getLayoutParams();
        params2.height = height;
        audioRecycler.setLayoutParams(params2);
    }

    private void showNoGpsDialog() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.dialog_gps_off_message)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(R.string.no, null)
                .create().show();
    }

    //this method checks that the common intent is available or not
    public static boolean isAvailable(Context ctx, Intent intent) {
        final PackageManager mgr = ctx.getPackageManager();
        List<ResolveInfo> list =
                mgr.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    /**
     * This function shows an Dialog helping the image uploading process.
     */
    private void showImageSourceDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_image_source_title)
                .setItems(R.array.image_source, new DialogInterface.OnClickListener() {
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
                })
                .setNeutralButton(R.string.cancel, null)
                .create().show();
    }

    private void dispatchChooseImageIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.dialog_image_chooser_title)),
                REQ_IMAGE_CHOOSER);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = fileHelper.createImageFile();
                selectedImagePath = photoFile.getAbsolutePath();
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
                Toast.makeText(this, R.string.error_no_file, Toast.LENGTH_LONG).show();
            }
        }
    }

    private class QuickNoteClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String noteLetter = (String) v.getTag();
            String quickNote = sharedPrefStorage.getQuickNote(noteLetter);

            if (TextUtils.isEmpty(quickNote)) {
                showNoQuickNoteDialog();
                return;
            }

            etNote.setText(quickNote);
            etNote.setSelection(quickNote.length());
            note.setComment(quickNote);
            saveNote();
        }

        private void showNoQuickNoteDialog() {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.dialog_no_quick_note_title)
                    .setMessage(R.string.dialog_no_quick_note_message)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                        }
                    })
                    .setNegativeButton(R.string.no, null)
                    .create().show();
        }
    }

    private class ExternalLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                gpsHelper.setExternalListener(null);
                progressGps.setVisibility(View.GONE);
                currentLocation = location;
                note.setLocation(currentLocation);
                note.setDate(new Date());
                note.setComment(etNote.getText().toString());
                saveNote();
                updateUI();
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }
}
