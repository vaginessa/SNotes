package in.snotes.snotes.utils;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

import in.snotes.snotes.model.Note;
import timber.log.Timber;

public class NotesUtils {

    private static FirebaseFirestore mFirebaseFirestore;
    private static FirebaseAuth mFirebaseAuth;

    private static CollectionReference noteCollection;

    private NotesUtils() {
    }

    public static void instantiate() {
        if (mFirebaseFirestore == null) {
            mFirebaseFirestore = FirebaseFirestore.getInstance();
        }

        if (mFirebaseAuth == null) {
            mFirebaseAuth = FirebaseAuth.getInstance();
        }

        noteCollection = mFirebaseFirestore.collection(AppConstants.COLLECTION_USERS)
                .document(mFirebaseAuth.getCurrentUser().getUid())
                .collection(AppConstants.COLLECTION_NOTES);

    }

    public static void updateNote(Context context, String path, Map<String, Object> note) {
        mFirebaseFirestore.document(path)
                .update(note)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Note Updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Timber.e("Error updating note %s", task.getException().getMessage());
                    }
                });
    }

    public static boolean addNote(Note note) {
        Task<DocumentReference> task = noteCollection.add(note);

        if (task.getException() != null) {
            Timber.e(task.getException());
            return false;
        }

        return task.isSuccessful();
    }

    public static boolean deleteNote(Note note) {
        DocumentReference document = noteCollection.document(note.getDocumentReference().getPath());

        Task<Void> deleted = document.delete();

        if (deleted.getException() != null) {
            Timber.e(deleted.getException());
            return false;
        }

        return deleted.isSuccessful();

    }

}
