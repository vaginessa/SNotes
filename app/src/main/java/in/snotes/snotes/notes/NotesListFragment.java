package in.snotes.snotes.notes;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import in.snotes.snotes.R;
import in.snotes.snotes.model.Note;
import in.snotes.snotes.utils.AppConstants;
import in.snotes.snotes.utils.Utils;
import timber.log.Timber;

public class NotesListFragment extends Fragment implements NotesAdapter.NotesListener, SharedPreferences.OnSharedPreferenceChangeListener {


    @BindView(R.id.pb_notes)
    ProgressBar pbNotes;
    @BindView(R.id.layout_empty)
    LinearLayout layoutEmpty;
    private FirebaseFirestore mFirebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

    @BindView(R.id.rv_notes_list)
    RecyclerView rvNotesList;
    Unbinder unbinder;

    private NotesListFragmentListener mListener;

    private NotesAdapter adapter;
    private ArrayList<Note> notes;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notes_list, container, false);
        unbinder = ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        adapter = new NotesAdapter(getContext(), this);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rvNotesList.setLayoutManager(layoutManager);
        rvNotesList.setAdapter(adapter);

        getDataFromDatabase();

        //  registering the sharedprefs change listener
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getNotesMainActivity());
        prefs.registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onDestroy() {
        //  un-registering the sharedprefs change listener
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getNotesMainActivity());
        prefs.unregisterOnSharedPreferenceChangeListener(this);

        super.onDestroy();
    }

    private NotesMainActivity getNotesMainActivity() {
        return (NotesMainActivity) getActivity();
    }

    private void getDataFromDatabase() {

        Timber.d("Getting data from firebase");

        String uid = mFirebaseAuth.getCurrentUser().getUid();

        pbNotes.setVisibility(View.VISIBLE);

        mFirebaseFirestore.collection("users").document(uid)
                .collection("notes")
                .addSnapshotListener((documentSnapshots, e) -> {

                    if (pbNotes != null) {
                        pbNotes.setVisibility(View.GONE);
                    }

                    if (e != null) {
                        Timber.e("Error getting notes %s", e.getMessage());
                        return;
                    }

                    if (documentSnapshots.isEmpty()) {
                        Timber.e("It is empty");
                        Toast.makeText(getContext(), "You have no notes.", Toast.LENGTH_SHORT).show();
                        layoutEmpty.setVisibility(View.VISIBLE);
                        return;
                    }

                    Timber.d("Notes are being updated");
                    setRecyclerView(documentSnapshots.getDocuments());
                    layoutEmpty.setVisibility(View.GONE);


                });

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(AppConstants.PREFS_IS_PIN_SET)) {
            boolean isLocked = sharedPreferences.getBoolean(AppConstants.PREFS_IS_PIN_SET, false);
            // if the settings is changed to locked, we can't lock all the notes.
            // We only have to unlock all the notes if the lock is removed
            if (isLocked) {
                return;
            }

            if (notes == null || notes.isEmpty()) {
                return;
            }

            for (Note note : notes) {
                note.setLocked(false);
            }

            adapter.setNotes(notes);
        }
    }

    public interface NotesListFragmentListener {
        void onFabClicked();

        void onNoteClicked(Note note);
    }

    private void setRecyclerView(List<DocumentSnapshot> documents) {

        Timber.d("Setting note to recycler view");

        ArrayList<Note> notes = Utils.getNotesFromSnapshot(documents);
        this.notes = notes;

        adapter.setNotes(notes);

    }


    @Override
    public void onNoteClicked(Note note) {
        Timber.d("Note clicked");
        mListener.onNoteClicked(note);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.fab_add_notes)
    public void onFabClicked() {
        mListener.onFabClicked();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NotesListFragmentListener) {
            mListener = (NotesListFragmentListener) context;
        } else {
            throw new ClassCastException(context.toString() + " should implement NotesListFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
