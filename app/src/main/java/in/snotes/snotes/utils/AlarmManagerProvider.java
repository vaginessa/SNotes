package in.snotes.snotes.utils;

import android.app.AlarmManager;
import android.content.Context;

public class AlarmManagerProvider {

    private static AlarmManager mAlarmManager;

    static synchronized AlarmManager getAlarmManager(Context context) {
        if (mAlarmManager == null) {
            mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        }
        return mAlarmManager;
    }
}
