package com.selaliadobor.darkskyweather;

import android.app.Application;

import com.evernote.android.job.JobManager;
import com.facebook.soloader.SoLoader;
import com.selaliadobor.darkskyweather.job.ApplicationJobCreator;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import timber.log.Timber;

/**
 * Initializes various application subsystems
 */
public class DarkSkyClientApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initRealm();

        Timber.plant(new Timber.DebugTree());

        JobManager.create(this).addJobCreator(new ApplicationJobCreator());

        SoLoader.init(this, false);
    }

    private void initRealm() {
        Realm.init(this);

        if (BuildConfig.DEBUG) {
            RealmConfiguration debugConfiguration = new RealmConfiguration.Builder()
                    .deleteRealmIfMigrationNeeded()
                    .build();
            Realm.setDefaultConfiguration(debugConfiguration);
        }
    }
}
