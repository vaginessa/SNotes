package in.snotes.snotes.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;

import in.snotes.snotes.R;
import in.snotes.snotes.notes.NotesMainActivity;
import in.snotes.snotes.service.UserRegistrationService;
import timber.log.Timber;

public class AuthActivity extends AppCompatActivity implements AuthFragment.AuthListener, LoginFragment.LoginListener, RegisterFragment.RegisterListener {

    private static final String TAG = "AuthActivity";

    MaterialDialog authDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_auth, new AuthFragment())
                    .commit();
        }

    }

    // login clicked in AuthFragment
    @Override
    public void onLoginClicked() {
        navToLogin();
    }

    // register clicked in AuthFragment
    @Override
    public void onRegisterClicked() {
        navToRegister();
    }

    private void authenticatingDialog() {
        authDialog = new MaterialDialog.Builder(this)
                .title(R.string.progress_title)
                .content(R.string.please_content)
                .progress(true, 0)
                .build();

        authDialog.show();
    }

    // logging in user with Firebase
    @Override
    public void loginUser(String email, String password) {
        authenticatingDialog();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show();
                        authDialog.dismiss();
                        startSyncServiceOnLogin();
                        goToMainActivity();
                    } else {
                        Timber.d("Login error %s", task.getException().getMessage());
                    }
                });
    }

    private void startSyncServiceOnLogin() {
        // this is called because we want to sync in users preferences from backup
        Intent i = new Intent(this, UserRegistrationService.class);
        i.setAction(UserRegistrationService.ACTION_USER_LOGGED_IN_SYNC);
        startService(i);
    }

    // navigating to NotesMainActivity after registering and logging in
    private void goToMainActivity() {
        Intent i = new Intent(AuthActivity.this, NotesMainActivity.class);
        startActivity(i);
        finish();
        return;
    }

    @Override
    public void navToRegister() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_auth, new RegisterFragment())
                .addToBackStack("register-fragment")
                .commit();
    }

    @Override
    public void forgotPassword() {
        Toast.makeText(this, "Forgot Password", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void registerUser(String name, String email, String password) {
        authenticatingDialog();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // start the IntentService
                        authDialog.dismiss();
                        startRegistrationService(name, task.getResult().getUser().getUid());
                        goToMainActivity();
                    } else {
                        Timber.e("Error registering user %s", task.getException().getMessage());
                    }
                });
    }

    private void startRegistrationService(String name, String uid) {
        Intent i = new Intent(AuthActivity.this, UserRegistrationService.class);
        i.setAction(UserRegistrationService.ACTION_REGISTER_USER);
        i.putExtra("user-uid", uid);
        i.putExtra("user-name", name);
        startService(i);
    }

    private void navToLogin() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_auth, new LoginFragment())
                .addToBackStack("login-fragment")
                .commit();
    }

    @Override
    public void userIsAlreadyRegistered() {
        navToLogin();
    }
}
