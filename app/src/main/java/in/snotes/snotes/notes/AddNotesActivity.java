package in.snotes.snotes.notes;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.snotes.snotes.R;
import in.snotes.snotes.model.Note;

public class AddNotesActivity extends AppCompatActivity implements ColorChooserDialog.ColorCallback, AddNotesFragment.AddNotesListener {

    @BindView(R.id.toolbar_notes_add)
    Toolbar toolbarNotesAdd;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore mFirebaseFirestore;

    private boolean isLocked = false;
    private boolean isStarred = false;
    private boolean hasRemainder = false;
    // default black
    private int color = -16777216;

    private Menu menu;

    private static final String TAG = "AddNotesActivity";
    private static String CURRENT_ACTION;

    private String title;
    private String content;

    private boolean lockedOnEdit;
    private boolean starredOnEdit;
    private int colorOnEdit;

    private Note note;
    private String reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);
        ButterKnife.bind(this);

        setSupportActionBar(toolbarNotesAdd);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();

        Intent i = getIntent();

        if (!i.hasExtra("action")) {
            Log.e(TAG, "Error loading page");
            Toast.makeText(this, "Error loading activity", Toast.LENGTH_SHORT).show();
        }

        String action = i.getStringExtra("action");
        CURRENT_ACTION = action;

        if (action.equals(AddNotesFragment.ACTION_EDIT_NOTE)) {
            note = i.getParcelableExtra("note");
            reference = i.getStringExtra("reference");

            Log.d(TAG, "Reference is " + reference);

            title = note.getTitle();
            content = note.getContent();
            lockedOnEdit = note.isLocked();
            starredOnEdit = note.isStarred();
            colorOnEdit = note.getColorOfNote();
            color = colorOnEdit;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.frame_add_notes, AddNotesFragment.getInstance(action, note), "add-notes-fragment")
                        .commit();
            }
            return;
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_add_notes, AddNotesFragment.getInstance(action), "add-notes-fragment")
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
        tintMenuIcon(menu.findItem(R.id.action_star), color);
        isStarred = !isStarred;
    }

    private void lock() {
        MenuItem lockItem = menu.findItem(R.id.action_lock);
        if (isLocked) {
            lockItem.setIcon(R.drawable.ic_lock_open);
        } else {
            lockItem.setIcon(R.drawable.ic_lock_black);
        }
        tintMenuIcon(menu.findItem(R.id.action_lock), color);
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
        deleteNoteFromDb();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notes, menu);
        this.menu = menu;

        if (AddNotesFragment.ACTION_NEW_NOTE.equals(CURRENT_ACTION)) {
            MenuItem archiveItem = menu.findItem(R.id.action_archive);
            archiveItem.setVisible(false);
        } else if (AddNotesFragment.ACTION_EDIT_NOTE.equals(CURRENT_ACTION)) {
            MenuItem lock = menu.findItem(R.id.action_lock);
            MenuItem star = menu.findItem(R.id.action_star);
            if (note.isLocked()) {
                isLocked = true;
                lock.setIcon(R.drawable.ic_lock_black);
            }
            if (note.isStarred()) {
                isStarred = true;
                star.setIcon(R.drawable.ic_star_closed);
            }
            tintMenuIcon(star, colorOnEdit);
            tintMenuIcon(lock, colorOnEdit);

        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        AddNotesFragment addNotesFragment = (AddNotesFragment) getSupportFragmentManager().findFragmentByTag("add-notes-fragment");
        if (AddNotesFragment.ACTION_NEW_NOTE.equals(CURRENT_ACTION)) {
            addNotesFragment.saveToDatabase();
        } else {
            addNotesFragment.saveToDatabaseOnEdit();
        }

        super.onBackPressed();
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

        int colorToDo;
        if (AddNotesFragment.ACTION_EDIT_NOTE.equals(CURRENT_ACTION)) {
            colorToDo = color;
        } else {
            colorToDo = colorOnEdit;
        }

        tintMenuIcon(menu.findItem(R.id.action_archive), colorToDo);
        tintMenuIcon(menu.findItem(R.id.action_color_choser), colorToDo);
        tintMenuIcon(menu.findItem(R.id.action_lock), colorToDo);
        tintMenuIcon(menu.findItem(R.id.action_star), colorToDo);
        tintMenuIcon(menu.findItem(R.id.action_menu), colorToDo);
//      tintMenuIcon(menu.findItem(android.R.id.home), color);

        AddNotesFragment addNotesFragment = (AddNotesFragment) getSupportFragmentManager().findFragmentByTag("add-notes-fragment");
        addNotesFragment.changeColor(color);
    }

    @Override
    public void onSaveToDatabase(String title, String content) {

        Note note = new Note.Builder()
                .title(title)
                .content(content)
                .colorOfNote(color)
                .createdTime(System.currentTimeMillis())
                .hasRemainder(hasRemainder)
                .remainderTime(0)
                .isLocked(isLocked)
                .isStarred(isStarred)
                .buildNote();

        if (mFirebaseAuth.getCurrentUser() == null) {
            Log.e(TAG, "User is null. Cannot add user to database");
            return;
        }

        String userUid = mFirebaseAuth.getCurrentUser().getUid();

        mFirebaseFirestore.collection("users").document(userUid).collection("notes")
                .add(note)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Added note successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Log.e(TAG, "Error adding note to database " + task.getException().getMessage());
                        Toast.makeText(this, "Error adding note to database", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void deleteNoteFromDb() {
        mFirebaseFirestore.document(reference)
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error deleting note", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error deleting note " + task.getException().getMessage());
                    }
                });
    }

    @Override
    public void updateNote(String title, String content) {

        note.setTitle(title);
        note.setContent(content);
        note.setLocked(isLocked);
        note.setStarred(isStarred);
        note.setColorOfNote(color);

        Map<String, Object> updatedNote = new HashMap<>();
        updatedNote.put("title", title);
        updatedNote.put("content", content);
        updatedNote.put("colorOfNote", color);
        updatedNote.put("locked", isLocked);
        updatedNote.put("starred", isStarred);

        mFirebaseFirestore.document(reference)
                .update(updatedNote)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Note Updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "Error updating note " + task.getException().getMessage());
                    }
                });
    }


    public void tintMenuIcon(MenuItem item, int color) {
        Drawable normalDrawable = item.getIcon();
        Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
        DrawableCompat.setTint(wrapDrawable, color);

        item.setIcon(wrapDrawable);
    }
}
