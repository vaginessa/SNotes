package in.snotes.snotes.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.Html;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import in.snotes.snotes.R;
import in.snotes.snotes.model.Note;
import in.snotes.snotes.service.UserRegistrationService;
import timber.log.Timber;

public class UserRegistrationServiceUtils {

    private static final String TAG = "UserRegistrationService";
    private static final FirebaseFirestore mFirebaseFirestore = FirebaseFirestore.getInstance();
    private static final FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

    public static void registerUser(Intent intent) {
        String userUid = intent.getStringExtra("user-uid");
        String userName = intent.getStringExtra("user-name");

        if (userUid == null || userName == null) {
            Timber.e("Check if you've sent uid or name with name " + userName + " and uid " + userUid);
            return;
        }

        String userPassword = "0000";

        // constructing the map to add
        Map<String, Object> user = new HashMap<>();
        user.put(AppConstants.NAME, userName);
        user.put(AppConstants.PREFS_PIN, userPassword);
        user.put(AppConstants.PREFS_IS_PIN_SET, false);

        // since the user is registering for the first time, we need to add the values to the shared prefs
        // so that the app doesn't crash reading data that isn't created
        SharedPrefsHelper.setIsPinSet(false);
        SharedPrefsHelper.setPin(Integer.parseInt(userPassword));

        // creating the document for the registered user
        mFirebaseFirestore.collection("users").document(userUid).set(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Timber.d("Successfully added user object");
            } else {
                Timber.e("Error adding user object with error %s", task.getException().getMessage());
            }
        });

        // adding the user name to the
        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(userName)
                .build();

        if (mFirebaseAuth.getCurrentUser() == null) {
            Timber.e("Error getting current user in UserRegistrationService.");
            return;
        }
        mFirebaseAuth.getCurrentUser().updateProfile(userProfileChangeRequest).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Timber.d("User registration completed");
            } else {
                Timber.e("Error adding user name to profile with error %s", task.getException().getMessage());
            }
        });


    }


    public static void showRemainder(String path, UserRegistrationService service) {
        Timber.i("Showing remainder");
        mFirebaseFirestore.document(path)
                .get().addOnCompleteListener(task -> {
            Note note = Utils.getNote(task.getResult());
            showRemainderNotification(note, service, path);
        });

    }

    private static void showRemainderNotification(Note note, UserRegistrationService service, String path) {

        if (!note.isRemainderSet()) {
            Timber.e("Remainder has been cancelled by the user");
            return;
        }

        // get the intent to open on notifcation click
        Intent action = Utils.getAddNotesIntent(note, service);

        NotificationManager notificationManager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);

        // create the pending intent for the notification
        PendingIntent resultantPendingIntent = PendingIntent.getActivity(service,
                0,
                action,
                PendingIntent.FLAG_UPDATE_CURRENT);

        String channelId = AppConstants.REMAINDER_NOTIFICATION_CHANNEL_ID;

        String content = Html.fromHtml(note.getContent()).toString();
        String title = note.getTitle();

        Notification notification = new NotificationCompat.Builder(service, channelId)
                .setContentTitle(title)
                .setContentText("Ping Ping")
                .setSmallIcon(R.drawable.ic_done_white_24dp)
                .setContentIntent(resultantPendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .build();

        if (notificationManager != null) {
            notificationManager.notify(AppConstants.NOTIFICATION_ID, notification);
            Timber.i("Notified");
        }

        // once the remainder is notified, we need to update the note
        // to remainder set is false
        Map<String, Object> updateNote = new HashMap<>();
        updateNote.put(AppConstants.REMAINDER_SET, false);
        updateNote.put(AppConstants.REMAINDER_TIME, 0);

        NotesUtils.updateNote(service, path, updateNote);
    }

    public static void syncUserPreferences(Service service) {

        if (mFirebaseAuth.getCurrentUser() == null) {
            Timber.e("Error syncing prefs. Current User is null");
            return;
        }

        String uid = mFirebaseAuth.getCurrentUser().getUid();

        mFirebaseFirestore.collection(AppConstants.COLLECTION_USERS)
                .document(uid)
                .get().addOnCompleteListener(task -> {
            if (task.getException() != null) {
                Timber.e(task.getException(), "Error getting prefs from db");
                return;
            }

            if (task.getResult() == null) {
                Timber.e("result is null %s", task.getResult());
            }

            DocumentSnapshot result = task.getResult();

            boolean isPinSet;

            if (result.contains(AppConstants.PREFS_IS_PIN_SET)){
                isPinSet = task.getResult().getBoolean(AppConstants.PREFS_IS_PIN_SET);
            }else{
                isPinSet = false;
            }

            int pin;

            if (result.contains(AppConstants.PREFS_PIN)){
                pin = Integer.parseInt(task.getResult().get(AppConstants.PREFS_PIN).toString());
            }else{
                pin = 0;
            }

            SharedPrefsHelper.setIsPinSet(isPinSet);
            SharedPrefsHelper.setPin(pin);

        });

    }
}
