package in.snotes.snotes.locked;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import in.snotes.snotes.R;
import in.snotes.snotes.model.Note;
import in.snotes.snotes.notes.NotesAdapter;
import in.snotes.snotes.utils.AppConstants;
import in.snotes.snotes.utils.Utils;
import timber.log.Timber;

public class LockedFragment extends Fragment implements NotesAdapter.NotesListener {

    @BindView(R.id.rv_locked)
    RecyclerView rvLocked;
    Unbinder unbinder;
    @BindView(R.id.layout_empty)
    LinearLayout layoutEmpty;

    private NotesAdapter adapter;
    private FirebaseFirestore mFirebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

    private static final String TAG = "LockedFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_locked, container, false);
        unbinder = ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        boolean isTablet = getContext().getResources().getBoolean(R.bool.isTablet);

        int span;
        if (isTablet) {
            span = 4;
        } else {
            span = 2;
        }

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(span, StaggeredGridLayoutManager.VERTICAL);
        adapter = new NotesAdapter(getLockedActivity(), this);

        rvLocked.setLayoutManager(layoutManager);
        rvLocked.setAdapter(adapter);

        mFirebaseFirestore.collection(AppConstants.COLLECTION_USERS)
                .document(mFirebaseAuth.getCurrentUser().getUid())
                .collection(AppConstants.COLLECTION_NOTES)
                .whereEqualTo(AppConstants.LOCKED, true)
                .addSnapshotListener((documentSnapshots, e) -> {
                    if (e != null) {
                        Timber.e("Error collecting starred notes");
                        return;
                    }

                    if (documentSnapshots.isEmpty()) {
                        handleEmpty();
                        return;
                    }
                    setDataToRv(documentSnapshots.getDocuments());
                    layoutEmpty.setVisibility(View.GONE);
                });

    }

    private void setDataToRv(List<DocumentSnapshot> documents) {
        ArrayList<Note> notes = Utils.getNotesFromSnapshot(documents);

        adapter.setNotes(notes);
    }

    private void handleEmpty() {
        layoutEmpty.setVisibility(View.VISIBLE);
    }

    private LockedActivity getLockedActivity() {
        return (LockedActivity) getActivity();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onNoteClicked(Note note) {
        if (note.isLocked()) {

            new MaterialDialog.Builder(getLockedActivity())
                    .title("Enter Password")
                    .inputType(InputType.TYPE_CLASS_NUMBER)
                    .input("0000", null, (dialog, input) -> {
                        // Do something
                        // adding passowrd to sharedPref
                        SharedPreferences sharedPreferences = getLockedActivity().getSharedPreferences("snotes-prefs", Context.MODE_PRIVATE);
                        String pin = sharedPreferences.getString("pin", "0000");

                        String inputPassword = String.valueOf(input);

                        if (!Objects.equals(pin, inputPassword)) {
                            Utils.showPinError(getLockedActivity());
                        } else {
                            Utils.goToAddNotes(note, getLockedActivity());
                        }
                    }).show();

        } else {
            Utils.goToAddNotes(note, getLockedActivity());
        }
    }
}
