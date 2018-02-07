package in.snotes.snotes.service;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import in.snotes.snotes.utils.UserRegistrationServiceUtils;
import timber.log.Timber;

public class UserRegistrationService extends IntentService {

    public static final String ACTION_REGISTER_USER = "action-register-user";
//    public static final String ACTION_ADD_NOTE_TO_DB = "action-add-note-to-db";
    public static final String ACTION_REMAINDER_NOTE = "action-remainder-note";
    public static final String REMAINDER_EXTRA = "remainder-extra";
    public static final String ACTION_USER_LOGGED_IN_SYNC = "action-user_logged_in-sync";

    private FirebaseAuth mFirebaseAuth;

    private static final String TAG = "UserRegistrationService";

    public UserRegistrationService() {
        super("user-registration-service");
        Timber.d("Registration service started");
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onDestroy() {
        Timber.d("Registration Service Destroyed");
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) {
            return;
        }

        String action = intent.getAction();

        if (Objects.equals(action, ACTION_REGISTER_USER)) {
            UserRegistrationServiceUtils.registerUser(intent);
        } else if (Objects.equals(action, ACTION_REMAINDER_NOTE)) {
            String path = intent.getStringExtra(REMAINDER_EXTRA);
            UserRegistrationServiceUtils.showRemainder(path, this);
        } else if (Objects.equals(action, ACTION_USER_LOGGED_IN_SYNC)){
            UserRegistrationServiceUtils.syncUserPreferences(this);
        }

    }

    public static PendingIntent getNotesRemainderPendingIntent(Context context, String path) {
        Intent action = new Intent(context, UserRegistrationService.class);
        action.setAction(ACTION_REMAINDER_NOTE);
        action.putExtra(REMAINDER_EXTRA, path);
        return PendingIntent.getService(context, 0, action, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
