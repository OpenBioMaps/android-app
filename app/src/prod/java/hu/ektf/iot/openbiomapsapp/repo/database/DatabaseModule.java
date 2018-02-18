package hu.ektf.iot.openbiomapsapp.repo.database;

import android.arch.persistence.room.Room;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DatabaseModule {
    private static final String DATABASE_NAME = "OBM-database";

    @Provides
    @Singleton
    AppDatabase provideDatabase(Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME)
                // TODO Turn off after development period
                .fallbackToDestructiveMigration()
                .build();
    }
}
