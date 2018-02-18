package hu.ektf.iot.openbiomapsapp.screen;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;
import hu.ektf.iot.openbiomapsapp.repo.ObmRepo;

public abstract class BaseActivity extends DaggerAppCompatActivity {

    @Inject
    ObmRepo repo;
}
