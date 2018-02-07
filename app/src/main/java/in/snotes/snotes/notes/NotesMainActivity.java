package in.snotes.snotes.notes;

import android.content.Intent;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import in.snotes.snotes.R;
import in.snotes.snotes.about.AboutActivity;
import in.snotes.snotes.auth.AuthActivity;
import in.snotes.snotes.locked.LockedActivity;
import in.snotes.snotes.model.Note;
import in.snotes.snotes.settings.SettingsActivity;
import in.snotes.snotes.starred.StarredActivity;
import in.snotes.snotes.utils.SharedPrefsHelper;
import in.snotes.snotes.utils.Utils;

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
            showSettings();
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

        switch (id) {
            case R.id.nav_favourites:
                Intent favIntent = new Intent(NotesMainActivity.this, StarredActivity.class);
                startActivity(favIntent);
                break;
            case R.id.nav_locked:
                Intent lockedIntent = new Intent(NotesMainActivity.this, LockedActivity.class);
                startActivity(lockedIntent);
                break;
            case R.id.nav_settings:
                showSettings();
                break;
            case R.id.nav_about:
                showAbout();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showAbout() {
        Intent i = new Intent(this, AboutActivity.class);
        startActivity(i);
    }

    private void showSettings() {

        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
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
                        int pin = SharedPrefsHelper.getPin();

                        int inputPassword = Integer.parseInt(input.toString());

                        if (pin == inputPassword) {
                            Utils.goToAddNotes(note, NotesMainActivity.this);
                        } else {
                            Utils.showPinError(NotesMainActivity.this);
                        }

                    }).show();

        } else {
            Utils.goToAddNotes(note, NotesMainActivity.this);
        }
    }

    private void logoutUser() {
        mFirebaseAuth.signOut();
        Intent i = new Intent(NotesMainActivity.this, AuthActivity.class);
        startActivity(i);
        finish();
        return;
    }
}
