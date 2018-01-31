package in.snotes.snotes;

import android.app.Application;

import com.facebook.stetho.Stetho;

public class SNotesApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
