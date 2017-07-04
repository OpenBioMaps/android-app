package hu.ektf.iot.openbiomapsapp.screen;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import hu.ektf.iot.openbiomapsapp.R;
import hu.ektf.iot.openbiomapsapp.helper.StorageHelper;

public class SettingsActivity extends AppCompatActivity {

    private EditText editA;
    private EditText editB;
    private EditText editC;
    private EditText editD;
    private Button saveButton;

    private StorageHelper storageHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        storageHelper = new StorageHelper(SettingsActivity.this);

        editA = (EditText) findViewById(R.id.quick_a_editText);
        editB = (EditText) findViewById(R.id.quick_b_editText);
        editC = (EditText) findViewById(R.id.quick_c_editText);
        editD = (EditText) findViewById(R.id.quick_d_editText);

        editA.setText(storageHelper.getQuickNote("A"));
        editB.setText(storageHelper.getQuickNote("B"));
        editC.setText(storageHelper.getQuickNote("C"));
        editD.setText(storageHelper.getQuickNote("D"));

        saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quickA = editA.getText().toString();
                String quickB = editB.getText().toString();
                String quickC = editC.getText().toString();
                String quickD = editD.getText().toString();

                storageHelper.setQuickNote("A", quickA);
                storageHelper.setQuickNote("B", quickB);
                storageHelper.setQuickNote("C", quickC);
                storageHelper.setQuickNote("D", quickD);
                finish();
            }
        });
    }
}
