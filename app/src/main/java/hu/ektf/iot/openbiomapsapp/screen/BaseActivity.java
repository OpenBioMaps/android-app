package hu.ektf.iot.openbiomapsapp.screen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import hu.ektf.iot.openbiomapsapp.repo.ObmClient;
import hu.ektf.iot.openbiomapsapp.repo.ObmClientImpl;

public class BaseActivity extends AppCompatActivity {

    protected ObmClient client;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = new ObmClientImpl(this);
    }
}
