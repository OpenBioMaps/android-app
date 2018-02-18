package hu.ektf.iot.openbiomapsapp.di;

import android.app.Application;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import hu.ektf.iot.openbiomapsapp.BioMapsApplication;
import hu.ektf.iot.openbiomapsapp.repo.ObmRepo;
import hu.ektf.iot.openbiomapsapp.repo.RepositoryModule;
import hu.ektf.iot.openbiomapsapp.sync.SyncAdapter;

@Singleton
@Component(modules = {RepositoryModule.class,
        ApplicationModule.class,
        ActivityBindingModule.class,
        AndroidSupportInjectionModule.class})
public interface AppComponent extends AndroidInjector<BioMapsApplication> {

    ObmRepo getRepository();

    void inject(SyncAdapter syncAdapter);

    @Component.Builder
    interface Builder {

        @BindsInstance
        AppComponent.Builder application(Application application);

        AppComponent build();
    }
}
