package hu.ektf.iot.openbiomapsapp.screen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import hu.ektf.iot.openbiomapsapp.repo.ObmRepo;
import hu.ektf.iot.openbiomapsapp.repo.ObmRepoImpl;

public class BaseActivity extends AppCompatActivity {

    protected ObmRepo repo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repo = new ObmRepoImpl(this);
    }
}
