package hu.ektf.iot.openbiomapsapp.database;

import android.accounts.Account;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

import timber.log.Timber;

/**
 * Created by szugyi on 30/11/15.
 */
public class BioMapsContentObserver extends ContentObserver {
    // TODO Should it be a WeakReference?
    private Account account;

    public BioMapsContentObserver(Account account, Handler handler) {
        super(handler);
        this.account = account;
    }

    @Override
    public void onChange(boolean selfChange) {
        onChange(selfChange, null);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        Timber.i("BioMapsContentObserver's onChange method was called. Sync will be requested");
        ContentResolver.requestSync(account, BioMapsContentProvider.AUTHORITY, null);
    }
}
