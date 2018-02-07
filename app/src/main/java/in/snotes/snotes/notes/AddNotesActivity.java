package in.snotes.snotes.notes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.snotes.snotes.R;
import in.snotes.snotes.model.Note;
import in.snotes.snotes.utils.AppConstants;
import in.snotes.snotes.utils.NotesUtils;
import in.snotes.snotes.utils.Utils;
import timber.log.Timber;

public class AddNotesActivity extends AppCompatActivity implements ColorChooserDialog.ColorCallback, AddNotesFragment.AddNotesListener, AddNotesBottomSheet.BottomSheetListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

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

    private Note note;
    private String reference;

    private Calendar remainder = Calendar.getInstance();

    AddNotesBottomSheet bottomSheetFragment;

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

        if (Intent.ACTION_SEND.equals(i.getAction())) {
            CURRENT_ACTION = i.getAction();
            note = new Note();
            if ("text/plain".equals(i.getType())) {
                if (savedInstanceState == null) {
                    String content = i.getStringExtra(Intent.EXTRA_TEXT);
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.frame_add_notes, AddNotesFragment.getInstance(i.getAction(), content), "add-notes-fragment")
                            .commit();
                }
                return;
            }
        }

        if (!i.hasExtra("action")) {
            Timber.e("Error loading page");
            Toast.makeText(this, "Error loading activity", Toast.LENGTH_SHORT).show();
        }

        String action = i.getStringExtra("action");
        CURRENT_ACTION = action;


        if (action.equals(AddNotesFragment.ACTION_EDIT_NOTE)) {
            note = i.getParcelableExtra("note");
            reference = i.getStringExtra("reference");

            Timber.d("Reference is %s", reference);

            color = note.getColorOfNote();

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
                showColorChooser();
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
        bottomSheetFragment = new AddNotesBottomSheet();
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
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
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isPinSet = prefs.getBoolean(AppConstants.PREFS_IS_PIN_SET, false);
        if (!isPinSet) {
            Toast.makeText(this, "Pin is not set. Please set a pin from settings", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isLocked) {
            lockItem.setIcon(R.drawable.ic_lock_open);
        } else {
            lockItem.setIcon(R.drawable.ic_lock_black);
        }
        tintMenuIcon(menu.findItem(R.id.action_lock), color);
        isLocked = !isLocked;
    }

    private void showColorChooser() {
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

        MenuItem lock = menu.findItem(R.id.action_lock);
        MenuItem star = menu.findItem(R.id.action_star);
        MenuItem color = menu.findItem(R.id.action_color_choser);
        MenuItem archive = menu.findItem(R.id.action_archive);
        MenuItem menuBtn = menu.findItem(R.id.action_menu);

        if (AddNotesFragment.ACTION_NEW_NOTE.equals(CURRENT_ACTION)) {
            archive.setVisible(false);
            menuBtn.setVisible(false);
        } else if (AddNotesFragment.ACTION_EDIT_NOTE.equals(CURRENT_ACTION)) {
            if (note.isLocked()) {
                isLocked = true;
                lock.setIcon(R.drawable.ic_lock_black);
            }
            if (note.isStarred()) {
                isStarred = true;
                star.setIcon(R.drawable.ic_star_closed);
            }

        }

        tintMenuIcon(star, this.color);
        tintMenuIcon(lock, this.color);
        tintMenuIcon(color, this.color);
        tintMenuIcon(archive, this.color);
        tintMenuIcon(menuBtn, this.color);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        AddNotesFragment addNotesFragment = (AddNotesFragment) getSupportFragmentManager().findFragmentByTag("add-notes-fragment");
        if (AddNotesFragment.ACTION_NEW_NOTE.equals(CURRENT_ACTION) || Intent.ACTION_SEND.equals(CURRENT_ACTION)) {
            addNotesFragment.saveToDatabase();
        } else {
            addNotesFragment.saveToDatabaseOnEdit();
        }

        super.onBackPressed();
    }

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, int selectedColor) {
        Timber.d("Color selected is %s", selectedColor);
        this.color = selectedColor;
    }

    @Override
    public void onColorChooserDismissed(@NonNull ColorChooserDialog dialog) {
        Timber.d("dialog dismissed");

        if (menu == null) {
            return;
        }

        int colorToDo = color;

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
            Timber.e("User is null. Cannot add user to database");
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
                        Timber.e("Error adding note to database %s", task.getException().getMessage());
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
                        Timber.e("Error deleting note %s", task.getException().getMessage());
                    }
                });
    }

    @Override
    public void updateNote(String title, String content) {

        if (!TextUtils.isEmpty(title)) {
            note.setTitle(title);
        }

        if (!TextUtils.isEmpty(content)) {
            note.setContent(content);
        }

        note.setLocked(isLocked);
        note.setStarred(isStarred);
        note.setColorOfNote(color);

        Map<String, Object> updatedNote = new HashMap<>();
        updatedNote.put("title", title);
        updatedNote.put("content", content);
        updatedNote.put("colorOfNote", color);
        updatedNote.put("locked", isLocked);
        updatedNote.put("starred", isStarred);

        NotesUtils.updateNote(this, reference, updatedNote);
    }


    public void tintMenuIcon(MenuItem item, int color) {
        Drawable normalDrawable = item.getIcon();
        Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
        DrawableCompat.setTint(wrapDrawable, color);

        item.setIcon(wrapDrawable);
    }

    @Override
    public void onBottomSheetItemClicked(String tag) {
        if (reference == null || TextUtils.isEmpty(reference)) {
            Timber.e("Reference is empty");
        }

        switch (tag) {
            case AppConstants.TAG_DELETE:
                deleteNoteFromDb();
                break;
            case AppConstants.TAG_REMAINDER:
                setRemainder();
                break;
            case AppConstants.TAG_COPY:
                copyNote();
                break;
            case AppConstants.TAG_EXPORT:
                exportNote();
                break;
            case AppConstants.TAG_SHARE:
                shareNote();
                break;
        }

        bottomSheetFragment.dismiss();
    }

    private void shareNote() {
        String contentToSend = "Title :" + note.getTitle() + " \nContent :" + (Html.fromHtml(note.getContent()).toString());
        Intent i = new Intent();
        i.setAction(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_TEXT, contentToSend);
        i.setType("text/plain");
        startActivity(Intent.createChooser(i, getResources().getText(R.string.send_to)));
    }

    private void exportNote() {

    }

    private void copyNote() {
    }

    private void setRemainder() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                AddNotesActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        remainder.set(year, monthOfYear, dayOfMonth);
        showTimeDialog();
    }

    private void showTimeDialog() {
        Calendar now = Calendar.getInstance();
        TimePickerDialog dpd = TimePickerDialog.newInstance(
                AddNotesActivity.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                now.get(Calendar.SECOND),
                false
        );
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        remainder.set(Calendar.HOUR_OF_DAY, hourOfDay);
        remainder.set(Calendar.MINUTE, minute);
        remainder.set(Calendar.SECOND, 0);

        note.setRemainderSet(true);
        note.setRemainderTime(remainder.getTimeInMillis());

        Utils.scheduleAlarm(this, note, reference);

        // we need to update the note object in the database. We aren't using the method in this class
        // because we need a different configuration from earlier

        Map<String, Object> updatedNote = new HashMap<>();
        updatedNote.put(AppConstants.REMAINDER_SET, true);
        updatedNote.put(AppConstants.REMAINDER_TIME, remainder.getTimeInMillis());

        NotesUtils.updateNote(this, reference, updatedNote);

    }

}
