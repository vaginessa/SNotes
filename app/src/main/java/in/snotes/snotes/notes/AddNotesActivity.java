package in.snotes.snotes.notes;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.afollestad.materialdialogs.color.ColorChooserDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.snotes.snotes.R;

public class AddNotesActivity extends AppCompatActivity implements ColorChooserDialog.ColorCallback {

    @BindView(R.id.toolbar_notes_add)
    Toolbar toolbarNotesAdd;


    private boolean isLocked = false;
    private boolean isStarred = false;
    private int color;

    private Menu menu;

    private static final String TAG = "AddNotesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);
        ButterKnife.bind(this);


        setSupportActionBar(toolbarNotesAdd);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_add_notes, new AddNotesFragment(), "add-notes-fragment")
                    .commit();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_archive:
                makeArchive();
                break;
            case R.id.action_color_choser:
                showColorChoser();
                break;
            case R.id.action_lock:
                lock();
                break;
            case R.id.action_star:
                star();
                break;
            case R.id.action_menu:
                showBottomSheet();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showBottomSheet() {

    }

    private void star() {
        MenuItem starItem = menu.findItem(R.id.action_star);
        if (isStarred) {
            starItem.setIcon(R.drawable.ic_star_open);
        } else {
            starItem.setIcon(R.drawable.ic_star_closed);
        }
        tintMenuIcon(starItem, color);
        isStarred = !isStarred;
    }

    private void lock() {
        MenuItem lockItem = menu.findItem(R.id.action_lock);
        if (isLocked) {
            lockItem.setIcon(R.drawable.ic_lock_open);
        } else {
            lockItem.setIcon(R.drawable.ic_lock_black);
        }
        tintMenuIcon(lockItem, color);
        isLocked = !isLocked;
    }

    private void showColorChoser() {
        new ColorChooserDialog.Builder(this, R.string.color_title)
                .titleSub(R.string.colors)  // title of dialog when viewing shades of a color
                .doneButton(R.string.md_done_label)  // changes label of the done button
                .cancelButton(R.string.md_cancel_label)  // changes label of the cancel button
                .backButton(R.string.md_back_label)  // changes label of the back button
                .dynamicButtonColor(true)  // defaults to true, false will disable changing action buttons' color to currently selected color
                .show(this); // an AppCompatActivity which implements ColorCallback
    }

    private void makeArchive() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notes, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AddNotesFragment addNotesFragment = (AddNotesFragment) getSupportFragmentManager().findFragmentByTag("add-notes-fragment");
        addNotesFragment.saveToDatabase();
    }

    public void tintMenuIcon(MenuItem item, int color) {
        Context context = AddNotesActivity.this;
        Drawable normalDrawable = item.getIcon();
        Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
        DrawableCompat.setTint(wrapDrawable, color);

        item.setIcon(wrapDrawable);
    }

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, int selectedColor) {
        Log.d(TAG, "Color selected is " + selectedColor);
        this.color = selectedColor;
    }

    @Override
    public void onColorChooserDismissed(@NonNull ColorChooserDialog dialog) {
        Log.d(TAG, "dialog dismissed");

        if (menu == null) {
            return;
        }

        tintMenuIcon(menu.findItem(R.id.action_archive), color);
        tintMenuIcon(menu.findItem(R.id.action_color_choser), color);
        tintMenuIcon(menu.findItem(R.id.action_lock), color);
        tintMenuIcon(menu.findItem(R.id.action_star), color);
        tintMenuIcon(menu.findItem(R.id.action_menu), color);
//      tintMenuIcon(menu.findItem(android.R.id.home), color);

        AddNotesFragment addNotesFragment = (AddNotesFragment) getSupportFragmentManager().findFragmentByTag("add-notes-fragment");
        addNotesFragment.changeColor(color);
    }
}
