package in.snotes.snotes.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPrefsHelper {

    @SuppressLint("StaticFieldLeak")
    protected static Context context;
    private static SharedPreferences prefs;

    // private so that no one can try to instantiate the class
    private SharedPrefsHelper() {
    }

    public static void instantiate(Context applicationContext) {
        context = applicationContext;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static boolean isPinSet() {
        return prefs.getBoolean(AppConstants.PREFS_IS_PIN_SET, false);
    }

    public static void setIsPinSet(boolean isPinSet) {
        prefs.edit()
                .putBoolean(AppConstants.PREFS_IS_PIN_SET, isPinSet)
                .apply();
    }

    public static int getPin() {
        return prefs.getInt(AppConstants.PREFS_PIN, 0);
    }

    public static void setPin(int pin) {
        prefs.edit()
                .putInt(AppConstants.PREFS_PIN, pin)
                .apply();
    }

}
