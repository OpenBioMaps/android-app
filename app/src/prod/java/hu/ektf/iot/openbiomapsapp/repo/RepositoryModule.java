package hu.ektf.iot.openbiomapsapp.repo;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import hu.ektf.iot.openbiomapsapp.repo.api.ApiModule;
import hu.ektf.iot.openbiomapsapp.repo.database.DatabaseModule;

@Module(includes = {ApiModule.class, DatabaseModule.class})
abstract public class RepositoryModule {

    @Singleton
    @Binds
    abstract ObmRepo provideRepository(ObmRepoImpl repository);
}
