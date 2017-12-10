package hu.ektf.iot.openbiomapsapp.util.intent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import hu.ektf.iot.openbiomapsapp.util.FragmentManagerUtil;
import rx.Emitter;
import rx.Observable;
import rx.functions.Action2;

public class IntentRequester {

    private final Context context;

    public IntentRequester(Context context) {
        this.context = context;
    }

    public Observable<Result> startIntentForResult(Intent intent, int requestCode) {
        return Observable.create(objectEmitter -> {

            Action2<Integer, Intent> action2 = (integer, intent1) -> {
                objectEmitter.onNext(new Result(integer, intent1));
                objectEmitter.onCompleted();
            };

            IntentRequesterFragment intentRequesterFragment
                    = FragmentManagerUtil.findFragment(context, getTagName(requestCode));

            if (intentRequesterFragment != null) {
                intentRequesterFragment.restoreCallback(action2);
                return;
            }

            intentRequesterFragment = IntentRequesterFragment.newInstance(action2, requestCode);

            final FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(intentRequesterFragment, getTagName(requestCode))
                    .commitNow();
            intentRequesterFragment.requestIntentForResult(intent);

        }, Emitter.BackpressureMode.BUFFER);
    }

    public static boolean isRequestPending(Context context, int requestCode) {
        return FragmentManagerUtil.findFragment(context, getTagName(requestCode)) != null;
    }

    public static String getTagName(int requestCode) {
        return FragmentManagerUtil.getTagName(IntentRequesterFragment.class) + "_" + requestCode;
    }

    public static class Result {

        private final int resultCode;
        private final Intent data;

        Result(int resultCode, Intent data) {
            this.resultCode = resultCode;
            this.data = data;
        }

        public int getResultCode() {
            return resultCode;
        }

        public Intent getIntentData() {
            return data;
        }
    }
}