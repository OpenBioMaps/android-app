package hu.ektf.iot.openbiomapsapp.util;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import org.androidannotations.api.view.HasViews;

public final class FragmentManagerUtil {

    private FragmentManagerUtil() {
        // utility class
    }

    @SuppressWarnings("unchecked")
    public static <T extends Fragment> T findFragment(FragmentManager fragmentManager, String tag) {
        return (T) fragmentManager.findFragmentByTag(tag);
    }

    public static <T extends Fragment> T findFragment(Context context, String tag) {
        FragmentActivity fragmentActivity = (FragmentActivity) context;
        FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
        return findFragment(fragmentManager, tag);
    }

    public static <T extends Fragment> T findFragment(Context context, Class<T> fragmentClass) {
        return findFragment(context, getTagName(fragmentClass));
    }

    public static String getTagName(Class<?> fragmentClass) {
        if (HasViews.class.isAssignableFrom(fragmentClass)) { // AA generated Fragments are subclasses of HasViews
            return fragmentClass.getSuperclass().getName(); // We need to return the name of the original class
        }

        return fragmentClass.getName();
    }
}
