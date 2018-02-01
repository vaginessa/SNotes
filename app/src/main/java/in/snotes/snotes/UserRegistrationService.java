package in.snotes.snotes;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserRegistrationService extends IntentService {

    public static final String ACTION_REGISTER_USER = "action-register-user";
    public static final String ACTION_ADD_NOTE_TO_DB = "action-add-note-to-db";

    private FirebaseFirestore mFirebaseFirestore;
    private FirebaseAuth mFirebaseAuth;

    private static final String TAG = "UserRegistrationService";

    public UserRegistrationService() {
        super("user-registration-service");
        Log.d(TAG, "Registration service started");
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Registration Service Destroyed");
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) {
            return;
        }

        String action = intent.getAction();

        if (Objects.equals(action, ACTION_REGISTER_USER)) {
            registerUser(intent);
        }else if (Objects.equals(action, ACTION_ADD_NOTE_TO_DB)){
            addDataToDatabase(intent);
        }

    }

    private void addDataToDatabase(Intent intent) {


    }

    private void registerUser(Intent intent) {
        String userUid = intent.getStringExtra("user-uid");
        String userName = intent.getStringExtra("user-name");

        if (userUid == null || userName == null) {
            Log.e(TAG, "Check if you've sent uid or name with name " + userName + " and uid " + userUid);
            return;
        }

        String userPassword = "0000";

        // adding passowrd to sharedPref
        SharedPreferences sharedPreferences = getSharedPreferences("snotes-prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("pin", userPassword);
        editor.apply();

        // constructing the map to add
        Map<String, String> user = new HashMap<>();
        user.put("name", userName);
        user.put("pin", userPassword);

        // creating the document for the registered user
        mFirebaseFirestore.collection("users").document(userUid).set(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Succesfully added user object");
            } else {
                Log.e(TAG, "Error adding user object with error " + task.getException().getMessage());
            }
        });

        // adding the user name to the
        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(userName)
                .build();

        if (mFirebaseAuth.getCurrentUser() == null) {
            Log.e(TAG, "Error getting current user in UserRegistrationService.");
            return;
        }
        mFirebaseAuth.getCurrentUser().updateProfile(userProfileChangeRequest).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "User registration completed");
            } else {
                Log.e(TAG, "Error adding user name to profile with error " + task.getException().getMessage());
            }
        });


    }
}
