package in.snotes.snotes;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.facebook.stetho.Stetho;

import in.snotes.snotes.utils.AppConstants;
import in.snotes.snotes.utils.NotesUtils;
import in.snotes.snotes.utils.SharedPrefsHelper;
import timber.log.Timber;

public class SNotesApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        Timber.plant(new Timber.DebugTree());

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // creating the notification channel if it doesn't exist already
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            if (manager.getNotificationChannel(AppConstants.REMAINDER_NOTIFICATION_CHANNEL_ID) == null) {
                String displayChannelName = getString(R.string.notification_channel_display_title);
                NotificationChannel channel = new NotificationChannel(AppConstants.REMAINDER_NOTIFICATION_CHANNEL_ID, displayChannelName, NotificationManager.IMPORTANCE_HIGH);
                channel.enableLights(true);
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

                manager.createNotificationChannel(channel);
            }

        }

        // instantiate notes utils once in Application
        NotesUtils.instantiate();

        // instantiating SharedPrefs helper
        SharedPrefsHelper.instantiate(this);

    }
}
