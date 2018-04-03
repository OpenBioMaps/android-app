package hu.ektf.iot.openbiomapsapp.repo;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract ObmRepo provideRepository(ObmRepoImpl repository);
}
