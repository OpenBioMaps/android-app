package hu.ektf.iot.openbiomapsapp.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import hu.ektf.iot.openbiomapsapp.screen.FormActivity;
import hu.ektf.iot.openbiomapsapp.screen.FormListActivity;
import hu.ektf.iot.openbiomapsapp.screen.LoginActivity;
import hu.ektf.iot.openbiomapsapp.screen.SavedDataActivity;
import hu.ektf.iot.openbiomapsapp.screen.UploadActivity;

@Module
public abstract class ActivityBindingModule {

    @ActivityScoped
    @ContributesAndroidInjector
    abstract LoginActivity loginActivity();

    @ActivityScoped
    @ContributesAndroidInjector
    abstract FormListActivity formListActivity();

    @ActivityScoped
    @ContributesAndroidInjector
    abstract FormActivity formActivity();

    @ActivityScoped
    @ContributesAndroidInjector
    abstract UploadActivity uploadActivity();

    @ActivityScoped
    @ContributesAndroidInjector
    abstract SavedDataActivity savedDataActivity();
}
