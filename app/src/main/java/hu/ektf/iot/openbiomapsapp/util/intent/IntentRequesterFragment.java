package hu.ektf.iot.openbiomapsapp.util.intent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import rx.functions.Action2;
import timber.log.Timber;

public class IntentRequesterFragment extends Fragment {

    private static final String REQUEST_CODE_KEY = "requestCode";

    private Action2<Integer, Intent> callback;
    private int intentRequestCode;
    private boolean hasResult;
    private int savedResultCode;
    private Intent savedData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            intentRequestCode = savedInstanceState.getInt(REQUEST_CODE_KEY);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (intentRequestCode == requestCode) {
            if (callback != null) {
                notifyCallback(resultCode, data);
                remove();
            } else {
                hasResult = true;
                savedResultCode = resultCode;
                savedData = data;
            }
        }
    }

    private void remove() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .remove(this)
                .commit();
    }

    private void notifyCallback(int resultCode, Intent data) {
        try {
            callback.call(resultCode, data);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public void requestIntentForResult(Intent intent) {
        startActivityForResult(intent, intentRequestCode);
    }

    public void restoreCallback(Action2<Integer, Intent> newCallback) {
        this.callback = newCallback;

        if (hasResult) {
            notifyCallback(savedResultCode, savedData);
            remove();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(REQUEST_CODE_KEY, intentRequestCode);

        super.onSaveInstanceState(outState);
    }

    public static IntentRequesterFragment newInstance(Action2<Integer, Intent> callback, int requestCode) {
        IntentRequesterFragment fragment = new IntentRequesterFragment();
        fragment.callback = callback;
        fragment.intentRequestCode = requestCode;
        return fragment;
    }
}
