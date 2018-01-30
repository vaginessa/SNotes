package in.snotes.snotes.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import in.snotes.snotes.R;
import in.snotes.snotes.notes.MainActivity;

public class AuthActivity extends AppCompatActivity implements AuthFragment.AuthListener, LoginFragment.LoginListener, RegisterFragment.RegisterListener {

    private FirebaseAuth mAuth;
    private static final String TAG = "AuthActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mAuth = FirebaseAuth.getInstance();

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

    // logging in user with Firebase
    @Override
    public void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show();
                        goToMainActivity();
                    } else {
                        Log.d(TAG, "Login error " + task.getException().getMessage());
                    }
                });
    }

    // navigating to MainActivity after registering and logging in
    private void goToMainActivity() {
        Intent i = new Intent(AuthActivity.this, MainActivity.class);
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
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        // start the IntentService
                        goToMainActivity();
                    }else{
                        Log.e(TAG,"Error registering user "+task.getException().getMessage());
                    }
                });
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
