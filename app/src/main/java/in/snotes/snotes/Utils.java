package in.snotes.snotes;

import android.text.format.DateFormat;

import java.util.Calendar;

public class Utils {
    public static String getDate(long timestamp) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);

        return DateFormat.format("d MMM yyyy HH:mm:ss", calendar).toString();

    }
}
