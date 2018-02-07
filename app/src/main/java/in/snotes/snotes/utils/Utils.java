package in.snotes.snotes.utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import in.snotes.snotes.BuildConfig;
import in.snotes.snotes.R;
import in.snotes.snotes.model.AboutDescModel;
import in.snotes.snotes.model.AboutModel;
import in.snotes.snotes.model.Note;
import in.snotes.snotes.notes.AddNotesActivity;
import in.snotes.snotes.notes.AddNotesFragment;
import in.snotes.snotes.service.UserRegistrationService;

public class Utils {
    public static String getDate(long timestamp) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);

        return DateFormat.format("d MMM yyyy HH:mm:ss", calendar).toString();

    }

    public static void goToAddNotes(Note note, Activity activity) {
        Intent i = new Intent(activity, AddNotesActivity.class);
        i.putExtra("action", AddNotesFragment.ACTION_EDIT_NOTE);
        i.putExtra("note", note);
        i.putExtra("reference", note.getDocumentReference().getPath());
        activity.startActivity(i);
    }

    public static Intent getAddNotesIntent(Note note, Service service) {
        Intent i = new Intent(service, AddNotesActivity.class);
        i.putExtra("action", AddNotesFragment.ACTION_EDIT_NOTE);
        i.putExtra("note", note);
        i.putExtra("reference", note.getDocumentReference().getPath());
        return i;
    }

    public static void showPinError(Activity activity) {
        new MaterialDialog.Builder(activity)
                .title("Wrong Password")
                .content("You have entered the wrong password. Please try again")
                .neutralText("Ok")
                .show();
    }

    public static ArrayList<Note> getNotesFromSnapshot(List<DocumentSnapshot> documents) {

        ArrayList<Note> notes = new ArrayList<>();

        for (DocumentSnapshot document : documents) {
            Note note = getNote(document);
            notes.add(note);
        }

        return notes;
    }

    public static void scheduleAlarm(Context context, Note note, String path) {
        AlarmManager alarmManager = AlarmManagerProvider.getAlarmManager(context);

        PendingIntent operation = UserRegistrationService.getNotesRemainderPendingIntent(context, path);

        alarmManager.setExact(AlarmManager.RTC, note.getRemainderTime(), operation);

    }

    public static Note getNote(DocumentSnapshot document) {
        Note note = new Note.Builder()
                .title(document.getString(AppConstants.TITLE))
                .content(document.getString(AppConstants.CONTENT))
                .createdTime(document.getLong(AppConstants.CREATED_TIMESTAMP))
                .isLocked(document.getBoolean(AppConstants.LOCKED))
                .isStarred(document.getBoolean(AppConstants.STARRED))
                .hasRemainder(document.getBoolean(AppConstants.REMAINDER_SET))
                .remainderTime(document.getLong(AppConstants.REMAINDER_TIME))
                .colorOfNote(Integer.parseInt(document.get(AppConstants.COLOR_OF_NOTE).toString()))
                .reference(document.getReference())
                .buildNote();

        return note;
    }

    public static List<Object> getAboutItems(Context context) {

        List<Object> aboutItems = new ArrayList<>();

        aboutItems.add(new AboutDescModel(context.getString(R.string.version_title), BuildConfig.VERSION_NAME, R.drawable.ic_info));
        aboutItems.add(new AboutDescModel(context.getString(R.string.rate_title), context.getString(R.string.rate_desc), R.drawable.ic_star_closed));
        aboutItems.add(new AboutModel(context.getString(R.string.licenses), R.drawable.ic_note));
        aboutItems.add(new AboutModel(context.getString(R.string.share_title), R.drawable.ic_share));
        aboutItems.add(new AboutDescModel(context.getString(R.string.author_name), context.getString(R.string.author_location), R.drawable.ic_person));
        aboutItems.add(new AboutModel(context.getString(R.string.follow_on_github), R.drawable.ic_github));
        aboutItems.add(new AboutDescModel(context.getString(R.string.report_title), context.getString(R.string.report_desc), R.drawable.ic_report));
        aboutItems.add(new AboutDescModel(context.getString(R.string.donate), context.getString(R.string.donate_desc), R.drawable.ic_heart));

        return aboutItems;

    }
}
