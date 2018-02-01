package in.snotes.snotes.notes;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class NotesListFragment extends Fragment implements NotesAdapter.NotesListener {


    @BindView(R.id.pb_notes)
    ProgressBar pbNotes;
    private FirebaseFirestore mFirebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

    @Override
    public void onNoteClicked(Note note) {
        Log.d(TAG, "Note clicked");
        mListener.onNoteClicked(note);
    }

    public interface NotesListFragmentListener {
        void onFabClicked();

        void onNoteClicked(Note note);
    }

    @BindView(R.id.rv_notes_list)
    RecyclerView rvNotesList;
    Unbinder unbinder;

    private NotesListFragmentListener mListener;

    private NotesAdapter adapter;

    private static final String TAG = "NotesListFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notes_list, container, false);
        unbinder = ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        RecyclerView rv_notes = getActivity().findViewById(R.id.rv_notes_list);

        adapter = new NotesAdapter(getContext(), this);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rv_notes.setLayoutManager(layoutManager);
        rv_notes.setAdapter(adapter);

        getDataFromDatabase();

    }

    private void getDataFromDatabase() {

        Log.d(TAG, "Getting data from firebase");

        String uid = mFirebaseAuth.getCurrentUser().getUid();

        pbNotes.setVisibility(View.VISIBLE);

        mFirebaseFirestore.collection("users").document(uid)
                .collection("notes")
                .addSnapshotListener((documentSnapshots, e) -> {

                    if (e != null) {
                        Log.e(TAG, "Error getting notes " + e.getMessage());
                        return;
                    }

                    if (documentSnapshots.isEmpty()) {
                        Log.e(TAG, "It is empty");
                        Toast.makeText(getContext(), "You have no notes.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Log.d(TAG, "Notes are being updated");
                    setRecyclerView(documentSnapshots.getDocuments());

                });

    }

    private void setRecyclerView(List<DocumentSnapshot> documents) {

        Log.d(TAG, "Setting note to recycler view");
        ArrayList<Note> notes = new ArrayList<>();

        for (DocumentSnapshot docuemnt : documents) {

            Note note = new Note.Builder()
                    .title(docuemnt.getString("title"))
                    .content(docuemnt.getString("content"))
                    .colorOfNote(Integer.parseInt(docuemnt.get("colorOfNote").toString()))
                    .isStarred(docuemnt.getBoolean("starred"))
                    .isLocked(docuemnt.getBoolean("locked"))
                    .hasRemainder(docuemnt.getBoolean("remainderSet"))
                    .remainderTime(docuemnt.getLong("remainderTime"))
                    .createdTime(docuemnt.getLong("createdTimestamp"))
                    .reference(docuemnt.getReference())
                    .buildNote();

            notes.add(note);
            Log.d(TAG, note.toString());
        }

        if (pbNotes != null) {
            pbNotes.setVisibility(View.GONE);
        }
        adapter.setNotes(notes);

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
