package in.snotes.snotes.utils;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

import timber.log.Timber;

public class NotesUtils {

    private static FirebaseFirestore mFirebaseFirestore;
    private static FirebaseAuth mFirebaseAuth;

    private NotesUtils(){}

    public static void instantiate(){
        if (mFirebaseFirestore == null){
            mFirebaseFirestore = FirebaseFirestore.getInstance();
        }

        if (mFirebaseAuth == null){
            mFirebaseAuth = FirebaseAuth.getInstance();
        }

    }

    public static void updateNote(Context context, String path, Map<String, Object> note){
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

}
