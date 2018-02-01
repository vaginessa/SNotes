package in.snotes.snotes.notes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.snotes.snotes.R;
import in.snotes.snotes.auth.AuthActivity;
import in.snotes.snotes.model.Note;

public class NotesMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NotesListFragment.NotesListFragmentListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user == null) {
            Intent i = new Intent(this, AuthActivity.class);
            startActivity(i);
            finish();
            return;
        }

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_main, new NotesListFragment())
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_logout) {
            logoutUser();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFabClicked() {
        Intent i = new Intent(NotesMainActivity.this, AddNotesActivity.class);
        i.putExtra("action", AddNotesFragment.ACTION_NEW_NOTE);
        startActivity(i);
    }

    @Override
    public void onNoteClicked(Note note) {

        if (note.isLocked()) {

            new MaterialDialog.Builder(this)
                    .title("Enter Password")
                    .inputType(InputType.TYPE_CLASS_NUMBER)
                    .input("0000", null, (dialog, input) -> {
                        // Do something
                        // adding passowrd to sharedPref
                        SharedPreferences sharedPreferences = getSharedPreferences("snotes-prefs", Context.MODE_PRIVATE);
                        String pin = sharedPreferences.getString("pin", "0000");

                        String inputPassword = String.valueOf(input);

                        if (!Objects.equals(pin, inputPassword)) {
                            showPinError();
                        } else {
                            startAddNotesActivity(note);
                        }
                    }).show();

        } else {
            startAddNotesActivity(note);
        }

    }

    private void showPinError() {
        new MaterialDialog.Builder(this)
                .title("Wrong Password")
                .content("You have entered the wrong password. Please try again")
                .neutralText("Ok")
                .show();
    }

    private void startAddNotesActivity(Note note) {
        Intent i = new Intent(NotesMainActivity.this, AddNotesActivity.class);
        i.putExtra("action", AddNotesFragment.ACTION_EDIT_NOTE);
        i.putExtra("note", note);
        i.putExtra("reference", note.getDocumentReference().getPath());
        startActivity(i);
    }

    private void logoutUser() {
        mFirebaseAuth.signOut();
        Intent i = new Intent(NotesMainActivity.this, AuthActivity.class);
        startActivity(i);
        finish();
        return;
    }
}
